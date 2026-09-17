package com.trianglefx.parser;

import com.trianglefx.model.Line;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated tests verifying that the {@link EquationParser} can correctly read
 * various human equation writing styles.
 */
public class EquationParserTest {

    /** Allowed margin of numerical rounding difference (0.000001). */
    private static final double EPSILON = 1e-6;

    /**
     * Default constructor for test runner.
     */
    public EquationParserTest() {
        // Default constructor
    }

    /**
     * Tests reading standard equations in {@code Ax + By = C} format.
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testStandardEquations() throws ParseException {
        // x + y = 8  =>  A=1, B=1, C=8
        Line l1 = EquationParser.parse("x + y = 8");
        assertEquals(1.0, l1.getA(), EPSILON);
        assertEquals(1.0, l1.getB(), EPSILON);
        assertEquals(8.0, l1.getC(), EPSILON);

        // x - y = 2  =>  A=1, B=-1, C=2
        Line l2 = EquationParser.parse("x - y = 2");
        assertEquals(1.0, l2.getA(), EPSILON);
        assertEquals(-1.0, l2.getB(), EPSILON);
        assertEquals(2.0, l2.getC(), EPSILON);

        // 2x - 3y = 10  =>  A=2, B=-3, C=10
        Line l3 = EquationParser.parse("2x - 3y = 10");
        assertEquals(2.0, l3.getA(), EPSILON);
        assertEquals(-3.0, l3.getB(), EPSILON);
        assertEquals(10.0, l3.getC(), EPSILON);
    }

    /**
     * Tests reading pure vertical ({@code x = c}) and pure horizontal ({@code y = c}) lines.
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testSingleVariableEquations() throws ParseException {
        // Vertical line x = 1  =>  A=1, B=0, C=1
        Line l1 = EquationParser.parse("x = 1");
        assertEquals(1.0, l1.getA(), EPSILON);
        assertEquals(0.0, l1.getB(), EPSILON);
        assertEquals(1.0, l1.getC(), EPSILON);

        // Vertical line x = 4  =>  A=1, B=0, C=4
        Line l2 = EquationParser.parse("x = 4");
        assertEquals(1.0, l2.getA(), EPSILON);
        assertEquals(0.0, l2.getB(), EPSILON);
        assertEquals(4.0, l2.getC(), EPSILON);

        // Horizontal line y = -3  =>  A=0, B=1, C=-3
        Line l3 = EquationParser.parse("y = -3");
        assertEquals(0.0, l3.getA(), EPSILON);
        assertEquals(1.0, l3.getB(), EPSILON);
        assertEquals(-3.0, l3.getC(), EPSILON);
    }

    /**
     * Tests reading slope-intercept form ({@code y = mx + b}).
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testSlopeInterceptForm() throws ParseException {
        // y = 2x + 1  =>  -2x + y = 1
        Line l1 = EquationParser.parse("y = 2x + 1");
        assertEquals(-2.0, l1.getA(), EPSILON);
        assertEquals(1.0, l1.getB(), EPSILON);
        assertEquals(1.0, l1.getC(), EPSILON);

        // y = -x + 5  =>  x + y = 5
        Line l2 = EquationParser.parse("y = -x + 5");
        assertEquals(1.0, l2.getA(), EPSILON);
        assertEquals(1.0, l2.getB(), EPSILON);
        assertEquals(5.0, l2.getC(), EPSILON);
    }

    /**
     * Tests reading decimal numbers and explicit multiplication signs ({@code *}).
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testDecimalsAndMultiplication() throws ParseException {
        // 2.5x - 1.5y = 3.0
        Line l1 = EquationParser.parse("2.5x - 1.5y = 3.0");
        assertEquals(2.5, l1.getA(), EPSILON);
        assertEquals(-1.5, l1.getB(), EPSILON);
        assertEquals(3.0, l1.getC(), EPSILON);

        // 3*x + 4*y = 12
        Line l2 = EquationParser.parse("3*x + 4*y = 12");
        assertEquals(3.0, l2.getA(), EPSILON);
        assertEquals(4.0, l2.getB(), EPSILON);
        assertEquals(12.0, l2.getC(), EPSILON);
    }

    /**
     * Tests reading equations with variables and constants mixed on both sides of '='.
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testTermsOnBothSides() throws ParseException {
        // 2x + 3 = y + 5  =>  2x - y = 2
        Line l = EquationParser.parse("2x + 3 = y + 5");
        assertEquals(2.0, l.getA(), EPSILON);
        assertEquals(-1.0, l.getB(), EPSILON);
        assertEquals(2.0, l.getC(), EPSILON);
    }

    /**
     * Tests that broken, invalid, or non-linear expressions are safely rejected with alarms.
     */
    @Test
    public void testInvalidEquations() {
        // Blank inputs
        assertThrows(ParseException.class, () -> EquationParser.parse(""));
        assertThrows(ParseException.class, () -> EquationParser.parse("   "));
        assertThrows(ParseException.class, () -> EquationParser.parse(null));

        // Missing or extra equals sign
        assertThrows(ParseException.class, () -> EquationParser.parse("x + y + 2"));
        assertThrows(ParseException.class, () -> EquationParser.parse("x = y = 2"));

        // No variable present (not a 2D line)
        assertThrows(ParseException.class, () -> EquationParser.parse("4 = 4"));
        assertThrows(ParseException.class, () -> EquationParser.parse("0x + 0y = 5"));

        // Invalid letters or curves
        assertThrows(ParseException.class, () -> EquationParser.parse("x + y + z = 1"));
        assertThrows(ParseException.class, () -> EquationParser.parse("x^2 + y = 3"));

        // Double operators typo
        assertThrows(ParseException.class, () -> EquationParser.parse("x ++ y = 3"));
    }

    /**
     * Tests reading equations where 'y' is written before 'x' (e.g. {@code y + 2x = 7}).
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testReversedVariableOrder() throws ParseException {
        Line l = EquationParser.parse("y + 2x = 7");
        assertEquals(2.0, l.getA(), EPSILON);
        assertEquals(1.0, l.getB(), EPSILON);
        assertEquals(7.0, l.getC(), EPSILON);
    }

    /**
     * Tests reading negative decimal numbers.
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testNegativeSignsAndDecimals() throws ParseException {
        Line l = EquationParser.parse("-0.5x - 2.5y = -10.0");
        assertEquals(-0.5, l.getA(), EPSILON);
        assertEquals(-2.5, l.getB(), EPSILON);
        assertEquals(-10.0, l.getC(), EPSILON);
    }

    /**
     * Tests reading equations where the target number is zero (passes through the origin).
     *
     * @throws ParseException if parsing unexpectedly fails
     */
    @Test
    public void testZeroRightHandSide() throws ParseException {
        Line l = EquationParser.parse("x - y = 0");
        assertEquals(1.0, l.getA(), EPSILON);
        assertEquals(-1.0, l.getB(), EPSILON);
        assertEquals(0.0, l.getC(), EPSILON);
    }
}
