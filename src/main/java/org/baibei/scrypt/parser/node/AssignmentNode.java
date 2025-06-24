package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

public class AssignmentNode extends ExpressionNode {

    private final String name;
    private final ExpressionNode value;

    public AssignmentNode(String name, ExpressionNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Object execute(Context context) {
        Object val = value != null ? value.execute(context) : null;
        context.setVariable(name, val);
        return val;
    }
}
