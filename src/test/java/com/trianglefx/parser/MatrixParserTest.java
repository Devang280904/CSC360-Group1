package com.trianglefx.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated tests verifying that {@link MatrixParser} correctly reads matrix inputs
 * and strictly enforces the fixed constraints:
 * - Matrix A is fixed at 3 rows by 2 columns (6 numbers).
 * - Vector x is fixed at 2 rows by 1 column ([x; y]).
 * - Vector B is fixed at 3 rows by 1 column (3 numbers).
 * - Total required numbers: exactly 9.
 */
public class MatrixParserTest {

    /** Allowed margin of numerical rounding difference (0.000001). */
    private static final double EPSILON = 1e-6;

    /**
     * Default constructor for test runner.
     */
    public MatrixParserTest() {
        // Default constructor
    }

    /**
     * Tests reading 9 numbers arranged in 3 rows (Row 1: a11, a12, b1; Row 2: a21, a22, b2; Row 3: a31, a32, b3).
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testParseStandardAugmented() throws ParseException {
        String input = """
                1  1  8
                1 -1  2
                1  0  1
                """;

        MatrixParser.ParsedMatrix pm = MatrixParser.parse(input);
        assertNotNull(pm);

        double[][] A = pm.getA();
        double[] b = pm.getB();

        // Row 1: 1x + 1y = 8
        assertEquals(1.0, A[0][0], EPSILON);
        assertEquals(1.0, A[0][1], EPSILON);
        assertEquals(8.0, b[0], EPSILON);

        // Row 2: 1x - 1y = 2
        assertEquals(1.0, A[1][0], EPSILON);
        assertEquals(-1.0, A[1][1], EPSILON);
        assertEquals(2.0, b[1], EPSILON);

        // Row 3: 1x + 0y = 1
        assertEquals(1.0, A[2][0], EPSILON);
        assertEquals(0.0, A[2][1], EPSILON);
        assertEquals(1.0, b[2], EPSILON);
    }

    /**
     * Tests reading matrix brackets format: [[1, 1], [1, -1], [1, 0]], [8, 2, 1].
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testParseSeparatedBrackets() throws ParseException {
        String input = "[[1, 1], [1, -1], [1, 0]], [8, 2, 1]";

        MatrixParser.ParsedMatrix pm = MatrixParser.parse(input);
        assertNotNull(pm);

        double[][] A = pm.getA();
        double[] b = pm.getB();

        assertEquals(1.0, A[0][0], EPSILON);
        assertEquals(1.0, A[0][1], EPSILON);
        assertEquals(8.0, b[0], EPSILON);

        assertEquals(1.0, A[1][0], EPSILON);
        assertEquals(-1.0, A[1][1], EPSILON);
        assertEquals(2.0, b[1], EPSILON);

        assertEquals(1.0, A[2][0], EPSILON);
        assertEquals(0.0, A[2][1], EPSILON);
        assertEquals(1.0, b[2], EPSILON);
    }

    /**
     * Tests that entering fewer than 9 numbers or more than 9 numbers triggers a clear error.
     */
    @Test
    public void testInvalidNumberCount() {
        // Only 8 numbers entered (missing one number)
        assertThrows(ParseException.class, () -> MatrixParser.parse("1 1 8 1 -1 2 1 0"));

        // 10 numbers entered (too many numbers)
        assertThrows(ParseException.class, () -> MatrixParser.parse("1 1 8 1 -1 2 1 0 1 5"));

        // Completely blank
        assertThrows(ParseException.class, () -> MatrixParser.parse(""));
        assertThrows(ParseException.class, () -> MatrixParser.parse(null));
    }
}
