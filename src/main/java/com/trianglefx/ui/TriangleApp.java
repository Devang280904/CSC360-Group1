package com.trianglefx.ui;

import com.trianglefx.geometry.GeometryException;
import com.trianglefx.geometry.GeometryService;
import com.trianglefx.model.Line;
import com.trianglefx.model.Triangle;
import com.trianglefx.parser.MatrixParser;
import com.trianglefx.parser.ParseException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The main interactive control room and user interface window for TriangleFX.
 *
 * BEGINNER'S GUIDE (0 to 100):
 *
 * This application is designed so anyone can draw and analyze triangles using a pure
 * Matrix Equation system:
 *
 *   [ A (3x2) ] · [ x (2x1) ] = [ B (3x1) ]
 *
 * WHAT YOU SEE ON THE SCREEN:
 * - Matrix A (3 rows x 2 columns): Six input boxes inside square brackets.
 *   Each row represents one straight line. The two columns are the multipliers for x and y.
 * - Vector x (2 rows x 1 column): The visible column vector [x; y] showing the two unknown coordinates.
 * - Vector B (3 rows x 1 column): Three input boxes inside square brackets representing the target numbers.
 * - "Generate Diagram" Button: When clicked, the computer reads the numbers, decodes the 3 lines,
 *   finds where they cross, checks that a real triangle exists, and draws the diagram on the right-hand canvas.
 * - "Understood Line Equations" Panel: Shows you the standard algebra equations the computer extracted from your matrix.
 * - "Geometric Properties" Card: Displays the exact corner coordinates (A, B, C), side lengths, area,
 *   perimeter, and the augmented matrix [A | B].
 */
public class TriangleApp extends Application {

    /** The mathematical brain service that computes line crossings and verifies triangle geometry. */
    private final GeometryService geometryService = new GeometryService();

    /** 3 rows by 2 columns of input text boxes for Matrix A coefficients. */
    private final TextField[][] aMatrixFields = new TextField[3][2];
    /** 3 rows by 1 column of input text boxes for Vector B target constants. */
    private final TextField[] bVectorFields = new TextField[3];

    /** Optional text box where users can paste raw matrix text or numbers. */
    private TextArea rawMatrixArea;
    /** Expandable container holding the paste area. */
    private VBox rawMatrixBox;

    /** Badge label displaying understood Line 1 equation. */
    private Label eq1UnderstoodLabel;
    /** Badge label displaying understood Line 2 equation. */
    private Label eq2UnderstoodLabel;
    /** Badge label displaying understood Line 3 equation. */
    private Label eq3UnderstoodLabel;

    /** Notification banner displaying success or helpful error messages. */
    private Label statusLabel;
    /** Information card showing geometric measurements. */
    private VBox infoCard;
    /** Label showing corner A coordinates. */
    private Label vertexALabel;
    /** Label showing corner B coordinates. */
    private Label vertexBLabel;
    /** Label showing corner C coordinates. */
    private Label vertexCLabel;
    /** Label showing side lengths a, b, and c. */
    private Label sidesLabel;
    /** Label showing surface area. */
    private Label areaLabel;
    /** Label showing total perimeter. */
    private Label perimeterLabel;
    /** Label showing augmented matrix [A | B]. */
    private Label matrixAugmentedLabel;

    /** The canvas where the triangle and coordinate axes are rendered. */
    private TriangleCanvas triangleCanvas;

    /**
     * Creates a new application window instance.
     */
    public TriangleApp() {
        // Default constructor invoked by JavaFX runtime
    }

    /**
     * Sets up the main window, builds the user interface, loads the default sample, and shows the window.
     *
     * @param primaryStage the main window stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TriangleFX — Matrix System Solver (A · [x; y] = B)");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0B0F19;");

        // Left sidebar for inputs and measurements
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);

        // Center canvas for drawing
        triangleCanvas = new TriangleCanvas();
        StackPane canvasContainer = new StackPane(triangleCanvas);
        canvasContainer.setPadding(new Insets(16));
        canvasContainer.setStyle("-fx-background-color: #0F172A; -fx-border-color: #1E293B; -fx-border-width: 1px;");
        root.setCenter(canvasContainer);

        // Load default example: A = [[1, 1], [1, -1], [1, 0]], B = [8, 2, 1]
        loadMatrixPreset(
                new double[][]{{1, 1}, {1, -1}, {1, 0}},
                new double[]{8, 2, 1}
        );
        handleGenerate();

        Scene scene = new Scene(root, 1120, 750);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(920);
        primaryStage.setMinHeight(640);
        primaryStage.show();
    }

    /**
     * Builds the sidebar containing title, matrix input brackets, action buttons, and readout cards.
     *
     * @return the complete sidebar container {@link VBox}
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPrefWidth(390);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #111827; -fx-border-color: #1F2937; -fx-border-width: 0 1px 0 0;");

        // Title and description
        Label titleLabel = new Label("📐 TriangleFX");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");

        Label subtitle = new Label("Matrix System: A (3x2) · [x; y] = B (3x1)");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #38BDF8; -fx-font-weight: bold;");

        VBox titleBox = new VBox(4, titleLabel, subtitle);

        // Matrix Equation Input Section
        VBox matrixEquationCard = createMatrixEquationCard();

        // Raw matrix paste toggle & box
        VBox pasteSection = createRawPasteSection();

        // Action buttons
        Button generateBtn = new Button("Generate Diagram");
        generateBtn.setMaxWidth(Double.MAX_VALUE);
        generateBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10px; -fx-background-radius: 6px; -fx-cursor: hand;");
        generateBtn.setOnAction(e -> handleGenerate());

        Button clearBtn = new Button("Clear");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setStyle("-fx-background-color: #374151; -fx-text-fill: #E5E7EB; -fx-font-size: 13px; -fx-padding: 8px; -fx-background-radius: 6px; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> handleClear());

        HBox btnBox = new HBox(8, generateBtn, clearBtn);
        HBox.setHgrow(generateBtn, Priority.ALWAYS);

        // Understood Equations Card
        VBox understoodEquationsCard = createUnderstoodEquationsCard();

        // Presets selector
        Label presetHeader = new Label("MATRIX PRESETS");
        presetHeader.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6B7280;");

        ComboBox<String> presetCombo = new ComboBox<>();
        presetCombo.setMaxWidth(Double.MAX_VALUE);
        presetCombo.getItems().addAll(
                "Preset 1: A=[[1, 1], [1, -1], [1, 0]], B=[8, 2, 1]",
                "Preset 2: A=[[0, 1], [1, 0], [1, 1]], B=[1, 2, 6] (Right Triangle)",
                "Preset 3: A=[[0, 1], [1.73, -1], [1.73, 1]], B=[0, 0, 6.93] (Equilateral)",
                "Preset 4: A=[[2, -1], [1, 2], [3, -4]], B=[4, 8, -12] (Oblique)"
        );
        presetCombo.setPromptText("Select a matrix preset...");
        presetCombo.setStyle("-fx-background-color: #1F2937; -fx-mark-color: #9CA3AF;");
        presetCombo.setOnAction(e -> {
            int idx = presetCombo.getSelectionModel().getSelectedIndex();
            if (idx == 0) {
                loadMatrixPreset(new double[][]{{1, 1}, {1, -1}, {1, 0}}, new double[]{8, 2, 1});
            } else if (idx == 1) {
                loadMatrixPreset(new double[][]{{0, 1}, {1, 0}, {1, 1}}, new double[]{1, 2, 6});
            } else if (idx == 2) {
                loadMatrixPreset(new double[][]{{0, 1}, {1.732, -1}, {1.732, 1}}, new double[]{0, 0, 6.928});
            } else if (idx == 3) {
                loadMatrixPreset(new double[][]{{2, -1}, {1, 2}, {3, -4}}, new double[]{4, 8, -12});
            }
            handleGenerate();
        });

        // Status & feedback label
        statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-padding: 10px; -fx-background-radius: 6px;");
        statusLabel.setVisible(false);
        statusLabel.managedProperty().bind(statusLabel.visibleProperty());

        // Triangle Properties card
        infoCard = createInfoCard();

        sidebar.getChildren().addAll(
                titleBox,
                new Separator(),
                matrixEquationCard,
                pasteSection,
                btnBox,
                understoodEquationsCard,
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

    /**
     * Builds the matrix equation card with real square brackets:
     *
     *   [ A (3x2) ] · [ x (2x1) ] = [ B (3x1) ]
     *
     * @return the matrix equation UI card {@link VBox}
     */
    private VBox createMatrixEquationCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: #1F2937; -fx-background-radius: 8px; -fx-border-color: #374151; -fx-border-radius: 8px;");

        Label header = new Label("MATRIX EQUATION INPUT:  A · [x; y] = B");
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

        // 1. Matrix A: 3x2 Grid of coefficients only
        GridPane gridA = new GridPane();
        gridA.setHgap(5);
        gridA.setVgap(6);
        gridA.setAlignment(Pos.CENTER);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 2; c++) {
                TextField cell = createMatrixCell("a" + (r + 1) + (c + 1));
                aMatrixFields[r][c] = cell;
                gridA.add(cell, c, r);
            }
        }
        HBox bracketA = wrapInMatrixBrackets(gridA, "#94A3B8");
        VBox colA = new VBox(4, bracketA, createMatrixSubLabel("A (3×2)"));
        colA.setAlignment(Pos.CENTER);

        // Multiplication dot
        Label dotLabel = new Label("·");
        dotLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 20px; -fx-font-weight: bold;");

        // 2. Vector x: 2x1 Column showing x and y
        Label xLabel = new Label("x");
        xLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        xLabel.setAlignment(Pos.CENTER);
        xLabel.setPrefWidth(20);

        Label yLabel = new Label("y");
        yLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 15px; -fx-font-weight: bold; -fx-font-family: 'Monospaced';");
        yLabel.setAlignment(Pos.CENTER);
        yLabel.setPrefWidth(20);

        VBox xyBox = new VBox(18, xLabel, yLabel);
        xyBox.setAlignment(Pos.CENTER);
        xyBox.setPadding(new Insets(12, 4, 12, 4));

        HBox bracketX = wrapInMatrixBrackets(xyBox, "#38BDF8");
        VBox colX = new VBox(4, bracketX, createMatrixSubLabel("x (2×1)"));
        colX.setAlignment(Pos.CENTER);

        // Equals operator
        Label eqLabel = new Label("=");
        eqLabel.setStyle("-fx-text-fill: #F9FAFB; -fx-font-size: 18px; -fx-font-weight: bold;");

        // 3. Vector B: 3x1 Grid of constants only
        GridPane gridB = new GridPane();
        gridB.setHgap(5);
        gridB.setVgap(6);
        gridB.setAlignment(Pos.CENTER);

        for (int r = 0; r < 3; r++) {
            TextField cell = createMatrixCell("b" + (r + 1));
            bVectorFields[r] = cell;
            gridB.add(cell, 0, r);
        }
        HBox bracketB = wrapInMatrixBrackets(gridB, "#94A3B8");
        VBox colB = new VBox(4, bracketB, createMatrixSubLabel("B (3×1)"));
        colB.setAlignment(Pos.CENTER);

        // Entire matrix equation row
        HBox equationRow = new HBox(8, colA, dotLabel, colX, eqLabel, colB);
        equationRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(header, equationRow);
        return card;
    }

    /**
     * Encloses an inner UI element within visual square brackets `[` and `]`.
     *
     * @param content     the inner UI component (e.g. number grid or column)
     * @param borderColor color of the bracket lines
     * @return an {@link HBox} containing the left bracket, content, and right bracket
     */
    private HBox wrapInMatrixBrackets(Region content, String borderColor) {
        Region leftBracket = new Region();
        leftBracket.setPrefWidth(5);
        leftBracket.setMinWidth(5);
        leftBracket.setMaxWidth(5);
        leftBracket.setStyle(String.format("-fx-border-color: %s; -fx-border-width: 2px 0 2px 2px; -fx-border-radius: 2px;", borderColor));

        Region rightBracket = new Region();
        rightBracket.setPrefWidth(5);
        rightBracket.setMinWidth(5);
        rightBracket.setMaxWidth(5);
        rightBracket.setStyle(String.format("-fx-border-color: %s; -fx-border-width: 2px 2px 2px 0; -fx-border-radius: 2px;", borderColor));

        leftBracket.prefHeightProperty().bind(content.heightProperty());
        rightBracket.prefHeightProperty().bind(content.heightProperty());

        HBox box = new HBox(4, leftBracket, content, rightBracket);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Creates a small label under each matrix showing its dimensions (e.g. "A (3x2)").
     *
     * @param text label text
     * @return styled {@link Label}
     */
    private Label createMatrixSubLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 10px; -fx-font-weight: bold;");
        return lbl;
    }

    /**
     * Creates an optional expandable panel where users can paste raw matrix text.
     *
     * @return the container {@link VBox}
     */
    private VBox createRawPasteSection() {
        Button togglePasteBtn = new Button("Paste Raw Matrix Text ▼");
        togglePasteBtn.setMaxWidth(Double.MAX_VALUE);
        togglePasteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #60A5FA; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 2px;");

        rawMatrixArea = new TextArea();
        rawMatrixArea.setPromptText("Paste 9 numbers or format e.g.:\n1, 1, 8\n1, -1, 2\n1, 0, 1\nor: [[1,1],[1,-1],[1,0]], [8,2,1]");
        rawMatrixArea.setPrefRowCount(3);
        rawMatrixArea.setStyle("-fx-control-inner-background: #111827; -fx-font-family: 'Monospaced'; -fx-font-size: 11px; -fx-text-fill: #E5E7EB;");

        Button loadPasteBtn = new Button("Apply Pasted Values");
        loadPasteBtn.setStyle("-fx-background-color: #374151; -fx-text-fill: #E5E7EB; -fx-font-size: 11px; -fx-padding: 4px 10px; -fx-background-radius: 4px;");
        loadPasteBtn.setOnAction(e -> applyPastedMatrix());

        rawMatrixBox = new VBox(6, rawMatrixArea, loadPasteBtn);
        rawMatrixBox.setVisible(false);
        rawMatrixBox.setManaged(false);

        togglePasteBtn.setOnAction(e -> {
            boolean visible = !rawMatrixBox.isVisible();
            rawMatrixBox.setVisible(visible);
            rawMatrixBox.setManaged(visible);
            togglePasteBtn.setText(visible ? "Hide Paste Area ▲" : "Paste Raw Matrix Text ▼");
        });

        return new VBox(4, togglePasteBtn, rawMatrixBox);
    }

    /**
     * Reads text pasted into the raw text box, validates the 9 numbers, and fills the matrix grid.
     */
    private void applyPastedMatrix() {
        String text = rawMatrixArea.getText();
        try {
            MatrixParser.ParsedMatrix pm = MatrixParser.parse(text);
            loadMatrixPreset(pm.getA(), pm.getB());
            showSuccess("Parsed 3x2 Matrix A and 3x1 Vector B into grid!");
            handleGenerate();
        } catch (ParseException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Creates the card displaying the three standard line equations decoded from the matrix.
     *
     * @return the container {@link VBox}
     */
    private VBox createUnderstoodEquationsCard() {
        VBox card = new VBox(6);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #1E293B; -fx-background-radius: 6px; -fx-border-color: #334155; -fx-border-radius: 6px;");

        Label header = new Label("UNDERSTOOD LINE EQUATIONS");
        header.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        eq1UnderstoodLabel = createEquationBadge("Line 1: —", "#F43F5E");
        eq2UnderstoodLabel = createEquationBadge("Line 2: —", "#10B981");
        eq3UnderstoodLabel = createEquationBadge("Line 3: —", "#F59E0B");

        card.getChildren().addAll(header, eq1UnderstoodLabel, eq2UnderstoodLabel, eq3UnderstoodLabel);
        return card;
    }

    /**
     * Creates a styled text badge for displaying a line equation.
     *
     * @param text  the equation text
     * @param color the accent color
     * @return styled {@link Label}
     */
    private Label createEquationBadge(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle(String.format("-fx-text-fill: %s; -fx-font-family: 'Monospaced'; -fx-font-size: 12px; -fx-font-weight: bold;", color));
        return lbl;
    }

    /**
     * Creates a styled single-number input field for matrix elements.
     *
     * @param placeholder placeholder text
     * @return styled {@link TextField}
     */
    private TextField createMatrixCell(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setPrefWidth(50);
        tf.setPrefHeight(30);
        tf.setAlignment(Pos.CENTER);
        tf.setStyle("-fx-background-color: #111827; -fx-text-fill: #F9FAFB; -fx-prompt-text-fill: #4B5563; " +
                "-fx-border-color: #374151; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-padding: 4px; -fx-font-family: 'Monospaced'; -fx-font-size: 12px;");
        tf.setOnAction(e -> handleGenerate());
        return tf;
    }

    /**
     * Creates the geometric properties card showing corners, side lengths, area, and augmented matrix.
     *
     * @return the container {@link VBox}
     */
    private VBox createInfoCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: #1F2937; -fx-background-radius: 8px; -fx-border-color: #374151; -fx-border-radius: 8px;");

        Label header = new Label("GEOMETRIC PROPERTIES");
        header.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

        vertexALabel = createPropertyLabel("Vertex A: —");
        vertexBLabel = createPropertyLabel("Vertex B: —");
        vertexCLabel = createPropertyLabel("Vertex C: —");
        sidesLabel = createPropertyLabel("Sides: —");
        areaLabel = createPropertyLabel("Area: —");
        perimeterLabel = createPropertyLabel("Perimeter: —");

        Label matrixHeader = new Label("AUGMENTED MATRIX [ A (3x2) | B (3x1) ]");
        matrixHeader.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
        matrixAugmentedLabel = createPropertyLabel("");

        card.getChildren().addAll(header, vertexALabel, vertexBLabel, vertexCLabel,
                new Separator(), sidesLabel, areaLabel, perimeterLabel,
                new Separator(), matrixHeader, matrixAugmentedLabel);
        card.setVisible(false);
        card.managedProperty().bind(card.visibleProperty());
        return card;
    }

    /**
     * Creates a styled monospace readout label.
     *
     * @param text initial text
     * @return styled {@link Label}
     */
    private Label createPropertyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #E5E7EB; -fx-font-size: 12px; -fx-font-family: 'Monospaced';");
        return lbl;
    }

    /**
     * Loads a 3x2 matrix A and 3x1 vector B into the input grid.
     *
     * @param A 3x2 matrix of multipliers
     * @param b 3-element column of target numbers
     */
    private void loadMatrixPreset(double[][] A, double[] b) {
        for (int r = 0; r < 3; r++) {
            aMatrixFields[r][0].setText(formatNumber(A[r][0]));
            aMatrixFields[r][1].setText(formatNumber(A[r][1]));
            bVectorFields[r].setText(formatNumber(b[r]));
        }
    }

    /**
     * Formats a double value cleanly, omitting decimal points for whole integers.
     *
     * @param val numeric value
     * @return clean text string
     */
    private String formatNumber(double val) {
        if (Math.abs(val - Math.round(val)) < 1e-9) {
            return String.valueOf(Math.round(val));
        }
        return String.format("%.2f", val);
    }

    /**
     * The master action triggered when "Generate Diagram" is clicked.
     * Reads matrix inputs, understands line equations, computes corners, and draws the triangle.
     */
    private void handleGenerate() {
        try {
            double[][] A = new double[3][2];
            double[] b = new double[3];

            for (int r = 0; r < 3; r++) {
                String a1Str = aMatrixFields[r][0].getText();
                String a2Str = aMatrixFields[r][1].getText();
                String bStr = bVectorFields[r].getText();

                if (a1Str == null || a1Str.isBlank() || a2Str == null || a2Str.isBlank() || bStr == null || bStr.isBlank()) {
                    showError("Please enter all 9 matrix numbers: Matrix A (3x2) and Vector B (3x1).");
                    triangleCanvas.clear();
                    infoCard.setVisible(false);
                    return;
                }

                A[r][0] = Double.parseDouble(a1Str.trim());
                A[r][1] = Double.parseDouble(a2Str.trim());
                b[r] = Double.parseDouble(bStr.trim());
            }

            // Extract the 3 lines from matrix rows
            Line[] lines = geometryService.extractLines(A, b);
            eq1UnderstoodLabel.setText("L1:  " + lines[0].formatStandard());
            eq2UnderstoodLabel.setText("L2:  " + lines[1].formatStandard());
            eq3UnderstoodLabel.setText("L3:  " + lines[2].formatStandard());

            // Build the verified triangle
            Triangle triangle = geometryService.buildTriangle(lines[0], lines[1], lines[2]);

            // Draw the diagram and display measurements
            triangleCanvas.setTriangle(triangle);
            showSuccess("Matrix validated & diagram generated successfully!");
            updateInfoCard(triangle);
        } catch (NumberFormatException e) {
            showError("Invalid number format in matrix inputs: " + e.getMessage());
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

    /**
     * Wipes all input boxes, clears status messages, hides property cards, and clears the canvas.
     */
    private void handleClear() {
        for (int r = 0; r < 3; r++) {
            aMatrixFields[r][0].clear();
            aMatrixFields[r][1].clear();
            bVectorFields[r].clear();
        }
        eq1UnderstoodLabel.setText("Line 1: —");
        eq2UnderstoodLabel.setText("Line 2: —");
        eq3UnderstoodLabel.setText("Line 3: —");
        rawMatrixArea.clear();
        triangleCanvas.clear();
        statusLabel.setVisible(false);
        infoCard.setVisible(false);
    }

    /**
     * Updates the UI property card with calculated values from the given triangle.
     *
     * @param t the verified {@link Triangle}
     */
    private void updateInfoCard(Triangle t) {
        vertexALabel.setText(String.format("Vertex A: (%.2f, %.2f)", t.getP1().getX(), t.getP1().getY()));
        vertexBLabel.setText(String.format("Vertex B: (%.2f, %.2f)", t.getP2().getX(), t.getP2().getY()));
        vertexCLabel.setText(String.format("Vertex C: (%.2f, %.2f)", t.getP3().getX(), t.getP3().getY()));

        sidesLabel.setText(String.format("Sides: a=%.2f, b=%.2f, c=%.2f",
                t.getSideA(), t.getSideB(), t.getSideC()));
        areaLabel.setText(String.format("Area: %.4f", t.getArea()));
        perimeterLabel.setText(String.format("Perimeter: %.4f", t.getPerimeter()));

        // Augmented matrix [A | B]
        String augMatrix = String.format(
                "[ %+6.2f  %+6.2f  | %+6.2f ]\n[ %+6.2f  %+6.2f  | %+6.2f ]\n[ %+6.2f  %+6.2f  | %+6.2f ]",
                t.getL1().getA(), t.getL1().getB(), t.getL1().getC(),
                t.getL2().getA(), t.getL2().getB(), t.getL2().getC(),
                t.getL3().getA(), t.getL3().getB(), t.getL3().getC()
        );
        matrixAugmentedLabel.setText(augMatrix);

        infoCard.setVisible(true);
    }

    /**
     * Displays a green success banner at the bottom of the sidebar.
     *
     * @param message the positive feedback message
     */
    private void showSuccess(String message) {
        statusLabel.setText("✓ " + message);
        statusLabel.setStyle("-fx-text-fill: #34D399; -fx-background-color: #064E3B; -fx-padding: 10px; -fx-background-radius: 6px;");
        statusLabel.setVisible(true);
    }

    /**
     * Displays a red error banner explaining what went wrong and how to fix it.
     *
     * @param message the error explanation
     */
    private void showError(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.setStyle("-fx-text-fill: #F87171; -fx-background-color: #450A0A; -fx-padding: 10px; -fx-background-radius: 6px;");
        statusLabel.setVisible(true);
    }

    /**
     * Java entry point that kicks off the JavaFX application lifecycle.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
