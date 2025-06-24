package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

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
        context.enterScope();
        for (ASTNode stmt : statements) {
            if (stmt != null) {
                stmt.execute(context);
            }
        }
        context.exitScope();
        return null;
    }
}
