package org.hunch.utils;

import org.hunch.dto.UserDetailsDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for formatted console output
 */
public final class PrintResultOutput {

    private PrintResultOutput() {
        // Private constructor to prevent instantiation
    }

    /**
     * Prints a collection of UserDetailsDTO items in a formatted box
     * Displays user_id, phone_number, and gender for each user
     *
     * @param title The title of the box
     * @param items Collection of UserDetailsDTO items to display
     */
    public static void printBox(String title, Collection<UserDetailsDTO> items) {
        if (items == null || items.isEmpty()) {
            printEmptyBox(title);
            return;
        }

        // Create header
        String header = String.format("%-40s | %-15s | %-10s", "User ID", "Phone Number", "Gender");

        // Format all user data lines
        List<String> formattedLines = new ArrayList<>();
        for (UserDetailsDTO user : items) {
            if (user == null) continue;

            String userId = user.getUser_id() != null ? user.getUser_id() : "N/A";
            String phoneNumber = user.getPhone_number() != null ? user.getPhone_number() : "N/A";
            String gender = user.getGender() != null ? user.getGender().toString() : "N/A";

            String line = String.format("%-40s | %-15s | %-10s", userId, phoneNumber, gender);
            formattedLines.add(line);
        }

        // Calculate box width (minimum width for header)
        int boxWidth = Math.max(title.length(), header.length()) + 4;

        // Print top border
        System.out.println("\n┌" + "─".repeat(boxWidth) + "┐");

        // Print title (centered)
        String titleLine = "│ " + title + " ".repeat(boxWidth - title.length() - 1) + "│";
        System.out.println(titleLine);

        // Print separator
        System.out.println("├" + "─".repeat(boxWidth) + "┤");

        // Print header
        String headerLine = "│ " + header + " ".repeat(boxWidth - header.length() - 1) + "│";
        System.out.println(headerLine);

        // Print header separator
        System.out.println("├" + "─".repeat(boxWidth) + "┤");

        // Print user data
        for (String line : formattedLines) {
            String dataLine = "│ " + line + " ".repeat(boxWidth - line.length() - 1) + "│";
            System.out.println(dataLine);
        }

        // Print bottom border
        System.out.println("└" + "─".repeat(boxWidth) + "┘");
    }

    /**
     * Prints an empty box with a message
     *
     * @param title The title of the box
     */
    private static void printEmptyBox(String title) {
        int boxWidth = Math.max(title.length(), 20) + 4;
        String emptyMessage = "No items";

        System.out.println("\n┌" + "─".repeat(boxWidth) + "┐");

        String titleLine = "│ " + title + " ".repeat(boxWidth - title.length() - 1) + "│";
        System.out.println(titleLine);

        System.out.println("├" + "─".repeat(boxWidth) + "┤");

        String emptyLine = "│ " + emptyMessage + " ".repeat(boxWidth - emptyMessage.length() - 1) + "│";
        System.out.println(emptyLine);

        System.out.println("└" + "─".repeat(boxWidth) + "┘");
    }

    /**
     * Prints a simple message box
     *
     * @param title   The title of the box
     * @param message The message to display
     */
    public static void printMessageBox(String title, String message) {
        int maxWidth = Math.max(title.length(), message.length());
        int boxWidth = maxWidth + 4;

        System.out.println("\n┌" + "─".repeat(boxWidth) + "┐");

        String titleLine = "│ " + title + " ".repeat(boxWidth - title.length() - 1) + "│";
        System.out.println(titleLine);

        System.out.println("├" + "─".repeat(boxWidth) + "┤");

        String messageLine = "│ " + message + " ".repeat(boxWidth - message.length() - 1) + "│";
        System.out.println(messageLine);

        System.out.println("└" + "─".repeat(boxWidth) + "┘");
    }
}
