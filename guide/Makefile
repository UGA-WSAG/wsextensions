all: user-guide install-guide

mkdirs:
	mkdie -p tmp
	mkdir -p doc

header: mkdirs
	markdown include/header.md > tmp/header.part

footer: mkdirs
	markdown include/footer.md > tmp/footer.part

user-guide: header footer
	markdown user-guide.md > tmp/user-guide.part
	cat tmp/header.part tmp/user-guide.part tmp/footer.part > out/user-guide.html

install-guide: header footer
	markdown install-guide.md > tmp/install-guide.part
	cat tmp/header.part tmp/install-guide.part tmp/footer.part > out/install-guide.html

clean:
	rm -f tmp/*.part
	rm -f out/*.md

