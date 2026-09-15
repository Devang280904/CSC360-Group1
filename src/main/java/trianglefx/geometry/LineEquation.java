package trianglefx.geometry;

/**
 * Represents a line in standard form: Ax + By = C.
 */
public record LineEquation(double a, double b, double c) {

    private static final double EPSILON = 1e-10;

    public LineEquation {
        if (Math.abs(a) <= EPSILON && Math.abs(b) <= EPSILON) {
            throw new IllegalArgumentException("Invalid line equation: both A and B cannot be zero.");
        }
    }
}
