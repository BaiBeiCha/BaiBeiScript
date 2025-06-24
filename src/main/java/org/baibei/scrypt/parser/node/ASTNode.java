package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

public abstract class ASTNode {
    public abstract Object execute(Context context);
}
