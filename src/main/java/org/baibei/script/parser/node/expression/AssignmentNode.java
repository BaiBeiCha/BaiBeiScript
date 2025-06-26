package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;

public class AssignmentNode extends ExpressionNode {

    private final String name;
    private final ExpressionNode value;

    public AssignmentNode(String name, ExpressionNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        Object val = value != null ? value.execute(context) : null;
        context.setVariable(name, val);
        return val;
    }
}
