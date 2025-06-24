package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.MathEvaluator;

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
