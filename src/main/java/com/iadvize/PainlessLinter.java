package com.iadvize;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;

import java.io.File;
import java.nio.file.Files;
import java.util.BitSet;

class PainlessLexerImpl extends PainlessLexer {
    // adapted from https://github.com/elastic/elasticsearch/blob/main/modules/lang-painless/src/main/java/org/elasticsearch/painless/antlr/EnhancedPainlessLexer.java#L29
    // in order to implement the isSlashRegex method required by the lexer (@members of PainlessLexer.g4)
    
    private final String sourceName;
    private Token current = null;

    PainlessLexerImpl(CharStream charStream, String sourceName) {
        super(charStream);
        this.sourceName = sourceName;
    }

    @Override
    public Token nextToken() {
        current = super.nextToken();
        return current;
    }

    @Override
    public void recover(final LexerNoViableAltException lnvae) {
        final CharStream charStream = lnvae.getInputStream();
        final int startIndex = lnvae.getStartIndex();
        final String text = charStream.getText(Interval.of(startIndex, charStream.index()));

        String message = "unexpected character [" + getErrorDisplay(text) + "].";
        char firstChar = text.charAt(0);
        if ((firstChar == '\'' || firstChar == '"') && text.length() - 2 > 0 && text.charAt(text.length() - 2) == '\\') {
            /* Use a simple heuristic to guess if the unrecognized characters were trying to be a string but has a broken escape sequence.
             * If it was add an extra message about valid string escape sequences. */
            message += " The only valid escape sequences in strings starting with ["
                    + firstChar
                    + "] are [\\\\] and [\\"
                    + firstChar
                    + "].";
        }
        throw new IllegalArgumentException(message, lnvae);
    }

    @Override
    protected boolean isSlashRegex() {
        Token lastToken = current;
        if (lastToken == null) {
            return true;
        }
        switch (lastToken.getType()) {
            case PainlessLexer.RBRACE:
            case PainlessLexer.RP:
            case PainlessLexer.OCTAL:
            case PainlessLexer.HEX:
            case PainlessLexer.INTEGER:
            case PainlessLexer.DECIMAL:
            case PainlessLexer.ID:
            case PainlessLexer.DOTINTEGER:
            case PainlessLexer.DOTID:
                return false;
            default:
                return true;
        }
    }
}

public class PainlessLinter {
    public static void main(String[] args) throws Exception {
        String fileArg = args[0];
        
        if (fileArg == null) {
            System.out.println("Please provide a file path as argument");
            return;
        }
        
        File file = new File(fileArg);
        String input = new String(Files.readAllBytes(file.toPath()));

        System.out.println("Input: " + input);
        
        // Create an input stream from the input string
        CharStream charStream = CharStreams.fromString(input);

        // Create an instance of your generated lexer
        PainlessLexer lexer = new PainlessLexerImpl(charStream, file.getName());

        // Use the lexer to produce a stream of tokens
        PainlessParser parser = getParser(lexer);

        // use the parser
        
        try {
            PainlessParser.SourceContext source = parser.source();
            System.out.println("Parsed successfully: " + source.toStringTree(parser));
        } catch (Exception e) {
            System.out.println("Error when parsing painless code: " + e.getMessage());
        }

        
    }

    private static PainlessParser getParser(PainlessLexer lexer) {
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create an instance of your generated parser using the token stream
        PainlessParser parser = new PainlessParser(tokens);

        // Parse the input string
        parser.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object o, int i, int i1, String s, RecognitionException e) {
                System.out.println("Syntax error: " + s);
            }

            @Override
            public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
                System.out.println("Ambiguity: " + i + " " + i1);
            }

            @Override
            public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
                System.out.println("Attempting full context: " + i + " " + i1);
            }

            @Override
            public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
                System.out.println("Context sensitivity: " + i + " " + i1);
            }
        });
        return parser;
    }
}
