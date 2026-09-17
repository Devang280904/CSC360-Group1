package com.trianglefx.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An automatic text reader that extracts numbers from typed or pasted text and organizes them
 * into the fixed matrix equation:
 *
 *   [ A (3x2) ] · [ x (2x1) ] = [ B (3x1) ]
 *
 * STRICT DIMENSIONS ENFORCED (0 to 100 Guide):
 * - Matrix A must be 3 rows by 2 columns (6 numbers total): Each row contains the multiplier for {@code x} and {@code y}.
 * - Vector x is fixed at 2 rows by 1 column: Represents the coordinates {@code [x; y]}.
 * - Vector B must be 3 rows by 1 column (3 numbers total): Represents the 3 target constants on the right side of the equals sign.
 * - Total numbers required: Exactly 9 numbers (6 + 3 = 9). If someone enters 8 or 10 numbers, this reader will stop and ask for the exact 9 needed.
 */
public class MatrixParser {

    /**
     * A text pattern finder that recognizes numbers (including decimals like {@code 3.5}, negative numbers like {@code -2}, and integers).
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[-+]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][-+]?\\d+)?");

    /**
     * Private constructor since this is a utility reader class and does not need to be instantiated.
     */
    private MatrixParser() {
        // Utility class
    }

    /**
     * A tidy container box holding the extracted 3x2 matrix A and 3x1 vector B.
     */
    public static class ParsedMatrix {
        /** The 3-row by 2-column grid of line multipliers. */
        private final double[][] a;
        /** The 3-number column of target constants. */
        private final double[] b;

        /**
         * Packages the extracted matrix A and vector B together.
         *
         * @param a 3x2 matrix of line multipliers
         * @param b 3-element column of constants
         */
        public ParsedMatrix(double[][] a, double[] b) {
            this.a = a;
            this.b = b;
        }

        /**
         * Gets the 3x2 matrix of multipliers.
         *
         * @return 2D array of size 3x2
         */
        public double[][] getA() {
            return a;
        }

        /**
         * Gets the 3x1 column of constants.
         *
         * @return 1D array of length 3
         */
        public double[] getB() {
            return b;
        }
    }

    /**
     * Reads a chunk of text, extracts the numbers, and organizes them into matrix A and vector B.
     *
     * Supports multiple common text formats:
     * - Row-by-row numbers:
     *     1, 1, 8
     *     1, -1, 2
     *     1, 0, 1
     * - Brackets format:
     *     [[1, 1], [1, -1], [1, 0]], [8, 2, 1]
     * - Augmented bar format:
     *     [1 1 | 8; 1 -1 | 2; 1 0 | 1]
     *
     * @param input the text typed or pasted by the user
     * @return a {@link ParsedMatrix} holding the validated 3x2 A and 3x1 B
     * @throws ParseException if the text is empty or does not contain exactly 9 numbers
     */
    public static ParsedMatrix parse(String input) throws ParseException {
        if (input == null || input.trim().isEmpty()) {
            throw new ParseException("Matrix input is empty. Please enter your 9 matrix numbers.");
        }

        Matcher matcher = NUMBER_PATTERN.matcher(input);
        List<Double> numbers = new ArrayList<>();

        while (matcher.find()) {
            try {
                numbers.add(Double.parseDouble(matcher.group()));
            } catch (NumberFormatException e) {
                throw new ParseException("Could not understand '" + matcher.group() + "' as a valid number.", e);
            }
        }

        if (numbers.size() != 9) {
            throw new ParseException(String.format(
                    "A 3x2 matrix A (6 numbers) and 3x1 vector B (3 numbers) requires exactly 9 numbers. Found %d numbers.",
                    numbers.size()
            ));
        }

        double[][] a = new double[3][2];
        double[] b = new double[3];

        String lower = input.toLowerCase();
        if (lower.contains("b=") || lower.contains("b =") || input.contains("]],")) {
            // Format separates A (first 6 numbers) from B (last 3 numbers)
            a[0][0] = numbers.get(0);
            a[0][1] = numbers.get(1);
            a[1][0] = numbers.get(2);
            a[1][1] = numbers.get(3);
            a[2][0] = numbers.get(4);
            a[2][1] = numbers.get(5);

            b[0] = numbers.get(6);
            b[1] = numbers.get(7);
            b[2] = numbers.get(8);
        } else {
            // Row-major format: [A_row1, B_1], [A_row2, B_2], [A_row3, B_3]
            a[0][0] = numbers.get(0);
            a[0][1] = numbers.get(1);
            b[0] = numbers.get(2);

            a[1][0] = numbers.get(3);
            a[1][1] = numbers.get(4);
            b[1] = numbers.get(5);

            a[2][0] = numbers.get(6);
            a[2][1] = numbers.get(7);
            b[2] = numbers.get(8);
        }

        return new ParsedMatrix(a, b);
    }
}
