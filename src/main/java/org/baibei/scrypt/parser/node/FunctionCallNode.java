package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

import java.util.List;

public class FunctionCallNode extends ExpressionNode {

    private final String name;
    private final List<ExpressionNode> arguments;

    public FunctionCallNode(String name, List<ExpressionNode> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Object execute(Context context) {
        Object[] args = arguments.stream()
                .map(arg -> arg.execute(context))
                .toArray();

        return context.call(name, args);
    }
}
