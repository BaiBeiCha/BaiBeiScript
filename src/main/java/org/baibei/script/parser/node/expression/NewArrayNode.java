package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;

import java.lang.reflect.Array;

public class NewArrayNode extends ExpressionNode {

    private final String elementType;
    private final ExpressionNode sizeExpr;

    public NewArrayNode(String elementType, ExpressionNode sizeExpr) {
        this.elementType = elementType;
        this.sizeExpr = sizeExpr;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        Object sizeObj = sizeExpr.execute(context);
        if (!(sizeObj instanceof Number)) {
            throw new ScriptException("Array size must be a number");
        }

        int size = ((Number) sizeObj).intValue();
        if (size < 0) {
            throw new ScriptException("Array size cannot be negative");
        }

        return createArray(elementType, size);
    }

    private Object createArray(String type, int size) {
        return switch (type) {
            case "int" -> new int[size];
            case "long" -> new long[size];
            case "double" -> new double[size];
            case "string" -> new String[size];
            default -> Array.newInstance(Object.class, size);
        };
    }
}