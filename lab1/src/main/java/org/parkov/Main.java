package org.parkov;

public class Main {
    public static void main(String[] args) {
        try {
            FileFilterUtility utility = new FileFilterUtility();
            utility.process(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}