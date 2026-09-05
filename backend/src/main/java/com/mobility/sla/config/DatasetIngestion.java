package com.mobility.sla.config;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Component
public class DatasetIngestion implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final Path datasetDirectory;

    public DatasetIngestion(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, @Value("${app.dataset.path}") String datasetPath) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.datasetDirectory = Paths.get(datasetPath).toAbsolutePath().normalize();
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        try {
             System.out.println("Data ingestion starting");
        
        if (!Files.isDirectory(datasetDirectory)) throw new IllegalStateException("Dataset directory does not exist: " + datasetDirectory);
        List<Path> csvFiles;
        try (var stream = Files.list(datasetDirectory)) { csvFiles = stream.filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")).sorted().collect(Collectors.toList()); }
        if (csvFiles.isEmpty()) throw new IllegalStateException("No CSV datasets found in " + datasetDirectory + "; startup stopped to prevent mocked metrics");
        for (Path csv : csvFiles) ingest(csv);

                System.out.println("Data ingestion Completed");
        } catch(Exception e) {
            System.out.println("Some error during ingestion");
            throw e;
        }
    }

    private void ingest(Path csv) throws Exception {
        String datasetName = csv.getFileName().toString();
        jdbcTemplate.update("DELETE FROM dataset_row WHERE dataset_name = ?", datasetName);

        try (BufferedReader reader = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) return;

            List<String> headers = parseLine(headerLine).stream().map(this::normalizeKey).toList();
            jdbcTemplate.execute((Connection connection) -> {
                try {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO dataset_row (dataset_name, row_number, values_json) VALUES (?, ?, ?)")) {
                        String line;
                        long rowNumber = 2;
                        int pending = 0;
                        while ((line = reader.readLine()) != null) {
                            List<String> values = parseLine(line);
                            if (values.stream().allMatch(String::isBlank)) {
                                rowNumber++;
                                continue;
                            }

                            Map<String, String> normalized = new LinkedHashMap<>();
                            for (int column = 0; column < headers.size(); column++) {
                                normalized.put(headers.get(column), column < values.size() ? values.get(column).trim() : "");
                            }
                            statement.setString(1, datasetName);
                            statement.setLong(2, rowNumber++);
                            statement.setString(3, objectMapper.writeValueAsString(normalized));
                            statement.addBatch();
                            if (++pending == 1000) {
                                statement.executeBatch();
                                pending = 0;
                            }
                        }
                        if (pending > 0) statement.executeBatch();
                    }
                } catch (Exception exception) {
                    throw new DataAccessException("Failed to ingest " + datasetName, exception) { };
                }
                return null;
            });
        }
    }

    private String normalizeKey(String value) { return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("(^_|_$)", ""); }
    private List<String> parseLine(String line) {
        List<String> values = new ArrayList<>(); StringBuilder current = new StringBuilder(); boolean quoted = false;
        for (int index = 0; index < line.length(); index++) { char character = line.charAt(index); if (character == '"') quoted = !quoted; else if (character == ',' && !quoted) { values.add(current.toString()); current.setLength(0); } else current.append(character); }
        values.add(current.toString()); return values;
    }
}