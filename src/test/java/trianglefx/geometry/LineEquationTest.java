package trianglefx.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineEquationTest {

    @Test
    void constructorAllowsValidLine() {
        assertDoesNotThrow(() -> new LineEquation(1, -2, 3));
        assertDoesNotThrow(() -> new LineEquation(0, 5, 10));
        assertDoesNotThrow(() -> new LineEquation(4, 0, -2));
    }

    @Test
    void constructorRejectsWhenAAndBAreZero() {
        assertThrows(IllegalArgumentException.class, () -> new LineEquation(0, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new LineEquation(1e-12, -1e-12, 0));
    }
}
