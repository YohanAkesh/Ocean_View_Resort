package com.app.util;

import java.util.Scanner;

//Input Validations
public class InputValidator {
    private static Scanner scanner = new Scanner(System.in);

    // Reads string
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }


     // Reads interger
    public static int readInteger(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input! Please enter a valid number.");
            }
        }
    }
    //Validators
    public static boolean isValidUsername(String username) {
        return username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }


    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }


    public static boolean isValidName(String name) {
        return !name.isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }


    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    public static Scanner getScanner() {
        return scanner;
    }
}
