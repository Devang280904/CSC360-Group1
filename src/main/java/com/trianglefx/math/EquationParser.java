package com.trianglefx.math;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing text representations of linear equations into Line objects.
 */
public class EquationParser {
    
    /**
     * Parses a string equation into a standard form Line (Ax + By = C).
     * Supports formats like "x + y = 10", "-2x = 5", "y = 3x - 2".
     *
     * @param text The string representation of the equation.
     * @return A Line object with parsed coefficients A, B, and C.
     * @throws IllegalArgumentException if the equation is malformed or invalid.
     */
    public static Line parse(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Equation cannot be empty.");
        }
        
        // Remove all spaces and make lowercase
        String eq = text.replaceAll("\\s+", "").toLowerCase();
        
        if (!eq.contains("=")) {
            throw new IllegalArgumentException("Equation must contain an '=' sign.");
        }
        
        String[] parts = eq.split("=");
        if (parts.length > 2) {
            throw new IllegalArgumentException("Equation must have exactly one '=' sign.");
        }
        
        String leftSide = parts[0];
        String rightSide = parts.length > 1 ? parts[1] : "";
        
        double a = 0; // x coefficient
        double b = 0; // y coefficient
        double c = 0; // constant
        
        // Parse left side
        double[] leftCoeffs = parseSide(leftSide);
        a += leftCoeffs[0];
        b += leftCoeffs[1];
        c -= leftCoeffs[2]; // constant on left moves to right as negative
        
        // Parse right side
        double[] rightCoeffs = parseSide(rightSide);
        a -= rightCoeffs[0]; // x on right moves to left as negative
        b -= rightCoeffs[1]; // y on right moves to left as negative
        c += rightCoeffs[2]; // constant on right stays on right
        
        if (a == 0 && b == 0) {
            throw new IllegalArgumentException("Invalid equation: both x and y coefficients are 0.");
        }
        
        return new Line(a, b, c);
    }
    
    /**
     * Helper method to parse one side of an equation into aggregated coefficients.
     *
     * @param side The string representation of one side of the equation.
     * @return An array containing [xCoefficient, yCoefficient, constantTerm].
     */
    private static double[] parseSide(String side) {
        double[] result = new double[3]; // [x, y, const]
        if (side.isEmpty()) return result;
        
        // Fix starting without sign
        if (!side.startsWith("+") && !side.startsWith("-")) {
            side = "+" + side;
        }
        
        Pattern pattern = Pattern.compile("([+-]\\d*\\.?\\d*)([xy]?)");
        Matcher matcher = pattern.matcher(side);
        
        int lastMatchEnd = 0;
        while (matcher.find()) {
            if (matcher.start() != lastMatchEnd) {
                 throw new IllegalArgumentException("Invalid characters found in equation side: " + side);
            }
            lastMatchEnd = matcher.end();
            
            String coeffStr = matcher.group(1);
            String var = matcher.group(2);
            
            // Handle standalone + or -
            if (coeffStr.equals("+")) coeffStr = "+1";
            if (coeffStr.equals("-")) coeffStr = "-1";
            
            double coeff;
            try {
                coeff = Double.parseDouble(coeffStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in equation.");
            }
            
            if ("x".equals(var)) {
                result[0] += coeff;
            } else if ("y".equals(var)) {
                result[1] += coeff;
            } else {
                result[2] += coeff; // constant
            }
        }
        
        if (lastMatchEnd != side.length()) {
             throw new IllegalArgumentException("Unparseable sequence at end of equation.");
        }
        
        return result;
    }
}
