package org.baibei.script.parser.node.common.loop;

import org.baibei.script.parser.node.common.loop.WhileNode;
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
        if (init != null) {
            init.execute(context);
        }
        Object result = null;
        while (true) {
            if (condition != null) {
                Object condValue = condition.execute(context);
                if (!isTruthy(condValue)) {
                    break;
                }
            }
            try {
                result = body.execute(context);
            } catch (WhileNode.BreakException be) {
                // break out of loop
                break;
            } catch (WhileNode.ContinueException ce) {
                // skip to increment
            }
            if (increment != null) {
                increment.execute(context);
            }
        }
        return result;
    }

    private boolean isTruthy(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        if (value instanceof String) return !((String) value).isEmpty();
        return true;
    }
}
