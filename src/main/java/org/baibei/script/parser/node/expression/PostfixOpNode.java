package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.lexer.TokenType;

public class PostfixOpNode extends ExpressionNode {

    private final TokenType operator;
    private final VariableNode operand;

    public PostfixOpNode(TokenType operator, VariableNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public Object execute(Context ctx) {
        String name = operand.getName();
        Object value = ctx.getVariable(name);
        Object newValue;

        if (value instanceof Integer) {
            int val = (Integer) value;
            newValue = (operator == TokenType.INCREMENT) ? val + 1 : val - 1;
        } else if (value instanceof Long) {
            long val = (Long) value;
            newValue = (operator == TokenType.INCREMENT) ? val + 1 : val - 1;
        } else if (value instanceof Double) {
            double val = (Double) value;
            newValue = (operator == TokenType.INCREMENT) ? val + 1 : val - 1;
        } else {
            throw new RuntimeException("Increment/decrement requires a number");
        }

        ctx.setVariable(name, newValue);
        return value;
    }
}