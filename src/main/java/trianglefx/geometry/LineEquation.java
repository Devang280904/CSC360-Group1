package trianglefx.geometry;

/**
 * Represents a line in standard form: {@code Ax + By = C}.
 *
 * @param a coefficient of x
 * @param b coefficient of y
 * @param c right-hand constant term
 */
public record LineEquation(double a, double b, double c) {
	private static final double EPSILON = 1e-10;

	/**
	 * Validates that the equation has at least one non-zero variable coefficient.
	 *
	 * @throws IllegalArgumentException if both {@code a} and {@code b} are effectively zero
	 */
	public LineEquation {
		if (Math.abs(a) <= EPSILON && Math.abs(b) <= EPSILON) {
			throw new IllegalArgumentException(
				"Invalid line equation: both A and B cannot be zero."
			);
		}
	}
}
