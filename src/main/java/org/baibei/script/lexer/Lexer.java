package org.baibei.script.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int positionInLine = 0;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("func", TokenType.FUNCTION);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("return", TokenType.RETURN);
        keywords.put("switch", TokenType.SWITCH);
        keywords.put("case", TokenType.CASE);
        keywords.put("default", TokenType.DEFAULT);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
        keywords.put("then", TokenType.THEN);
        keywords.put("import", TokenType.IMPORT);
        keywords.put("var", TokenType.VAR);
    }

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() throws LexerException {
        tokens.clear();
        start = 0;
        current = 0;
        line = 1;
        positionInLine = 0;

        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", line, positionInLine));
        return tokens;
    }

    private void scanToken() throws LexerException {
        char c = advance();

        switch (c) {
            // Односимвольные лексемы
            case '(': addToken(TokenType.LPAREN); break;
            case ')': addToken(TokenType.RPAREN); break;
            case '{': addToken(TokenType.LBRACE); break;
            case '}': addToken(TokenType.RBRACE); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '?': addToken(TokenType.QUESTION); break;
            case ':': addToken(TokenType.COLON); break;

            // Операторы
            case '+':
                if (match('+')) {
                    addToken(TokenType.INCREMENT);
                } else {
                    addToken(TokenType.ADD);
                }
                break;
            case '-':
                if (match('-')) {
                    addToken(TokenType.DECREMENT);
                } else {
                    addToken(TokenType.SUB);
                }
                break;
            case '*':
                if (match('*')) {
                    addToken(TokenType.POW);
                } else {
                    addToken(TokenType.MUL);
                }
                break;
            case '%': addToken(TokenType.MOD); break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(TokenType.DIV);
                }
                break;

            case '!':
                addToken(match('=') ? TokenType.NEQ : TokenType.NOT);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQ : TokenType.ASSIGN);
                break;
            case '<':
                addToken(match('=') ? TokenType.LTE : TokenType.LT);
                break;
            case '>':
                addToken(match('=') ? TokenType.GTE : TokenType.GT);
                break;
            case '&':
                if (match('&')) {
                    addToken(TokenType.AND);
                } else {
                    error("Expected '&' after '&'");
                }
                break;
            case '|':
                if (match('|')) {
                    addToken(TokenType.OR);
                } else {
                    error("Expected '|' after '|'");
                }
                break;

            // Пробельные символы
            case ' ':
            case '\r':
            case '\t':
                // Пропускаем
                break;

            // Новая строка
            case '\n':
                line++;
                positionInLine = 0;
                break;

            // Строки
            case '"':
                string();
                break;

            // Числа
            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                number();
                break;

            // Идентификаторы
            default:
                if (isAlpha(c)) {
                    identifier();
                } else {
                    error("Unexpected character: " + c);
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    private void number() throws LexerException {
        while (Character.isDigit(peek())) advance();

        // Поддержка дробных чисел
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // Пропускаем точку

            while (Character.isDigit(peek())) advance();
        }

        // Поддержка экспоненциальной записи
        if (peek() == 'e' || peek() == 'E') {
            advance();

            if (peek() == '+' || peek() == '-') {
                advance();
            }

            if (!Character.isDigit(peek())) {
                error("Invalid exponent");
            }

            while (Character.isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER);
    }

    private void string() throws LexerException {
        StringBuilder sb = new StringBuilder();
        //advance(); // Пропускаем открывающую кавычку

        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\\') {
                advance(); // Пропускаем '\'
                switch (peek()) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default: error("Invalid escape sequence");
                }
                advance();
            } else {
                sb.append(advance());
            }
        }

        if (isAtEnd()) error("Unterminated string");
        advance(); // Пропускаем закрывающую кавычку
        addToken(TokenType.STRING, sb.toString());
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        positionInLine++;
        return true;
    }

    private char peek() {
        if (current >= source.length()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_' || c == '$';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || Character.isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private char advance() {
        current++;
        positionInLine++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, source.substring(start, current));
    }

    private void addToken(TokenType type, String literal) {
        tokens.add(new Token(type, literal, line, start));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void error(String message) throws LexerException {
        throw new LexerException(line, positionInLine, message);
    }

    public static class LexerException extends Exception {
        final int line;
        final int position;

        public LexerException(int line, int position, String message) {
            super(String.format("Lexing error at line %d, position %d: %s", line, position, message));
            this.line = line;
            this.position = position;
        }
    }
}
