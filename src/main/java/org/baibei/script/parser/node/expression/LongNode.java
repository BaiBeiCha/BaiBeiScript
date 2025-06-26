package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;

public class LongNode extends ExpressionNode {
    private final long value;

    public LongNode(long value) {
        this.value = value;
    }

    @Override
    public Object execute(Context context) {
        return value;
    }
}