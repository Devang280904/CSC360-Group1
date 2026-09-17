package com.trianglefx.geometry;

import com.trianglefx.model.Point;
import com.trianglefx.model.Triangle;
import com.trianglefx.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated safety checks and tests for the {@link GeometryService}.
 * <p>
 * These tests ensure that the mathematical formulas work reliably:
 * </p>
 * <ul>
 *   <li>Verifies correct corner finding (intersections) and area calculation.</li>
 *   <li>Verifies the 3&times;2 matrix $A$ and 3&times;1 vector $B$ system.</li>
 *   <li>Verifies that parallel lines, identical lines, and concurrent lines are correctly detected and rejected with safety alarms.</li>
 * </ul>
 */
public class GeometryServiceTest {

    /** Allowed margin of numerical rounding difference (0.0001). */
    private static final double EPSILON = 1e-4;

    /** The geometry calculator instance under test. */
    private final GeometryService service = new GeometryService();

    /**
     * Default constructor for the automated test runner.
     */
    public GeometryServiceTest() {
        // Test suite constructor
    }

    /**
     * Tests the classic textbook example:
     * Line 1: {@code x + y = 8}
     * Line 2: {@code x - y = 2}
     * Line 3: {@code x = 1}
     * <p>
     * Expected corners: (5, 3), (1, -1), and (1, 7). Expected area = 16.00.
     * </p>
     *
     * @throws ParseException    if equations cannot be parsed
     * @throws GeometryException if geometry calculations fail
     */
    @Test
    public void testProblemStatementExample() throws ParseException, GeometryException {
        Triangle triangle = service.buildTriangle("x + y = 8", "x - y = 2", "x = 1");

        assertNotNull(triangle);
        assertFalse(triangle.isDegenerate());

        Point p1 = triangle.getP1(); // Line 1 & Line 2 meet at (5, 3)
        Point p2 = triangle.getP2(); // Line 2 & Line 3 meet at (1, -1)
        Point p3 = triangle.getP3(); // Line 3 & Line 1 meet at (1, 7)

        assertEquals(5.0, p1.getX(), EPSILON);
        assertEquals(3.0, p1.getY(), EPSILON);

        assertEquals(1.0, p2.getX(), EPSILON);
        assertEquals(-1.0, p2.getY(), EPSILON);

        assertEquals(1.0, p3.getX(), EPSILON);
        assertEquals(7.0, p3.getY(), EPSILON);

        // Base is 8 units along x=1, height is 4 units (from x=1 to x=5). Area = 0.5 * 8 * 4 = 16.
        assertEquals(16.0, triangle.getArea(), EPSILON);
    }

    /**
     * Tests entering numbers directly via a 3&times;2 matrix A and 3&times;1 vector b.
     *
     * @throws GeometryException if matrix geometry calculations fail
     */
    @Test
    public void testMatrixRepresentation() throws GeometryException {
        // Row 1:  1x +  1y = 8
        // Row 2:  1x -  1y = 2
        // Row 3:  1x +  0y = 1
        double[][] A = {
                {1.0,  1.0},
                {1.0, -1.0},
                {1.0,  0.0}
        };
        double[] b = {8.0, 2.0, 1.0};

        Triangle triangle = service.buildTriangle(A, b);

        assertNotNull(triangle);
        assertFalse(triangle.isDegenerate());
        assertEquals(16.0, triangle.getArea(), EPSILON);

        Point p1 = triangle.getP1();
        Point p2 = triangle.getP2();
        Point p3 = triangle.getP3();

        assertEquals(5.0, p1.getX(), EPSILON);
        assertEquals(3.0, p1.getY(), EPSILON);
        assertEquals(1.0, p2.getX(), EPSILON);
        assertEquals(-1.0, p2.getY(), EPSILON);
        assertEquals(1.0, p3.getX(), EPSILON);
        assertEquals(7.0, p3.getY(), EPSILON);
    }

    /**
     * Tests that the system strictly blocks matrices with incorrect dimensions or all-zero rows.
     */
    @Test
    public void testMatrixInvalidDimensions() {
        // A has only 2 rows instead of required 3
        double[][] invalidA = {
                {1.0, 1.0},
                {1.0, -1.0}
        };
        double[] b = {8.0, 2.0, 1.0};
        assertThrows(GeometryException.class, () -> service.buildTriangle(invalidA, b));

        // b has only 2 numbers instead of required 3
        double[][] validA = {
                {1.0,  1.0},
                {1.0, -1.0},
                {1.0,  0.0}
        };
        double[] shortB = {8.0, 2.0};
        assertThrows(GeometryException.class, () -> service.buildTriangle(validA, shortB));

        // Row 1 is all zeroes (0x + 0y = 5 is not a line)
        double[][] zeroRowA = {
                {0.0,  0.0},
                {1.0, -1.0},
                {1.0,  0.0}
        };
        assertThrows(GeometryException.class, () -> service.buildTriangle(zeroRowA, b));
    }

    /**
     * Tests that parallel lines (like {@code x + y = 5} and {@code x + y = 10}) trigger a safety alarm.
     */
    @Test
    public void testParallelLines() {
        GeometryException ex = assertThrows(GeometryException.class, () ->
                service.buildTriangle("x + y = 5", "x + y = 10", "x = 1")
        );
        assertTrue(ex.getMessage().contains("parallel"));
    }

    /**
     * Tests that identical lines (like {@code x + y = 5} and {@code 2x + 2y = 10}) trigger a safety alarm.
     */
    @Test
    public void testCoincidentLines() {
        GeometryException ex = assertThrows(GeometryException.class, () ->
                service.buildTriangle("x + y = 5", "2x + 2y = 10", "x = 1")
        );
        assertTrue(ex.getMessage().contains("identical") || ex.getMessage().contains("coincident"));
    }

    /**
     * Tests that three lines crossing at the exact same point (like bicycle spokes) trigger a safety alarm.
     */
    @Test
    public void testConcurrentLines() {
        GeometryException ex = assertThrows(GeometryException.class, () ->
                service.buildTriangle("y = x", "y = -x", "y = 0")
        );
        assertTrue(ex.getMessage().contains("concurrent"));
    }

    /**
     * Tests construction of a right-angled triangle with known area and perimeter.
     *
     * @throws ParseException    if parsing fails
     * @throws GeometryException if geometry calculation fails
     */
    @Test
    public void testRightTriangle() throws ParseException, GeometryException {
        // x = 0 (Y-axis), y = 0 (X-axis), x + y = 4
        // Corners at (0, 0), (0, 4), (4, 0). Legs = 4, 4. Hypotenuse = sqrt(32). Area = 0.5 * 4 * 4 = 8.
        Triangle t = service.buildTriangle("x = 0", "y = 0", "x + y = 4");
        assertNotNull(t);
        assertEquals(8.0, t.getArea(), EPSILON);
        assertEquals(4.0 + 4.0 + Math.sqrt(32.0), t.getPerimeter(), EPSILON);
    }
}
