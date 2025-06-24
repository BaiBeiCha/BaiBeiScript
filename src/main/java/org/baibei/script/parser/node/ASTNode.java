package org.baibei.script.parser.node;

import org.baibei.script.interpreter.Context;

public abstract class ASTNode {
    public abstract Object execute(Context context);
}
