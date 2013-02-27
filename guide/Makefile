
# the directories for the project
SRC_DIR = src
INC_DIR = include
OUT_DIR = doc
TMP_DIR = tmp

# grab all the .md files from the source directory
SRCS = $(wildcard $(SRC_DIR)/*.md)
TARGETS = $(patsubst $(SRC_DIR)/%,%,$(SRCS))

all: clean $(TARGETS) license css
	@echo "MAKE: COMPLETE"
	@echo "The generated files are in '$(OUT_DIR)' directory."

css:
	@echo "Make: Copying the CSS files..."
	cp $(INC_DIR)/markdown.css $(OUT_DIR)/markdown.css

mkdirs:
	@echo "MAKE: Creating directories, if needed..."
	mkdir -p $(TMP_DIR)
	mkdir -p $(OUT_DIR)

header: mkdirs
	@echo "MAKE: Building the header..."
	markdown $(INC_DIR)/header.md > $(TMP_DIR)/header.part

footer: mkdirs
	@echo "MAKE: Building the footer..."
	markdown $(INC_DIR)/footer.md > $(TMP_DIR)/footer.part

license: mkdirs
	@echo "MAKE: Building the license..."
	markdown $(INC_DIR)/license.md > $(TMP_DIR)/license.part
	cat $(TMP_DIR)/header.part $(TMP_DIR)/license.part $(TMP_DIR)/footer.part > $(OUT_DIR)/license.html

%.md: header footer
	@echo "MAKE: Building $@..."
	markdown $(SRC_DIR)/$@ > $(TMP_DIR)/$(@:.md=.part)
	cat $(TMP_DIR)/header.part $(TMP_DIR)/$(@:.md=.part) $(TMP_DIR)/footer.part > $(OUT_DIR)/$(@:.md=.html)

clean:
	@echo "MAKE: Cleaning the project..."
	rm -rf $(TMP_DIR)
	rm -rf $(OUT_DIR)

