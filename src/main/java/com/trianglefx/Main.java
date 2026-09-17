package com.trianglefx;

import com.trianglefx.ui.TriangleApp;

/**
 * The main starter class for the TriangleFX application.
 *
 * Think of this class as the "power button" or "ignition key" of the program.
 * When you run this software, Java starts executing here first, and this class
 * immediately wakes up and launches the graphical window ({@link TriangleApp}).
 */
public class Main {

    /**
     * Private constructor to prevent creating copies of this starter class.
     * Since this class is only used to kick off the application, nobody needs
     * to instantiate it with {@code new Main()}.
     */
    private Main() {
        // Starter class; no objects needed.
    }

    /**
     * The very first method executed by Java when the software is run.
     *
     * @param args any extra startup instructions or settings passed in from
     *             the terminal or command prompt (command-line arguments)
     */
    public static void main(String[] args) {
        TriangleApp.main(args);
    }
}
