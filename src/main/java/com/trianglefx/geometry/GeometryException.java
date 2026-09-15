package com.trianglefx.geometry;

/**
 * Exception thrown when geometric validation fails (e.g. parallel lines or degenerate triangle).
 */
public class GeometryException extends Exception {
    public GeometryException(String message) {
        super(message);
    }
}
