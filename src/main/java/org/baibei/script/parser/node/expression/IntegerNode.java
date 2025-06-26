package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;

public class IntegerNode extends ExpressionNode {
    private final int value;

    public IntegerNode(int value) {
        this.value = value;
    }

    @Override
    public Object execute(Context context) {
        return value;
    }
}