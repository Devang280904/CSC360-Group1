package trianglefx.geometry;

import java.util.Objects;

/**
 * Utility for finding intersections between two line equations.
 */
public final class LineIntersection {

	private static final double EPSILON = 1e-10;

	private LineIntersection() {
		// Utility class
	}

	/**
	 * Classification of the relationship between two lines.
	 */
	public enum Type {
		INTERSECTING,
		PARALLEL,
		COINCIDENT,
	}

	/**
	 * Result of attempting to intersect two lines.
	 *
	 * @param type relationship between the lines
	 * @param point intersection point when type is INTERSECTING, otherwise null
	 */
	public record Result(Type type, Point point) {}

	/**
	 * Finds the intersection relationship for two lines in standard form (Ax + By = C).
	 */
	public static Result find(LineEquation l1, LineEquation l2) {
		Objects.requireNonNull(l1, "l1 must not be null");
		Objects.requireNonNull(l2, "l2 must not be null");

		double determinant = l1.a() * l2.b() - l2.a() * l1.b();

		if (Math.abs(determinant) <= EPSILON) {
			if (areCoincident(l1, l2)) {
				return new Result(Type.COINCIDENT, null);
			}
			return new Result(Type.PARALLEL, null);
		}

		double x = (l1.c() * l2.b() - l2.c() * l1.b()) / determinant;
		double y = (l1.a() * l2.c() - l2.a() * l1.c()) / determinant;

		return new Result(Type.INTERSECTING, new Point(x, y));
	}

	private static boolean areCoincident(LineEquation l1, LineEquation l2) {
		double ab = l1.a() * l2.b() - l2.a() * l1.b();
		double ac = l1.a() * l2.c() - l2.a() * l1.c();
		double bc = l1.b() * l2.c() - l2.b() * l1.c();

		return (
			Math.abs(ab) <= EPSILON &&
			Math.abs(ac) <= EPSILON &&
			Math.abs(bc) <= EPSILON
		);
	}
}
