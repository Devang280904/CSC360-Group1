package com.trianglefx.geometry;

/**
 * A safety alarm (exception) triggered when three lines fail to create a valid, real triangle.
 * <p>
 * In geometry, you need three straight lines that cross each other at three distinct points
 * to make a closed, 3-sided shape. This alarm goes off in situations like:
 * </p>
 * <ul>
 *   <li><b>Parallel lines:</b> Two lines run side-by-side like train tracks and never cross.</li>
 *   <li><b>Identical lines:</b> Two equations describe the exact same line drawn on top of itself.</li>
 *   <li><b>Concurrent lines:</b> All three lines cross at the exact same single pinpoint (like spokes on a wheel), creating zero interior space.</li>
 *   <li><b>Degenerate triangle:</b> The corners are so squished together or aligned in a straight line that the enclosed area is basically zero.</li>
 * </ul>
 */
public class GeometryException extends Exception {

    /**
     * Creates a new geometric error report with a clear explanation of what went wrong.
     *
     * @param message an easy-to-read explanation telling the user why a triangle
     *                could not be constructed from their numbers
     */
    public GeometryException(String message) {
        super(message);
    }
}
