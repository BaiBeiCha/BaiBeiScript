package org.baibei.scrypt.lexer;

public class Token {

    final TokenType type;
    final String lexeme;
    final int line;
    final int position;

    public Token(TokenType type, String lexeme, int line, int position) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("Token[%s: '%s'] (line %d, pos %d)",
                type, lexeme, line, position);
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }
}
