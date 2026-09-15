package com.trianglefx.ui;

import com.trianglefx.geometry.GeometryException;
import com.trianglefx.geometry.GeometryService;
import com.trianglefx.model.Triangle;
import com.trianglefx.parser.ParseException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TriangleApp extends Application {

    private final GeometryService geometryService = new GeometryService();

    private TextField eq1Field;
    private TextField eq2Field;
    private TextField eq3Field;
    private Label statusLabel;
    private VBox infoCard;
    private Label vertexALabel;
    private Label vertexBLabel;
    private Label vertexCLabel;
    private Label sidesLabel;
    private Label areaLabel;
    private Label perimeterLabel;
    private TriangleCanvas triangleCanvas;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TriangleFX — JavaFX Triangle Drawer");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0B0F19;");

        // Left sidebar for controls and info
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Center area for the canvas
        triangleCanvas = new TriangleCanvas();
        StackPane canvasContainer = new StackPane(triangleCanvas);
        canvasContainer.setPadding(new Insets(16));
        canvasContainer.setStyle("-fx-background-color: #0F172A; -fx-border-color: #1E293B; -fx-border-width: 1px;");
        root.setCenter(canvasContainer);

        // Load default example
        loadPreset("x + y = 8", "x - y = 2", "x = 1");
        handleDraw();

        Scene scene = new Scene(root, 1050, 700);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.setPrefWidth(360);
        sidebar.setPadding(new Insets(24));
        sidebar.setStyle("-fx-background-color: #111827; -fx-border-color: #1F2937; -fx-border-width: 0 1px 0 0;");

        // App Title
        Label titleLabel = new Label("📐 TriangleFX");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");

        Label subtitle = new Label("Draw triangles from 3 linear equations");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #9CA3AF;");

        VBox titleBox = new VBox(4, titleLabel, subtitle);

        // Equation input fields
        Label inputHeader = new Label("LINE EQUATIONS");
        inputHeader.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6B7280; -fx-letter-spacing: 1px;");

        eq1Field = createEquationField("Line 1 (e.g. x + y = 8)");
        eq2Field = createEquationField("Line 2 (e.g. x - y = 2)");
        eq3Field = createEquationField("Line 3 (e.g. x = 1)");

        VBox inputsBox = new VBox(10,
                inputHeader,
                createFieldGroup("Line 1", eq1Field, "#F43F5E"),
                createFieldGroup("Line 2", eq2Field, "#10B981"),
                createFieldGroup("Line 3", eq3Field, "#F59E0B")
        );

        // Action buttons
        Button drawBtn = new Button("Draw Triangle");
        drawBtn.setMaxWidth(Double.MAX_VALUE);
        drawBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
        drawBtn.setOnAction(e -> handleDraw());

        Button clearBtn = new Button("Clear");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setStyle("-fx-background-color: #374151; -fx-text-fill: #E5E7EB; -fx-font-size: 13px; -fx-padding: 8px; -fx-background-radius: 6px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> handleClear());

        HBox btnBox = new HBox(8, drawBtn, clearBtn);
        HBox.setHgrow(drawBtn, Priority.ALWAYS);

        // Presets selector
        Label presetHeader = new Label("EXAMPLES / PRESETS");
        presetHeader.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6B7280;");

        ComboBox<String> presetCombo = new ComboBox<>();
        presetCombo.setMaxWidth(Double.MAX_VALUE);
        presetCombo.getItems().addAll(
                "Default Example (x+y=8, x-y=2, x=1)",
                "Right Triangle (y = 1, x = 2, y = -x + 6)",
                "Equilateral-like (y = 0, y = 1.732x, y = -1.732x + 6.928)",
                "General Oblique (2x - y = 4, x + 2y = 8, 3x - 4y = -12)"
        );
        presetCombo.setPromptText("Choose sample preset...");
        presetCombo.setStyle("-fx-background-color: #1F2937; -fx-mark-color: #9CA3AF;");
        presetCombo.setOnAction(e -> {
            int idx = presetCombo.getSelectionModel().getSelectedIndex();
            if (idx == 0) loadPreset("x + y = 8", "x - y = 2", "x = 1");
            else if (idx == 1) loadPreset("y = 1", "x = 2", "y = -x + 6");
            else if (idx == 2) loadPreset("y = 0", "y = 1.732x", "y = -1.732x + 6.928");
            else if (idx == 3) loadPreset("2x - y = 4", "x + 2y = 8", "3x - 4y = -12");
            handleDraw();
        });

        // Status & feedback label
        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-padding: 10px; -fx-background-radius: 6px;");
        statusLabel.setVisible(false);
        statusLabel.managedProperty().bind(statusLabel.visibleProperty());

        // Triangle Details card
        infoCard = createInfoCard();

        sidebar.getChildren().addAll(
                titleBox,
                new Separator(),
                inputsBox,
                btnBox,
                new VBox(6, presetHeader, presetCombo),
                statusLabel,
                infoCard
        );

        ScrollPane scrollPane = new ScrollPane(sidebar);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #111827; -fx-background-color: #111827; -fx-border-color: transparent;");

        VBox container = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return container;
    }

    private VBox createFieldGroup(String label, TextField field, String indicatorColor) {
        Label lbl = new Label(label);
        lbl.setStyle(String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-font-size: 12px;", indicatorColor));

        VBox group = new VBox(4, lbl, field);
        return group;
    }

    private TextField createEquationField(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB; -fx-prompt-text-fill: #6B7280; " +
                "-fx-border-color: #374151; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 8px;");
        field.setOnAction(e -> handleDraw());
        return field;
    }

    private VBox createInfoCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #1F2937; -fx-background-radius: 8px; -fx-border-color: #374151; -fx-border-radius: 8px;");

        Label header = new Label("PROPERTIES");
        header.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

        vertexALabel = createPropertyLabel("Vertex A: —");
        vertexBLabel = createPropertyLabel("Vertex B: —");
        vertexCLabel = createPropertyLabel("Vertex C: —");
        sidesLabel = createPropertyLabel("Sides: —");
        areaLabel = createPropertyLabel("Area: —");
        perimeterLabel = createPropertyLabel("Perimeter: —");

        card.getChildren().addAll(header, vertexALabel, vertexBLabel, vertexCLabel, new Separator(), sidesLabel, areaLabel, perimeterLabel);
        card.setVisible(false);
        card.managedProperty().bind(card.visibleProperty());
        return card;
    }

    private Label createPropertyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #E5E7EB; -fx-font-size: 12px; -fx-font-family: 'Monospaced';");
        return lbl;
    }

    private void loadPreset(String eq1, String eq2, String eq3) {
        eq1Field.setText(eq1);
        eq2Field.setText(eq2);
        eq3Field.setText(eq3);
    }

    private void handleDraw() {
        String eq1 = eq1Field.getText();
        String eq2 = eq2Field.getText();
        String eq3 = eq3Field.getText();

        if (eq1 == null || eq1.isBlank() ||
            eq2 == null || eq2.isBlank() ||
            eq3 == null || eq3.isBlank()) {
            showError("Please enter all three linear equations.");
            triangleCanvas.clear();
            infoCard.setVisible(false);
            return;
        }

        try {
            Triangle triangle = geometryService.buildTriangle(eq1, eq2, eq3);
            triangleCanvas.setTriangle(triangle);
            showSuccess("Triangle rendered successfully!");
            updateInfoCard(triangle);
        } catch (ParseException e) {
            showError("Equation Parse Error: " + e.getMessage());
            triangleCanvas.clear();
            infoCard.setVisible(false);
        } catch (GeometryException e) {
            showError("Geometry Error: " + e.getMessage());
            triangleCanvas.clear();
            infoCard.setVisible(false);
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            triangleCanvas.clear();
            infoCard.setVisible(false);
        }
    }

    private void handleClear() {
        eq1Field.clear();
        eq2Field.clear();
        eq3Field.clear();
        triangleCanvas.clear();
        statusLabel.setVisible(false);
        infoCard.setVisible(false);
    }

    private void updateInfoCard(Triangle t) {
        vertexALabel.setText(String.format("Vertex A: (%.2f, %.2f)", t.getP1().getX(), t.getP1().getY()));
        vertexBLabel.setText(String.format("Vertex B: (%.2f, %.2f)", t.getP2().getX(), t.getP2().getY()));
        vertexCLabel.setText(String.format("Vertex C: (%.2f, %.2f)", t.getP3().getX(), t.getP3().getY()));

        sidesLabel.setText(String.format("Sides: a=%.2f, b=%.2f, c=%.2f",
                t.getSideA(), t.getSideB(), t.getSideC()));
        areaLabel.setText(String.format("Area: %.4f", t.getArea()));
        perimeterLabel.setText(String.format("Perimeter: %.4f", t.getPerimeter()));

        infoCard.setVisible(true);
    }

    private void showSuccess(String message) {
        statusLabel.setText("✓ " + message);
        statusLabel.setStyle("-fx-text-fill: #34D399; -fx-background-color: #064E3B; -fx-padding: 10px; -fx-background-radius: 6px;");
        statusLabel.setVisible(true);
    }

    private void showError(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.setStyle("-fx-text-fill: #F87171; -fx-background-color: #450A0A; -fx-padding: 10px; -fx-background-radius: 6px;");
        statusLabel.setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
