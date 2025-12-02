package org.bigcompany.controller;

import org.bigcompany.util.CsvEmployeeReader;
import org.bigcompany.model.Employee;
import org.bigcompany.service.OrgAnalyzerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class OrgAnalyzerController {

    private final OrgAnalyzerService service;

    public OrgAnalyzerController(CsvEmployeeReader reader) {
        // Load employees once from CSV
        List<Employee> employees = reader.read("employees.csv");
        this.service = new OrgAnalyzerService(employees);
    }

    @GetMapping("/api/underpaid")
    public Map<String, Object> getUnderpaidManagers() {
        return Map.of("underpaidManagers", service.managersUnderpaid());
    }

    @GetMapping("/api/overpaid")
    public Map<String, Object> getOverpaidManagers() {
        return Map.of("overpaidManagers", service.managersOverpaid());
    }

    @GetMapping("/api/long-reporting-lines")
    public Map<String, Object> getLongReportingLines() {
        return Map.of("tooLongReportingLines", service.employeesWithTooLongReportingLines());
    }
}