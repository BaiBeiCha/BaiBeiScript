package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;

public class EmptyNode extends ASTNode {
    @Override
    public Object execute(Context context) {
        return null;
    }
}
