package org.baibei.script.parser.node;

import org.baibei.script.interpreter.Context;
import org.baibei.script.interpreter.ScriptException;

public abstract class ASTNode {
    public abstract Object execute(Context context) throws ScriptException;
}
