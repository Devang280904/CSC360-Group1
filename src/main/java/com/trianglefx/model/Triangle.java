package com.trianglefx.model;

/**
 * Represents a triangle formed by three vertices in 2D space.
 */
public class Triangle {
    private static final double MIN_AREA = 1e-6;

    private final Point p1;
    private final Point p2;
    private final Point p3;
    private final Line l1;
    private final Line l2;
    private final Line l3;

    public Triangle(Point p1, Point p2, Point p3, Line l1, Line l2, Line l3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public Point getP3() {
        return p3;
    }

    public Line getL1() {
        return l1;
    }

    public Line getL2() {
        return l2;
    }

    public Line getL3() {
        return l3;
    }

    public double getSideA() {
        return p2.distanceTo(p3);
    }

    public double getSideB() {
        return p3.distanceTo(p1);
    }

    public double getSideC() {
        return p1.distanceTo(p2);
    }

    public double getPerimeter() {
        return getSideA() + getSideB() + getSideC();
    }

    public double getArea() {
        return 0.5 * Math.abs(
                p1.getX() * (p2.getY() - p3.getY()) +
                p2.getX() * (p3.getY() - p1.getY()) +
                p3.getX() * (p1.getY() - p2.getY())
        );
    }

    public Point getCentroid() {
        return new Point(
                (p1.getX() + p2.getX() + p3.getX()) / 3.0,
                (p1.getY() + p2.getY() + p3.getY()) / 3.0
        );
    }

    public boolean isDegenerate() {
        return getArea() < MIN_AREA;
    }
}
