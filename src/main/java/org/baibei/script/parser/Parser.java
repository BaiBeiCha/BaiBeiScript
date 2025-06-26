package org.baibei.script.parser;

import org.baibei.script.interpreter.Context;
import org.baibei.script.lexer.Token;
import org.baibei.script.lexer.TokenType;
import org.baibei.script.parser.node.*;
import org.baibei.script.parser.node.common.*;
import org.baibei.script.parser.node.common.loop.BreakNode;
import org.baibei.script.parser.node.common.loop.ContinueNode;
import org.baibei.script.parser.node.expression.*;
import org.baibei.script.parser.node.common.loop.ForNode;
import org.baibei.script.parser.node.common.loop.WhileNode;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode parse() throws ParserException {
        ProgramNode program = new ProgramNode();
        while (!isAtEnd()) {
            program.addStatement(declaration());
        }
        return program;
    }

    private ASTNode declaration() {
        try {
            if (match(TokenType.IMPORT)) {
                return importStatement();
            }
            if (match(TokenType.FUNCTION)) {
                return functionDeclaration();
            }
            if (check(TokenType.STATIC) || check(TokenType.FINAL) || check(TokenType.CONST) ||
                    check(TokenType.VAR) || check(TokenType.INT) || check(TokenType.LONG) ||
                    check(TokenType.DOUBLE) || check(TokenType.STRING)) {
                return variableDeclaration();
            }
            return statement();
        } catch (ParserException e) {
            synchronize();
            return new EmptyNode();
        }
    }

    private FunctionNode functionDeclaration() throws ParserException {
        Token name = consume(TokenType.IDENTIFIER, "Expect function name.");

        consume(TokenType.LPAREN, "Expect '(' after function name.");
        List<String> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name.").getLexeme());
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RPAREN, "Expect ')' after parameters.");

        ASTNode body = block();
        return new FunctionNode(name.getLexeme(), parameters, body);
    }

    private ASTNode variableDeclaration() throws ParserException {
        boolean isStatic = match(TokenType.STATIC);
        boolean isFinal = match(TokenType.FINAL) || match(TokenType.CONST);

        String type = "object";
        int dimensions = 0;

        if (match(TokenType.INT, TokenType.LONG, TokenType.DOUBLE, TokenType.STRING)) {
            type = previous().getType().name().toLowerCase();

            while (match(TokenType.LBRACKET)) {
                dimensions++;
                consume(TokenType.RBRACKET, "Expect ']' after array dimension");
            }
        } else if (match(TokenType.VAR)) {
            type = "var";
        }

        Token name = consume(TokenType.IDENTIFIER, "Expect variable name");

        ExpressionNode initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration");

        return new VariableDeclarationNode(
                type, name.getLexeme(), initializer, isFinal, isStatic, dimensions
        );
    }

    private String parseType() throws ParserException {
        if (match(TokenType.INT)) return "int";
        if (match(TokenType.LONG)) return "long";
        if (match(TokenType.DOUBLE)) return "double";
        if (match(TokenType.STRING)) return "string";
        throw new ParserException(peek(), "Incorrect type of variable");
    }

    private ASTNode statement() throws ParserException {
        if (match(TokenType.SEMICOLON)) return new EmptyNode();
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.FOR)) return forStatement();
        if (check(TokenType.LBRACE)) return block();
        if (match(TokenType.RETURN)) return returnStatement();
        if (match(TokenType.BREAK)) return new BreakNode();
        if (match(TokenType.CONTINUE)) return new ContinueNode();
        if (match(TokenType.VAR)) return variableDeclaration();
        return expressionStatement();
    }

    private ASTNode returnStatement() throws ParserException {
        ExpressionNode value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return.");
        return new ReturnNode(value);
    }

    private IfNode ifStatement() throws ParserException {
        consume(TokenType.LPAREN, "Expect '(' after 'if'.");
        ExpressionNode condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after if condition.");

        ASTNode ifBlock = statement();
        ASTNode elseBlock = null;

        if (match(TokenType.ELSE)) {
            elseBlock = statement();
        }

        return new IfNode(condition, ifBlock, elseBlock);
    }

    private WhileNode whileStatement() throws ParserException {
        consume(TokenType.LPAREN, "Expect '(' after 'while'.");
        ExpressionNode condition = expression();
        consume(TokenType.RPAREN, "Expect ')' after while condition.");
        ASTNode body = statement();
        return new WhileNode(condition, body);
    }

    private ForNode forStatement() throws ParserException {
        consume(TokenType.LPAREN, "Expect '(' after 'for'.");

        ASTNode init;
        if (match(TokenType.VAR)) {
            Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
            ExpressionNode initializer = null;
            if (match(TokenType.ASSIGN)) {
                initializer = expression();
            }
            init = new AssignmentNode(name.getLexeme(), initializer);
        } else {
            init = expressionStatement();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after initializer.");

        ExpressionNode condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after condition.");

        ExpressionNode increment = null;
        if (!check(TokenType.RPAREN)) {
            increment = expression();
        }
        consume(TokenType.RPAREN, "Expect ')' after for clauses.");

        ASTNode body = statement();
        return new ForNode(init, condition, increment, body);
    }

    private BlockNode block() throws ParserException {
        consume(TokenType.LBRACE, "Expect '{' before block.");
        BlockNode block = new BlockNode();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            ASTNode stmt = declaration();
            if (stmt == null) continue;
            block.addStatement(stmt);
        }

        consume(TokenType.RBRACE, "Expect '}' after block.");
        return block;
    }

    private ImportNode importStatement() throws ParserException {
        String path;
        if (match(TokenType.STRING)) {
            path = previous().getLexeme();
        } else if (match(TokenType.IDENTIFIER)) {
            StringBuilder sb = new StringBuilder(previous().getLexeme());
            while (match(TokenType.DOT)) {
                Token part = consume(TokenType.IDENTIFIER, "Expect identifier in path after '.'.");
                sb.append('.').append(part.getLexeme());
            }
            path = sb.toString();
        } else {
            throw new ParserException(peek(), "Expect file path after import.");
        }
        consume(TokenType.SEMICOLON, "Expect ';' after import.");
        return new ImportNode(path);
    }

    private ASTNode expressionStatement() throws ParserException {
        ExpressionNode expr = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return expr;
    }

    private ExpressionNode expression() throws ParserException {
        return assignment();
    }

    private ExpressionNode assignment() throws ParserException {
        ExpressionNode expr = logicalOr();

        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            ExpressionNode value = assignment();

            if (expr instanceof VariableNode) {
                String varName = ((VariableNode)expr).getName();
                return new AssignmentNode(varName, value);
            }

            throw new ParserException(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private ExpressionNode logicalOr() throws ParserException {
        ExpressionNode expr = logicalAnd();

        while (match(TokenType.OR)) {
            Token operator = previous();
            ExpressionNode right = logicalAnd();
            expr = new BinaryOpNode(operator.getType(), expr, right);
        }

        return expr;
    }

    private ExpressionNode logicalAnd() throws ParserException {
        ExpressionNode expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            ExpressionNode right = equality();
            expr = new BinaryOpNode(operator.getType(), expr, right);
        }

        return expr;
    }

    private ExpressionNode equality() throws ParserException {
        ExpressionNode expr = comparison();

        while (match(TokenType.EQ, TokenType.NEQ)) {
            Token operator = previous();
            ExpressionNode right = comparison();
            expr = new BinaryOpNode(operator.getType(), expr, right);
        }

        return expr;
    }

    private ExpressionNode comparison() throws ParserException {
        ExpressionNode expr = term();

        while (match(TokenType.LT, TokenType.GT, TokenType.LTE, TokenType.GTE)) {
            Token operator = previous();
            ExpressionNode right = term();
            expr = new BinaryOpNode(operator.getType(), expr, right);
        }

        return expr;
    }

    private ExpressionNode term() throws ParserException {
        ExpressionNode expr = factor();

        while (match(TokenType.ADD, TokenType.SUB)) {
            Token operator = previous();
            ExpressionNode right = factor();
            expr = new BinaryOpNode(operator.getType(), expr, right);
        }

        return expr;
    }

    private ExpressionNode factor() throws ParserException {
        ExpressionNode expr = unary();

        while (match(TokenType.MUL, TokenType.DIV, TokenType.MOD)) {
            Token operator = previous();
            ExpressionNode right = unary();
            expr = new BinaryOpNode(operator.getType(), expr, right);
        }

        return expr;
    }

    private ExpressionNode unary() throws ParserException {
        if (match(TokenType.INCREMENT, TokenType.DECREMENT)) {
            TokenType op = previous().getType();
            ExpressionNode operand = unary();
            if (!(operand instanceof VariableNode)) {
                throw new ParserException(peek(), "Increment/decrement must apply to a variable.");
            }
            return new PrefixOpNode(op, (VariableNode) operand);
        }

        if (match(TokenType.NOT, TokenType.SUB)) {
            TokenType op = previous().getType();
            ExpressionNode right = unary();
            return new UnaryOpNode(op, right);
        }

        return call();
    }

    private ExpressionNode call() throws ParserException {
        ExpressionNode expr = primary();

        while (true) {
            if (match(TokenType.LPAREN)) {
                expr = finishCall(expr);
            } else if (match(TokenType.LBRACKET)) {
                ExpressionNode index = expression();
                consume(TokenType.RBRACKET, "Expect ']' after index.");
                expr = new ArrayAccessNode(expr, index);
            } else if (match(TokenType.INCREMENT, TokenType.DECREMENT)) {
                TokenType op = previous().getType();
                if (!(expr instanceof VariableNode)) {
                    throw new ParserException(peek(),
                            "Increment/decrement must apply to a variable.");
                }
                expr = new PostfixOpNode(op, (VariableNode) expr);
            } else {
                break;
            }
        }

        return expr;
    }

    private ExpressionNode finishCall(ExpressionNode callee) throws ParserException {
        List<ExpressionNode> arguments = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                if (arguments.size() >= 255) {
                    throw new ParserException(peek(), "Can't have more than 255 arguments.");
                }
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        Token paren = consume(TokenType.RPAREN, "Expect ')' after arguments.");

        if (callee instanceof VariableNode) {
            String name = ((VariableNode) callee).getName();
            return new FunctionCallNode(name, arguments);
        }
        throw new ParserException(paren, "Can only call functions.");
    }

    private ExpressionNode primary() throws ParserException {
        if (match(TokenType.NUMBER)) {
            String number = previous().getLexeme();
            if (number.contains(".") || number.contains("e") || number.contains("E")) {
                return new DoubleNode(Double.parseDouble(number));
            } else {
                try {
                    return new IntegerNode(Integer.parseInt(number));
                } catch (NumberFormatException e) {
                    return new LongNode(Long.parseLong(number));
                }
            }
        }
        if (match(TokenType.STRING)) {
            String raw = previous().getLexeme();
            String value = (raw.startsWith("\"") && raw.endsWith("\""))
                    ? raw.substring(1, raw.length() - 1)
                    : raw;
            return new StringNode(value);
        }
        if (match(TokenType.IDENTIFIER)) {
            return new VariableNode(previous().getLexeme());
        }
        if (match(TokenType.LBRACE)) {
            return arrayLiteral();
        }
        if (match(TokenType.LPAREN)) {
            ExpressionNode expr = expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return expr;
        }
        throw new ParserException(peek(), "Expect expression.");
    }

    private ExpressionNode arrayLiteral() throws ParserException {
        List<ExpressionNode> elements = new ArrayList<>();

        if (!check(TokenType.RBRACE)) {
            do {
                if (match(TokenType.LBRACE)) {
                    elements.add(arrayLiteral());
                } else {
                    elements.add(expression());
                }
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RBRACE, "Expect '}' after array elements.");
        return new ArrayLiteralNode(elements);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return tokens.get(current).getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return current >= tokens.size() ||
                tokens.get(current).getType() == TokenType.EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token consume(TokenType type, String message) throws ParserException {
        if (check(type)) return advance();
        throw new ParserException(peek(), message);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().getType() == TokenType.SEMICOLON) return;

            switch (peek().getType()) {
                case FUNCTION:
                case IF:
                case WHILE:
                case FOR:
                    return;
                default:
                    // Do nothing :)
                    break;
            }

            advance();
        }
    }

    public static class ParserException extends Exception {
        final Token token;

        public ParserException(Token token, String message) {
            super(message);
            this.token = token;
        }
    }
}
