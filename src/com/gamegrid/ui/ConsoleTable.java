package com.gamegrid.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility for rendering aesthetic ASCII tables in the terminal.
 */
public class ConsoleTable {
    private final String[] headers;
    private final List<String[]> rows = new ArrayList<>();

    public ConsoleTable(String... headers) {
        this.headers = headers;
    }

    public void addRow(String... cells) {
        rows.add(cells);
    }

    public void print() {
        int columns = headers.length;
        int[] colWidths = new int[columns];

        for (int i = 0; i < columns; i++) {
            colWidths[i] = headers[i].length();
        }

        for (String[] row : rows) {
            for (int i = 0; i < Math.min(row.length, columns); i++) {
                if (row[i] != null && row[i].length() > colWidths[i]) {
                    colWidths[i] = row[i].length();
                }
            }
        }

        // Print top border
        printDivider(colWidths, "+", "+", "+");

        // Print header
        System.out.print("|");
        for (int i = 0; i < columns; i++) {
            System.out.print(" " + ConsoleColors.BOLD + ConsoleColors.CYAN + padRight(headers[i], colWidths[i]) + ConsoleColors.RESET + " |");
        }
        System.out.println();

        // Print header separator
        printDivider(colWidths, "+", "+", "+");

        // Print rows
        if (rows.isEmpty()) {
            System.out.print("|");
            int totalInner = 0;
            for (int w : colWidths) totalInner += (w + 3);
            totalInner -= 1;
            String emptyMsg = " No records found ";
            System.out.print(padCenter(emptyMsg, totalInner));
            System.out.println("|");
        } else {
            for (String[] row : rows) {
                System.out.print("|");
                for (int i = 0; i < columns; i++) {
                    String val = (i < row.length && row[i] != null) ? row[i] : "";
                    System.out.print(" " + padRight(val, colWidths[i]) + " |");
                }
                System.out.println();
            }
        }

        // Print bottom border
        printDivider(colWidths, "+", "+", "+");
    }

    private void printDivider(int[] colWidths, String left, String mid, String right) {
        System.out.print(left);
        for (int i = 0; i < colWidths.length; i++) {
            System.out.print("-".repeat(colWidths[i] + 2));
            if (i < colWidths.length - 1) {
                System.out.print(mid);
            }
        }
        System.out.println(right);
    }

    private String padRight(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        return text + " ".repeat(width - text.length());
    }

    private String padCenter(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        int diff = width - text.length();
        int left = diff / 2;
        int right = diff - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }
}
