#!/bin/bash

INSTALLER_TITLE="Galaxy Web Service Extensions"
INSTALLER_PWD=$(pwd)
INSTALLER_PATCH_PATH=""
INSTALLER_GALAXY_PATH="$(dirname $1)/$(basename $1)"
INSTALLER_PYTHON_PATH=""
INSTALLER_JAVA_PATH=""

INSTALLER_SSE_PATH=""
INSTALLER_WSX_PATH=""

INSTALLER_UPGRADE_SSE=0
INSTALLER_UPGRADE_WSX=0

function vercomp {

    # barrowed from http://stackoverflow.com/questions/4023830/bash-how-compare-two-strings-in-version-format

    if [[ $1 == $2 ]]
    then
        return 0
    fi

    local IFS=.
    local i ver1=($1) ver2=($2)
    
    for ((i=${#ver1[@]}; i<${#ver2[@]}; i++))
    do
        ver1[i]=0
    done
    
    for ((i=0; i<${#ver1[@]}; i++))
    do
        if [[ -z ${ver2[i]} ]]
        then
            ver2[i]=0
        fi

        if ((10#${ver1[i]} > 10#${ver2[i]}))
        then
            return 1
        fi

        if ((10#${ver1[i]} < 10#${ver2[i]}))
        then
            return 2
        fi
    done
    return 0

} # vercomp

function check_python {

    PYTHON_CHECK=" "
    PYTHON_PATH=$(which python)

    if [ $? -ne 0 ]; then
        PYTHON_CHECK="Could not find python on the path."
    fi

    PYTHON_VERSION=$(python -c 'import sys; print(".".join(map(str, sys.version_info[:3])))')

    vercomp $PYTHON_VERSION "2.7"
    if [ $? -eq 2 ]; then
        PYTHON_CHECK="Requires python 2.7+"
    fi

    if [ "$PYTHON_CHECK" = " " ]; then
	PYTHON_VERSION="($PYTHON_VERSION)"
        INSTALLER_PYTHON_PATH=$PYTHON_PATH
        print_item_good "python" "$INSTALLER_PYTHON_PATH $PYTHON_VERSION"
    else
        print_item_good "python" "$INSTALLER_PYTHON_PATH" "$PYTHON_CHECK"
	exit
    fi

} # check python

function check_java {

    JAVA_CHECK=" "
    JAVA_PATH=$(which java)

    if [ $? -ne 0 ]; then
        JAVA_CHECK="Could not find java on the path."
    fi

    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    JAVA_VERSION=$( echo `expr "$JAVA_VERSION$" : '\([0-9]*\\.[0-9]*\)'`)

    vercomp $JAVA_VERSION "1.7"
    if [ $? -eq 2 ]; then
        JAVA_CHECK="Requires java 7+ (1.7+)"
    fi

    if [ "$JAVA_CHECK" = " " ]; then
	JAVA_VERSION="($JAVA_VERSION)"
        INSTALLER_JAVA_PATH=$JAVA_PATH
        print_item_good "java" "$INSTALLER_JAVA_PATH $JAVA_VERSION"
    else
        print_item_good "java" "$INSTALLER_JAVA_PATH" "$JAVA_CHECK"
	exit
    fi

} # check_java

function check_all_prereqs {
    echo ""
    echo "Checking prerequisites..."
    check_python
    check_java
} # check_all_prereqs

function check_env {
    echo ""
    echo "Checking environment..."

    if [ -w "$INSTALLER_PWD" ]; then
        print_item_good "pwd" "$INSTALLER_PWD"
    else
        print_item_good "pwd" "$INSTALLER_PWD" "Need write permission."
    fi

    INSTALLER_GALAXY_PATH_CHECK=" "
    GALAXY_VERSION="($(hg log ~/galaxy-dist/ | head -n 1 | awk '{ print $2; }'))"

    if [ $? -ne 0 ]; then
        GALAXY_VERSION=""
    fi

    if [ ! -d "$INSTALLER_GALAXY_PATH/templates/webapps/galaxy/workflow/" ]; then
        INSTALLER_GALAXY_PATH_CHECK="Installer is not able to verify that this is a path to a Galaxy installation."
    fi

    if [ ! -w "$INSTALLER_GALAXY_PATH" ]; then
        INSTALLER_GALAXY_PATH_CHECK="Need write permission."
    fi

    if [ "$INSTALLER_GALAXY_PATH_CHECK" = " " ]; then
        print_item_good "galaxy" "$INSTALLER_GALAXY_PATH $GALAXY_VERSION"
    else
        print_item_bad "galaxy" "$INSTALLER_GALAXY_PATH" "$INSTALLER_GALAXY_PATH_CHECK"
	exit
    fi

    INSTALLER_PATCH_CHECK=" "
    INSTALLER_PATCH_PATH="$(which patch)"

    if [ $? -ne 0 ]; then
        INSTALLER_PATCH_CHECK="Need patch utility."
    fi

    if [ "$INSTALLER_PATCH_CHECK" = " " ]; then
        print_item_good "patch" "$INSTALLER_PATCH_PATH"
    else
        print_item_bad "patch" "$INSTALLER_PATCH_PATH" "$INSTALLER_PATCH_CHECK"
	exit
    fi

    INSTALLER_HG_CHECK=" "
    INSTALLER_HG_PATH="$(which hg)"

    if [ $? -ne 0 ]; then
        INSTALLER_HG_CHECK="Need mercurial utility."
    fi

    if [ "$INSTALLER_HG_CHECK" = " " ]; then
        print_item_good "hg" "$INSTALLER_HG_PATH"
    else
        print_item_bad "hg" "$INSTALLER_HG_PATH" "$INSTALLER_HG_CHECK"
	exit
    fi

} # check env

function check_prev_installs {

    echo ""
    echo "Checking for previous component installations..."

    INSTALLER_SSE_PATH="$INSTALLER_GALAXY_PATH/templates/webapps/galaxy/workflow/wsextensions"

    if [ -d "$INSTALLER_SSE_PATH" ]; then
        INSTALLER_UPGRADE_SSE=1
        print_item_good "sse" "$INSTALLER_SSE_PATH" 
    fi

    INSTALLER_WSX_PATH="$INSTALLER_GALAXY_PATH/tools/WebServiceToolWorkflow_REST_SOAP"

    if [ -d "$INSTALLER_WSX_PATH" ]; then
        INSTALLER_UPGRADE_WSX=1
        print_item_good "wsx" "$INSTALLER_WSX_PATH" 
    fi

    if [ $(($INSTALLER_UPGRADE_SSE + $INSTALLER_UPGRADE_WSX)) -eq 0 ]; then
	print_item_good "none" "nothing to upgrade"
    fi
    
} # check_prev_installs

function ask_upgrade {

    print_item_q "choose" "1) Replace, 2) Ignore, 3) Quit"

    read n
    case $n in
        1) return 1;;
        2) return 2;;
        3) exit;;
        *) return 4;;
    esac

} # ask_upgrade

function do_upgrade_sse {

    echo ""
    echo "The installer found a previous installation of the SSE component."

    print_item "sse" "$INSTALLER_SSE_PATH"
    ask_upgrade

    RET=$?
    while [ $RET -eq 4 ]; do
        ask_upgrade
    done

    if [ $RET -eq 1 ]; then
        do_clean_prev_sse
        do_install_sse
    fi

} # do_upgrade_sse

function do_upgrade_wsx {

    echo ""
    echo "The installer found a previous installation of the WSX component."

    print_item "wsx" "$INSTALLER_WSX_PATH"
    ask_upgrade

    RET=$?
    while [ $RET -eq 4 ]; do
        ask_upgrade
    done

    if [ $RET -eq 1 ]; then
        do_clean_prev_wsx
        do_install_wsx
    fi

} # do_upgrade_wsx

function do_clean_prev_sse {

    echo ""
    echo "Removing previous installation of the SSE component..."

    MKDIR_RET=$(mkdir -p "$INSTALLER_PWD/backups")
    print_item_good "backup" "'backups' directory successfully created"

    BAK_DATE=$(date +"%Y.%m.%d.%H.%M.%S")
    BAK_NAME="wsextensions.$BAK_DATE.tar.gz"
    TAR_RET=$(tar zcPf "$INSTALLER_PWD/backups/$BAK_NAME" "$INSTALLER_SSE_PATH")
    print_item_good "tar" "'backups/$BAK_NAME' successfully created"

    RM_RET=$(rm -rf "$INSTALLER_SSE_PATH")
    print_item_good "rm" "'wsextensions' directory successfully deleted"

    EDITOR_ORIG="$INSTALLER_GALAXY_PATH/templates/webapps/galaxy/workflow/editor.mako"
    HG_RET=$(hg revert -q "$EDITOR_ORIG")
    print_item_good "hg" "'editor.mako' successfully reverted"

} # do_clean_prev_sse

function do_clean_prev_wsx {

    echo ""
    echo "Removing previous installation of the WSX component..."

} # do_clean_prev_wsx

function do_install_sse {

    echo ""
    echo "Installing SSE component..."

    CP_RET=$(cp -r "$INSTALLER_PWD/templates/wsextensions" "$INSTALLER_SSE_PATH")

    print_item_good "cp" "'wsextensions' directory successfully copied"

    EDITOR_ORIG="$INSTALLER_GALAXY_PATH/templates/webapps/galaxy/workflow/editor.mako"
    EDITOR_PATCH="$INSTALLER_PWD/patches/editor.mako.patch"
    
    PATCH_RET=$(patch -f -b "$EDITOR_ORIG" < "$EDITOR_PATCH")
    print_item_good "patch" "$PATCH_RET"

} # do_install_sse

function do_install_wsx {

    echo ""
    echo "Installing WSX component..."

    

} # do_install_sse

function do_patch_run_sh {

    echo ""
    echo "Patching run.sh to add GALAXY_HOME"

    RUN_ORIG="$INSTALLER_GALAXY_PATH/run.sh"

    MKDIR_RET=$(mkdir -p "$INSTALLER_PWD/backups")
    print_item_good "backup" "'backups' directory successfully created"

    BAK_DATE=$(date +"%Y.%m.%d.%H.%M.%S")
    BAK_NAME="run.sh.$BAK_DATE"
    CP_RET=$(cp "$RUN_ORIG" "$INSTALLER_PWD/backups/$BAK_NAME")
    print_item_good "cp" "'backups/$BAK_NAME' successfully created"

    HG_RET=$(hg revert -q "$RUN_ORIG")
    
    if [ HG_RET != "" ]; then
        HG_RET="'run.sh' successfully reverted"
    fi

    print_item_good "hg" "$HG_RET"

    PATCH_DATE=$(date +"%Y.%m.%d.%H.%M.%S")
    PATCH_FILE="/tmp/run.sh.$PATCH_DATE.patch"
    
    SED_PATH=$(echo "$$INSTALLER_GALAXY_PATH" | sed 's/\//\/\//g')
    SED_RET=$(sed "s/_GALAXY_HOME/$SED_PATH/g" "$INSTALLER_PWD/patches/run.sh.patch.unready"> "$PATCH_FILE")
    
    print_item_good "sed" "'$PATCH_FILE' generated"
    
    PATCH_RET=$(patch -f -b "$RUN_ORIG" < "$PATCH_FILE")
    print_item_good "patch" "$PATCH_RET"
    
} # do_patch_run_sh

function print_item {
    printf "   %16s: %s \n" $1 $2
} # print_item

function print_item_good {
    printf "  \e[1;32m[OK]\e[m %12s: %s \n" "$1" "$2"
} # print_item_good

function print_item_q {
    printf "  \e[1;33m[??]\e[m %12s: %s > " "$1" "$2"
} # print_item_q

function print_item_bad {
    printf " \e[1;31m[BAD]\e[m %12s: %s \n" "$1" "$2"
    printf "       %12s  %s \n" "" "$3"
} # print_item_bad

function main {
    echo "Installer for $INSTALLER_TITLE"

    if [ "$2" -ne 1 ]; then
        echo "Usage: `basename $1` PATH_TO_GALAXY"
        exit
    fi

    check_env
    check_all_prereqs
    check_prev_installs
    
    if [ "$INSTALLER_UPGRADE_SSE" -eq 1 ]; then
        do_upgrade_sse
    else
        do_install_sse
    fi

    if [ "$INSTALLER_UPGRADE_WSX" -eq 1 ]; then
        do_upgrade_wsx
    else
        do_install_wsx
    fi

    do_patch_run_sh

} # main

# run the installer
main "$0" "$#"

