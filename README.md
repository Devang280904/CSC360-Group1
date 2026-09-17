# 📐 TriangleFX — Matrix Triangle Solver & Drawer

A beginner-friendly desktop **JavaFX** application that takes a simple **Matrix Equation** as input:

$$\begin{bmatrix} 
a_{11} & a_{12} \\ 
a_{21} & a_{22} \\ 
a_{31} & a_{32} 
\end{bmatrix} 
\cdot 
\begin{bmatrix} 
x \\ 
y 
\end{bmatrix} 
= 
\begin{bmatrix} 
b_1 \\ 
b_2 \\ 
b_3 
\end{bmatrix}$$

The software automatically decodes the underlying 2D linear equations, finds where the lines cross (the corners/vertices), verifies that a real open triangle is formed, and paints the interactive diagram with full geometric measurements.

---

## 📖 Complete Guide: From 0 to 100

### What is this project in simple words?
Imagine you have three straight sticks. If you lay them down on a table so they cross each other, the space trapped inside the three crossing sticks forms a **triangle** (a 3-sided shape).

This application allows you to enter the numbers that describe those three straight lines. Instead of typing complicated algebra equations, you enter the numbers directly in a clean **Matrix layout**:
- **Matrix A ($3 \times 2$):** Contains 6 numbers (the multipliers for $x$ and $y$ for each of the 3 lines).
- **Vector x ($2 \times 1$):** Shows $\begin{bmatrix} x \\ y \end{bmatrix}$ (the horizontal and vertical positions).
- **Vector B ($3 \times 1$):** Contains 3 numbers (the target constants on the right side of the equals sign).

Once you click **"Generate Diagram"**, the application:
1. Translates Row 1 into Line 1: $a_{11}x + a_{12}y = b_1$
2. Translates Row 2 into Line 2: $a_{21}x + a_{22}y = b_2$
3. Translates Row 3 into Line 3: $a_{31}x + a_{32}y = b_3$
4. Solves where the lines meet to find the three corners (Vertices A, B, and C).
5. Checks all safety rules (ensuring no lines run parallel like railroad tracks and no lines collapse into a flat point).
6. Draws the coordinate grid, axes, colored boundary lines, glowing corner dots, and shaded triangle!

---

## 🔑 Key Concepts Explained

| Concept | Plain English Explanation |
| :--- | :--- |
| **Matrix $A$ ($3 \times 2$)** | A grid of 3 rows and 2 columns holding 6 numbers. Each row defines one line. The first column is the weight for horizontal position ($x$), and the second column is the weight for vertical position ($y$). |
| **Vector $\mathbf{x}$ ($2 \times 1$)** | A column showing the two coordinate variables: $x$ (horizontal) and $y$ (vertical). |
| **Vector $B$ ($3 \times 1$)** | A column of 3 target numbers, one for each line equation. |
| **Vertex (Corner)** | The exact crossing point where two straight lines intersect. A triangle has 3 vertices ($A, B, C$). |
| **Perimeter** | The total walking distance if you walk all the way around the outside border of the triangle ($\text{side}_a + \text{side}_b + \text{side}_c$). |
| **Area** | The amount of flat surface enclosed inside the triangle, computed using Gauss's Shoelace formula. |
| **Centroid** | The physical center of gravity. If the triangle were cut out of cardboard, you could balance it on the tip of a pencil placed at the centroid! |
| **Parallel Lines (Error)** | Two lines that slope in the exact same direction (like railroad tracks). Because they never cross, no corner can form. |
| **Concurrent Lines (Error)** | Three lines that all pass through the exact same single pinpoint (like spokes on a bicycle wheel). Because they don't enclose any space, the area is 0. |

---

## 🚀 How to Build and Run

### Prerequisites
- **Java JDK 21+** installed
- **Apache Maven 3.8+** installed

### 1. Launch the Desktop App
```bash
mvn clean javafx:run
```

### 2. Run All Automated Unit Tests
```bash
mvn test
```

### 3. Generate Complete Javadoc Documentation
```bash
mvn javadoc:javadoc
```
The generated HTML documentation will be created in `target/reports/apidocs/index.html`.

---

## 🎨 User Interface Walkthrough

```
┌───────────────────────────────────────┬──────────────────────────────────────────┐
│              SIDEBAR                  │                 CANVAS                   │
│                                       │                                          │
│  [ a11  a12 ]   [ x ]     [ b1 ]      │        Y                                 │
│  [ a21  a22 ] · [ y ]  =  [ b2 ]      │        |        Line 1 (Rose)            │
│  [ a31  a32 ]             [ b3 ]      │        |   /                             │
│     (3x2)       (2x1)      (3x1)      │        |  /  Vertex A (5, 3)             │
│                                       │        | /   *                           │
│  [ Generate Diagram ]  [ Clear ]      │        |/   / \                          │
│                                       │  ------+---*---*-----> X                 │
│  UNDERSTOOD LINE EQUATIONS:           │        |    B   C                        │
│   L1: 1x + 1y = 8                     │        |   (1,-1)(1,7)                   │
│   L2: 1x - 1y = 2                     │        |                                 │
│   L3: 1x = 1                          │        |                                 │
│                                       │                                          │
│  GEOMETRIC PROPERTIES:                │                                          │
│   Vertex A: (5.00, 3.00)              │                                          │
│   Vertex B: (1.00, -1.00)             │                                          │
│   Vertex C: (1.00, 7.00)              │                                          │
│   Area: 16.0000 | Perimeter: 20.4853  │                                          │
└───────────────────────────────────────┴──────────────────────────────────────────┘
```

---

## 📊 Preset Matrix Examples

| Example / Shape | Matrix $A$ ($3 \times 2$) | Vector $B$ ($3 \times 1$) | Decoded Lines | Visual Result |
| :--- | :--- | :--- | :--- | :--- |
| **Default Example** | `[[1, 1], [1, -1], [1, 0]]` | `[8, 2, 1]` | $x+y=8$<br>$x-y=2$<br>$x=1$ | Corners at $(5, 3)$, $(1, -1)$, $(1, 7)$. **Area = 16.00**. |
| **Right Triangle** | `[[0, 1], [1, 0], [1, 1]]` | `[1, 2, 6]` | $y=1$<br>$x=2$<br>$x+y=6$ | $90^\circ$ right triangle at $(2, 1)$, $(2, 4)$, $(5, 1)$. **Area = 4.50**. |
| **Equilateral-like** | `[[0, 1], [1.732, -1], [1.732, 1]]` | `[0, 0, 6.928]` | $y=0$<br>$1.73x-y=0$<br>$1.73x+y=6.93$ | Corners at $(0, 0)$, $(2, 3.46)$, $(4, 0)$. **Area ≈ 6.93**. |
| **Oblique Triangle** | `[[2, -1], [1, 2], [3, -4]]` | `[4, 8, -12]` | $2x-y=4$<br>$x+2y=8$<br>$3x-4y=-12$ | Slanted general triangle with all sides non-perpendicular. |

---

## 🏗️ Project Architecture

```
src/
├── main/java/com/trianglefx/
│   ├── Main.java                     # Startup ignition switch
│   ├── model/
│   │   ├── Point.java                # Dot on graph paper (x, y)
│   │   ├── Line.java                 # Straight line (Ax + By = C)
│   │   └── Triangle.java             # Triangle with corners, sides, area
│   ├── parser/
│   │   ├── MatrixParser.java         # Reads raw matrix strings with 3x2 & 3x1 constraints
│   │   ├── EquationParser.java       # Translates text equations into lines
│   │   └── ParseException.java       # Syntax & formatting error alarm
│   ├── geometry/
│   │   ├── GeometryService.java      # Intersection solver and validation engine
│   │   └── GeometryException.java    # Geometric error alarm (parallel, concurrent, flat)
│   └── ui/
│       ├── TriangleApp.java          # Matrix input UI and control room
│       └── TriangleCanvas.java       # Responsive canvas artist
└── test/java/com/trianglefx/
    ├── parser/
    │   ├── MatrixParserTest.java     # Tests for matrix dimensions & text parsing
    │   └── EquationParserTest.java   # Tests for equation syntax parsing
    └── geometry/
        └── GeometryServiceTest.java  # Tests for intersections, area, and edge cases
```
