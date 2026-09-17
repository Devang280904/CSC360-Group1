package com.trianglefx.model;

import java.util.Optional;

/**
 * Represents an infinitely long, straight 2D line on graph paper.
 *
 * In mathematics, every straight line on flat 2D paper can be written in the standard equation:
 *
 *   A*x + B*y = C
 *
 * Here is what each part means in plain English:
 * - {@code A}: How much horizontal tilt or slope the line has along the x-axis.
 * - {@code B}: How much vertical tilt or slope the line has along the y-axis.
 * - {@code C}: The constant target number that positions the line on the graph paper.
 *
 * IMPORTANT RULE: {@code A} and {@code B} cannot both be zero at the same time,
 * because {@code 0*x + 0*y = C} has no direction and does not describe a line.
 */
public class Line {

    /**
     * A microscopic numerical cushion (0.000000001) used to avoid floating-point computer rounding errors.
     */
    private static final double EPSILON = 1e-9;

    /** The multiplier in front of x (A in Ax + By = C). */
    private final double a;

    /** The multiplier in front of y (B in Ax + By = C). */
    private final double b;

    /** The constant number on the other side of the equals sign (C in Ax + By = C). */
    private final double c;

    /** The original text equation entered by the user (if one was typed). */
    private final String rawEquation;

    /**
     * Creates a new line given the numbers A, B, and C for {@code A*x + B*y = C}.
     *
     * @param a the multiplier for x
     * @param b the multiplier for y
     * @param c the constant result on the right side of the equals sign
     * @throws IllegalArgumentException if both {@code a} and {@code b} are zero (an impossible line)
     */
    public Line(double a, double b, double c) {
        this(a, b, c, null);
    }

    /**
     * Creates a new line given A, B, C, and an optional text label describing it.
     *
     * @param a           the multiplier for x
     * @param b           the multiplier for y
     * @param c           the constant result on the right side of the equals sign
     * @param rawEquation the original equation text typed by the user, if available
     * @throws IllegalArgumentException if both {@code a} and {@code b} are zero
     */
    public Line(double a, double b, double c, String rawEquation) {
        if (Math.hypot(a, b) < EPSILON) {
            throw new IllegalArgumentException("Coefficients A and B cannot both be zero; at least one variable must exist.");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.rawEquation = rawEquation != null ? rawEquation.trim() : formatStandard();
    }

    /**
     * Gets the multiplier {@code A} in front of {@code x}.
     *
     * @return number A
     */
    public double getA() {
        return a;
    }

    /**
     * Gets the multiplier {@code B} in front of {@code y}.
     *
     * @return number B
     */
    public double getB() {
        return b;
    }

    /**
     * Gets the constant number {@code C} on the right-hand side.
     *
     * @return number C
     */
    public double getC() {
        return c;
    }

    /**
     * Gets the original text or equation string describing this line.
     *
     * @return equation text like {@code "1x + 1y = 8"}
     */
    public String getRawEquation() {
        return rawEquation;
    }

    /**
     * Finds the exact cross point (intersection dot) where this line meets another line.
     *
     * Solves the two simultaneous equations using Cramer's rule (calculating the 2x2 determinant).
     * If the lines run parallel (never touch) or are identical (touch everywhere), there is no
     * single unique intersection, so this returns an empty result.
     *
     * @param other the second line you want to cross with this one
     * @return an {@link Optional} holding the meeting {@link Point} if they cross, or empty if parallel
     */
    public Optional<Point> intersectionWith(Line other) {
        double det = this.a * other.b - other.a * this.b;
        if (Math.abs(det) < EPSILON) {
            return Optional.empty(); // Parallel or identical lines do not have a single unique meeting point
        }
        double x = (this.c * other.b - other.c * this.b) / det;
        double y = (this.a * other.c - other.a * this.c) / det;

        // Clean up any tiny negative zeros (-0.0) from computer math
        if (Math.abs(x) < 1e-12) x = 0.0;
        if (Math.abs(y) < 1e-12) y = 0.0;

        return Optional.of(new Point(x, y));
    }

    /**
     * Tests if this line runs completely parallel to another line (like railroad tracks).
     *
     * Two lines are parallel if they slope at the exact same angle. Parallel lines never
     * cross each other at any point on the graph.
     *
     * @param other the line to check against
     * @return {@code true} if both lines slope in the exact same direction; {@code false} if they will eventually cross
     */
    public boolean isParallelTo(Line other) {
        double det = this.a * other.b - other.a * this.b;
        return Math.abs(det) < EPSILON;
    }

    /**
     * Tests if this line is identical (coincident) to another line.
     *
     * For example, {@code x + y = 5} and {@code 2x + 2y = 10} look different, but every point
     * on one line is also on the other. They are the exact same physical line drawn twice.
     *
     * @param other the line to compare with
     * @return {@code true} if both equations describe the exact same line; {@code false} otherwise
     */
    public boolean isCoincidentWith(Line other) {
        if (!isParallelTo(other)) {
            return false;
        }
        // If they slope the same, check if their constant offsets match proportionally
        double detAC = this.a * other.c - other.a * this.c;
        double detBC = this.b * other.c - other.c * this.b;
        return Math.abs(detAC) < EPSILON && Math.abs(detBC) < EPSILON;
    }

    /**
     * Formats this line into standard algebra notation {@code A*x + B*y = C} (e.g. {@code "2x - 3y = 10"}).
     *
     * @return cleanly formatted algebra string
     */
    public String formatStandard() {
        StringBuilder sb = new StringBuilder();
        boolean hasA = Math.abs(a) > EPSILON;
        boolean hasB = Math.abs(b) > EPSILON;

        if (hasA) {
            sb.append(formatCoeff(a)).append("x");
        }
        if (hasB) {
            if (hasA) {
                if (b > 0) {
                    sb.append(" + ").append(formatCoeff(b)).append("y");
                } else {
                    sb.append(" - ").append(formatCoeff(Math.abs(b))).append("y");
                }
            } else {
                sb.append(formatCoeff(b)).append("y");
            }
        }
        sb.append(" = ").append(formatCoeff(c));
        return sb.toString();
    }

    /**
     * Helper to display a number nicely, dropping decimal points if it is a whole number (e.g. 5 instead of 5.00).
     *
     * @param val number to format
     * @return clean text representation
     */
    private String formatCoeff(double val) {
        if (Math.abs(val - Math.round(val)) < EPSILON) {
            return String.valueOf(Math.round(val));
        }
        return String.format("%.2f", val);
    }

    /**
     * Returns the printable name or equation text for this line.
     *
     * @return the equation text
     */
    @Override
    public String toString() {
        return rawEquation != null && !rawEquation.isBlank() ? rawEquation : formatStandard();
    }
}
