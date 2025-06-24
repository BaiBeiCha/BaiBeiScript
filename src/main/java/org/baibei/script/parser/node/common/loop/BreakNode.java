package org.baibei.script.parser.node.common.loop;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;

public class BreakNode extends ASTNode {

    @Override
    public Object execute(Context context) {
        throw new WhileNode.BreakException();
    }
}
