package org.bigcompany.controller;

import org.bigcompany.util.CsvEmployeeReader;
import org.bigcompany.model.Employee;
import org.bigcompany.service.OrgAnalyzerService;
import org.springframework.web.bind.annotation.*;

import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrgAnalyzerController {

    private OrgAnalyzerService service;
    private final CsvEmployeeReader reader;

    public OrgAnalyzerController(CsvEmployeeReader reader) {
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
    public Map<String, Object> loadFromCsv(@RequestParam(required = false) String path) {
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

        } catch (Exception ex) {
            return Map.of("error", "Failed to load CSV: " + ex.getMessage());
        }
    }

    private void printAnalysisToConsole(OrgAnalyzerService svc) {
        System.out.println("\n--- UNDERPAID MANAGERS ---");
        System.out.println("[Employee Id = Underpaid by Amount]");
        Map<String, ?> underpaid = svc.managersUnderpaid();
        System.out.println(underpaid.isEmpty() ? "None" : underpaid);

        System.out.println("\n--- OVERPAID MANAGERS ---");
        System.out.println("[Employee Id = Overpaid by Amount]");
        Map<String, ?> overpaid = svc.managersOverpaid();
        System.out.println(overpaid.isEmpty() ? "None" : overpaid);

        System.out.println("\n--- LONG REPORTING LINES (>4) ---");
        System.out.println("[Employee Id = Length of Reporting line]");
        Map<String, ?> longLines = svc.employeesWithTooLongReportingLines();
        System.out.println(longLines.isEmpty() ? "None" : longLines);
    }

    @GetMapping("/underpaid")
    public Map<String, Object> getUnderpaidManagers() {
        return Map.of("underpaidManagers", service.managersUnderpaid());
    }

    @GetMapping("/overpaid")
    public Map<String, Object> getOverpaidManagers() {
        return Map.of("overpaidManagers", service.managersOverpaid());
    }

    @GetMapping("/long-reporting-lines")
    public Map<String, Object> getLongReportingLines() {
        return Map.of("tooLongReportingLines", service.employeesWithTooLongReportingLines());
    }
}