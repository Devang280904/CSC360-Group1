# TriangleFX

TriangleFX is a JavaFX desktop application for constructing and visualizing a triangle from three linear constraints in matrix form.

The application accepts a coefficient matrix and right-hand-side vector for a 2D linear model, computes pairwise line intersections, validates geometric feasibility, and renders the resulting triangle on a coordinate canvas.

---

## Table of Contents

- [Overview](#overview)
- [Mathematical Model](#mathematical-model)
- [Feature Set](#feature-set)
- [System Architecture](#system-architecture)
- [Input Contract](#input-contract)
- [Execution Flow](#execution-flow)
- [Build, Test, and Run](#build-test-and-run)
- [Documentation and API](#documentation-and-api)
- [Project Layout](#project-layout)
- [Screenshots](#screenshots)
- [Current Limitations](#current-limitations)
- [Roadmap](#roadmap)

---

## Overview

TriangleFX solves the following problem:

Given three linear equations in two variables, determine whether the three corresponding lines form a non-degenerate triangle, and if so, draw that triangle.

The implementation is strict about input representation: the UI accepts **matrix-form input only**.

---

## Mathematical Model

The system is modeled as:

\[
A\mathbf{x}=\mathbf{b}
\]

with:

\[
A =
\begin{bmatrix}
a_{11} & a_{12} \\
a_{21} & a_{22} \\
a_{31} & a_{32}
\end{bmatrix}
\in \mathbb{R}^{3\times 2},
\quad
\mathbf{x}=
\begin{bmatrix}
x \\
y
\end{bmatrix}
\in \mathbb{R}^{2\times 1},
\quad
\mathbf{b}=
\begin{bmatrix}
b_1 \\
b_2 \\
b_3
\end{bmatrix}
\in \mathbb{R}^{3\times 1}
\]

Each row defines a line in \(\mathbb{R}^2\):

\[
a_{i1}x + a_{i2}y = b_i,\quad i\in\{1,2,3\}
\]

The application computes intersections:

\[
P_{12}=L_1\cap L_2,\quad
P_{23}=L_2\cap L_3,\quad
P_{31}=L_3\cap L_1
\]

A valid triangle exists iff:

1. each pair produces a unique intersection (not parallel, not coincident), and
2. \(P_{12},P_{23},P_{31}\) are non-collinear and pairwise distinct.

Triangle degeneracy is checked via twice-area determinant:

\[
2\Delta =
x_1(y_2-y_3)+x_2(y_3-y_1)+x_3(y_1-y_2)
\]

A non-degenerate triangle requires:

\[
|2\Delta| > \varepsilon
\]

for configured tolerance \(\varepsilon\).

---

## Feature Set

- Strict matrix input UI for \(A\in\mathbb{R}^{3\times 2}\), \(\mathbf{b}\in\mathbb{R}^{3\times 1}\)
- Pairwise line intersection classification:
  - intersecting
  - parallel
  - coincident
- Triangle validity verification
- Auto-fit rendering on a dark coordinate plane
- Grid, axis lines, origin marker, and labeled vertices
- Clear separation between geometry logic and UI layer
- Unit test coverage for geometry and linear-system components

---

## System Architecture

### Geometry Core (`trianglefx.geometry`)

- `Point` — immutable 2D value type
- `LineEquation` — standard-form line model \(Ax+By=C\)
- `LineIntersection` — relationship and intersection computation for two lines
- `LinearSystem2x2` — solver and classification for 2×2 systems
- `TriangleValidator` — geometric validity checks for three points

### JavaFX Application (`trianglefx.app`)

- `TriangleApp` — matrix-input UI, orchestration, rendering, status/error handling

### Console Client (`trianglefx.client`)

- `LinearSystemClient` — CLI solver for two equations in coefficient form

---

## Input Contract

The UI requires 9 scalar inputs:

- Matrix entries: `A[1,1]`, `A[1,2]`, `A[2,1]`, `A[2,2]`, `A[3,1]`, `A[3,2]`
- Vector entries: `b[1]`, `b[2]`, `b[3]`

All values must be valid floating-point numbers.

### Example input

- Row 1: `A[1,1]=1`, `A[1,2]=1`, `b[1]=8`
- Row 2: `A[2,1]=1`, `A[2,2]=-1`, `b[2]=2`
- Row 3: `A[3,1]=1`, `A[3,2]=0`, `b[3]=1`

Equivalent equations:

\[
\begin{aligned}
x+y&=8 \\
x-y&=2 \\
x&=1
\end{aligned}
\]

---

## Execution Flow

1. Parse UI fields into matrix/vector values.
2. Build three line equations from rows of \(A\) and \(\mathbf{b}\).
3. Compute \(P_{12}, P_{23}, P_{31}\) using pairwise line intersection.
4. Reject invalid line relationships (parallel/coincident).
5. Validate non-degenerate triangle using area-based test.
6. Render triangle and labels on the canvas.

---

## Build, Test, and Run

### Prerequisites

- Java 17 or newer
- Maven 3.8 or newer

### Run test suite

```bash
mvn test
```

### Launch JavaFX desktop application

```bash
mvn javafx:run
```

### Generate Javadoc

```bash
mvn javadoc:javadoc
```

---

## Documentation and API

Additional project documents:

- `docs/PROBLEM_STATEMENT.md`
- `docs/IMPLEMENTATION_PLAN.md`
- `docs/DESIGN_DOC_CURRENT_STATE.md`

---

## Project Layout

```text
src/
├── main/
│   ├── java/
│   │   └── trianglefx/
│   │       ├── app/
│   │       │   └── TriangleApp.java
│   │       ├── client/
│   │       │   └── LinearSystemClient.java
│   │       └── geometry/
│   │           ├── LineEquation.java
│   │           ├── LineIntersection.java
│   │           ├── LinearSystem2x2.java
│   │           ├── Point.java
│   │           └── TriangleValidator.java
│   └── resources/
│       └── trianglefx/
│           └── app/
│               └── dark-theme.css
└── test/
    └── java/
        └── trianglefx/
            └── geometry/
                ├── LineEquationTest.java
                ├── LineIntersectionTest.java
                ├── LinearSystem2x2Test.java
                └── TriangleValidatorTest.java
```

---

## Screenshots

### Matrix input and rendered triangle

![TriangleFX screenshot 1](src/main/java/com/trianglefx/resources/Screenshot%202026-09-29%20at%2014.46.21.png)

### Alternate application view

![TriangleFX screenshot 2](src/main/java/com/trianglefx/resources/Screenshot%202026-09-29%20at%2014.46.35.png)

---

## Current Limitations

- Input mode is matrix-only; symbolic equation parsing (e.g., `x + y = 8`) is not yet implemented in the UI.
- The renderer focuses on triangle output and reference plane; original infinite line overlays are not yet drawn.

---

## Roadmap

- Add optional symbolic equation parser with robust normalization.
- Add overlay rendering for all three source lines.
- Expand automated UI/integration test coverage.
- Add export capability for rendered diagrams (image/vector output).
