package trianglefx.geometry;

import java.util.Objects;

/**
 * Solves a 2x2 linear system in the form Ax = b:
 *
 * [a11 a12] [x] = [b1]
 * [a21 a22] [y]   [b2]
 */
public final class LinearSystem2x2 {

    private static final double EPSILON = 1e-10;

    private LinearSystem2x2() {
        // Utility class
    }

    public enum Type {
        UNIQUE_SOLUTION,
        NO_SOLUTION,
        INFINITE_SOLUTIONS
    }

    public record Result(Type type, Point solution) {
    }

    /**
     * Solves Ax = b using scalar coefficients.
     */
    public static Result solve(double a11, double a12, double b1,
                               double a21, double a22, double b2) {

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
     * Solves Ax = b using matrix/vector inputs.
     * A must be 2x2 and b must have size 2.
     */
    public static Result solve(double[][] a, double[] b) {
        Objects.requireNonNull(a, "a must not be null");
        Objects.requireNonNull(b, "b must not be null");

        if (a.length != 2 || a[0] == null || a[1] == null || a[0].length != 2 || a[1].length != 2) {
            throw new IllegalArgumentException("A must be a 2x2 matrix.");
        }
        if (b.length != 2) {
            throw new IllegalArgumentException("b must be a vector of length 2.");
        }

        return solve(a[0][0], a[0][1], b[0], a[1][0], a[1][1], b[1]);
    }

    /**
     * Convenience method to solve the system represented by two line equations:
     * a1*x + b1*y = c1
     * a2*x + b2*y = c2
     */
    public static Result fromLines(LineEquation l1, LineEquation l2) {
        Objects.requireNonNull(l1, "l1 must not be null");
        Objects.requireNonNull(l2, "l2 must not be null");

        return solve(l1.a(), l1.b(), l1.c(), l2.a(), l2.b(), l2.c());
    }
}
