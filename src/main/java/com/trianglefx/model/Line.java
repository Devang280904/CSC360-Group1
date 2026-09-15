package com.trianglefx.model;

import java.util.Optional;

/**
 * Represents a 2D line in standard form: A*x + B*y = C
 */
public class Line {
    private static final double EPSILON = 1e-9;

    private final double a;
    private final double b;
    private final double c;
    private final String rawEquation;

    public Line(double a, double b, double c) {
        this(a, b, c, null);
    }

    public Line(double a, double b, double c, String rawEquation) {
        if (Math.hypot(a, b) < EPSILON) {
            throw new IllegalArgumentException("Coefficients A and B cannot both be zero.");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.rawEquation = rawEquation != null ? rawEquation.trim() : formatStandard();
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public String getRawEquation() {
        return rawEquation;
    }

    /**
     * Finds the intersection point between this line and another.
     * Returns Optional.empty() if lines are parallel or coincident.
     */
    public Optional<Point> intersectionWith(Line other) {
        double det = this.a * other.b - other.a * this.b;
        if (Math.abs(det) < EPSILON) {
            return Optional.empty(); // Parallel or coincident
        }
        double x = (this.c * other.b - other.c * this.b) / det;
        double y = (this.a * other.c - other.a * this.c) / det;

        // Clean up any tiny negative zeros
        if (Math.abs(x) < 1e-12) x = 0.0;
        if (Math.abs(y) < 1e-12) y = 0.0;

        return Optional.of(new Point(x, y));
    }

    public boolean isParallelTo(Line other) {
        double det = this.a * other.b - other.a * this.b;
        return Math.abs(det) < EPSILON;
    }

    public boolean isCoincidentWith(Line other) {
        if (!isParallelTo(other)) {
            return false;
        }
        // If parallel, check if c ratio matches
        double detAC = this.a * other.c - other.a * this.c;
        double detBC = this.b * other.c - other.c * this.b;
        return Math.abs(detAC) < EPSILON && Math.abs(detBC) < EPSILON;
    }

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

    private String formatCoeff(double val) {
        if (Math.abs(val - Math.round(val)) < EPSILON) {
            return String.valueOf(Math.round(val));
        }
        return String.format("%.2f", val);
    }

    @Override
    public String toString() {
        return rawEquation != null && !rawEquation.isBlank() ? rawEquation : formatStandard();
    }
}
