
# the directories for the project
SRC_DIR = src
INC_DIR = include
IMG_DIR = images
CSS_DIR = css
JS_DIR = js

# output and temporary directories
OUT_DIR = doc
TMP_DIR = tmp

# grab all the .md files from the source directory
SRCS = $(wildcard $(SRC_DIR)/*.md)
INCS = $(wildcard $(INC_DIR)/*.md)
IMGS = $(wildcard $(IMG_DIR)/*)
CSSS = $(wildcard $(CSS_DIR)/*)
JSS = $(wildcard $(JS_DIR)/*)

all: $(SRCS) support
	@echo "# MAKE: COMPLETE"
	@echo "The generated output files are in the '$(OUT_DIR)' directory."

support: $(IMGS) $(CSSS) $(JSS) 

$(CSS_DIR)/%: mkdirs
	cp $@ $(OUT_DIR)/$@

$(IMG_DIR)/%: mkdirs
	cp $@ $(OUT_DIR)/$@

$(JS_DIR)/%: mkdirs
	cp $@ $(OUT_DIR)/$@

mkdirs:
	mkdir -p $(TMP_DIR)
	mkdir -p $(TMP_DIR)/$(INC_DIR)
	mkdir -p $(OUT_DIR)
	mkdir -p $(OUT_DIR)/$(IMG_DIR)
	mkdir -p $(OUT_DIR)/$(CSS_DIR)
	mkdir -p $(OUT_DIR)/$(JS_DIR)

$(INC_DIR)/%.md: mkdirs support
	$(eval DEST := $(patsubst %.md,$(TMP_DIR)/%.part,$@))
	markdown $@ > $(DEST)

$(SRC_DIR)/%.md: mkdirs $(INCS) support
	$(eval FILE := $(patsubst $(SRC_DIR)/%,%,$@))
	markdown $@ > $(TMP_DIR)/$(FILE:.md=.part)
	cat $(TMP_DIR)/$(INC_DIR)/header.part $(TMP_DIR)/$(FILE:.md=.part) $(TMP_DIR)/$(INC_DIR)/footer.part > $(OUT_DIR)/$(FILE:.md=.html)

clean:
	rm -rf $(TMP_DIR)
	rm -rf $(OUT_DIR)

