package org.baibei.script.parser.node.common.loop;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;
import org.baibei.script.parser.node.expression.ExpressionNode;

public class WhileNode extends ASTNode {

    private final ExpressionNode condition;
    private final ASTNode body;

    public WhileNode(ExpressionNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object execute(Context context) {
        try {
            context.enterScope();
            while (true) {
                Object condResult = condition.execute(context);

                if (!(condResult instanceof Boolean)) {
                    throw new RuntimeException("While condition must be boolean");
                }
                if (!((Boolean) condResult)) break;

                try {
                    body.execute(context);
                } catch (ContinueException ignored) {
                    // Переходим к следующей итерации
                } catch (BreakException e) {
                    break;
                }
            }
            return null;
        } finally {
            context.exitScope();
        }
    }

    public static class BreakException extends RuntimeException {}
    public static class ContinueException extends RuntimeException {}
}
