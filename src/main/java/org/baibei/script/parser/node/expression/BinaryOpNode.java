package org.baibei.script.parser.node.expression;

import org.baibei.script.lexer.TokenType;
import org.baibei.script.interpreter.Context;

public class BinaryOpNode extends ExpressionNode {

    private final TokenType operator;
    private final ExpressionNode left;
    private final ExpressionNode right;

    public BinaryOpNode(TokenType operator, ExpressionNode left, ExpressionNode right) {
        this.operator = operator;
        this.left     = left;
        this.right    = right;
    }

    @Override
    public Object execute(Context context) {
        Object l = left != null ? left.execute(context) : null;
        Object r = right.execute(context);

        if (operator == TokenType.AND || operator == TokenType.OR) {
            boolean lb = isTruthy(l);
            boolean rb = isTruthy(r);
            return operator == TokenType.AND ? (lb && rb) : (lb || rb);
        }

        if (operator == TokenType.ADD && (l instanceof String || r instanceof String)) {
            return String.valueOf(l) + String.valueOf(r);
        }

        if (l instanceof Number ln && r instanceof Number rn) {
            double ld = ln.doubleValue();
            double rd = rn.doubleValue();
            switch (operator) {
                case ADD:
                    return ld + rd;
                case SUB:
                    return ld - rd;
                case MUL:
                    return ld * rd;
                case DIV:
                    if (rd == 0) throw new RuntimeException("Math error: division by zero");
                    return ld / rd;
                case MOD:
                    return ld % rd;
                case POW:
                    return Math.pow(ld, rd);
                case LT:
                    return ld < rd;
                case LTE:
                    return ld <= rd;
                case GT:
                    return ld > rd;
                case GTE:
                    return ld >= rd;
                case EQ:
                    return ld == rd;
                case NEQ:
                    return ld != rd;
                default:
                    break;
            }
        }

        // Equality for any type
        if (operator == TokenType.EQ) {
            return (l == null && r == null) || (l != null && l.equals(r));
        }
        if (operator == TokenType.NEQ) {
            return (l == null && r != null) || (l != null && !l.equals(r));
        }

        throw new RuntimeException("Unsupported operator or operand types: " + operator);
    }

    private boolean isTruthy(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Boolean b) return b;
        if (obj instanceof Number n) return n.doubleValue() != 0;
        if (obj instanceof String s) return !s.isEmpty();
        return true;
    }
}
