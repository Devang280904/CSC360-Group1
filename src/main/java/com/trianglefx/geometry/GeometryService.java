package com.trianglefx.geometry;

import com.trianglefx.model.Line;
import com.trianglefx.model.Point;
import com.trianglefx.model.Triangle;
import com.trianglefx.parser.EquationParser;
import com.trianglefx.parser.ParseException;

import java.util.Optional;

/**
 * The mathematician engine of the application.
 * <p>
 * This service takes your input numbers—either as a <b>3&times;2 matrix $A$ and 3&times;1 vector $B$</b>
 * or as 3 linear equation strings—turns them into physical straight lines, finds where those lines cross
 * to form corners, checks all safety rules, and returns a verified {@link Triangle}.
 * </p>
 * <p>
 * <b>How the math works step-by-step:</b>
 * </p>
 * <ol>
 *   <li><b>Row to Line:</b> Each row in the matrix system represents one straight line:
 *       Row 1 is {@code a11*x + a12*y = b1}, Row 2 is {@code a21*x + a22*y = b2}, and Row 3 is {@code a31*x + a32*y = b3}.</li>
 *   <li><b>Parallel Check:</b> It checks every pair of lines. If two lines run parallel (like train tracks), they will never meet, so no corner can exist.</li>
 *   <li><b>Identical Check:</b> If two lines lie right on top of each other, they are the same line, which cannot form a triangle.</li>
 *   <li><b>Find Corners:</b> It calculates where Line 1 crosses Line 2 (Corner 1), where Line 2 crosses Line 3 (Corner 2), and where Line 3 crosses Line 1 (Corner 3).</li>
 *   <li><b>Concurrency Check:</b> If all three lines cross at the exact same single dot (like bicycle spokes), no triangle is created.</li>
 *   <li><b>Area Check:</b> If the 3 corners are in a straight line, the area is 0 (a flat line). If the area is greater than 0, a real triangle is born!</li>
 * </ol>
 */
public class GeometryService {

    /**
     * Minimum triangle area required (0.000001) to make sure the triangle has real thickness and isn't flat.
     */
    private static final double MIN_AREA = 1e-6;

    /**
     * Microscopic margin of error (0.000001) used to see if two corner dots are practically sitting in the same spot.
     */
    private static final double POINT_TOLERANCE = 1e-6;

    /**
     * Creates a new geometry calculation service.
     */
    public GeometryService() {
        // Default constructor
    }

    /**
     * Builds a real triangle from 3 typed equation strings (e.g. {@code "x + y = 8"}, {@code "x - y = 2"}, {@code "x = 1"}).
     *
     * @param eq1 the text of the first line equation
     * @param eq2 the text of the second line equation
     * @param eq3 the text of the third line equation
     * @return a verified, non-flat {@link Triangle}
     * @throws ParseException    if an equation has spelling mistakes, invalid characters, or missing equals signs
     * @throws GeometryException if the lines are parallel, identical, cross at one point, or form a flat triangle
     */
    public Triangle buildTriangle(String eq1, String eq2, String eq3) throws ParseException, GeometryException {
        Line l1 = EquationParser.parse(eq1);
        Line l2 = EquationParser.parse(eq2);
        Line l3 = EquationParser.parse(eq3);

        return buildTriangle(l1, l2, l3);
    }

    /**
     * Builds a verified {@link Triangle} from a matrix system:
     * <pre>
     *   [ A (3x2) ] · [ x (2x1) ] = [ B (3x1) ]
     * </pre>
     * where:
     * <pre>
     *   [ A[0][0]  A[0][1] ] [ x ]   [ b[0] ]
     *   [ A[1][0]  A[1][1] ] [ y ] = [ b[1] ]
     *   [ A[2][0]  A[2][1] ]         [ b[2] ]
     * </pre>
     *
     * @param A a 3-row by 2-column grid of numbers containing the multipliers for x and y
     * @param b a 3-number column containing the target numbers on the right side of the equals sign
     * @return a verified, non-flat {@link Triangle}
     * @throws GeometryException if the matrix does not have exactly 3 rows and 2 columns,
     *                           if b does not have 3 numbers, or if the lines fail geometric checks
     */
    public Triangle buildTriangle(double[][] A, double[] b) throws GeometryException {
        Line[] lines = extractLines(A, b);
        return buildTriangle(lines[0], lines[1], lines[2]);
    }

    /**
     * Takes the numbers in a 3&times;2 matrix A and 3&times;1 vector b and turns each row into a straight {@link Line}.
     * <p>
     * Row 1 becomes {@code A[0][0]*x + A[0][1]*y = b[0]}.<br>
     * Row 2 becomes {@code A[1][0]*x + A[1][1]*y = b[1]}.<br>
     * Row 3 becomes {@code A[2][0]*x + A[2][1]*y = b[2]}.
     * </p>
     *
     * @param A the 3x2 matrix of multipliers
     * @param b the 3x1 column of target numbers
     * @return an array containing the 3 corresponding {@link Line} objects
     * @throws GeometryException if dimensions are wrong or any row has both x and y multipliers set to zero
     */
    public Line[] extractLines(double[][] A, double[] b) throws GeometryException {
        if (A == null || A.length != 3) {
            throw new GeometryException("Matrix A must have fixed dimensions 3x2 (exactly 3 rows).");
        }
        for (int i = 0; i < 3; i++) {
            if (A[i] == null || A[i].length != 2) {
                throw new GeometryException("Row " + (i + 1) + " of matrix A must contain exactly 2 columns (one for x and one for y).");
            }
        }
        if (b == null || b.length != 3) {
            throw new GeometryException("Vector b must have fixed dimension 3x1 (exactly 3 numbers).");
        }

        Line[] lines = new Line[3];
        for (int i = 0; i < 3; i++) {
            try {
                lines[i] = new Line(A[i][0], A[i][1], b[i]);
            } catch (IllegalArgumentException e) {
                throw new GeometryException("Row " + (i + 1) + " coefficients cannot both be zero (at least x or y must exist).");
            }
        }
        return lines;
    }

    /**
     * Finds where three lines cross each other and confirms they make a valid triangle.
     *
     * @param l1 the first boundary line
     * @param l2 the second boundary line
     * @param l3 the third boundary line
     * @return the resulting verified {@link Triangle}
     * @throws GeometryException if any pair of lines is parallel, identical, concurrent at a single dot,
     *                           or if the enclosed area is practically zero
     */
    public Triangle buildTriangle(Line l1, Line l2, Line l3) throws GeometryException {
        // Step 1: Check every pair to make sure none run parallel or sit on top of each other
        checkPair(l1, l2, 1, 2);
        checkPair(l2, l3, 2, 3);
        checkPair(l3, l1, 3, 1);

        // Step 2: Calculate where each pair meets (the 3 corners)
        Point p12 = l1.intersectionWith(l2)
                .orElseThrow(() -> new GeometryException("Line 1 and Line 2 do not cross at a single unique point."));
        Point p23 = l2.intersectionWith(l3)
                .orElseThrow(() -> new GeometryException("Line 2 and Line 3 do not cross at a single unique point."));
        Point p31 = l3.intersectionWith(l1)
                .orElseThrow(() -> new GeometryException("Line 3 and Line 1 do not cross at a single unique point."));

        // Step 3: Check if all 3 corners are the exact same point (concurrency)
        if (p12.equalsWithTolerance(p23, POINT_TOLERANCE) ||
            p23.equalsWithTolerance(p31, POINT_TOLERANCE) ||
            p31.equalsWithTolerance(p12, POINT_TOLERANCE)) {
            throw new GeometryException("The three lines are concurrent (they all pass through the exact same pinpoint), so no open triangle can form.");
        }

        Triangle triangle = new Triangle(p12, p23, p31, l1, l2, l3);

        // Step 4: Ensure the triangle actually opens up and has real inside space (area)
        if (triangle.getArea() < MIN_AREA) {
            throw new GeometryException(String.format(
                    "The lines cross in a nearly flat row, forming a collapsed triangle with near-zero area (%.6f).",
                    triangle.getArea()
            ));
        }

        return triangle;
    }

    /**
     * Safety check to verify that two lines are neither identical nor parallel.
     *
     * @param la   the first line
     * @param lb   the second line
     * @param idxA line number of the first line (for human-friendly error messages)
     * @param idxB line number of the second line (for human-friendly error messages)
     * @throws GeometryException if the lines are identical or parallel
     */
    private void checkPair(Line la, Line lb, int idxA, int idxB) throws GeometryException {
        if (la.isCoincidentWith(lb)) {
            throw new GeometryException(String.format(
                    "Line %d ('%s') and Line %d ('%s') are identical (drawn right on top of each other).",
                    idxA, la.getRawEquation(), idxB, lb.getRawEquation()
            ));
        }
        if (la.isParallelTo(lb)) {
            throw new GeometryException(String.format(
                    "Line %d ('%s') and Line %d ('%s') run parallel (like train tracks) and never cross.",
                    idxA, la.getRawEquation(), idxB, lb.getRawEquation()
            ));
        }
    }
}
