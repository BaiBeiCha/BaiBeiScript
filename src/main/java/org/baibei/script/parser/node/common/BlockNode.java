package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends ASTNode {

    private final List<ASTNode> statements = new ArrayList<>();

    public void addStatement(ASTNode statement) {
        if (statement != null) {
            statements.add(statement);
        }
    }

    @Override
    public Object execute(Context context) {
        Object result = null;
        for (ASTNode stmt : statements) {
            result = stmt.execute(context);
        }
        return result;
    }
}
