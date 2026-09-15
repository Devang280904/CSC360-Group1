package trianglefx.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineIntersectionTest {

    private static final double DELTA = 1e-9;

    @Test
    void findsIntersectionForTwoNonParallelLines() {
        // x + y = 5
        // x - y = 1
        LineEquation l1 = new LineEquation(1, 1, 5);
        LineEquation l2 = new LineEquation(1, -1, 1);

        LineIntersection.Result result = LineIntersection.find(l1, l2);

        assertEquals(LineIntersection.Type.INTERSECTING, result.type());
        assertNotNull(result.point());
        assertEquals(3.0, result.point().x(), DELTA);
        assertEquals(2.0, result.point().y(), DELTA);
    }

    @Test
    void findsIntersectionForVerticalAndHorizontalLines() {
        // x = 2  -> 1x + 0y = 2
        // y = -3 -> 0x + 1y = -3
        LineEquation vertical = new LineEquation(1, 0, 2);
        LineEquation horizontal = new LineEquation(0, 1, -3);

        LineIntersection.Result result = LineIntersection.find(vertical, horizontal);

        assertEquals(LineIntersection.Type.INTERSECTING, result.type());
        assertNotNull(result.point());
        assertEquals(2.0, result.point().x(), DELTA);
        assertEquals(-3.0, result.point().y(), DELTA);
    }

    @Test
    void detectsParallelLines() {
        // x + y = 5
        // 2x + 2y = 20 (same slope, different intercept)
        LineEquation l1 = new LineEquation(1, 1, 5);
        LineEquation l2 = new LineEquation(2, 2, 20);

        LineIntersection.Result result = LineIntersection.find(l1, l2);

        assertEquals(LineIntersection.Type.PARALLEL, result.type());
        assertNull(result.point());
    }

    @Test
    void detectsCoincidentLines() {
        // x + y = 5
        // 2x + 2y = 10 (same line)
        LineEquation l1 = new LineEquation(1, 1, 5);
        LineEquation l2 = new LineEquation(2, 2, 10);

        LineIntersection.Result result = LineIntersection.find(l1, l2);

        assertEquals(LineIntersection.Type.COINCIDENT, result.type());
        assertNull(result.point());
    }

    @Test
    void detectsCoincidentLinesWithNegativeScaleFactor() {
        // x - 2y = 7
        // -2x + 4y = -14 (same line)
        LineEquation l1 = new LineEquation(1, -2, 7);
        LineEquation l2 = new LineEquation(-2, 4, -14);

        LineIntersection.Result result = LineIntersection.find(l1, l2);

        assertEquals(LineIntersection.Type.COINCIDENT, result.type());
        assertNull(result.point());
    }

    @Test
    void throwsForNullInputs() {
        LineEquation line = new LineEquation(1, 1, 1);

        assertThrows(NullPointerException.class, () -> LineIntersection.find(null, line));
        assertThrows(NullPointerException.class, () -> LineIntersection.find(line, null));
    }
}
