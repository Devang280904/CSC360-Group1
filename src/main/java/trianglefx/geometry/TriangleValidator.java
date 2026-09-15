package trianglefx.geometry;

/**
 * Utility for validating whether three points form a non-degenerate triangle.
 */
public final class TriangleValidator {

    private static final double EPSILON = 1e-10;

    private TriangleValidator() {
        // Utility class
    }

    /**
     * Returns true if the given points form a valid (non-degenerate) triangle.
     *
     * A valid triangle requires:
     * 1) All points are non-null
     * 2) No two points are identical
     * 3) Points are not collinear
     */
    public static boolean canFormTriangle(Point p1, Point p2, Point p3) {
        if (p1 == null || p2 == null || p3 == null) {
            return false;
        }

        if (areSamePoint(p1, p2) || areSamePoint(p2, p3) || areSamePoint(p1, p3)) {
            return false;
        }

        double twiceArea =
                p1.x() * (p2.y() - p3.y()) +
                p2.x() * (p3.y() - p1.y()) +
                p3.x() * (p1.y() - p2.y());

        return Math.abs(twiceArea) > EPSILON;
    }

    private static boolean areSamePoint(Point a, Point b) {
        return Math.abs(a.x() - b.x()) <= EPSILON
                && Math.abs(a.y() - b.y()) <= EPSILON;
    }
}
