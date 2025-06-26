package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;

public class DoubleNode extends ExpressionNode {
    private final double value;

    public DoubleNode(double value) {
        this.value = value;
    }

    @Override
    public Object execute(Context context) {
        return value;
    }
}