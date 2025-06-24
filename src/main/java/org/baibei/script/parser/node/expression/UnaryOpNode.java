package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.lexer.TokenType;

public class UnaryOpNode extends ExpressionNode {

    private final TokenType operator;
    private final ExpressionNode right;

    public UnaryOpNode(TokenType operator, ExpressionNode right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object execute(Context context) {
        Object rightVal = right.execute(context);
        return switch (operator) {
            case SUB -> -(double) rightVal;
            case NOT -> !isTruthy(rightVal);
            default -> throw new RuntimeException("Invalid unary operator");
        };
    }

    private boolean isTruthy(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean) return (boolean) obj;
        return true;
    }
}
