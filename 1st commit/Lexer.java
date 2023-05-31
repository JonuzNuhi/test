package parser;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private static final String OPERATOR_CHARS = "+-*/()=%";
    private static final TokenType[] OPERATOR_TOKENS = {
            TokenType.PLUS, TokenType.MINUS,
            TokenType.STAR, TokenType.SLASH,
            TokenType.LPAREN, TokenType.RPAREN,
            TokenType.EQ, TokenType.REMAINING
    };
    private final String input;
    private final int length;
    private final List<Token> tokens;
    private int pos;

    public Lexer(String input){
        this.input = input;
        length = input.length();

        tokens = new ArrayList<>();
    }

}
