package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;

public class StringNode extends ExpressionNode {

    private final String value;

    public StringNode(String value) {
        this.value = value;
    }

    @Override
    public Object execute(Context context) {
        return value;
    }
}

