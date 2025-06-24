package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;

import java.util.List;

public class FunctionNode extends ASTNode {

    private final String name;
    private final List<String> parameters;
    private final ASTNode body;

    public FunctionNode(String name, List<String> parameters, ASTNode body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public Object execute(Context context) {
        context.addFunction(name, (args) -> {
            context.enterScope();
            for (int i = 0; i < parameters.size(); i++) {
                context.setVariable(parameters.get(i), args[i]);
            }
            Object result;
            try {
                result = body.execute(context);
            } catch (ReturnNode.ReturnException ret) {
                result = ret.value;
            }
            context.exitScope();
            return result;
        });
        return null;
    }
}
