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
	 * Determines whether three points can form a valid triangle.
	 *
	 * <p>A valid triangle requires all points to be non-null, distinct, and non-collinear.</p>
	 *
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 * @return {@code true} if the points form a non-degenerate triangle; {@code false} otherwise
	 */
	public static boolean canFormTriangle(Point p1, Point p2, Point p3) {
		if (p1 == null || p2 == null || p3 == null) {
			return false;
		}

		if (
			areSamePoint(p1, p2) || areSamePoint(p2, p3) || areSamePoint(p1, p3)
		) {
			return false;
		}

		double twiceArea =
			p1.x() * (p2.y() - p3.y()) +
			p2.x() * (p3.y() - p1.y()) +
			p3.x() * (p1.y() - p2.y());

		return Math.abs(twiceArea) > EPSILON;
	}

	/**
	 * Checks if two points are effectively equal under tolerance.
	 *
	 * @param a first point
	 * @param b second point
	 * @return {@code true} if both coordinates differ by at most {@link #EPSILON}
	 */
	private static boolean areSamePoint(Point a, Point b) {
		return (
			Math.abs(a.x() - b.x()) <= EPSILON &&
			Math.abs(a.y() - b.y()) <= EPSILON
		);
	}
}
