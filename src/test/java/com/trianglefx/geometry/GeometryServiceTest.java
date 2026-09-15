package com.trianglefx.geometry;

import com.trianglefx.model.Point;
import com.trianglefx.model.Triangle;
import com.trianglefx.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeometryServiceTest {

    private static final double EPSILON = 1e-4;
    private final GeometryService service = new GeometryService();

    @Test
    public void testProblemStatementExample() throws ParseException, GeometryException {
        // x + y = 8
        // x - y = 2
        // x = 1
        // Intersections:
        // L1 & L2: 2x = 10 => x = 5, y = 3 -> (5, 3)
        // L2 & L3: x = 1 => 1 - y = 2 => y = -1 -> (1, -1)
        // L3 & L1: x = 1 => 1 + y = 8 => y = 7 -> (1, 7)
        Triangle triangle = service.buildTriangle("x + y = 8", "x - y = 2", "x = 1");

        assertNotNull(triangle);
        assertFalse(triangle.isDegenerate());

        Point p1 = triangle.getP1(); // L1 & L2
        Point p2 = triangle.getP2(); // L2 & L3
        Point p3 = triangle.getP3(); // L3 & L1

        assertEquals(5.0, p1.getX(), EPSILON);
        assertEquals(3.0, p1.getY(), EPSILON);

        assertEquals(1.0, p2.getX(), EPSILON);
        assertEquals(-1.0, p2.getY(), EPSILON);

        assertEquals(1.0, p3.getX(), EPSILON);
        assertEquals(7.0, p3.getY(), EPSILON);

        // Area: base is along x=1 from -1 to 7, height is 5 - 1 = 4.
        // Base length = 8, height = 4 => Area = 0.5 * 8 * 4 = 16.
        assertEquals(16.0, triangle.getArea(), EPSILON);
    }

    @Test
    public void testParallelLines() {
        // x + y = 5 and x + y = 10 are parallel
        GeometryException ex = assertThrows(GeometryException.class, () ->
                service.buildTriangle("x + y = 5", "x + y = 10", "x = 1")
        );
        assertTrue(ex.getMessage().contains("parallel"));
    }

    @Test
    public void testCoincidentLines() {
        // 2x + 2y = 10 is identical to x + y = 5
        GeometryException ex = assertThrows(GeometryException.class, () ->
                service.buildTriangle("x + y = 5", "2x + 2y = 10", "x = 1")
        );
        assertTrue(ex.getMessage().contains("identical") || ex.getMessage().contains("coincident"));
    }

    @Test
    public void testConcurrentLines() {
        // Three lines intersecting at (0, 0): y = x, y = -x, y = 0
        GeometryException ex = assertThrows(GeometryException.class, () ->
                service.buildTriangle("y = x", "y = -x", "y = 0")
        );
        assertTrue(ex.getMessage().contains("concurrent"));
    }

    @Test
    public void testRightTriangle() throws ParseException, GeometryException {
        // x = 0, y = 0, x + y = 4
        // Intersections: (0, 0), (0, 4), (4, 0)
        Triangle t = service.buildTriangle("x = 0", "y = 0", "x + y = 4");
        assertNotNull(t);
        assertEquals(8.0, t.getArea(), EPSILON);
        assertEquals(4.0 + 4.0 + Math.sqrt(32.0), t.getPerimeter(), EPSILON);
    }
}
