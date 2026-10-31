package com.gamegrid.ui;

import java.util.Scanner;

/**
 * Robust console input scanner with validation and error-recovery.
 * Prevents application crashes from malformed user input.
 */
public class InputReader {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(ConsoleColors.YELLOW + prompt + ConsoleColors.RESET + ": ");
        return scanner.nextLine().trim();
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(ConsoleColors.RED + "Input cannot be empty. Please try again." + ConsoleColors.RESET);
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(ConsoleColors.YELLOW + prompt + " (" + min + "-" + max + ")" + ConsoleColors.RESET + ": ");
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println(ConsoleColors.RED + "Number must be between " + min + " and " + max + "." + ConsoleColors.RESET);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid number format. Please enter an integer." + ConsoleColors.RESET);
            }
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(ConsoleColors.YELLOW + prompt + ConsoleColors.RESET + ": $");
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf(ConsoleColors.RED + "Amount must be between $%.2f and $%.2f.\n" + ConsoleColors.RESET, min, max);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "Invalid price format. Please enter a valid number (e.g., 29.99)." + ConsoleColors.RESET);
            }
        }
    }

    public static boolean readBoolean(String prompt) {
        while (true) {
            System.out.print(ConsoleColors.YELLOW + prompt + " (y/n)" + ConsoleColors.RESET + ": ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("true") || input.equals("1")) {
                return true;
            }
            if (input.equals("n") || input.equals("no") || input.equals("false") || input.equals("0")) {
                return false;
            }
            System.out.println(ConsoleColors.RED + "Please answer with 'y' or 'n'." + ConsoleColors.RESET);
        }
    }

    public static void pause() {
        System.out.println(ConsoleColors.DIM + "\nPress Enter to return..." + ConsoleColors.RESET);
        scanner.nextLine();
    }
}
