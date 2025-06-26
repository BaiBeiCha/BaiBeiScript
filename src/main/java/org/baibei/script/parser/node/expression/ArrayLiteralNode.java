package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;

import java.util.List;

public class ArrayLiteralNode extends ExpressionNode {

    private final List<ExpressionNode> elements;

    public ArrayLiteralNode(List<ExpressionNode> elements) {
        this.elements = elements;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        String elementType = "object";
        if (!elements.isEmpty()) {
            Object firstElement = elements.get(0).execute(context);
            if (firstElement != null) {
                elementType = context.inferType(firstElement);
            }
        }

        return createArray(context, elementType, elements);
    }

    private Object createArray(Context context, String elementType, List<ExpressionNode> elements)
            throws ScriptException {

        Object[] array = new Object[elements.size()];

        for (int i = 0; i < elements.size(); i++) {
            Object value = elements.get(i).execute(context);

            if (value instanceof ArrayLiteralNode) {
                value = ((ArrayLiteralNode) value).execute(context);
            }

            value = convertValue(elementType, value);
            array[i] = value;
        }

        return array;
    }

    private Object convertValue(String type, Object value) {
        if (value == null) return null;

        switch (type) {
            case "int":
                if (value instanceof Number) return ((Number) value).intValue();
                break;
            case "long":
                if (value instanceof Number) return ((Number) value).longValue();
                break;
            case "double":
                if (value instanceof Number) return ((Number) value).doubleValue();
                break;
            case "string":
                return value.toString();
        }

        return value;
    }
}