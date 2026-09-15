package com.trianglefx.ui;

import com.trianglefx.math.EquationParser;
import com.trianglefx.math.Line;
import com.trianglefx.math.Point;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * The main user interface view for the TriangleFX application.
 * Manages the text fields for equations, the draw button, the error label, and the canvas.
 */
public class MainView extends VBox {
    
    private TextField eq1Field;
    private TextField eq2Field;
    private TextField eq3Field;
    private Label errorLabel;
    private Canvas canvas;

    /**
     * Constructs the MainView, initializing and arranging all UI components.
     */
    public MainView() {
        setPadding(new Insets(15));
        setSpacing(10);
        
        Label title = new Label("Enter Three Line Equations (e.g., x + y = 8, 2x - 3y = 10, x = 1)");
        
        eq1Field = new TextField();
        eq1Field.setPromptText("Equation 1");
        
        eq2Field = new TextField();
        eq2Field.setPromptText("Equation 2");
        
        eq3Field = new TextField();
        eq3Field.setPromptText("Equation 3");
        
        Button drawButton = new Button("Draw Triangle");
        drawButton.setOnAction(e -> handleDraw());
        
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        
        canvas = new Canvas(600, 400);
        
        getChildren().addAll(title, eq1Field, eq2Field, eq3Field, drawButton, errorLabel, canvas);
    }
    
    /**
     * Handles the "Draw Triangle" button click event.
     * Parses the equations, calculates intersections, verifies collinearity, and triggers the drawing logic.
     */
    private void handleDraw() {
        errorLabel.setText("");
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        try {
            Line l1 = EquationParser.parse(eq1Field.getText());
            Line l2 = EquationParser.parse(eq2Field.getText());
            Line l3 = EquationParser.parse(eq3Field.getText());
            
            // If lines are parallel, getIntersection throws ArithmeticException
            Point p1 = l1.getIntersection(l2);
            Point p2 = l2.getIntersection(l3);
            Point p3 = l1.getIntersection(l3);
            
            // Collinearity check (Area = 0)
            double area = 0.5 * Math.abs(p1.x() * (p2.y() - p3.y()) + p2.x() * (p3.y() - p1.y()) + p3.x() * (p1.y() - p2.y()));
            if (area < 1e-9) {
                throw new ArithmeticException("Lines intersect at a single point or are collinear (Area is 0).");
            }
            
            TriangleDrawer.draw(canvas, p1, p2, p3);
            
        } catch (IllegalArgumentException | ArithmeticException ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("Unexpected Error: " + ex.getMessage());
        }
    }
}
