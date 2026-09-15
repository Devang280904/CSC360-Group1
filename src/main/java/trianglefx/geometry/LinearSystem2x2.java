package trianglefx.geometry;

import java.util.Objects;

/**
 * Solves a 2x2 linear system in the form {@code Ax = b}:
 *
 * <pre>
 * [a11 a12] [x] = [b1]
 * [a21 a22] [y]   [b2]
 * </pre>
 */
public final class LinearSystem2x2 {

	private static final double EPSILON = 1e-10;

	private LinearSystem2x2() {
		// Utility class
	}

	/**
	 * Classification of solution cardinality.
	 */
	public enum Type {
		UNIQUE_SOLUTION,
		NO_SOLUTION,
		INFINITE_SOLUTIONS,
	}

	/**
	 * Result of solving a 2x2 system.
	 *
	 * @param type solution cardinality
	 * @param solution unique solution point when {@code type} is {@link Type#UNIQUE_SOLUTION}; otherwise {@code null}
	 */
	public record Result(Type type, Point solution) {}

	/**
	 * Solves {@code Ax = b} from scalar coefficients.
	 *
	 * @param a11 matrix entry at row 1, col 1
	 * @param a12 matrix entry at row 1, col 2
	 * @param b1 right-hand side row 1
	 * @param a21 matrix entry at row 2, col 1
	 * @param a22 matrix entry at row 2, col 2
	 * @param b2 right-hand side row 2
	 * @return solution classification and point when unique
	 */
	public static Result solve(
		double a11,
		double a12,
		double b1,
		double a21,
		double a22,
		double b2
	) {
		double detA = a11 * a22 - a21 * a12;

		if (Math.abs(detA) > EPSILON) {
			double x = (b1 * a22 - b2 * a12) / detA;
			double y = (a11 * b2 - a21 * b1) / detA;
			return new Result(Type.UNIQUE_SOLUTION, new Point(x, y));
		}

		double detAugX = a11 * b2 - a21 * b1;
		double detAugY = a12 * b2 - a22 * b1;

		if (Math.abs(detAugX) <= EPSILON && Math.abs(detAugY) <= EPSILON) {
			return new Result(Type.INFINITE_SOLUTIONS, null);
		}

		return new Result(Type.NO_SOLUTION, null);
	}

	/**
	 * Solves {@code Ax = b} using matrix/vector representation.
	 *
	 * @param a 2x2 coefficient matrix
	 * @param b right-hand side vector of length 2
	 * @return solution classification and point when unique
	 * @throws NullPointerException if {@code a} or {@code b} is {@code null}
	 * @throws IllegalArgumentException if dimensions are not 2x2 and 2 respectively
	 */
	public static Result solve(double[][] a, double[] b) {
		Objects.requireNonNull(a, "a must not be null");
		Objects.requireNonNull(b, "b must not be null");

		if (
			a.length != 2 ||
			a[0] == null ||
			a[1] == null ||
			a[0].length != 2 ||
			a[1].length != 2
		) {
			throw new IllegalArgumentException("A must be a 2x2 matrix.");
		}
		if (b.length != 2) {
			throw new IllegalArgumentException(
				"b must be a vector of length 2."
			);
		}

		return solve(a[0][0], a[0][1], b[0], a[1][0], a[1][1], b[1]);
	}

	/**
	 * Solves the linear system represented by two line equations:
	 *
	 * <pre>
	 * a1*x + b1*y = c1
	 * a2*x + b2*y = c2
	 * </pre>
	 *
	 * @param l1 first line equation
	 * @param l2 second line equation
	 * @return solution classification and point when unique
	 * @throws NullPointerException if either line is {@code null}
	 */
	public static Result fromLines(LineEquation l1, LineEquation l2) {
		Objects.requireNonNull(l1, "l1 must not be null");
		Objects.requireNonNull(l2, "l2 must not be null");

		return solve(l1.a(), l1.b(), l1.c(), l2.a(), l2.b(), l2.c());
	}
}
