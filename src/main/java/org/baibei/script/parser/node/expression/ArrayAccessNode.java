package org.baibei.script.parser.node.expression;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;

public class ArrayAccessNode extends ExpressionNode {

    private final ExpressionNode arrayExpr;
    private final ExpressionNode indexExpr;

    public ArrayAccessNode(ExpressionNode arrayExpr, ExpressionNode indexExpr) {
        this.arrayExpr = arrayExpr;
        this.indexExpr = indexExpr;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        Object array = arrayExpr.execute(context);
        Object index = indexExpr.execute(context);
        return java.lang.reflect.Array.get(array, ((Number) index).intValue());
    }
}
