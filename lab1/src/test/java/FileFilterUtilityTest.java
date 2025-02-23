
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.parkov.FileFilterUtility;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileFilterUtilityTest {

    @TempDir
    Path tempDir;

    @Test
    void testSampleInput() throws Exception {
        // Create input files
        Path inputFile1 = tempDir.resolve("inputfile1.txt");
        Files.write(inputFile1, List.of(
                "java",
                "42",
                "-3.5",
                "programming",
                "0",
                "1000.01",
                "test",
                "123"
        ));

        Path inputFile2 = tempDir.resolve("inputfile2.txt");
        Files.write(inputFile2, List.of(
                "123",
                "45.67",
                "hello",
                "world",
                "-789",
                "3.14",
                "1 2 3"
        ));

        String[] args = {
                inputFile1.toString(),
                inputFile2.toString(),
                "-o", tempDir.toString(),
                "-f"
        };

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            FileFilterUtility utility = new FileFilterUtility();
            utility.process(args);
        } finally {
            System.setOut(originalOut);
        }

        // Check output files
        assertFileContents(tempDir.resolve("floats.txt"), Set.of("1000.01", "3.14", "-3.5", "45.67"));
        assertFileContents(tempDir.resolve("integers.txt"), Set.of("0", "-789", "42", "123"));
        assertFileContents(tempDir.resolve("strings.txt"), Set.of("java", "world", "test", "1 2 3", "hello", "programming"));

        // Verify statistics
        String output = outContent.toString();
        assertStatistics(output);
    }

    @Test
    void testPrefixOption() throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.write(inputFile, List.of("3.14", "-5", "test"));

        String[] args = {
                inputFile.toString(),
                "-o", tempDir.toString(),
                "-p", "myprefix"
        };

        FileFilterUtility utility = new FileFilterUtility();
        utility.process(args);

        assertTrue(Files.exists(tempDir.resolve("myprefixfloats.txt")));
        assertTrue(Files.exists(tempDir.resolve("myprefixintegers.txt")));
        assertTrue(Files.exists(tempDir.resolve("myprefixstrings.txt")));
    }

    @Test
    void testInvalidArgument() {
        String[] args = {"-z"};
        FileFilterUtility utility = new FileFilterUtility();
        assertThrows(IllegalArgumentException.class, () -> utility.process(args));
    }

    @Test
    void testShortStats() throws Exception {
        Path inputFile = tempDir.resolve("input.txt");
        Files.write(inputFile, List.of("3.14", "-5", "test"));

        String[] args = {
                inputFile.toString(),
                "-o", tempDir.toString(),
                "-s"
        };

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            FileFilterUtility utility = new FileFilterUtility();
            utility.process(args);
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();
        assertTrue(output.contains("Count"));
        assertFalse(output.contains("Min")); // Ensure full stats are not present
    }

    private void assertFileContents(Path file, Set<String> expected) throws Exception {
        Set<String> actual = new HashSet<>(Files.readAllLines(file));
        assertEquals(expected, actual);
    }

    private void assertStatistics(String output) {
        String[] lines = output.split(System.lineSeparator());

        // Check Integers section
        int intSection = findLineIndex(lines, "Integers:");
        assertLineEquals(lines[intSection + 1], "- Count: 4");
        assertLineEquals(lines[intSection + 2], "- Min: -789.0");
        assertLineEquals(lines[intSection + 3], "- Max: 123.0");
        assertLineEquals(lines[intSection + 4], "- Sum: -624.0");
        assertLineEquals(lines[intSection + 5], "- Average: -156.0");

        // Check Floats section
        int floatSection = findLineIndex(lines, "Floats:");
        assertLineEquals(lines[floatSection + 1], "- Count: 4");
        assertLineEquals(lines[floatSection + 2], "- Min: -3.5");
        assertLineEquals(lines[floatSection + 3], "- Max: 1000.01");
        assertEquals(1045.32, Double.parseDouble(lines[floatSection + 4].split(": ")[1]), 0.001);
        assertEquals(261.33, Double.parseDouble(lines[floatSection + 5].split(": ")[1]), 0.001);

        // Check Strings section
        int strSection = findLineIndex(lines, "Strings:");
        assertLineEquals(lines[strSection + 1], "- Count: 6");
        assertLineEquals(lines[strSection + 2], "- Shortest length: 4");
        assertLineEquals(lines[strSection + 3], "- Longest length: 11");
    }

    private int findLineIndex(String[] lines, String prefix) {
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].equals(prefix)) {
                return i;
            }
        }
        fail("Section not found: " + prefix);
        return -1;
    }

    private void assertLineEquals(String actual, String expected) {
        assertEquals(expected, actual.trim());
    }
}