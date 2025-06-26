package org.baibei.script.parser.node.common.loop;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;
import org.baibei.script.parser.node.expression.ExpressionNode;

public class ForNode extends ASTNode {

    private final ASTNode init;
    private final ExpressionNode condition;
    private final ExpressionNode increment;
    private final ASTNode body;

    public ForNode(ASTNode init, ExpressionNode condition, ExpressionNode increment, ASTNode body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public Object execute(Context context) {
        context.enterScope();
        try {
            if (init != null) {
                init.execute(context);
            }

            while (true) {
                if (condition != null) {
                    Object condValue = condition.execute(context);
                    if (!isTruthy(condValue)) break;
                }

                try {
                    body.execute(context);
                } catch (WhileNode.BreakException be) {
                    break;
                } catch (WhileNode.ContinueException ce) {
                    // Пропускаем остаток итерации
                }

                if (increment != null) {
                    increment.execute(context);
                }
            }
            return null;
        } finally {
            context.exitScope();
        }
    }

    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        return true;
    }
}
