PHONY: all get-grammar keep-grammar patch build clean check-package

# Define repository URL and paths
ANTLR_VERSION=4.13.2

# Main target: clone repo and copy the antlr folder
all: clean get-grammar keep-grammar build check-package

# Get the painless grammar
get-grammar:
	git clone --depth 1 --filter=blob:none --sparse https://github.com/elastic/elasticsearch.git
	cd elasticsearch && git sparse-checkout init --cone && git sparse-checkout set modules/lang-painless/src/main/antlr

# Copy the grammar files (*.g4)
keep-grammar:
	mkdir -p src/main/antlr4/com/iadvize
	cp -r elasticsearch/modules/lang-painless/src/main/antlr/PainlessLexer.g4 elasticsearch/modules/lang-painless/src/main/antlr/PainlessParser.g4 src/main/antlr4/com/iadvize
	rm -rf elasticsearch

# Patch the generated source files, lexer does not implement the isSlashRegex
# This is called by maven
patch:
	sed -i '' "s/public class PainlessLexer/public abstract class PainlessLexer/" target/generated-sources/antlr4/com/iadvize/PainlessLexer.java

build:
	mvn compile package

clean:
	rm -rf elasticsearch
	rm -rf src/main/antlr4
	rm -rf gen
	mvn clean

check-package:
	java -jar ./target/painless-lint-jar-with-dependencies.jar ./samples/test1.painless
