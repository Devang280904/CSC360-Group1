package com.trianglefx.parser;

import com.trianglefx.model.Line;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EquationParserTest {

    private static final double EPSILON = 1e-6;

    @Test
    public void testStandardEquations() throws ParseException {
        // x + y = 8
        Line l1 = EquationParser.parse("x + y = 8");
        assertEquals(1.0, l1.getA(), EPSILON);
        assertEquals(1.0, l1.getB(), EPSILON);
        assertEquals(8.0, l1.getC(), EPSILON);

        // x - y = 2
        Line l2 = EquationParser.parse("x - y = 2");
        assertEquals(1.0, l2.getA(), EPSILON);
        assertEquals(-1.0, l2.getB(), EPSILON);
        assertEquals(2.0, l2.getC(), EPSILON);

        // 2x - 3y = 10
        Line l3 = EquationParser.parse("2x - 3y = 10");
        assertEquals(2.0, l3.getA(), EPSILON);
        assertEquals(-3.0, l3.getB(), EPSILON);
        assertEquals(10.0, l3.getC(), EPSILON);
    }

    @Test
    public void testSingleVariableEquations() throws ParseException {
        // x = 1
        Line l1 = EquationParser.parse("x = 1");
        assertEquals(1.0, l1.getA(), EPSILON);
        assertEquals(0.0, l1.getB(), EPSILON);
        assertEquals(1.0, l1.getC(), EPSILON);

        // x = 4
        Line l2 = EquationParser.parse("x = 4");
        assertEquals(1.0, l2.getA(), EPSILON);
        assertEquals(0.0, l2.getB(), EPSILON);
        assertEquals(4.0, l2.getC(), EPSILON);

        // y = -3
        Line l3 = EquationParser.parse("y = -3");
        assertEquals(0.0, l3.getA(), EPSILON);
        assertEquals(1.0, l3.getB(), EPSILON);
        assertEquals(-3.0, l3.getC(), EPSILON);
    }

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

    @Test
    public void testTermsOnBothSides() throws ParseException {
        // 2x + 3 = y + 5  =>  2x - y = 2
        Line l = EquationParser.parse("2x + 3 = y + 5");
        assertEquals(2.0, l.getA(), EPSILON);
        assertEquals(-1.0, l.getB(), EPSILON);
        assertEquals(2.0, l.getC(), EPSILON);
    }

    @Test
    public void testInvalidEquations() {
        // Empty or whitespace
        assertThrows(ParseException.class, () -> EquationParser.parse(""));
        assertThrows(ParseException.class, () -> EquationParser.parse("   "));
        assertThrows(ParseException.class, () -> EquationParser.parse(null));

        // Missing '='
        assertThrows(ParseException.class, () -> EquationParser.parse("x + y + 2"));

        // Multiple '='
        assertThrows(ParseException.class, () -> EquationParser.parse("x = y = 2"));

        // Missing variable
        assertThrows(ParseException.class, () -> EquationParser.parse("4 = 4"));
        assertThrows(ParseException.class, () -> EquationParser.parse("0x + 0y = 5"));

        // Invalid characters
        assertThrows(ParseException.class, () -> EquationParser.parse("x + y + z = 1"));
        assertThrows(ParseException.class, () -> EquationParser.parse("x^2 + y = 3"));

        // Malformed operators
        assertThrows(ParseException.class, () -> EquationParser.parse("x ++ y = 3"));
    }

    @Test
    public void testReversedVariableOrder() throws ParseException {
        // y + 2x = 7 => 2x + y = 7
        Line l = EquationParser.parse("y + 2x = 7");
        assertEquals(2.0, l.getA(), EPSILON);
        assertEquals(1.0, l.getB(), EPSILON);
        assertEquals(7.0, l.getC(), EPSILON);
    }

    @Test
    public void testNegativeSignsAndDecimals() throws ParseException {
        // -0.5x - 2.5y = -10.0
        Line l = EquationParser.parse("-0.5x - 2.5y = -10.0");
        assertEquals(-0.5, l.getA(), EPSILON);
        assertEquals(-2.5, l.getB(), EPSILON);
        assertEquals(-10.0, l.getC(), EPSILON);
    }

    @Test
    public void testZeroRightHandSide() throws ParseException {
        // x - y = 0
        Line l = EquationParser.parse("x - y = 0");
        assertEquals(1.0, l.getA(), EPSILON);
        assertEquals(-1.0, l.getB(), EPSILON);
        assertEquals(0.0, l.getC(), EPSILON);
    }
}
