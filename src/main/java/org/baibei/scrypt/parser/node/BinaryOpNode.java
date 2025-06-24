package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.lexer.TokenType;
import org.baibei.scrypt.interpreter.Context;

public class BinaryOpNode extends ExpressionNode {

    private final TokenType operator;
    private final ExpressionNode left;
    private final ExpressionNode right;

    public BinaryOpNode(TokenType operator, ExpressionNode left, ExpressionNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Object execute(Context context) {
        Object leftVal = left.execute(context);
        Object rightVal = right.execute(context);

        if (leftVal instanceof Number l && rightVal instanceof Number r) {
            return switch (operator) {
                case ADD -> l.doubleValue() + r.doubleValue();
                case SUB -> l.doubleValue() - r.doubleValue();
                case MUL -> l.doubleValue() * r.doubleValue();
                case DIV -> {
                    if (r.doubleValue() == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    yield l.doubleValue() / r.doubleValue();
                }
                case MOD -> l.doubleValue() % r.doubleValue();
                case POW -> Math.pow(l.doubleValue(), r.doubleValue());
                default -> throw new UnsupportedOperationException("Unsupported operator: " + operator);
            };
        }
        throw new UnsupportedOperationException("Unsupported operand types: " +
                leftVal.getClass() + " and " + rightVal.getClass());
    }
}
