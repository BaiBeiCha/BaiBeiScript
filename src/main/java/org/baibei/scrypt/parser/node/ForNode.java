package org.baibei.scrypt.parser.node;

import org.baibei.scrypt.interpreter.Context;

public class ForNode extends ASTNode {
    private final ASTNode init;
    private final ExpressionNode condition;
    private final ExpressionNode increment;
    private final ASTNode body;

    public ForNode(ASTNode init, ExpressionNode condition,
                   ExpressionNode increment, ASTNode body) {
        this.init = init;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public Object execute(Context context) {
        System.out.println("Entering for loop");
        context.enterScope();

        if (init != null) {
            System.out.println("Init: " + init);
            init.execute(context);
        }

        int iteration = 0;
        while (true) {
            if (condition != null) {
                Object condResult = condition.execute(context);
                if (!(condResult instanceof Boolean) || !(Boolean) condResult) {
                    System.out.println("Condition failed: " + condResult);
                    break;
                }
            }

            iteration++;
            System.out.println("Iteration " + iteration);
            body.execute(context);

            if (increment != null) {
                System.out.println("Increment: " + increment);
                increment.execute(context);
            }
        }

        context.exitScope();
        System.out.println("Exited for loop");
        return null;
    }
}