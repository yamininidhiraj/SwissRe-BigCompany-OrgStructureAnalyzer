package org.bigcompany.controller;

import org.bigcompany.util.CsvEmployeeReader;
import org.bigcompany.model.Employee;
import org.bigcompany.service.OrgAnalyzerService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrgAnalyzerController {

    private OrgAnalyzerService service;
    private final CsvEmployeeReader reader;

    public OrgAnalyzerController(CsvEmployeeReader reader) {
        this.reader = reader;
        this.service = loadDefaultService();
    }

    private OrgAnalyzerService loadDefaultService() {
        try {
            File file = new ClassPathResource("employees.csv").getFile();
            List<Employee> employees = reader.read(file.getAbsolutePath());
            return new OrgAnalyzerService(employees);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load employees.csv from resources", e);
        }
    }

    @PostMapping("/load")
    public Map<String, Object> loadFromCsv(@RequestParam(required = false) String path) {

        try {
            if (path == null || path.isBlank()) {
                // Load default CSV
                this.service = loadDefaultService();
                return Map.of("status", "Loaded default employees.csv from resources");
            } else {
                // Load CSV provided by client
                List<Employee> employees = reader.read(path);
                this.service = new OrgAnalyzerService(employees);
                return Map.of("status", "Loaded employees from: " + path);
            }
        } catch (Exception ex) {
            return Map.of("error", "Failed to load CSV: " + ex.getMessage());
        }
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