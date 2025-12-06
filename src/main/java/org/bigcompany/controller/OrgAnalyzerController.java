package org.bigcompany.controller;

import org.bigcompany.util.CsvEmployeeReader;
import org.bigcompany.model.Employee;
import org.bigcompany.service.OrgAnalyzerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrgAnalyzerController {

    private OrgAnalyzerService service;
    private final CsvEmployeeReader reader;

    public OrgAnalyzerController(CsvEmployeeReader reader) throws NoSuchFileException {
        this.reader = reader;
        this.service = null;
    }

    private OrgAnalyzerService loadDefaultService() throws NoSuchFileException {
        try {
            List<Employee> employees = reader.read(null); // handles default resource internally
            return new OrgAnalyzerService(employees);
        } catch (Exception e) {
            throw new NoSuchFileException ("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/load")
    public Map<String, Object> loadFromCsv(@RequestParam(required = false) String path) throws Exception {
        try {
            String message;

            if (path == null || path.isBlank()) {
                this.service = loadDefaultService();
                message = "Loaded Default CSV from resources. Report is printed to console and also available via APIs";
            } else {
                List<Employee> employees = reader.read(path);
                this.service = new OrgAnalyzerService(employees);
                message = "Loaded the provided CSV from: " + path + "  Report is printed to console and also available via APIs";
            }

            printAnalysisToConsole(this.service);

            return Map.of("status", message);

        } catch (NoSuchFileException e) {
            throw new NoSuchFileException("No CSV file loaded. " + e.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load CSV: " + ex.getMessage(), ex);
        }
    }

    private void printAnalysisToConsole(OrgAnalyzerService svc) {
        System.out.println("\n--- UNDERPAID MANAGERS ---");
        Map<String, ?> underpaid = svc.managersUnderpaid();
        underpaid.forEach((id, amount) -> {
            System.out.println("[Employee Id = " + id + "]");
            System.out.println("{Underpaid by =" + amount + "}");
        });
        if (underpaid.isEmpty())
            System.out.println("None");

        System.out.println("\n--- OVERPAID MANAGERS ---");
        Map<String, ?> overpaid = svc.managersOverpaid();
        overpaid.forEach((id, amount) -> {
            System.out.println("[Employee Id = " + id + "]");
            System.out.println("{Overpaid by =" + amount + "}");
        });
        if (overpaid.isEmpty())
            System.out.println("None");

        System.out.println("\n--- LONG REPORTING LINES (>4) ---");
        Map<String, ?> longLines = svc.employeesWithTooLongReportingLines();
        longLines.forEach((id, amount) -> {
            System.out.println("[Employee Id = " + id + "]");
            System.out.println("{long reporting Lines by =" + amount + "}");
        });
        if (longLines.isEmpty())
            System.out.println("None");
    }

    private void ensureCsvLoaded() throws NoSuchFileException {
        if (service == null) {
            this.service = loadDefaultService();
        }
    }

    @GetMapping("/underpaid")
    public List<Map<String, String>> getUnderpaidManagers() throws NoSuchFileException {
        ensureCsvLoaded();
        return service.managersUnderpaid().entrySet().stream()
                .map(e -> Map.of(
                        "employee ID: ", e.getKey(),
                        "underpaid by: ", e.getValue().toString()
                ))
                .toList();
    }

    @GetMapping("/overpaid")
    public List<Map<String, String>> getOverpaidManagers() throws NoSuchFileException {
        ensureCsvLoaded();
        return service.managersOverpaid().entrySet().stream()
                .map(e -> Map.of(
                        "employee ID: ", e.getKey(),
                        "overpaid by: ", e.getValue().toString()
                ))
                .toList();
    }

    @GetMapping("/long-reporting-lines")
    public List<Map<String, String>> getLongReportingLines() throws NoSuchFileException {
        ensureCsvLoaded();
        return service.employeesWithTooLongReportingLines().entrySet().stream()
                .map(e -> Map.of(
                        "employee ID: ", e.getKey(),
                        "reporting line too long by: ", e.getValue().toString()
                ))
                .toList();
    }

    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCsvNotFound(NoSuchFileException ex) {
        return Map.of("error", ex.getMessage());
    }
}