package trianglefx.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LinearSystem2x2Test {

    private static final double DELTA = 1e-9;

    @Test
    void solvesUniqueSolutionWithScalarInputs() {
        // x + y = 5
        // x - y = 1
        LinearSystem2x2.Result result = LinearSystem2x2.solve(1, 1, 5, 1, -1, 1);

        assertEquals(LinearSystem2x2.Type.UNIQUE_SOLUTION, result.type());
        assertNotNull(result.solution());
        assertEquals(3.0, result.solution().x(), DELTA);
        assertEquals(2.0, result.solution().y(), DELTA);
    }

    @Test
    void solvesUniqueSolutionWithMatrixInputs() {
        double[][] a = {
                {1, 0},
                {0, 1}
        };
        double[] b = {2, -3};

        LinearSystem2x2.Result result = LinearSystem2x2.solve(a, b);

        assertEquals(LinearSystem2x2.Type.UNIQUE_SOLUTION, result.type());
        assertNotNull(result.solution());
        assertEquals(2.0, result.solution().x(), DELTA);
        assertEquals(-3.0, result.solution().y(), DELTA);
    }

    @Test
    void detectsNoSolutionForParallelInconsistentSystem() {
        // x + y = 5
        // 2x + 2y = 20
        LinearSystem2x2.Result result = LinearSystem2x2.solve(1, 1, 5, 2, 2, 20);

        assertEquals(LinearSystem2x2.Type.NO_SOLUTION, result.type());
        assertNull(result.solution());
    }

    @Test
    void detectsInfiniteSolutionsForCoincidentSystem() {
        // x + y = 5
        // 2x + 2y = 10
        LinearSystem2x2.Result result = LinearSystem2x2.solve(1, 1, 5, 2, 2, 10);

        assertEquals(LinearSystem2x2.Type.INFINITE_SOLUTIONS, result.type());
        assertNull(result.solution());
    }

    @Test
    void solvesFromLineEquations() {
        LineEquation l1 = new LineEquation(1, 1, 5);
        LineEquation l2 = new LineEquation(1, -1, 1);

        LinearSystem2x2.Result result = LinearSystem2x2.fromLines(l1, l2);

        assertEquals(LinearSystem2x2.Type.UNIQUE_SOLUTION, result.type());
        assertNotNull(result.solution());
        assertEquals(3.0, result.solution().x(), DELTA);
        assertEquals(2.0, result.solution().y(), DELTA);
    }

    @Test
    void rejectsInvalidMatrixShapes() {
        assertThrows(IllegalArgumentException.class, () -> LinearSystem2x2.solve(new double[][]{{1, 2, 3}, {4, 5, 6}}, new double[]{1, 2}));
        assertThrows(IllegalArgumentException.class, () -> LinearSystem2x2.solve(new double[][]{{1, 2}}, new double[]{1, 2}));
        assertThrows(IllegalArgumentException.class, () -> LinearSystem2x2.solve(new double[][]{{1, 2}, null}, new double[]{1, 2}));
    }

    @Test
    void rejectsInvalidVectorLength() {
        assertThrows(IllegalArgumentException.class, () -> LinearSystem2x2.solve(new double[][]{{1, 2}, {3, 4}}, new double[]{1}));
    }

    @Test
    void rejectsNullInputs() {
        assertThrows(NullPointerException.class, () -> LinearSystem2x2.solve((double[][]) null, new double[]{1, 2}));
        assertThrows(NullPointerException.class, () -> LinearSystem2x2.solve(new double[][]{{1, 2}, {3, 4}}, null));
        assertThrows(NullPointerException.class, () -> LinearSystem2x2.fromLines(null, new LineEquation(1, 1, 1)));
        assertThrows(NullPointerException.class, () -> LinearSystem2x2.fromLines(new LineEquation(1, 1, 1), null));
    }
}
