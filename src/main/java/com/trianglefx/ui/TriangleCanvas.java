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
 * A responsive canvas pane for rendering the triangle, coordinate grid, and line equations.
 */
public class TriangleCanvas extends Pane {

    private final Canvas canvas = new Canvas();
    private Triangle currentTriangle = null;

    private static final Color BG_COLOR = Color.web("#0F172A");        // Slate 900
    private static final Color GRID_COLOR = Color.web("#1E293B");      // Slate 800
    private static final Color AXIS_COLOR = Color.web("#334155");      // Slate 700
    private static final Color TRIANGLE_FILL = Color.web("#6366F1", 0.25); // Indigo with alpha
    private static final Color TRIANGLE_STROKE = Color.web("#818CF8"); // Indigo light
    private static final Color EXTENDED_LINE_1 = Color.web("#F43F5E", 0.45); // Rose
    private static final Color EXTENDED_LINE_2 = Color.web("#10B981", 0.45); // Emerald
    private static final Color EXTENDED_LINE_3 = Color.web("#F59E0B", 0.45); // Amber
    private static final Color VERTEX_COLOR = Color.web("#38BDF8");    // Sky blue
    private static final Color TEXT_COLOR = Color.web("#F8FAFC");      // Slate 50

    public TriangleCanvas() {
        getChildren().add(canvas);
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        heightProperty().addListener((obs, oldVal, newVal) -> redraw());
    }

    public void setTriangle(Triangle triangle) {
        this.currentTriangle = triangle;
        redraw();
    }

    public void clear() {
        this.currentTriangle = null;
        redraw();
    }

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

    private void drawPlaceholder(GraphicsContext gc, double w, double h) {
        // Draw subtle background grid
        drawGrid(gc, w, h, 40);

        gc.setFill(Color.web("#64748B"));
        gc.setFont(Font.font("System", FontWeight.NORMAL, 14));
        String message = "Enter three linear equations and click 'Draw Triangle'";
        double textWidth = 370;
        gc.fillText(message, Math.max(20, (w - textWidth) / 2), h / 2);
    }

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

        // Add 35% margin for comfortable view and labels
        double viewSpanX = spanX * 1.7;
        double viewSpanY = spanY * 1.7;

        double midX = (minX + maxX) / 2.0;
        double midY = (minY + maxY) / 2.0;

        double scale = Math.min((w - 80) / viewSpanX, (h - 80) / viewSpanY);

        // Coordinate transformation helpers
        Transform tx = new Transform(w, h, midX, midY, scale);

        // Draw coordinate axes if they fall within the visible region
        drawAxes(gc, w, h, tx);

        // Draw extended lines for the 3 equations
        drawLine(gc, triangle.getL1(), tx, EXTENDED_LINE_1, w, h);
        drawLine(gc, triangle.getL2(), tx, EXTENDED_LINE_2, w, h);
        drawLine(gc, triangle.getL3(), tx, EXTENDED_LINE_3, w, h);

        // Transform vertices to screen coordinates
        double c1x = tx.toScreenX(p1.getX());
        double c1y = tx.toScreenY(p1.getY());
        double c2x = tx.toScreenX(p2.getX());
        double c2y = tx.toScreenY(p2.getY());
        double c3x = tx.toScreenX(p3.getX());
        double c3y = tx.toScreenY(p3.getY());

        // Draw filled triangle
        double[] xPoints = {c1x, c2x, c3x};
        double[] yPoints = {c1y, c2y, c3y};
        gc.setFill(TRIANGLE_FILL);
        gc.fillPolygon(xPoints, yPoints, 3);

        // Draw triangle perimeter
        gc.setStroke(TRIANGLE_STROKE);
        gc.setLineWidth(2.5);
        gc.strokePolygon(xPoints, yPoints, 3);

        // Draw vertices and coordinates
        Point centroid = triangle.getCentroid();
        double scCentroidX = tx.toScreenX(centroid.getX());
        double scCentroidY = tx.toScreenY(centroid.getY());

        drawVertex(gc, c1x, c1y, "A " + p1, scCentroidX, scCentroidY);
        drawVertex(gc, c2x, c2y, "B " + p2, scCentroidX, scCentroidY);
        drawVertex(gc, c3x, c3y, "C " + p3, scCentroidX, scCentroidY);
    }

    private void drawAxes(GraphicsContext gc, double w, double h, Transform tx) {
        double screenYAxis = tx.toScreenX(0); // line x = 0
        double screenXAxis = tx.toScreenY(0); // line y = 0

        gc.setStroke(AXIS_COLOR);
        gc.setLineWidth(1.2);

        // Y-axis (vertical)
        if (screenYAxis >= 0 && screenYAxis <= w) {
            gc.strokeLine(screenYAxis, 0, screenYAxis, h);
            gc.setFill(Color.web("#94A3B8"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.fillText("Y", screenYAxis + 5, 15);
        }

        // X-axis (horizontal)
        if (screenXAxis >= 0 && screenXAxis <= h) {
            gc.strokeLine(0, screenXAxis, w, screenXAxis);
            gc.setFill(Color.web("#94A3B8"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.fillText("X", w - 15, screenXAxis - 5);
        }
    }

    private void drawLine(GraphicsContext gc, Line line, Transform tx, Color color, double w, double h) {
        gc.save();
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.setLineDashes(6.0, 4.0);

        // Find intersection of line Ax + By = C with canvas viewport edges
        double a = line.getA();
        double b = line.getB();
        double c = line.getC();

        double mathMinX = tx.toMathX(0);
        double mathMaxX = tx.toMathX(w);
        double mathMaxY = tx.toMathY(0);
        double mathMinY = tx.toMathY(h);

        // If almost vertical (b is near 0)
        if (Math.abs(b) < 1e-9) {
            double x = c / a;
            double sx = tx.toScreenX(x);
            gc.strokeLine(sx, 0, sx, h);
        } else if (Math.abs(a) < 1e-9) {
            double y = c / b;
            double sy = tx.toScreenY(y);
            gc.strokeLine(0, sy, w, sy);
        } else {
            // General line: y = (c - a*x) / b
            double y1 = (c - a * mathMinX) / b;
            double y2 = (c - a * mathMaxX) / b;
            gc.strokeLine(tx.toScreenX(mathMinX), tx.toScreenY(y1), tx.toScreenX(mathMaxX), tx.toScreenY(y2));
        }
        gc.restore();
    }

    private void drawVertex(GraphicsContext gc, double vx, double vy, String label, double cx, double cy) {
        // Outer glow/dot
        gc.setFill(Color.WHITE);
        gc.fillOval(vx - 6, vy - 6, 12, 12);
        gc.setFill(VERTEX_COLOR);
        gc.fillOval(vx - 4, vy - 4, 8, 8);

        // Text label positioning outward from centroid
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

        // Label background pill
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

    private static class Transform {
        final double w, h, midX, midY, scale;

        Transform(double w, double h, double midX, double midY, double scale) {
            this.w = w;
            this.h = h;
            this.midX = midX;
            this.midY = midY;
            this.scale = scale;
        }

        double toScreenX(double mathX) {
            return (w / 2.0) + (mathX - midX) * scale;
        }

        double toScreenY(double mathY) {
            return (h / 2.0) - (mathY - midY) * scale;
        }

        double toMathX(double screenX) {
            return midX + (screenX - (w / 2.0)) / scale;
        }

        double toMathY(double screenY) {
            return midY - (screenY - (h / 2.0)) / scale;
        }
    }
}
