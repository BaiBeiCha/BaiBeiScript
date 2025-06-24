package org.baibei.script.parser.node.common;

import org.baibei.script.interpreter.Context;
import org.baibei.script.parser.node.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode {

    private final List<ASTNode> statements = new ArrayList<>();

    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }

    @Override
    public Object execute(Context context) {
        for (ASTNode stmt : statements) {
            if (stmt != null) {
                stmt.execute(context);
            } else {
                System.out.println("Null statement in program");
            }
        }
        return null;
    }
}