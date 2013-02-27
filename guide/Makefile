OUT_DIR = doc
TMP_DIR = tmp
INC_DIR = include
SRC_DIR = src

TARGETS = user-guide.md \
          install-guide.md

all: $(TARGETS)

mkdirs:
	mkdir -p $(TMP_DIR)
	mkdir -p $(OUT_DIR)

header: mkdirs
	markdown $(INC_DIR)/header.md > $(TMP_DIR)/header.part

footer: mkdirs
	markdown $(INC_DIR)/footer.md > $(TMP_DIR)/footer.part

%.md: header footer
	markdown $(SRC_DIR)/$@ > $(TMP_DIR)/$(@:.md=.part)
	cat $(TMP_DIR)/header.part $(TMP_DIR)/$(@:.md=.part) $(TMP_DIR)/footer.part > $(OUT_DIR)/$(@:.md=.html)

clean:
	rm -rf $(TMP_DIR)
	rm -rf $(OUT_DIR)

