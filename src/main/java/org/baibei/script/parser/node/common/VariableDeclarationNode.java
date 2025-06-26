package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;
import org.baibei.script.parser.node.ASTNode;
import org.baibei.script.parser.node.expression.ExpressionNode;

public class VariableDeclarationNode extends ASTNode {

    private final String type;
    private final String name;
    private final ExpressionNode initializer;
    private final boolean isFinal;
    private final boolean isStatic;
    private final int dimensions;

    public VariableDeclarationNode(String type, String name, ExpressionNode initializer,
                                   boolean isFinal, boolean isStatic, int dimensions) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.isFinal = isFinal;
        this.isStatic = isStatic;
        this.dimensions = dimensions;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        Object value = initializer != null ?
                initializer.execute(context) :
                null;

        context.declareVariable(name, type, value, isFinal, isStatic, dimensions);
        return null;
    }
}