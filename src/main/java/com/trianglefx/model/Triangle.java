package com.trianglefx.model;

/**
 * Represents a complete 3-sided closed shape (a triangle) on 2D graph paper.
 * <p>
 * A triangle is defined by:
 * </p>
 * <ul>
 *   <li><b>3 Corner points (Vertices):</b> {@code p1}, {@code p2}, and {@code p3} where the lines cross.</li>
 *   <li><b>3 Bounding lines:</b> {@code l1}, {@code l2}, and {@code l3} that form the edges.</li>
 * </ul>
 * <p>
 * This class calculates all key geometric properties of the triangle:
 * side lengths, perimeter, enclosed area, and center of balance (centroid).
 * </p>
 */
public class Triangle {

    /**
     * The smallest area a triangle must have (0.000001) to be considered a real, non-flat shape.
     */
    private static final double MIN_AREA = 1e-6;

    /** Corner 1: where Line 1 meets Line 2. */
    private final Point p1;

    /** Corner 2: where Line 2 meets Line 3. */
    private final Point p2;

    /** Corner 3: where Line 3 meets Line 1. */
    private final Point p3;

    /** The first boundary line. */
    private final Line l1;

    /** The second boundary line. */
    private final Line l2;

    /** The third boundary line. */
    private final Line l3;

    /**
     * Constructs a new triangle with 3 known corners and 3 boundary lines.
     *
     * @param p1 the first corner point (where l1 crosses l2)
     * @param p2 the second corner point (where l2 crosses l3)
     * @param p3 the third corner point (where l3 crosses l1)
     * @param l1 the first boundary line
     * @param l2 the second boundary line
     * @param l3 the third boundary line
     */
    public Triangle(Point p1, Point p2, Point p3, Line l1, Line l2, Line l3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
    }

    /**
     * Gets the first corner point (intersection of Line 1 and Line 2).
     *
     * @return corner point {@code p1}
     */
    public Point getP1() {
        return p1;
    }

    /**
     * Gets the second corner point (intersection of Line 2 and Line 3).
     *
     * @return corner point {@code p2}
     */
    public Point getP2() {
        return p2;
    }

    /**
     * Gets the third corner point (intersection of Line 3 and Line 1).
     *
     * @return corner point {@code p3}
     */
    public Point getP3() {
        return p3;
    }

    /**
     * Gets the first boundary line.
     *
     * @return boundary line {@code l1}
     */
    public Line getL1() {
        return l1;
    }

    /**
     * Gets the second boundary line.
     *
     * @return boundary line {@code l2}
     */
    public Line getL2() {
        return l2;
    }

    /**
     * Gets the third boundary line.
     *
     * @return boundary line {@code l3}
     */
    public Line getL3() {
        return l3;
    }

    /**
     * Computes the length of side {@code a}, which is the distance between corner 2 and corner 3.
     *
     * @return ruler length of side a
     */
    public double getSideA() {
        return p2.distanceTo(p3);
    }

    /**
     * Computes the length of side {@code b}, which is the distance between corner 3 and corner 1.
     *
     * @return ruler length of side b
     */
    public double getSideB() {
        return p3.distanceTo(p1);
    }

    /**
     * Computes the length of side {@code c}, which is the distance between corner 1 and corner 2.
     *
     * @return ruler length of side c
     */
    public double getSideC() {
        return p1.distanceTo(p2);
    }

    /**
     * Calculates the perimeter (the total distance if you walk all the way around the outside of the triangle).
     * <p>
     * Formula: {@code perimeter = sideA + sideB + sideC}.
     * </p>
     *
     * @return the total perimeter length
     */
    public double getPerimeter() {
        return getSideA() + getSideB() + getSideC();
    }

    /**
     * Calculates the 2D surface area (the amount of flat space enclosed inside the triangle).
     * <p>
     * Uses Gauss's Shoelace formula:
     * {@code 0.5 * |x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)|}.
     * </p>
     *
     * @return the surface area in square grid units
     */
    public double getArea() {
        return 0.5 * Math.abs(
                p1.getX() * (p2.getY() - p3.getY()) +
                p2.getX() * (p3.getY() - p1.getY()) +
                p3.getX() * (p1.getY() - p2.getY())
        );
    }

    /**
     * Computes the centroid (the physical center of gravity / balancing point of the triangle).
     * <p>
     * If this triangle were cut out of solid cardboard, you could balance it perfectly on the tip
     * of a pencil placed exactly at the centroid!
     * Calculated by taking the average of the 3 corners:
     * {@code x = (x1 + x2 + x3) / 3}, {@code y = (y1 + y2 + y3) / 3}.
     * </p>
     *
     * @return the centroid balancing {@link Point}
     */
    public Point getCentroid() {
        return new Point(
                (p1.getX() + p2.getX() + p3.getX()) / 3.0,
                (p1.getY() + p2.getY() + p3.getY()) / 3.0
        );
    }

    /**
     * Checks if this triangle is "degenerate" (fake or collapsed into a flat line or point).
     *
     * @return {@code true} if the area is smaller than {@link #MIN_AREA}; {@code false} if it is a real triangle
     */
    public boolean isDegenerate() {
        return getArea() < MIN_AREA;
    }
}
