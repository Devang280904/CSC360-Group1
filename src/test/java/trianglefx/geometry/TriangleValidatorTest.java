package trianglefx.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriangleValidatorTest {

    @Test
    void returnsTrueForValidTriangle() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(4, 0);
        Point p3 = new Point(0, 3);

        assertTrue(TriangleValidator.canFormTriangle(p1, p2, p3));
    }

    @Test
    void returnsFalseWhenAnyPointIsNull() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);

        assertFalse(TriangleValidator.canFormTriangle(null, p2, p1));
        assertFalse(TriangleValidator.canFormTriangle(p1, null, p2));
        assertFalse(TriangleValidator.canFormTriangle(p1, p2, null));
    }

    @Test
    void returnsFalseForExactlyDuplicatePoints() {
        Point p1 = new Point(2, 2);
        Point p2 = new Point(2, 2);
        Point p3 = new Point(3, 5);

        assertFalse(TriangleValidator.canFormTriangle(p1, p2, p3));
    }

    @Test
    void returnsFalseForNearDuplicatePointsWithinTolerance() {
        Point p1 = new Point(1.0, 1.0);
        Point p2 = new Point(1.0 + 1e-11, 1.0 - 1e-11);
        Point p3 = new Point(5.0, 2.0);

        assertFalse(TriangleValidator.canFormTriangle(p1, p2, p3));
    }

    @Test
    void returnsFalseForCollinearPoints() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1);
        Point p3 = new Point(2, 2);

        assertFalse(TriangleValidator.canFormTriangle(p1, p2, p3));
    }

    @Test
    void returnsFalseForAreaWithinTolerance() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 1e-12);
        Point p3 = new Point(2, 2e-12);

        assertFalse(TriangleValidator.canFormTriangle(p1, p2, p3));
    }

    @Test
    void returnsTrueWhenAreaIsClearlyAboveTolerance() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(1, 0);
        Point p3 = new Point(0, 1e-4);

        assertTrue(TriangleValidator.canFormTriangle(p1, p2, p3));
    }
}
