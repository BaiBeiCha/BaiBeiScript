package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

public class ReturnNode extends ASTNode {

    private final ExpressionNode value;

    public ReturnNode(ExpressionNode value) {
        this.value = value;
    }

    @Override
    public Object execute(Context context) {
        Object result = value != null ? value.execute(context) : null;
        throw new ReturnException(result);
    }

    public static class ReturnException extends RuntimeException {

        final Object value;

        public ReturnException(Object value) {
            this.value = value;
        }
    }
}
