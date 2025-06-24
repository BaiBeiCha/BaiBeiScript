package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

public class WhileNode extends ASTNode {

    private final ExpressionNode condition;
    private final ASTNode body;

    public WhileNode(ExpressionNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object execute(Context context) {
        while (true) {
            Object condResult = condition.execute(context);
            if (!(condResult instanceof Boolean) || !(Boolean) condResult) {
                break;
            }
            body.execute(context);
        }
        return null;
    }
}
