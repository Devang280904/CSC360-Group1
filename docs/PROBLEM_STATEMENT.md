# Problem Statement

## Title
JavaFX Triangle Drawer Using Matrix Representation ($A \cdot \mathbf{x} = B$)

## Objective
Build a JavaFX application where a user enters a **Matrix System** representing three 2D lines:

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

The application automatically decodes the underlying 2D linear equations from the matrix, computes the pairwise intersection points of the lines, validates that a non-degenerate triangle is formed, and renders the triangle interactively on a canvas with comprehensive geometric measurements.

## Functional Requirements

1. **Fixed Matrix Input:** The UI shall provide a matrix input grid with fixed constraints:
   - Matrix $A$: strictly $3 \times 2$ (3 rows, 2 columns of coefficients for $x$ and $y$).
   - Vector $\mathbf{x}$: visible fixed $2 \times 1$ column vector $\begin{bmatrix} x \\ y \end{bmatrix}$.
   - Vector $B$: strictly $3 \times 1$ (3 rows, 1 column of constants).
2. **Equation Understanding:** The system shall decode each matrix row into a standard linear equation:
   - Row 1 $\rightarrow L_1: a_{11}x + a_{12}y = b_1$
   - Row 2 $\rightarrow L_2: a_{21}x + a_{22}y = b_2$
   - Row 3 $\rightarrow L_3: a_{31}x + a_{32}y = b_3$
3. **Intersection Solver:** The system shall compute the 3 intersection points (corners) using Cramer's rule / $2 \times 2$ determinants.
4. **Geometric Validation:** The system shall verify that the lines are non-parallel, non-coincident, non-concurrent, and form a non-degenerate triangle ($\text{Area} > 10^{-6}$).
5. **Interactive Rendering:** The system shall draw the triangle, coordinate axes, extended dashed boundary lines, background grid, and labeled vertex coordinates on a responsive canvas.
6. **Properties Readout:** The system shall display computed side lengths, perimeter, surface area (via Shoelace formula), centroid, and the augmented matrix $[A \mid B]$.
7. **Error Handling:** The system shall display user-friendly error banners for invalid input formats or impossible geometry.

## Example Inputs

- Matrix $A$: `[[1, 1], [1, -1], [1, 0]]`
- Vector $B$: `[8, 2, 1]`

**Decoded Equations:**
- $L_1: 1x + 1y = 8$
- $L_2: 1x - 1y = 2$
- $L_3: 1x + 0y = 1$

**Computed Result:**
- Vertices: $(5, 3)$, $(1, -1)$, and $(1, 7)$.
- Area: $16.00$.
