package com.trianglefx.geometry;

import com.trianglefx.model.Line;
import com.trianglefx.model.Point;
import com.trianglefx.model.Triangle;
import com.trianglefx.parser.EquationParser;
import com.trianglefx.parser.ParseException;

import java.util.Optional;

/**
 * Service for computing intersections and constructing validated triangles from 3 equations.
 */
public class GeometryService {
    private static final double MIN_AREA = 1e-6;
    private static final double POINT_TOLERANCE = 1e-6;

    /**
     * Parses three equation strings and builds a non-degenerate Triangle.
     */
    public Triangle buildTriangle(String eq1, String eq2, String eq3) throws ParseException, GeometryException {
        Line l1 = EquationParser.parse(eq1);
        Line l2 = EquationParser.parse(eq2);
        Line l3 = EquationParser.parse(eq3);

        return buildTriangle(l1, l2, l3);
    }

    /**
     * Computes intersections of three lines and returns a non-degenerate Triangle.
     */
    public Triangle buildTriangle(Line l1, Line l2, Line l3) throws GeometryException {
        // Check pairwise parallel / coincident
        checkPair(l1, l2, 1, 2);
        checkPair(l2, l3, 2, 3);
        checkPair(l3, l1, 3, 1);

        Point p12 = l1.intersectionWith(l2)
                .orElseThrow(() -> new GeometryException("Line 1 and Line 2 do not have a unique intersection."));
        Point p23 = l2.intersectionWith(l3)
                .orElseThrow(() -> new GeometryException("Line 2 and Line 3 do not have a unique intersection."));
        Point p31 = l3.intersectionWith(l1)
                .orElseThrow(() -> new GeometryException("Line 3 and Line 1 do not have a unique intersection."));

        // Check if concurrent (all 3 points coincide)
        if (p12.equalsWithTolerance(p23, POINT_TOLERANCE) ||
            p23.equalsWithTolerance(p31, POINT_TOLERANCE) ||
            p31.equalsWithTolerance(p12, POINT_TOLERANCE)) {
            throw new GeometryException("The three lines are concurrent (intersect at the same point), so no triangle is formed.");
        }

        Triangle triangle = new Triangle(p12, p23, p31, l1, l2, l3);

        if (triangle.getArea() < MIN_AREA) {
            throw new GeometryException(String.format(
                    "The intersection points form a degenerate triangle with near-zero area (%.6f).",
                    triangle.getArea()
            ));
        }

        return triangle;
    }

    private void checkPair(Line la, Line lb, int idxA, int idxB) throws GeometryException {
        if (la.isCoincidentWith(lb)) {
            throw new GeometryException(String.format(
                    "Line %d ('%s') and Line %d ('%s') are identical (coincident).",
                    idxA, la.getRawEquation(), idxB, lb.getRawEquation()
            ));
        }
        if (la.isParallelTo(lb)) {
            throw new GeometryException(String.format(
                    "Line %d ('%s') and Line %d ('%s') are parallel and do not intersect.",
                    idxA, la.getRawEquation(), idxB, lb.getRawEquation()
            ));
        }
    }
}
