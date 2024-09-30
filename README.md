# painless-lint

A very basic linter for Painless scripts (Elasticsearch scripting language).
It uses the official [Elasticsearch Painless grammar](https://github.com/elastic/elasticsearch/blob/main/modules/lang-painless/src/main/antlr/PainlessParser.g4) to parse the scripts.
Returns a non-zero exit code if any errors are found.

## Usage

clone the repo
    
    git clone git@github.com:TidyMaze/painless-lint.git

build the tool

    cd painless-lint
    make all

run the tool

    java -jar ./target/painless-lint-mvn-1.0-SNAPSHOT-jar-with-dependencies.jar ./samples/test1.painless

more details on the makefile
