package com.toolshop.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a simple CSV file from src/test/resources/testdata into rows for a TestNG DataProvider.
 * The first line is a header and is skipped. Values must not contain commas.
 */
public final class CsvDataReader {

    private CsvDataReader() {
    }

    public static Object[][] read(String fileName) {
        String path = "testdata/" + fileName;
        List<Object[]> rows = new ArrayList<>();
        try (InputStream in = CsvDataReader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Test data file not found: " + path);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                rows.add(line.split(",", -1));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + path, e);
        }
        return rows.toArray(new Object[0][]);
    }
}
