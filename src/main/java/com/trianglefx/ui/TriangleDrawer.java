package com.trianglefx.ui;

import com.trianglefx.math.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Utility class responsible for rendering a triangle onto a JavaFX Canvas.
 */
public class TriangleDrawer {
    
    /**
     * Calculates the bounding box of the three intersection points, scales them to fit the Canvas,
     * and draws the triangle lines and vertex coordinates.
     *
     * @param canvas The JavaFX Canvas to draw on.
     * @param p1 The first intersection point.
     * @param p2 The second intersection point.
     * @param p3 The third intersection point.
     */
    public static void draw(Canvas canvas, Point p1, Point p2, Point p3) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        
        // Clear canvas
        gc.clearRect(0, 0, width, height);
        
        // Math bounding box
        double minX = Math.min(p1.x(), Math.min(p2.x(), p3.x()));
        double maxX = Math.max(p1.x(), Math.max(p2.x(), p3.x()));
        double minY = Math.min(p1.y(), Math.min(p2.y(), p3.y()));
        double maxY = Math.max(p1.y(), Math.max(p2.y(), p3.y()));
        
        double mathWidth = maxX - minX;
        double mathHeight = maxY - minY;
        
        // Handle case where triangle is a single line or point
        if (mathWidth == 0) mathWidth = 1;
        if (mathHeight == 0) mathHeight = 1;
        
        // Scale to 80% of canvas
        double scaleX = (width * 0.8) / mathWidth;
        double scaleY = (height * 0.8) / mathHeight;
        double scale = Math.min(scaleX, scaleY);
        
        // Margins to center it. Note: screen Y goes down, math Y goes up.
        double offsetX = (width - mathWidth * scale) / 2.0 - minX * scale;
        double offsetY = (height - mathHeight * scale) / 2.0 + maxY * scale;

        // Screen points
        double px1 = p1.x() * scale + offsetX;
        double py1 = offsetY - p1.y() * scale;
        
        double px2 = p2.x() * scale + offsetX;
        double py2 = offsetY - p2.y() * scale;
        
        double px3 = p3.x() * scale + offsetX;
        double py3 = offsetY - p3.y() * scale;
        
        // Draw lines
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2.0);
        
        gc.strokeLine(px1, py1, px2, py2);
        gc.strokeLine(px2, py2, px3, py3);
        gc.strokeLine(px3, py3, px1, py1);
        
        // Draw vertices and text
        gc.setFill(Color.RED);
        double r = 4.0; // radius
        gc.fillOval(px1 - r, py1 - r, r*2, r*2);
        gc.fillOval(px2 - r, py2 - r, r*2, r*2);
        gc.fillOval(px3 - r, py3 - r, r*2, r*2);
        
        gc.setFill(Color.BLACK);
        gc.fillText(String.format("(%.2f, %.2f)", p1.x(), p1.y()), px1 + 8, py1 - 8);
        gc.fillText(String.format("(%.2f, %.2f)", p2.x(), p2.y()), px2 + 8, py2 - 8);
        gc.fillText(String.format("(%.2f, %.2f)", p3.x(), p3.y()), px3 + 8, py3 - 8);
    }
}
