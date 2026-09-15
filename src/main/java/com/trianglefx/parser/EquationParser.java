package com.trianglefx.parser;

import com.trianglefx.model.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses linear equation strings into Line objects.
 * Supports various linear forms such as:
 *   - "x + y = 8"
 *   - "2x - 3y = 10"
 *   - "y = 2x + 1"
 *   - "x = 4"
 *   - "-x + 3.5y = -7"
 *   - "2*x + 3*y = 12"
 */
public class EquationParser {
    private static final double EPSILON = 1e-12;
    private static final Pattern VALID_CHARS = Pattern.compile("^[0-9xyXY.+\\-*\\s=]+$");

    public static Line parse(String equation) throws ParseException {
        if (equation == null || equation.trim().isEmpty()) {
            throw new ParseException("Equation cannot be empty.");
        }

        String raw = equation.trim();

        if (!VALID_CHARS.matcher(raw).matches()) {
            throw new ParseException("Equation contains invalid characters: " + raw);
        }

        int firstEquals = raw.indexOf('=');
        if (firstEquals == -1) {
            throw new ParseException("Equation must contain an '=' sign: " + raw);
        }
        if (raw.indexOf('=', firstEquals + 1) != -1) {
            throw new ParseException("Equation cannot contain multiple '=' signs: " + raw);
        }

        String lhs = raw.substring(0, firstEquals).trim();
        String rhs = raw.substring(firstEquals + 1).trim();

        if (lhs.isEmpty() || rhs.isEmpty()) {
            throw new ParseException("Both sides of '=' must contain valid expressions: " + raw);
        }

        ParsedSide parsedLhs = parseSide(lhs, raw);
        ParsedSide parsedRhs = parseSide(rhs, raw);

        // Ax + By = C
        // LHS terms: +xCoeff on A, +yCoeff on B, -constVal on C
        // RHS terms: -xCoeff on A, -yCoeff on B, +constVal on C
        double totalA = parsedLhs.coeffX - parsedRhs.coeffX;
        double totalB = parsedLhs.coeffY - parsedRhs.coeffY;
        double totalC = parsedRhs.constant - parsedLhs.constant;

        if (Math.abs(totalA) < EPSILON && Math.abs(totalB) < EPSILON) {
            throw new ParseException("Equation does not define a line (no x or y variable present): " + raw);
        }

        // Clean up small floating precision artifacts
        if (Math.abs(totalA) < EPSILON) totalA = 0.0;
        if (Math.abs(totalB) < EPSILON) totalB = 0.0;
        if (Math.abs(totalC) < EPSILON) totalC = 0.0;

        return new Line(totalA, totalB, totalC, raw);
    }

    private static ParsedSide parseSide(String expr, String fullEquation) throws ParseException {
        // Remove spaces
        String clean = expr.replaceAll("\\s+", "");
        if (clean.isEmpty()) {
            throw new ParseException("Expression side is empty in: " + fullEquation);
        }

        // Check for illegal patterns
        if (clean.contains("++") || clean.contains("--") || clean.contains("+-") || clean.contains("-+")
                || clean.contains("**") || clean.contains("..")) {
            throw new ParseException("Malformed operators in equation: " + fullEquation);
        }

        List<String> tokens = splitIntoTerms(clean);
        double coeffX = 0.0;
        double coeffY = 0.0;
        double constant = 0.0;

        for (String token : tokens) {
            String lower = token.toLowerCase();

            boolean hasX = lower.contains("x");
            boolean hasY = lower.contains("y");

            if (hasX && hasY) {
                throw new ParseException("Non-linear term with both x and y detected ('" + token + "'): " + fullEquation);
            }

            if (hasX) {
                coeffX += parseVariableCoeff(token, 'x', fullEquation);
            } else if (hasY) {
                coeffY += parseVariableCoeff(token, 'y', fullEquation);
            } else {
                // Constant term
                try {
                    constant += Double.parseDouble(token);
                } catch (NumberFormatException e) {
                    throw new ParseException("Invalid number format in term '" + token + "': " + fullEquation, e);
                }
            }
        }

        return new ParsedSide(coeffX, coeffY, constant);
    }

    private static double parseVariableCoeff(String token, char varChar, String fullEquation) throws ParseException {
        // Remove varChar (case-insensitive) and any '*'
        String stripped = token.replaceAll("(?i)" + varChar, "").replace("*", "");

        if (stripped.isEmpty() || stripped.equals("+")) {
            return 1.0;
        }
        if (stripped.equals("-")) {
            return -1.0;
        }

        try {
            return Double.parseDouble(stripped);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid coefficient in term '" + token + "': " + fullEquation, e);
        }
    }

    private static List<String> splitIntoTerms(String s) throws ParseException {
        List<String> terms = new ArrayList<>();
        int start = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if ((c == '+' || c == '-') && i > 0) {
                String term = s.substring(start, i);
                if (!term.isEmpty()) {
                    terms.add(term);
                }
                start = i;
            }
        }
        if (start < n) {
            String term = s.substring(start);
            if (!term.isEmpty()) {
                terms.add(term);
            }
        }

        if (terms.isEmpty()) {
            throw new ParseException("No valid terms found in: " + s);
        }

        return terms;
    }

    private static class ParsedSide {
        final double coeffX;
        final double coeffY;
        final double constant;

        ParsedSide(double coeffX, double coeffY, double constant) {
            this.coeffX = coeffX;
            this.coeffY = coeffY;
            this.constant = constant;
        }
    }
}
