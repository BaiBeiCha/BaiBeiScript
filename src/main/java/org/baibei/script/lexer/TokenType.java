package org.baibei.script.lexer;

public enum TokenType {
    // Специальные токены
    EOF,
    ERROR,

    // Литералы
    IDENTIFIER,
    NUMBER,
    STRING,
    VAR,

    // Ключевые слова
    IF, ELSE, WHILE, FOR, FUNCTION, RETURN,
    SWITCH, CASE, DEFAULT, BREAK, CONTINUE,
    IMPORT,

    // Операторы
    ASSIGN,         // =
    ADD,            // +
    SUB,            // -
    MUL,            // *
    DIV,            // /
    MOD,            // %
    POW,            // **
    INCREMENT,      // ++
    DECREMENT,      // --

    // Операторы сравнения
    EQ,             // ==
    NEQ,            // !=
    LT,             // <
    GT,             // >
    LTE,            // <=
    GTE,            // >=

    // Логические операторы
    AND,            // &&
    OR,             // ||
    NOT,            // !

    // Разделители
    LPAREN,         // (
    RPAREN,         // )
    LBRACE,         // {
    RBRACE,         // }
    SEMICOLON,      // ;
    COMMA,          // ,
    COLON,          // :
    DOT,            // .
    QUESTION,       // ?

    // Управляющие конструкции
    THEN,           // ->
}
