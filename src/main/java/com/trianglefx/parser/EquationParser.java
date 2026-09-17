package com.trianglefx.parser;

import com.trianglefx.model.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An automatic algebra sentence reader that translates text equations into a straight {@link Line}.
 * <p>
 * Humans write linear equations in many different styles. This parser understands formats such as:
 * </p>
 * <ul>
 *   <li><b>Standard form:</b> {@code "x + y = 8"}, {@code "2x - 3y = 10"}</li>
 *   <li><b>Slope-intercept form:</b> {@code "y = 2x + 1"}, {@code "y = -x + 5"}</li>
 *   <li><b>Single variable lines:</b> {@code "x = 4"} (vertical line), {@code "y = -3"} (horizontal line)</li>
 *   <li><b>Decimals and negatives:</b> {@code "-0.5x + 3.5y = -7.25"}</li>
 *   <li><b>Explicit multiplication stars:</b> {@code "2*x + 3*y = 12"}</li>
 *   <li><b>Terms scattered on both sides:</b> {@code "2x + 3 = y + 5"} (automatically rearranges to {@code 2x - y = 2})</li>
 * </ul>
 */
public class EquationParser {

    /**
     * Microscopic threshold (0.000000000001) to recognize when a number is zero.
     */
    private static final double EPSILON = 1e-12;

    /**
     * The approved list of allowed characters: numbers, x, y, decimal points, plus, minus, star, space, and equals.
     */
    private static final Pattern VALID_CHARS = Pattern.compile("^[0-9xyXY.+\\-*\\s=]+$");

    /**
     * Private constructor to prevent instantiating utility class.
     */
    private EquationParser() {
        // Utility class
    }

    /**
     * Takes an equation string typed by a user and converts it into a standard {@link Line}.
     *
     * @param equation the equation text (e.g. {@code "2x + 3y = 6"})
     * @return a clean {@link Line} object containing the numbers A, B, and C
     * @throws ParseException if the equation is blank, has forbidden characters, is missing
     *                        an equals sign, has multiple equals signs, or has no x or y variables
     */
    public static Line parse(String equation) throws ParseException {
        if (equation == null || equation.trim().isEmpty()) {
            throw new ParseException("Equation cannot be blank. Please enter an equation.");
        }

        String raw = equation.trim();

        if (!VALID_CHARS.matcher(raw).matches()) {
            throw new ParseException("Equation contains invalid characters: '" + raw + "'. Only numbers, x, y, +, -, *, and = are allowed.");
        }

        int firstEquals = raw.indexOf('=');
        if (firstEquals == -1) {
            throw new ParseException("Equation must contain an '=' sign: '" + raw + "'");
        }
        if (raw.indexOf('=', firstEquals + 1) != -1) {
            throw new ParseException("Equation cannot contain more than one '=' sign: '" + raw + "'");
        }

        String lhs = raw.substring(0, firstEquals).trim();
        String rhs = raw.substring(firstEquals + 1).trim();

        if (lhs.isEmpty() || rhs.isEmpty()) {
            throw new ParseException("Both the left and right sides of '=' must have numbers or variables: '" + raw + "'");
        }

        ParsedSide parsedLhs = parseSide(lhs, raw);
        ParsedSide parsedRhs = parseSide(rhs, raw);

        // Move all x and y to the left side, and all constants to the right side:
        // A = (left x - right x), B = (left y - right y), C = (right constant - left constant)
        double totalA = parsedLhs.coeffX - parsedRhs.coeffX;
        double totalB = parsedLhs.coeffY - parsedRhs.coeffY;
        double totalC = parsedRhs.constant - parsedLhs.constant;

        if (Math.abs(totalA) < EPSILON && Math.abs(totalB) < EPSILON) {
            throw new ParseException("Equation has no x or y variables (e.g. '4 = 4' is not a 2D line): '" + raw + "'");
        }

        // Clean up microscopic rounding artifacts
        if (Math.abs(totalA) < EPSILON) totalA = 0.0;
        if (Math.abs(totalB) < EPSILON) totalB = 0.0;
        if (Math.abs(totalC) < EPSILON) totalC = 0.0;

        return new Line(totalA, totalB, totalC, raw);
    }

    /**
     * Reads one side of an equation (left of '=' or right of '=') and tallies up all x, y, and constant numbers.
     *
     * @param expr         the text on one side of the equals sign
     * @param fullEquation the full equation text for context in error messages
     * @return a {@link ParsedSide} containing the total multipliers for x, y, and constant
     * @throws ParseException if operators are broken (like '++' or '**') or terms are invalid
     */
    private static ParsedSide parseSide(String expr, String fullEquation) throws ParseException {
        // Strip out all spaces
        String clean = expr.replaceAll("\\s+", "");
        if (clean.isEmpty()) {
            throw new ParseException("One side of the equals sign is empty in: '" + fullEquation + "'");
        }

        // Check for double operator typos
        if (clean.contains("++") || clean.contains("--") || clean.contains("+-") || clean.contains("-+")
                || clean.contains("**") || clean.contains("..")) {
            throw new ParseException("Typo with repeated operators in: '" + fullEquation + "'");
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
                throw new ParseException("Non-linear term with both x and y multiplied together ('" + token + "'): '" + fullEquation + "'");
            }

            if (hasX) {
                coeffX += parseVariableCoeff(token, 'x', fullEquation);
            } else if (hasY) {
                coeffY += parseVariableCoeff(token, 'y', fullEquation);
            } else {
                // Standalone number
                try {
                    constant += Double.parseDouble(token);
                } catch (NumberFormatException e) {
                    throw new ParseException("Could not read number in term '" + token + "': '" + fullEquation + "'", e);
                }
            }
        }

        return new ParsedSide(coeffX, coeffY, constant);
    }

    /**
     * Extracts the number attached to a variable letter (e.g. from {@code "3x"} it gets 3.0; from {@code "-y"} it gets -1.0).
     *
     * @param token        the individual chunk (e.g. {@code "+2.5x"}, {@code "-y"})
     * @param varChar      the letter to look for ('x' or 'y')
     * @param fullEquation the full equation for error message context
     * @return the numerical multiplier for that variable
     * @throws ParseException if the number cannot be read
     */
    private static double parseVariableCoeff(String token, char varChar, String fullEquation) throws ParseException {
        // Remove the variable letter and any multiplication asterisk
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
            throw new ParseException("Invalid number in term '" + token + "' in: '" + fullEquation + "'", e);
        }
    }

    /**
     * Cuts a long algebraic expression into individual chunks at each '+' or '-' sign.
     *
     * @param s clean equation text with no spaces
     * @return list of separate terms
     * @throws ParseException if no valid terms were found
     */
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
            throw new ParseException("No valid terms found in: '" + s + "'");
        }

        return terms;
    }

    /**
     * A temporary holding bin storing the total x-count, y-count, and constant count for one side of an equation.
     */
    private static class ParsedSide {
        /** Total multiplier for x on this side. */
        final double coeffX;
        /** Total multiplier for y on this side. */
        final double coeffY;
        /** Total constant number on this side. */
        final double constant;

        /**
         * Creates the holding bin for a parsed side.
         *
         * @param coeffX   sum of x multipliers
         * @param coeffY   sum of y multipliers
         * @param constant sum of constants
         */
        ParsedSide(double coeffX, double coeffY, double constant) {
            this.coeffX = coeffX;
            this.coeffY = coeffY;
            this.constant = constant;
        }
    }
}
