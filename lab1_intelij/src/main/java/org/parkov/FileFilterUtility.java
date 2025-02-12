package org.parkov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class FileFilterUtility {

    private String outputPath = ".";
    private String prefix = "";
    private boolean appendMode = false;
    private boolean fullStats = false;

    private final Set<String> readFiles = new HashSet<>();
    private final Set<Integer> integers = new HashSet<>();
    private final Set<Double> floats = new HashSet<>();
    private final Set<String> strings = new HashSet<>();

    public void process(String[] args) throws IOException {
        parseArguments(args);

        for (String filePath : readFiles) {
            if (filePath.startsWith("-")) {
                continue;
            }

            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    classifyAndStore(line);
                }
            } catch (IOException e) {
                System.err.println("Failed to read file: " + filePath);
            }
        }

        writeResults();
        printStatistics();
    }

    private void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o" -> outputPath = args[++i];
                case "-p" -> prefix = args[++i];
                case "-a" -> appendMode = true;
                case "-f" -> fullStats = true;
                case "-s" -> fullStats = false;
                default -> {
                    if (args[i].startsWith("-")) {
                        throw new IllegalArgumentException("Unknown argument: " + args[i]);
                    }

                    readFiles.add(args[i]);
                }
            }
        }
    }

    private void classifyAndStore(String line) {
        if (line.matches("^-?\\d+$")) { // Integer
            integers.add(Integer.valueOf(line));
        } else if (line.matches("^-?\\d*\\.\\d+$")) { // Float
            floats.add(Double.valueOf(line));
        } else { // String
            strings.add(line);
        }
    }

    private void writeResults() throws IOException {
        if (!integers.isEmpty()) writeToFile(integers, "integers.txt");
        if (!floats.isEmpty()) writeToFile(floats, "floats.txt");
        if (!strings.isEmpty()) writeToFile(strings, "strings.txt");
    }

    private <T> void writeToFile(Set<T> data, String fileName) throws IOException {
        Path filePath = Paths.get(outputPath, prefix + fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                appendMode ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
            for (T item : data) writer.write(item.toString() + System.lineSeparator());
        }
    }

    private void printStatistics() {
        System.out.println("Statistics:");
        printNumberStats("Integers", integers);
        printNumberStats("Floats", floats);
        printStringStats("Strings", strings);
    }

    private <T extends Number> void printNumberStats(String type, Set<T> data) {
        if (data.isEmpty()) return;

        System.out.println(type + ":");
        System.out.println("- Count: " + data.size());

        if (fullStats) {
            double sum = data.stream().mapToDouble(Number::doubleValue).sum();
            double min = data.stream().mapToDouble(Number::doubleValue).min().orElse(0);
            double max = data.stream().mapToDouble(Number::doubleValue).max().orElse(0);
            double avg = sum / data.size();

            System.out.println("- Min: " + min);
            System.out.println("- Max: " + max);
            System.out.println("- Sum: " + sum);
            System.out.println("- Average: " + avg);
        }
    }

    private void printStringStats(String type, Set<String> data) {
        if (data.isEmpty()) return;

        System.out.println(type + ":");
        System.out.println("- Count: " + data.size());

        if (fullStats) {
            int minLength = data.stream().mapToInt(String::length).min().orElse(0);
            int maxLength = data.stream().mapToInt(String::length).max().orElse(0);

            System.out.println("- Shortest length: " + minLength);
            System.out.println("- Longest length: " + maxLength);
        }
    }
}
