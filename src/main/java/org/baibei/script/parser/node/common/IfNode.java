package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;
import org.baibei.script.parser.node.ASTNode;
import org.baibei.script.parser.node.expression.ExpressionNode;

public class IfNode extends ASTNode {

    private final ExpressionNode condition;
    private final ASTNode ifBlock;
    private final ASTNode elseBlock;

    public IfNode(ExpressionNode condition, ASTNode ifBlock, ASTNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public Object execute(Context context) throws ScriptException {
        Object condResult = condition.execute(context);
        if (condResult instanceof Boolean && (Boolean) condResult) {
            return ifBlock.execute(context);
        } else if (elseBlock != null) {
            return elseBlock.execute(context);
        }
        return null;
    }
}
