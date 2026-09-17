package trianglefx.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import trianglefx.geometry.LineEquation;
import trianglefx.geometry.LineIntersection;
import trianglefx.geometry.Point;
import trianglefx.geometry.TriangleValidator;

/**
 * JavaFX client that accepts three equations in {@code a b c} format ({@code ax + by = c}),
 * computes pairwise intersections, validates triangle existence, and draws it.
 */
public final class TriangleApp extends Application {

	private static final double MARGIN = 20;
	private static final double TARGET_GRID_SPACING_PX = 60;
	private static final double EPSILON = 1e-10;

	private static final Color CANVAS_BG = Color.rgb(13, 17, 23);
	private static final Color GRID_COLOR = Color.rgb(48, 54, 61);
	private static final Color AXIS_COLOR = Color.rgb(139, 148, 158);
	private static final Color ORIGIN_COLOR = Color.rgb(46, 160, 67);
	private static final Color TEXT_COLOR = Color.rgb(230, 237, 243);
	private static final Color TRIANGLE_FILL = Color.rgb(47, 129, 247, 0.28);
	private static final Color TRIANGLE_STROKE = Color.rgb(88, 166, 255);
	private static final Color VERTEX_COLOR = Color.rgb(248, 81, 73);

	private TextField eq1Field;
	private TextField eq2Field;
	private TextField eq3Field;
	private Label statusLabel;
	private Canvas canvas;

	private Point lastP12;
	private Point lastP23;
	private Point lastP31;

	/**
	 * Initializes and shows the main JavaFX window.
	 *
	 * @param stage primary stage provided by JavaFX
	 */
	@Override
	public void start(Stage stage) {
		eq1Field = new TextField("1 1 8");
		eq2Field = new TextField("1 -1 2");
		eq3Field = new TextField("1 0 1");

		statusLabel = new Label(
			"Enter 3 equations as: a b c (ax + by = c). Click Draw to render triangle."
		);
		canvas = new Canvas(1, 1);

		Button drawButton = new Button("Draw");
		drawButton.setOnAction(event -> onDraw());

		Button clearButton = new Button("Clear");
		clearButton.setOnAction(event -> onClear());

		GridPane inputs = new GridPane();
		inputs.setHgap(10);
		inputs.setVgap(8);
		inputs.addRow(0, new Label("Equation 1 (a b c):"), eq1Field);
		inputs.addRow(1, new Label("Equation 2 (a b c):"), eq2Field);
		inputs.addRow(2, new Label("Equation 3 (a b c):"), eq3Field);

		HBox controls = new HBox(10, drawButton, clearButton);

		VBox top = new VBox(10, inputs, controls, statusLabel);
		top.setPadding(new Insets(12));

		StackPane canvasPane = new StackPane(canvas);

		canvas.widthProperty().bind(canvasPane.widthProperty());
		canvas.heightProperty().bind(canvasPane.heightProperty());
		canvas
			.widthProperty()
			.addListener((obs, oldVal, newVal) -> redrawCanvas());
		canvas
			.heightProperty()
			.addListener((obs, oldVal, newVal) -> redrawCanvas());

		BorderPane root = new BorderPane();
		root.setTop(top);
		root.setCenter(canvasPane);

		stage.setTitle("TriangleFX");
		Scene scene = new Scene(root, 1200, 800);
		scene
			.getStylesheets()
			.add(
				TriangleApp.class
					.getResource("/trianglefx/app/dark-theme.css")
					.toExternalForm()
			);
		stage.setScene(scene);
		stage.setMaximized(true);
		stage.show();
		redrawCanvas();
	}

	/**
	 * Handles Draw action: parses inputs, computes intersections, validates the triangle,
	 * and redraws if valid.
	 */
	private void onDraw() {
		try {
			LineEquation l1 = parseLine(eq1Field.getText(), "Equation 1");
			LineEquation l2 = parseLine(eq2Field.getText(), "Equation 2");
			LineEquation l3 = parseLine(eq3Field.getText(), "Equation 3");

			Point p12 = uniqueIntersectionOrThrow(
				l1,
				l2,
				"Equation 1 & Equation 2"
			);
			Point p23 = uniqueIntersectionOrThrow(
				l2,
				l3,
				"Equation 2 & Equation 3"
			);
			Point p31 = uniqueIntersectionOrThrow(
				l3,
				l1,
				"Equation 3 & Equation 1"
			);

			if (!TriangleValidator.canFormTriangle(p12, p23, p31)) {
				lastP12 = null;
				lastP23 = null;
				lastP31 = null;
				clearCanvas();
				statusLabel.setText(
					"No valid triangle can be formed (degenerate/collinear points)."
				);
				return;
			}

			lastP12 = p12;
			lastP23 = p23;
			lastP31 = p31;
			redrawCanvas();

			statusLabel.setText(
				String.format(
					"Triangle drawn. Vertices: P12(%.3f, %.3f), P23(%.3f, %.3f), P31(%.3f, %.3f)",
					p12.x(),
					p12.y(),
					p23.x(),
					p23.y(),
					p31.x(),
					p31.y()
				)
			);
		} catch (IllegalArgumentException ex) {
			lastP12 = null;
			lastP23 = null;
			lastP31 = null;
			clearCanvas();
			statusLabel.setText("Input error: " + ex.getMessage());
		}
	}

	/**
	 * Clears user input and removes the currently drawn triangle.
	 */
	private void onClear() {
		eq1Field.clear();
		eq2Field.clear();
		eq3Field.clear();
		lastP12 = null;
		lastP23 = null;
		lastP31 = null;
		clearCanvas();
		statusLabel.setText("Cleared. Enter equations as: a b c");
	}

	/**
	 * Parses one equation text in {@code a b c} format into a line equation.
	 *
	 * @param raw raw input text
	 * @param label field label used in error messages
	 * @return parsed line equation
	 * @throws IllegalArgumentException if the input is empty, badly formatted, or non-numeric
	 */
	private LineEquation parseLine(String raw, String label) {
		if (raw == null || raw.trim().isEmpty()) {
			throw new IllegalArgumentException(
				label + " is empty. Expected: a b c"
			);
		}

		String[] parts = raw.trim().split("\\s+");
		if (parts.length != 3) {
			throw new IllegalArgumentException(
				label + " must contain exactly 3 numbers: a b c"
			);
		}

		try {
			double a = Double.parseDouble(parts[0]);
			double b = Double.parseDouble(parts[1]);
			double c = Double.parseDouble(parts[2]);
			return new LineEquation(a, b, c);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException(
				label + " contains invalid number(s).",
				ex
			);
		}
	}

	/**
	 * Computes a unique intersection between two lines or throws a descriptive exception.
	 *
	 * @param l1 first line
	 * @param l2 second line
	 * @param label label identifying the pair in error text
	 * @return intersection point when it exists uniquely
	 * @throws IllegalArgumentException if the lines are parallel or coincident
	 */
	private Point uniqueIntersectionOrThrow(
		LineEquation l1,
		LineEquation l2,
		String label
	) {
		LineIntersection.Result result = LineIntersection.find(l1, l2);

		return switch (result.type()) {
			case INTERSECTING -> result.point();
			case PARALLEL -> throw new IllegalArgumentException(
				label + " are parallel (no intersection)."
			);
			case COINCIDENT -> throw new IllegalArgumentException(
				label + " are coincident (infinite intersections)."
			);
		};
	}

	/**
	 * Draws the reference background for the default viewport without any triangle.
	 */
	private void clearCanvas() {
		if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
			return;
		}
		drawReferenceBackground(defaultTransform());
	}

	/**
	 * Redraws the canvas state (background and, if present, the last triangle).
	 */
	private void redrawCanvas() {
		if (lastP12 != null && lastP23 != null && lastP31 != null) {
			drawTriangle(lastP12, lastP23, lastP31);
		} else {
			clearCanvas();
		}
	}

	/**
	 * Draws the triangle for the supplied world-space points.
	 *
	 * @param p1 first vertex
	 * @param p2 second vertex
	 * @param p3 third vertex
	 */
	private void drawTriangle(Point p1, Point p2, Point p3) {
		Transform transform = fitTransformForTriangle(p1, p2, p3);
		drawReferenceBackground(transform);

		ScreenPoint s1 = toScreen(p1, transform);
		ScreenPoint s2 = toScreen(p2, transform);
		ScreenPoint s3 = toScreen(p3, transform);

		GraphicsContext gc = canvas.getGraphicsContext2D();

		double[] xs = { s1.x, s2.x, s3.x };
		double[] ys = { s1.y, s2.y, s3.y };

		gc.setFill(TRIANGLE_FILL);
		gc.fillPolygon(xs, ys, 3);

		gc.setStroke(TRIANGLE_STROKE);
		gc.setLineWidth(2.0);
		gc.strokePolygon(xs, ys, 3);

		drawVertex(gc, s1, "P12", p1);
		drawVertex(gc, s2, "P23", p2);
		drawVertex(gc, s3, "P31", p3);
	}

	/**
	 * Draws a single vertex marker with label.
	 *
	 * @param gc graphics context
	 * @param sp screen-space position
	 * @param label vertex label
	 * @param worldPoint original world-space point (for coordinates in label)
	 */
	private void drawVertex(
		GraphicsContext gc,
		ScreenPoint sp,
		String label,
		Point worldPoint
	) {
		double r = 4;
		gc.setFill(VERTEX_COLOR);
		gc.fillOval(sp.x - r, sp.y - r, 2 * r, 2 * r);

		gc.setFill(TEXT_COLOR);
		gc.fillText(
			String.format(
				"%s (%.2f, %.2f)",
				label,
				worldPoint.x(),
				worldPoint.y()
			),
			sp.x + 6,
			sp.y - 6
		);
	}

	/**
	 * Creates a transform that auto-fits the triangle (and origin) into the current canvas.
	 *
	 * @param p1 first point
	 * @param p2 second point
	 * @param p3 third point
	 * @return fitted transform
	 */
	private Transform fitTransformForTriangle(Point p1, Point p2, Point p3) {
		double minX = Math.min(0, Math.min(p1.x(), Math.min(p2.x(), p3.x())));
		double maxX = Math.max(0, Math.max(p1.x(), Math.max(p2.x(), p3.x())));
		double minY = Math.min(0, Math.min(p1.y(), Math.min(p2.y(), p3.y())));
		double maxY = Math.max(0, Math.max(p1.y(), Math.max(p2.y(), p3.y())));

		double worldW = maxX - minX;
		double worldH = maxY - minY;

		if (worldW < 1) {
			minX -= 1;
			maxX += 1;
			worldW = maxX - minX;
		}
		if (worldH < 1) {
			minY -= 1;
			maxY += 1;
			worldH = maxY - minY;
		}

		double padX = worldW * 0.20;
		double padY = worldH * 0.20;
		minX -= padX;
		maxX += padX;
		minY -= padY;
		maxY += padY;

		return buildTransform(minX, maxX, minY, maxY);
	}

	/**
	 * Creates a default transform centered around the origin.
	 *
	 * @return default transform
	 */
	private Transform defaultTransform() {
		return buildTransform(-10, 10, -10, 10);
	}

	/**
	 * Builds a transform from world bounds and current canvas size.
	 *
	 * @param minX minimum world x
	 * @param maxX maximum world x
	 * @param minY minimum world y
	 * @param maxY maximum world y
	 * @return transform with visible bounds and pixel scale
	 */
	private Transform buildTransform(
		double minX,
		double maxX,
		double minY,
		double maxY
	) {
		double worldW = maxX - minX;
		double worldH = maxY - minY;

		double usableW = Math.max(1, canvas.getWidth() - 2 * MARGIN);
		double usableH = Math.max(1, canvas.getHeight() - 2 * MARGIN);

		double scaleX = usableW / worldW;
		double scaleY = usableH / worldH;
		double scale = Math.min(scaleX, scaleY);

		return new Transform(minX, maxX, minY, maxY, scale);
	}

	/**
	 * Draws the reference background: dark canvas, grid, axes, and origin marker.
	 *
	 * @param transform active viewport transform
	 */
	private void drawReferenceBackground(Transform transform) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(CANVAS_BG);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		double gridStep = chooseGridStep(transform.scale);

		gc.setStroke(GRID_COLOR);
		gc.setLineWidth(1.0);

		double startX = Math.floor(transform.minX / gridStep) * gridStep;
		for (double x = startX; x <= transform.maxX + EPSILON; x += gridStep) {
			ScreenPoint s = toScreen(new Point(x, 0), transform);
			gc.strokeLine(s.x, MARGIN, s.x, canvas.getHeight() - MARGIN);
		}

		double startY = Math.floor(transform.minY / gridStep) * gridStep;
		for (double y = startY; y <= transform.maxY + EPSILON; y += gridStep) {
			ScreenPoint s = toScreen(new Point(0, y), transform);
			gc.strokeLine(MARGIN, s.y, canvas.getWidth() - MARGIN, s.y);
		}

		gc.setStroke(AXIS_COLOR);
		gc.setLineWidth(1.8);

		if (transform.minX <= 0 && transform.maxX >= 0) {
			ScreenPoint yAxis = toScreen(new Point(0, 0), transform);
			gc.strokeLine(
				yAxis.x,
				MARGIN,
				yAxis.x,
				canvas.getHeight() - MARGIN
			);
		}

		if (transform.minY <= 0 && transform.maxY >= 0) {
			ScreenPoint xAxis = toScreen(new Point(0, 0), transform);
			gc.strokeLine(MARGIN, xAxis.y, canvas.getWidth() - MARGIN, xAxis.y);
		}

		if (
			transform.minX <= 0 &&
			transform.maxX >= 0 &&
			transform.minY <= 0 &&
			transform.maxY >= 0
		) {
			ScreenPoint origin = toScreen(new Point(0, 0), transform);
			double r = 4;
			gc.setFill(ORIGIN_COLOR);
			gc.fillOval(origin.x - r, origin.y - r, 2 * r, 2 * r);
			gc.setFill(TEXT_COLOR);
			gc.fillText("O(0, 0)", origin.x + 6, origin.y - 6);
		}
	}

	/**
	 * Selects a "nice" world-unit grid step so spacing remains readable.
	 *
	 * @param scale current pixels-per-world-unit scale
	 * @return chosen world-unit step between adjacent grid lines
	 */
	private double chooseGridStep(double scale) {
		double targetWorldUnits = TARGET_GRID_SPACING_PX / scale;
		double base = Math.pow(10, Math.floor(Math.log10(targetWorldUnits)));
		double[] steps = { 1, 2, 5, 10 };
		double best = base;
		double bestDiff = Double.MAX_VALUE;

		for (double m : steps) {
			double candidate = m * base;
			double diff = Math.abs(candidate - targetWorldUnits);
			if (diff < bestDiff) {
				bestDiff = diff;
				best = candidate;
			}
		}

		return best;
	}

	/**
	 * Converts world coordinates to screen coordinates.
	 *
	 * @param world world-space point
	 * @param t active transform
	 * @return projected screen-space point
	 */
	private ScreenPoint toScreen(Point world, Transform t) {
		double x = MARGIN + (world.x() - t.minX) * t.scale;
		double y = canvas.getHeight() - MARGIN - (world.y() - t.minY) * t.scale;
		return new ScreenPoint(x, y);
	}

	/**
	 * Immutable viewport transform describing world bounds and current scale.
	 */
	private static final class Transform {

		private final double minX;
		private final double maxX;
		private final double minY;
		private final double maxY;
		private final double scale;

		/**
		 * Creates a transform snapshot.
		 *
		 * @param minX minimum visible world x
		 * @param maxX maximum visible world x
		 * @param minY minimum visible world y
		 * @param maxY maximum visible world y
		 * @param scale pixels per world unit
		 */
		private Transform(
			double minX,
			double maxX,
			double minY,
			double maxY,
			double scale
		) {
			this.minX = minX;
			this.maxX = maxX;
			this.minY = minY;
			this.maxY = maxY;
			this.scale = scale;
		}
	}

	/**
	 * Immutable 2D point in screen pixel space.
	 */
	private static final class ScreenPoint {

		private final double x;
		private final double y;

		/**
		 * Creates a screen-space point.
		 *
		 * @param x x-coordinate in pixels
		 * @param y y-coordinate in pixels
		 */
		private ScreenPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * Application entry point.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
