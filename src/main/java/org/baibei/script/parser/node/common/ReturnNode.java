package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;
import org.baibei.script.parser.node.ASTNode;
import org.baibei.script.parser.node.expression.ExpressionNode;

public class ReturnNode extends ASTNode {

    private final ExpressionNode value;

    public ReturnNode(ExpressionNode value) {
        this.value = value;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        Object result = value != null ? value.execute(context) : null;
        throw new ReturnException(result);
    }

    public static class ReturnException extends RuntimeException {
        public final Object value;
        public ReturnException(Object value) {
            this.value = value;
        }
    }
}
