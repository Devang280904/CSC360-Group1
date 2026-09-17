package com.trianglefx.model;

import java.util.Objects;

/**
 * Represents a single specific dot or pin on a flat 2D sheet of graph paper (Cartesian plane).
 *
 * Just like finding a location on a city map using street coordinates:
 * - {@code x} tells you how far horizontally to go: right is positive, left is negative.
 * - {@code y} tells you how far vertically to go: up is positive, down is negative.
 *
 * This object is immutable (unchangeable): once a dot is placed at {@code (x, y)}, its location
 * never shifts.
 */
public class Point {

    /**
     * The horizontal position of this dot on the graph paper.
     */
    private final double x;

    /**
     * The vertical position of this dot on the graph paper.
     */
    private final double y;

    /**
     * Places a new dot at the exact horizontal (x) and vertical (y) coordinates.
     *
     * @param x how far to the right (positive) or left (negative) the point sits
     * @param y how far up (positive) or down (negative) the point sits
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the horizontal (x) coordinate of this dot.
     *
     * @return the horizontal number
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the vertical (y) coordinate of this dot.
     *
     * @return the vertical number
     */
    public double getY() {
        return y;
    }

    /**
     * Measures the straight-line ruler distance between this dot and another dot.
     *
     * Uses the famous Pythagorean theorem from basic geometry:
     * {@code distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)}.
     *
     * @param other the second dot you want to measure the distance to
     * @return the straight-line distance in grid units
     */
    public double distanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.hypot(dx, dy);
    }

    /**
     * Checks if two dots are sitting in practically the exact same place.
     *
     * Because computers do math with tiny decimal rounding artifacts (e.g. {@code 0.0000000001}),
     * this checks if both x and y are within a microscopic margin of error ({@code epsilon})
     * rather than demanding absolute machine-level exactness.
     *
     * @param other   the dot to compare with
     * @param epsilon the maximum allowed microscopic difference (e.g. {@code 0.000001})
     * @return {@code true} if both dots are close enough to be considered identical; {@code false} otherwise
     */
    public boolean equalsWithTolerance(Point other, double epsilon) {
        if (other == null) return false;
        return Math.abs(this.x - other.x) <= epsilon && Math.abs(this.y - other.y) <= epsilon;
    }

    /**
     * Checks if another object is a Point sitting at the exact same x and y.
     *
     * @param o the other item to compare with
     * @return {@code true} if both items are Points with identical x and y; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    /**
     * Produces a unique digital fingerprint (hash code) for this point so it can be stored in sets or maps.
     *
     * @return the numerical fingerprint code
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Formats this point into a clean, human-readable string like {@code (5.00, 3.00)}.
     *
     * @return text showing the coordinates rounded to two decimal places
     */
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
