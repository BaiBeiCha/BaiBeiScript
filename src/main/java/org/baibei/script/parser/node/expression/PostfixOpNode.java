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
        Number value = (Number) ctx.getVariable(name);
        int v = value.intValue();
        int newVal = (operator == TokenType.INCREMENT) ? v + 1 : v - 1;
        ctx.setVariable(name, newVal);
        return v;
    }
}
