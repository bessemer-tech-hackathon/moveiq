package com.mobility.sla.metric;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobility.sla.entity.DatasetRow;
import com.mobility.sla.repository.DatasetRowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

@Service
public class MetricEngine {
    private final DatasetRowRepository datasetRows;
    private final ObjectMapper objectMapper;

    public MetricEngine(DatasetRowRepository datasetRows, ObjectMapper objectMapper) {
        this.datasetRows = datasetRows;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> getMetric(String key, LocalDate date, Map<String, String> filters) {
        List<Map<String, String>> records = records(filters);
        double value = switch (key) {
            case "OTA_RATE" -> percentage(records, this::isOnTime);
            case "SLA_BREACH_RATE" -> percentage(records, record -> !isOnTime(record));
            case "AVERAGE_DELAY" -> average(records, "delay_minutes", "delay", "delay_mins");
            case "DELAYED_EMPLOYEES" -> distinctCount(records, "stwid", record -> number(record, "delay_minutes", "delay", "delay_mins") > 0);
            case "NO_SHOW_RATE" -> percentage(records, this::isNoShow);
            case "TRANSPORT_SPEND" -> sum(records, "cost", "fare", "amount", "transport_cost");
            case "COST_PER_TRIP" -> ratio(sum(records, "cost", "fare", "amount", "transport_cost"), records.size());
            case "SAFETY_ALERT_VOLUME" -> countWhere(records, record -> hasAny(record, "alert", "event_type", "safety"));
            case "CRITICAL_SAFETY_ALERTS" -> countWhere(records, record -> containsAny(record, "critical", "panic", "sos"));
            case "OPEN_SAFETY_ALERTS" -> countWhere(records, record -> containsAny(record, "open", "unresolved"));
            case "EMPLOYEE_EXPERIENCE_SCORE" -> average(records, "rating", "experience_score", "employee_rating");
            case "LOW_RATING_RATE" -> percentage(records, record -> number(record, "rating", "experience_score", "employee_rating") < 3);
            case "DRIVER_RATING" -> average(records, "driver_rating");
            case "CAB_RATING" -> average(records, "cab_rating");
            case "SAFETY_RATING" -> average(records, "safety_rating");
            case "ROUTE_RATING" -> average(records, "route_rating");
            case "COST_PER_KILOMETER" -> ratio(sum(records, "cost", "fare", "amount"), sum(records, "distance_km", "distance"));
            default -> 0d;
        };
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("metricKey", key);
        result.put("title", key.replace('_', ' '));
        result.put("description", "Calculated from normalized CSV rows stored in SQLite");
        result.put("value", value);
        result.put("unit", key.contains("RATE") || key.endsWith("_RATING") ? "percent" : "count");
        result.put("sla", 0d);
        result.put("baseline", 0d);
        result.put("trend", 0d);
        result.put("status", "CALCULATED");
        result.put("period", date.toString());
        result.put("filters", filters);
        result.put("rowCount", records.size());
        return result;
    }

    public List<Map<String, Object>> dashboard(LocalDate date) {
        return List.of("OTA_RATE", "SLA_BREACH_RATE", "AVERAGE_DELAY", "DELAYED_EMPLOYEES", "NO_SHOW_RATE", "TRANSPORT_SPEND", "COST_PER_TRIP", "SAFETY_ALERT_VOLUME", "EMPLOYEE_EXPERIENCE_SCORE")
            .stream().map(key -> getMetric(key, date, Map.of())).toList();
    }

    public Map<String, Object> investigationEvidence(LocalDate date) {
        Map<String, Object> evidence = new LinkedHashMap<>();
        evidence.put("period", date.toString());
        evidence.put("metricSnapshot", dashboard(date));
        evidence.put("employeeGranularity", grouped("stwid"));
        evidence.put("vendorGranularity", grouped("vendor"));
        evidence.put("officeGranularity", grouped("office"));
        evidence.put("shiftGranularity", grouped("shift"));
        return evidence;
    }

    private List<Map<String, String>> records(Map<String, String> filters) {
        return datasetRows.findAll().stream().map(this::values)
            .filter(record -> filters.entrySet().stream().allMatch(filter -> filter.getValue().isBlank() || filter.getValue().equalsIgnoreCase(record.getOrDefault(filter.getKey(), ""))))
            .toList();
    }

    private Map<String, String> values(DatasetRow row) {
        try { return objectMapper.readValue(row.getValuesJson(), new TypeReference<>() {}); }
        catch (Exception exception) { throw new IllegalStateException("Invalid normalized dataset row " + row.getId(), exception); }
    }

    private List<Map<String, Object>> grouped(String key) {
        Map<String, List<Map<String, String>>> groups = new LinkedHashMap<>();
        records(Map.of()).forEach(record -> {
            String value = record.getOrDefault(key, "");
            if (!value.isBlank() && !(key.equals("stwid") && value.equals("0"))) groups.computeIfAbsent(value, ignored -> new ArrayList<>()).add(record);
        });
        return groups.entrySet().stream().map(entry -> Map.<String, Object>of("key", entry.getKey(), "rows", entry.getValue().size(), "averageDelay", average(entry.getValue(), "delay_minutes", "delay", "delay_mins"))).toList();
    }

    private double percentage(List<Map<String, String>> rows, Predicate<Map<String, String>> predicate) { return rows.isEmpty() ? 0 : rows.stream().filter(predicate).count() * 100d / rows.size(); }
    private long countWhere(List<Map<String, String>> rows, Predicate<Map<String, String>> predicate) { return rows.stream().filter(predicate).count(); }
    private double average(List<Map<String, String>> rows, String... keys) { return rows.stream().mapToDouble(row -> number(row, keys)).filter(value -> !Double.isNaN(value)).average().orElse(0); }
    private double sum(List<Map<String, String>> rows, String... keys) { return rows.stream().mapToDouble(row -> number(row, keys)).filter(value -> !Double.isNaN(value)).sum(); }
    private double ratio(double numerator, double denominator) { return denominator == 0 ? 0 : numerator / denominator; }
    private long distinctCount(List<Map<String, String>> rows, String key, Predicate<Map<String, String>> predicate) { return rows.stream().filter(predicate).map(row -> row.getOrDefault(key, "")).filter(value -> !value.isBlank() && !value.equals("0")).distinct().count(); }
    private double number(Map<String, String> row, String... keys) { for (String key : keys) { try { if (row.containsKey(key) && !row.get(key).isBlank()) return Double.parseDouble(row.get(key).replace(",", "")); } catch (NumberFormatException ignored) { } } return Double.NaN; }
    private boolean hasAny(Map<String, String> row, String... keys) { return Arrays.stream(keys).anyMatch(row::containsKey); }
    private boolean containsAny(Map<String, String> row, String... values) { return row.values().stream().anyMatch(value -> Arrays.stream(values).anyMatch(candidate -> value.toLowerCase(Locale.ROOT).contains(candidate))); }
    private boolean isOnTime(Map<String, String> row) { double delay = number(row, "delay_minutes", "delay", "delay_mins"); return !Double.isNaN(delay) && delay <= 0; }
    private boolean isNoShow(Map<String, String> row) { return containsAny(row, "no_show", "no show", "absent", "not boarded"); }
}
