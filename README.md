# TriangleFX Project

A desktop **JavaFX** application that parses three 2D linear equations from user text input, computes their pairwise intersection points, validates that they form a non-degenerate triangle, and renders the triangle interactively on a canvas with coordinates and properties.

---

## Features

- **Flexible Linear Equation Parser**:
  - Standard form: `x + y = 8`, `2x - 3y = 10`
  - Slope-intercept form: `y = 2x + 1`, `y = -x + 5`
  - Single-variable vertical/horizontal lines: `x = 1`, `x = 4`, `y = -3`
  - Decimals and arbitrary ordering: `2.5x - 1.5y = 3.0`, `y + 2x = 7`
  - Terms on both sides: `2x + 3 = y + 5`
  - Optional explicit multiplication: `3*x + 4*y = 12`
- **Geometry & Intersection Engine**:
  - Solves pairwise intersections using Cramer's rule.
  - Detects parallel lines ($L_1 \parallel L_2$).
  - Detects coincident / identical lines.
  - Detects concurrent lines (all 3 lines meeting at a single point).
  - Validates triangle non-degeneracy ($\text{Area} > 10^{-6}$).
  - Computes side lengths, perimeter, and area (via the Shoelace formula).
- **Interactive Canvas Rendering**:
  - Coordinate axes ($X$ and $Y$) with origin alignment.
  - Background grid for spatial scale.
  - Extended colored dashed lines representing the original three equations.
  - Shaded triangle fill with colored border.
  - Highlighted vertex dots with coordinate labels offset away from the centroid.
  - Responsive auto-scaling and auto-centering on window resize.
- **Modern User Interface**:
  - Color-coded equation inputs.
  - 1-click Preset examples (Default, Right Triangle, Equilateral-like, Oblique).
  - Instant status and helpful error feedback (syntax error, parallel line notification, etc.).
  - Properties panel displaying vertex coordinates, side lengths, area, and perimeter.

---

## Architecture & Project Structure

```
src/
├── main/java/com/trianglefx/
│   ├── Main.java                     # Application launcher
│   ├── model/
│   │   ├── Point.java                # 2D Point (x, y)
│   │   ├── Line.java                 # Standard line Ax + By = C
│   │   └── Triangle.java             # Triangle model with vertices, area, sides
│   ├── parser/
│   │   ├── EquationParser.java       # Linear equation tokenizer and parser
│   │   └── ParseException.java       # Custom parsing error
│   ├── geometry/
│   │   ├── GeometryService.java      # Intersection solver and validation
│   │   └── GeometryException.java    # Custom geometry error (parallel, concurrent)
│   └── ui/
│       ├── TriangleApp.java          # JavaFX Application UI
│       └── TriangleCanvas.java       # Responsive canvas drawer
└── test/java/com/trianglefx/
    ├── parser/
    │   └── EquationParserTest.java   # Unit tests for equation parsing
    └── geometry/
        └── GeometryServiceTest.java  # Unit tests for intersections & geometry
```

---

## Prerequisites

- **Java JDK 21+**
- **Apache Maven 3.8+**

---

## How to Build and Run

### 1. Run the Application

```bash
mvn clean javafx:run
```

### 2. Run the Unit Tests

```bash
mvn test
```

### 3. Package into a JAR

```bash
mvn clean package
```

---

## Example Inputs

| Shape / Scenario | Line 1 | Line 2 | Line 3 | Result |
| :--- | :--- | :--- | :--- | :--- |
| **Problem Statement Example** | `x + y = 8` | `x - y = 2` | `x = 1` | Vertices: (5, 3), (1, -1), (1, 7). Area = 16.00 |
| **Right Triangle** | `y = 1` | `x = 2` | `y = -x + 6` | Vertices: (2, 1), (2, 4), (5, 1). Area = 4.50 |
| **Equilateral-like** | `y = 0` | `y = 1.732x` | `y = -1.732x + 6.928` | Vertices: (0, 0), (2, 3.46), (4, 0). Area ≈ 6.93 |
| **Parallel (Error)** | `x + y = 5` | `x + y = 10` | `x = 1` | *Geometry Error: Line 1 and Line 2 are parallel* |
| **Concurrent (Error)** | `y = x` | `y = -x` | `y = 0` | *Geometry Error: The three lines are concurrent* |
