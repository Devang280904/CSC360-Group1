package com.trianglefx.ui;

import com.trianglefx.model.Line;
import com.trianglefx.model.Point;
import com.trianglefx.model.Triangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The digital artist and canvas display of the application.
 *
 * HOW THIS CANVAS WORKS (0 to 100 Guide):
 * - Coordinate Mapping: In math on graph paper, (0, 0) is in the center and y goes UP.
 *   On a computer screen, pixel (0, 0) is at the top-left corner and pixel y goes DOWN.
 *   This class automatically converts mathematical coordinates into pixel coordinates on your monitor.
 * - Auto-Centering and Zooming: It calculates the size of the triangle and automatically scales
 *   it up or down with comfortable margins so it fits perfectly on your screen without distortion.
 * - Background Grid and Axes: Draws a subtle slate coordinate grid and labels the X and Y axes.
 * - Extended Dashed Lines: Draws the 3 infinite boundary lines in distinct colors (Rose, Emerald, Amber)
 *   so you can clearly see where the lines come from and how they cross.
 * - Triangle Shape: Fills the interior with a glowing semi-transparent Indigo polygon and outlines the edges.
 * - Corner Badges: Marks the corners (A, B, C) with glowing cyan dots and labels showing exact coordinates.
 */
public class TriangleCanvas extends Pane {

    /** Internal JavaFX Canvas drawing surface. */
    private final Canvas canvas = new Canvas();

    /** The current triangle being displayed, or null if empty. */
    private Triangle currentTriangle = null;

    /** Background fill color: Deep Slate 900 (#0F172A). */
    private static final Color BG_COLOR = Color.web("#0F172A");
    /** Background grid lines color: Slate 800 (#1E293B). */
    private static final Color GRID_COLOR = Color.web("#1E293B");
    /** Coordinate axes (X and Y) color: Slate 700 (#334155). */
    private static final Color AXIS_COLOR = Color.web("#334155");
    /** Inside triangle shaded fill color: Indigo with transparency (#6366F1, 25% opacity). */
    private static final Color TRIANGLE_FILL = Color.web("#6366F1", 0.25);
    /** Triangle outer border color: Bright Indigo light (#818CF8). */
    private static final Color TRIANGLE_STROKE = Color.web("#818CF8");
    /** Line 1 extended boundary color: Rose (#F43F5E). */
    private static final Color EXTENDED_LINE_1 = Color.web("#F43F5E", 0.45);
    /** Line 2 extended boundary color: Emerald (#10B981). */
    private static final Color EXTENDED_LINE_2 = Color.web("#10B981", 0.45);
    /** Line 3 extended boundary color: Amber (#F59E0B). */
    private static final Color EXTENDED_LINE_3 = Color.web("#F59E0B", 0.45);
    /** Corner dots and indicator color: Sky Blue (#38BDF8). */
    private static final Color VERTEX_COLOR = Color.web("#38BDF8");
    /** Main text color: Slate 50 (#F8FAFC). */
    private static final Color TEXT_COLOR = Color.web("#F8FAFC");

    /**
     * Creates a new canvas and binds its width and height to auto-resize with the window.
     */
    public TriangleCanvas() {
        getChildren().add(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // Whenever user resizes the window, repaint the canvas automatically
        widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        heightProperty().addListener((obs, oldVal, newVal) -> redraw());
    }

    /**
     * Hands a new triangle to the canvas and paints it immediately.
     *
     * @param triangle the verified {@link Triangle} to draw
     */
    public void setTriangle(Triangle triangle) {
        this.currentTriangle = triangle;
        redraw();
    }

    /**
     * Wipes the canvas clean and shows the initial helper instructions.
     */
    public void clear() {
        this.currentTriangle = null;
        redraw();
    }

    /**
     * Master repaint method that clears the screen and decides whether to draw the placeholder or triangle.
     */
    private void redraw() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, w, h);

        if (currentTriangle == null) {
            drawPlaceholder(gc, w, h);
            return;
        }

        drawTriangleScene(gc, w, h, currentTriangle);
    }

    /**
     * Draws an empty grid and a friendly prompt when no triangle has been generated yet.
     *
     * @param gc 2D drawing paintbrush
     * @param w  screen width in pixels
     * @param h  screen height in pixels
     */
    private void drawPlaceholder(GraphicsContext gc, double w, double h) {
        drawGrid(gc, w, h, 40);

        gc.setFill(Color.web("#64748B"));
        gc.setFont(Font.font("System", FontWeight.NORMAL, 14));
        String message = "Enter matrix coefficients and click 'Generate Diagram'";
        double textWidth = 370;
        gc.fillText(message, Math.max(20, (w - textWidth) / 2), h / 2);
    }

    /**
     * Draws background graph paper grid lines across the canvas.
     *
     * @param gc   2D drawing paintbrush
     * @param w    screen width in pixels
     * @param h    screen height in pixels
     * @param step distance between grid lines in pixels
     */
    private void drawGrid(GraphicsContext gc, double w, double h, double step) {
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(1.0);
        for (double x = 0; x < w; x += step) {
            gc.strokeLine(x, 0, x, h);
        }
        for (double y = 0; y < h; y += step) {
            gc.strokeLine(0, y, w, y);
        }
    }

    /**
     * The core drawing pipeline: computes scale, centers view, draws axes, lines, fill, and corner badges.
     *
     * @param gc       2D drawing paintbrush
     * @param w        screen width in pixels
     * @param h        screen height in pixels
     * @param triangle the triangle object to render
     */
    private void drawTriangleScene(GraphicsContext gc, double w, double h, Triangle triangle) {
        Point p1 = triangle.getP1();
        Point p2 = triangle.getP2();
        Point p3 = triangle.getP3();

        double minX = Math.min(p1.getX(), Math.min(p2.getX(), p3.getX()));
        double maxX = Math.max(p1.getX(), Math.max(p2.getX(), p3.getX()));
        double minY = Math.min(p1.getY(), Math.min(p2.getY(), p3.getY()));
        double maxY = Math.max(p1.getY(), Math.max(p2.getY(), p3.getY()));

        double spanX = Math.max(maxX - minX, 1.0);
        double spanY = Math.max(maxY - minY, 1.0);

        // Add 70% extra margin around triangle so it has breathing room and labels don't get cut off
        double viewSpanX = spanX * 1.7;
        double viewSpanY = spanY * 1.7;

        double midX = (minX + maxX) / 2.0;
        double midY = (minY + maxY) / 2.0;

        double scale = Math.min((w - 80) / viewSpanX, (h - 80) / viewSpanY);

        Transform tx = new Transform(w, h, midX, midY, scale);

        // 1. Draw coordinate axes (X and Y)
        drawAxes(gc, w, h, tx);

        // 2. Draw extended dashed lines for each equation
        drawLine(gc, triangle.getL1(), tx, EXTENDED_LINE_1, w, h);
        drawLine(gc, triangle.getL2(), tx, EXTENDED_LINE_2, w, h);
        drawLine(gc, triangle.getL3(), tx, EXTENDED_LINE_3, w, h);

        // 3. Convert math coordinates to screen pixels
        double c1x = tx.toScreenX(p1.getX());
        double c1y = tx.toScreenY(p1.getY());
        double c2x = tx.toScreenX(p2.getX());
        double c2y = tx.toScreenY(p2.getY());
        double c3x = tx.toScreenX(p3.getX());
        double c3y = tx.toScreenY(p3.getY());

        // 4. Draw filled triangle polygon
        double[] xPoints = {c1x, c2x, c3x};
        double[] yPoints = {c1y, c2y, c3y};
        gc.setFill(TRIANGLE_FILL);
        gc.fillPolygon(xPoints, yPoints, 3);

        // 5. Draw triangle outer perimeter border
        gc.setStroke(TRIANGLE_STROKE);
        gc.setLineWidth(2.5);
        gc.strokePolygon(xPoints, yPoints, 3);

        // 6. Draw glowing corner dots with text labels pointing away from center
        Point centroid = triangle.getCentroid();
        double scCentroidX = tx.toScreenX(centroid.getX());
        double scCentroidY = tx.toScreenY(centroid.getY());

        drawVertex(gc, c1x, c1y, "A " + p1, scCentroidX, scCentroidY);
        drawVertex(gc, c2x, c2y, "B " + p2, scCentroidX, scCentroidY);
        drawVertex(gc, c3x, c3y, "C " + p3, scCentroidX, scCentroidY);
    }

    /**
     * Draws the X and Y coordinate lines on the screen if they fit inside the viewing window.
     *
     * @param gc 2D drawing paintbrush
     * @param w  screen width in pixels
     * @param h  screen height in pixels
     * @param tx coordinate translator
     */
    private void drawAxes(GraphicsContext gc, double w, double h, Transform tx) {
        double screenYAxis = tx.toScreenX(0); // vertical line x = 0
        double screenXAxis = tx.toScreenY(0); // horizontal line y = 0

        gc.setStroke(AXIS_COLOR);
        gc.setLineWidth(1.2);

        // Vertical Y-axis
        if (screenYAxis >= 0 && screenYAxis <= w) {
            gc.strokeLine(screenYAxis, 0, screenYAxis, h);
            gc.setFill(Color.web("#94A3B8"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.fillText("Y", screenYAxis + 5, 15);
        }

        // Horizontal X-axis
        if (screenXAxis >= 0 && screenXAxis <= h) {
            gc.strokeLine(0, screenXAxis, w, screenXAxis);
            gc.setFill(Color.web("#94A3B8"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.fillText("X", w - 15, screenXAxis - 5);
        }
    }

    /**
     * Draws an infinite dashed line extending completely across the screen.
     *
     * @param gc    2D drawing paintbrush
     * @param line  the line equation to draw
     * @param tx    coordinate translator
     * @param color the dashed line color
     * @param w     screen width in pixels
     * @param h     screen height in pixels
     */
    private void drawLine(GraphicsContext gc, Line line, Transform tx, Color color, double w, double h) {
        gc.save();
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.setLineDashes(6.0, 4.0);

        double a = line.getA();
        double b = line.getB();
        double c = line.getC();

        double mathMinX = tx.toMathX(0);
        double mathMaxX = tx.toMathX(w);

        if (Math.abs(b) < 1e-9) {
            // Pure vertical line: x = c / a
            double x = c / a;
            double sx = tx.toScreenX(x);
            gc.strokeLine(sx, 0, sx, h);
        } else if (Math.abs(a) < 1e-9) {
            // Pure horizontal line: y = c / b
            double y = c / b;
            double sy = tx.toScreenY(y);
            gc.strokeLine(0, sy, w, sy);
        } else {
            // General sloping line: y = (c - a*x) / b
            double y1 = (c - a * mathMinX) / b;
            double y2 = (c - a * mathMaxX) / b;
            gc.strokeLine(tx.toScreenX(mathMinX), tx.toScreenY(y1), tx.toScreenX(mathMaxX), tx.toScreenY(y2));
        }
        gc.restore();
    }

    /**
     * Draws a glowing corner dot and a text bubble showing the coordinates.
     *
     * @param gc    2D drawing paintbrush
     * @param vx    pixel X of the corner
     * @param vy    pixel Y of the corner
     * @param label text to write (e.g. "A (5.00, 3.00)")
     * @param cx    pixel X of the triangle center
     * @param cy    pixel Y of the triangle center
     */
    private void drawVertex(GraphicsContext gc, double vx, double vy, String label, double cx, double cy) {
        // Draw double dot (white outer glow + cyan center dot)
        gc.setFill(Color.WHITE);
        gc.fillOval(vx - 6, vy - 6, 12, 12);
        gc.setFill(VERTEX_COLOR);
        gc.fillOval(vx - 4, vy - 4, 8, 8);

        // Push text label slightly outwards from the center of the triangle so it doesn't cover the shape
        double dx = vx - cx;
        double dy = vy - cy;
        double dist = Math.hypot(dx, dy);
        if (dist < 1e-5) {
            dx = 1;
            dy = 1;
            dist = 1.414;
        }
        double offsetX = (dx / dist) * 18.0;
        double offsetY = (dy / dist) * 18.0;

        // Draw pill bubble background for text readability
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        double textWidth = label.length() * 7.5;
        double rectX = vx + offsetX - 5;
        double rectY = vy + offsetY - 14;

        gc.setFill(Color.web("#1E293B", 0.85));
        gc.fillRoundRect(rectX, rectY, textWidth + 10, 20, 6, 6);

        gc.setStroke(Color.web("#475569"));
        gc.setLineWidth(1.0);
        gc.strokeRoundRect(rectX, rectY, textWidth + 10, 20, 6, 6);

        gc.setFill(TEXT_COLOR);
        gc.fillText(label, rectX + 5, rectY + 14);
    }

    /**
     * Mathematical helper that translates back and forth between graph coordinates and monitor pixels.
     */
    private static class Transform {
        /** Canvas width in pixels. */
        final double w;
        /** Canvas height in pixels. */
        final double h;
        /** Math X coordinate sitting at the visual center. */
        final double midX;
        /** Math Y coordinate sitting at the visual center. */
        final double midY;
        /** Zoom factor: how many pixels equal 1 graph unit. */
        final double scale;

        /**
         * Creates a new coordinate translator.
         *
         * @param w     canvas width in pixels
         * @param h     canvas height in pixels
         * @param midX  center math X
         * @param midY  center math Y
         * @param scale pixel zoom factor
         */
        Transform(double w, double h, double midX, double midY, double scale) {
            this.w = w;
            this.h = h;
            this.midX = midX;
            this.midY = midY;
            this.scale = scale;
        }

        /**
         * Converts math X to pixel X.
         *
         * @param mathX mathematical horizontal number
         * @return pixel horizontal coordinate
         */
        double toScreenX(double mathX) {
            return (w / 2.0) + (mathX - midX) * scale;
        }

        /**
         * Converts math Y to pixel Y (flips upside down since screen pixels increase downwards).
         *
         * @param mathY mathematical vertical number
         * @return pixel vertical coordinate
         */
        double toScreenY(double mathY) {
            return (h / 2.0) - (mathY - midY) * scale;
        }

        /**
         * Converts pixel X to math X.
         *
         * @param screenX pixel horizontal coordinate
         * @return mathematical horizontal number
         */
        double toMathX(double screenX) {
            return midX + (screenX - (w / 2.0)) / scale;
        }

        /**
         * Converts pixel Y to math Y.
         *
         * @param screenY pixel vertical coordinate
         * @return mathematical vertical number
         */
        double toMathY(double screenY) {
            return midY - (screenY - (h / 2.0)) / scale;
        }
    }
}
