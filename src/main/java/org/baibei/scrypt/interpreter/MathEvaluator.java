package org.baibei.scrypt.interpreter;

public class MathEvaluator {
    private String expr;
    private int length;
    private int pos;

    public MathEvaluator() {
        // Default constructor
    }

    /**
     * Evaluates the arithmetic expression or returns string literal as-is.
     * Usage: new MathEvaluator().evaluate("1+2*(3-4)**2"); // returns Double
     *        new MathEvaluator().evaluate("\"hello\"");     // returns String
     */
    public Object evaluate(String expression) {
        // Trim whitespace
        String trimmed = expression.trim();
        // Handle quoted string literal
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) ||
                (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            // Unescape simple quotes
            return trimmed.substring(1, trimmed.length() - 1);
        }
        // Otherwise treat as numeric expression
        this.expr = trimmed.replaceAll("\\s+", "");
        this.length = expr.length();
        this.pos = 0;

        double result = parseExpression();
        if (pos < length) {
            throw new IllegalArgumentException(
                    "Unexpected character at position " + pos + ": '" + expr.charAt(pos) + "'");
        }
        return result;
    }

    // Grammar:
    // expression = term { ('+' | '-') term }
    // term       = factor { ('*' | '/' | '%') factor }
    // factor     = primary { '**' factor }
    // primary    = number | '(' expression ')' | '-' primary

    private double parseExpression() {
        double value = parseTerm();
        while (pos < length) {
            char op = expr.charAt(pos);
            if (op == '+' || op == '-') {
                pos++;
                double right = parseTerm();
                value = (op == '+') ? value + right : value - right;
            } else break;
        }
        return value;
    }

    private double parseTerm() {
        double value = parseFactor();
        while (pos < length) {
            char op = expr.charAt(pos);
            if (op == '*' || op == '/' || op == '%') {
                pos++;
                double right = parseFactor();
                switch (op) {
                    case '*': value *= right; break;
                    case '/':
                        if (right == 0) throw new ArithmeticException("Division by zero");
                        value /= right;
                        break;
                    case '%': value %= right; break;
                }
            } else break;
        }
        return value;
    }

    private double parseFactor() {
        double value = parsePrimary();
        // exponentiation '**' is right-associative
        while (pos + 1 < length && expr.charAt(pos) == '*' && expr.charAt(pos + 1) == '*') {
            pos += 2;
            double exponent = parseFactor();
            value = Math.pow(value, exponent);
        }
        return value;
    }

    private double parsePrimary() {
        if (pos < length) {
            char ch = expr.charAt(pos);
            if (ch == '(') {
                pos++;
                double value = parseExpression();
                if (pos >= length || expr.charAt(pos) != ')') {
                    throw new IllegalArgumentException("Missing closing parenthesis at position " + pos);
                }
                pos++;
                return value;
            }
            if (ch == '-') {
                pos++;
                return -parsePrimary();
            }
            return parseNumber();
        }
        throw new IllegalArgumentException("Unexpected end of expression at position " + pos);
    }

    private double parseNumber() {
        int start = pos;
        while (pos < length) {
            char ch = expr.charAt(pos);
            if ((ch >= '0' && ch <= '9') || ch == '.') {
                pos++;
            } else break;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Expected number at position " + pos);
        }
        return Double.parseDouble(expr.substring(start, pos));
    }
}
