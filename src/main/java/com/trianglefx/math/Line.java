package com.trianglefx.math;

/**
 * Represents a mathematical line in the standard form Ax + By = C.
 */
public class Line {
    private final double a;
    private final double b;
    private final double c;

    /**
     * Constructs a Line given coefficients A, B, and C.
     *
     * @param a The coefficient for x.
     * @param b The coefficient for y.
     * @param c The constant term on the right side of the equals sign.
     */
    public Line(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Gets the x-coefficient (A).
     * @return the A coefficient.
     */
    public double getA() { return a; }
    
    /**
     * Gets the y-coefficient (B).
     * @return the B coefficient.
     */
    public double getB() { return b; }
    
    /**
     * Gets the constant term (C).
     * @return the C constant.
     */
    public double getC() { return c; }

    /**
     * Calculates the intersection point between this line and another line using Cramer's Rule.
     *
     * @param other The other line to intersect with.
     * @return The Point of intersection.
     * @throws ArithmeticException if the lines are parallel or coincident (determinant is 0).
     */
    public Point getIntersection(Line other) {
        // Cramer's rule for system of 2 linear equations
        // A1x + B1y = C1
        // A2x + B2y = C2
        
        double det = this.a * other.b - other.a * this.b;
        
        // If determinant is very close to 0, lines are parallel or coincident
        if (Math.abs(det) < 1e-9) {
            throw new ArithmeticException("Lines are parallel or coincident (no unique intersection).");
        }
        
        double x = (this.c * other.b - other.c * this.b) / det;
        double y = (this.a * other.c - other.a * this.c) / det;
        
        return new Point(x, y);
    }
}
