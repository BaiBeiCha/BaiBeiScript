package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.MathEvaluator;
import org.baibei.scrypt.interpreter.Context;

public class MathExpressionNode extends ExpressionNode {

    private final String expression;

    public MathExpressionNode(String expression) {
        this.expression = expression;
    }

    @Override
    public Object execute(Context context) {
        try {
            return new MathEvaluator().evaluate(expression);
        } catch (Exception e) {
            throw new RuntimeException("Math error: " + e.getMessage());
        }
    }
}
