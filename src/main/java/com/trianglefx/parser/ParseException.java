package com.trianglefx.parser;

/**
 * Exception thrown when an equation string cannot be parsed into a valid linear equation.
 */
public class ParseException extends Exception {
    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
