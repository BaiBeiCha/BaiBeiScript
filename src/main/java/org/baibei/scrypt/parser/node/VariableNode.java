package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

public class VariableNode extends ExpressionNode {

    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public Object execute(Context context) {
        return context.getVariable(name);
    }

    public String getName() {
        return name;
    }
}
