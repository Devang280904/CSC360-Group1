package com.trianglefx.parser;

/**
 * A grammar and format alarm (exception) triggered when typed text cannot be understood as mathematical numbers.
 * <p>
 * This exception is thrown when:
 * </p>
 * <ul>
 *   <li>The input is completely blank or empty.</li>
 *   <li>An equation is missing an equals sign ({@code =}) or contains more than one equals sign.</li>
 *   <li>Unexpected letters or weird symbols (like {@code @}, {@code $}, {@code z}) are entered when only numbers, {@code x}, and {@code y} are allowed.</li>
 *   <li>A matrix text input does not provide exactly 9 numerical values (6 for the 3&times;2 matrix A and 3 for the 3&times;1 vector B).</li>
 * </ul>
 */
public class ParseException extends Exception {

    /**
     * Creates a new parsing error with an easy-to-read explanation of what was malformed.
     *
     * @param message an explanation telling the user what was wrong with their typed input
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a new parsing error with an explanation and links to the underlying error cause.
     *
     * @param message an explanation telling the user what was wrong with their typed input
     * @param cause   the technical underlying Java error that triggered this problem
     */
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
