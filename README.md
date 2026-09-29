# 📐 TriangleFX

**TriangleFX** is a modern JavaFX desktop application designed to solve, analyze, and visualize geometric triangles defined by a system of three linear equations in matrix form:

```text
A · [x; y] = B
```

The application accepts coefficient matrices, computes pairwise line intersections, validates triangle non-degeneracy, calculates geometric metrics (area, perimeter, side lengths, vertices), and renders an interactive auto-scaled diagram on a 2D coordinate plane.

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Mathematical Model](#-mathematical-model)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Input Formats & Contract](#-input-formats--contract)
- [Execution Pipeline](#-execution-pipeline)
- [Build, Test, and Run](#-build-test-and-run)
- [Project Structure](#-project-structure)
- [Screenshots](#-screenshots)
- [Documentation](#-documentation)

---

## 💡 Overview

TriangleFX solves a fundamental computational geometry problem:

> **Given three linear constraints in two variables (x and y), determine whether their corresponding lines intersect to form a valid, non-degenerate triangle. If valid, compute its geometric properties and render the triangle with an adaptive coordinate system.**

The application provides a structured visual matrix equation layout:

```text
┌                 ┐   ┌   ┐     ┌    ┐
│  a11       a12  │   │ x │     │ b1 │
│  a21       a22  │ · │   │  =  │ b2 │
│  a31       a32  │   │ y │     │ b3 │
└                 ┘   └   ┘     └    ┘
   Matrix A (3×2)     Vector x   Vector B (3×1)
```

Users can enter numbers directly into the bracketed grid, select built-in presets, or paste raw matrix text.

---

## 🧮 Mathematical Model

### 1. Matrix System Formulation

The 2D system of lines is modeled using the linear matrix equation:

```text
A · x = B
```

Where:

- **Matrix A (3×2)**: Coefficients for variables `x` and `y`
- **Vector x (2×1)**: Unknown 2D coordinates `[x; y]`
- **Vector B (3×1)**: Target constants `[b1; b2; b3]`

```text
┌                 ┐   ┌   ┐     ┌    ┐
│  a11       a12  │   │ x │     │ b1 │
│  a21       a22  │ · │   │  =  │ b2 │
│  a31       a32  │   │ y │     │ b3 │
└                 ┘   └   ┘     └    ┘
```

Each row represents one straight line in the 2D Cartesian plane:

```text
Line 1 (L1):  (a11 · x) + (a12 · y) = b1
Line 2 (L2):  (a21 · x) + (a22 · y) = b2
Line 3 (L3):  (a31 · x) + (a32 · y) = b3
```

---

### 2. Pairwise Line Intersections

A triangle has three vertices (A, B, and C) formed by the pairwise intersections of the three lines:

```text
• Vertex C (P12) = Intersection of Line 1 and Line 2
• Vertex A (P23) = Intersection of Line 2 and Line 3
• Vertex B (P31) = Intersection of Line 3 and Line 1
```

To find the intersection point `(x, y)` of any two lines:

```text
Line 1:  a1·x + b1·y = c1
Line 2:  a2·x + b2·y = c2
```

We compute the 2×2 determinant:

```text
Determinant D = (a1 · b2) - (a2 · b1)
```

- **If D = 0**: The lines are **parallel** (never meet) or **coincident** (identical line). In either case, no triangle can be formed.
- **If D ≠ 0**: A unique intersection point exists (via Cramer's Rule):

```text
x = ((c1 · b2) - (c2 · b1)) / D
y = ((a1 · c2) - (a2 · c1)) / D
```

---

### 3. Non-Degeneracy & Triangle Validation

Even if each pair of lines intersects, they might not form a real triangle (for example, if all three lines cross at the exact same point, or if the three intersection points are collinear).

TriangleFX strictly validates three geometric rules:

1. **Unique Pairwise Intersections**: Determinant D ≠ 0 for all three line pairs (no parallel or identical lines).
2. **Three Distinct Vertices**: The three points must be separate coordinates:
   ```text
   P12 ≠ P23 ≠ P31
   ```
3. **Non-Collinear Points (Positive Surface Area)**: The points must not lie on a single line. Collinearity is evaluated using the signed twice-area Shoelace determinant:

```text
2 · Area = | x1·(y2 - y3) + x2·(y3 - y1) + x3·(y1 - y2) |
```

A non-degenerate triangle strictly requires:

```text
| 2 · Area | > 0.000001  (tolerance: 1e-6)
```

If the area is zero or below tolerance, the system rejects the input and provides a clear diagnostic message.

---

## ✨ Key Features

- **Interactive Matrix Input**: Direct input of coefficients into a bracketed `A (3×2) · [x; y] = B (3×1)` UI card.
- **Raw Matrix Text Parser**: Paste raw numbers, comma-separated values, or Python/JSON array format (e.g., `[[1,1],[1,-1],[1,0]], [8,2,1]`).
- **Matrix Presets**: Quick-load predefined triangles:
  - **Preset 1**: Standard Triangle (`A=[[1,1],[1,-1],[1,0]], B=[8,2,1]`)
  - **Preset 2**: Right Triangle (`A=[[0,1],[1,0],[1,1]], B=[1,2,6]`)
  - **Preset 3**: Equilateral Triangle (`A=[[0,1],[1.73,-1],[1.73,1]], B=[0,0,6.93]`)
  - **Preset 4**: Oblique Triangle (`A=[[2,-1],[1,2],[3,-4]], B=[4,8,-12]`)
- **Understood Line Equations**: Real-time display showing decoded equations for Line 1, Line 2, and Line 3 with color-coded badges.
- **Geometric Properties Readout**: Instant calculation of:
  - Exact coordinates for Vertex A, Vertex B, and Vertex C
  - Side lengths: `a`, `b`, and `c`
  - Total surface area and perimeter
  - Augmented matrix `[ A | B ]`
- **Auto-Fitting Coordinate Canvas**:
  - Dynamically calculates viewport bounds and adjusts scale.
  - Draws Cartesian axes (X and Y), grid lines, tick numbers, and origin `(0, 0)`.
  - Renders the filled semi-transparent triangle polygon with labeled corner points.
- **Dark Aesthetic**: Modern dark theme optimized for clarity and high-contrast readability.

---

## 🏛️ System Architecture

TriangleFX enforces strict separation of concerns across dedicated modules:

| Package | Key Classes | Description |
| :--- | :--- | :--- |
| `com.trianglefx.model` | `Point`, `Line`, `Triangle` | Immutable domain models representing 2D geometric entities. |
| `com.trianglefx.geometry` | `GeometryService`, `GeometryException` | Core mathematical engine for line intersection, determinant solving, and validity checks. |
| `com.trianglefx.parser` | `EquationParser`, `MatrixParser`, `ParseException` | Parses raw matrix strings, equations, and number grids into structured data. |
| `com.trianglefx.ui` | `TriangleApp`, `TriangleCanvas` | JavaFX interface, matrix inputs, status notifications, and high-DPI canvas renderer. |
| `com.trianglefx` | `Main` | Application entry point. |

---

## 📥 Input Formats & Contract

### 1. Matrix Input Contract

The application requires 9 scalar values:

| Row | Matrix A (x coeff) | Matrix A (y coeff) | Vector B (Constant) | Decoded Line Equation |
| :---: | :---: | :---: | :---: | :---: |
| **Row 1** | `a11` | `a12` | `b1` | `a11·x + a12·y = b1` |
| **Row 2** | `a21` | `a22` | `b2` | `a21·x + a22·y = b2` |
| **Row 3** | `a31` | `a32` | `b3` | `a31·x + a32·y = b3` |

### 2. Supported Raw Paste Formats

The paste utility parses diverse input representations:

- **Comma / Newline separated (Row by Row):**
  ```text
  1, 1, 8
  1, -1, 2
  1, 0, 1
  ```
- **Nested Array Syntax:**
  ```text
  [[1, 1], [1, -1], [1, 0]], [8, 2, 1]
  ```
- **Whitespace / Tabular stream:**
  ```text
  1 1 8 1 -1 2 1 0 1
  ```

---

## 🔄 Execution Pipeline

```text
  ┌────────────────────────────────────────────────────────┐
  │                   User Matrix Input                    │
  │     (Interactive Grid / Raw Paste / Sample Preset)     │
  └───────────────────────────┬────────────────────────────┘
                              │
                              ▼
  ┌────────────────────────────────────────────────────────┐
  │                 MatrixParser & Validator               │
  │       Converts input entries into 3 Line objects       │
  └───────────────────────────┬────────────────────────────┘
                              │
                              ▼
  ┌────────────────────────────────────────────────────────┐
  │                    GeometryService                     │
  │  • Computes pairwise intersections: L1∩L2, L2∩L3, L3∩L1│
  │  • Checks for parallel / coincident lines              │
  │  • Evaluates collinearity & 2·Area determinant         │
  └───────────────────────────┬────────────────────────────┘
                              │
                 ┌────────────┴────────────┐
                 │                         │
            Valid Triangle            Invalid (Error)
                 │                         │
                 ▼                         ▼
  ┌───────────────────────────┐ ┌──────────────────────────┐
  │ TriangleCanvas & InfoCard │ │   Status Banner Alert    │
  │ • Auto-scales viewport    │ │ • Explains exact reason  │
  │ • Draws axes, grid, ticks │ │   (parallel, collinear,  │
  │ • Renders triangle polygon│ │   or degenerate)         │
  │ • Computes area/perimeter │ └──────────────────────────┘
  └───────────────────────────┘
```

---

## 🚀 Build, Test, and Run

### Prerequisites

- **Java JDK**: Version 21 or higher
- **Apache Maven**: Version 3.8 or higher

Verify your installation:
```bash
java -version
mvn -version
```

### 1. Run Unit Tests

Execute the comprehensive test suite (geometry solvers, parser edge cases, and degeneracy checks):

```bash
mvn test
```

### 2. Launch the Application

Compile and launch the interactive JavaFX desktop app:

```bash
mvn clean javafx:run
```

### 3. Package Executable JAR

Generate the compiled project artifact in the `target/` directory:

```bash
mvn clean package
```

---

## 📁 Project Structure

```text
CSC360-Group1/
├── docs/
│   ├── IMPLEMENTATION_PLAN.md      # Architecture and phased milestone plan
│   └── PROBLEM_STATEMENT.md        # Mathematical problem specification
├── pom.xml                         # Maven dependencies and JavaFX configuration
├── README.md                       # Main project documentation
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── trianglefx/
    │               ├── Main.java                 # Main bootstrap launcher
    │               ├── geometry/
    │               │   ├── GeometryException.java # Domain exceptions for geometry errors
    │               │   └── GeometryService.java   # Intersection & triangle solver
    │               ├── model/
    │               │   ├── Line.java             # Line equation representation (ax + by = c)
    │               │   ├── Point.java            # Immutable 2D coordinate model
    │               │   └── Triangle.java         # Triangle entity with metrics
    │               ├── parser/
    │               │   ├── EquationParser.java   # Parses algebraic line equations
    │               │   ├── MatrixParser.java     # Flexible matrix/vector text parser
    │               │   └── ParseException.java   # Parser validation errors
    │               ├── resources/                # Embedded UI assets & screenshots
    │               └── ui/
    │                   ├── TriangleApp.java      # JavaFX UI controller & layout
    │                   └── TriangleCanvas.java   # Adaptive coordinate canvas renderer
    └── test/
        └── java/
            └── com/
                └── trianglefx/
                    ├── geometry/
                    │   └── GeometryServiceTest.java # Geometric calculation tests
                    └── parser/
                        ├── EquationParserTest.java  # Equation parsing tests
                        └── MatrixParserTest.java    # Matrix syntax parsing tests
```

---

## 📸 Screenshots

| Matrix Input & Visualization | Properties & Understood Equations |
| :---: | :---: |
| ![TriangleFX Screenshot 1](src/main/java/com/trianglefx/resources/Screenshot%202026-09-29%20at%2014.46.21.png) | ![TriangleFX Screenshot 2](src/main/java/com/trianglefx/resources/Screenshot%202026-09-29%20at%2014.46.35.png) |

---

## 📚 Documentation

For deeper details regarding mathematical proofs and system design:
- [Problem Statement Specification](docs/PROBLEM_STATEMENT.md)
- [Implementation & Architecture Plan](docs/IMPLEMENTATION_PLAN.md)
