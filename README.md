# painless-lint

A very basic linter for Painless scripts (Elasticsearch scripting language).
It uses the official [Elasticsearch Painless grammar](https://github.com/elastic/elasticsearch/blob/main/modules/lang-painless/src/main/antlr/PainlessParser.g4) and [ANTLR4](https://www.antlr.org/) to generate a JVM based linter. 
Returns a non-zero exit code if any errors are found.

## Usage

### Get the tool via sources
clone the repo
    
```shell
git clone git@github.com:TidyMaze/painless-lint.git
```

install maven

```shell
apt install maven # debian / ubuntu
````

```shell
brew install maven
```

build the tool

```shell
cd painless-lint
make all
```

### Or get from release

```shell
PAINLESS_LINTER_JAR=./painless-lint.jar
PAINLESS_LINTER_VERSION=0.0.1

wget https://github.com/TidyMaze/painless-lint/releases/download/$(PAINLESS_LINTER_VERSION)/painless-lint-jar-with-dependencies.jar -O $(PAINLESS_LINTER_JAR)
ls painless/*.painless | xargs -I {} java -jar $(PAINLESS_LINTER_JAR) {}
```

### run the tool

```shell
java -jar ./target/painless-lint-jar-with-dependencies.jar ./samples/test1.painless
```

More details on the makefile

## FAQ

### Why did you create this tool?

Painless scripts have currently (as of 2024) no linter available. It makes it hard to catch errors early on.

## License

I don't know yet. Let me know if you want to use this tool and I will figure it out.

## Troubleshooting

### Errors in IDE (PainlessLexer, PainlessParser not found)

Make sure to run `make all` to generate the necessary files.
