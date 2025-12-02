package org.bigcompany.controller;

import org.bigcompany.util.CsvEmployeeReader;
import org.bigcompany.model.Employee;
import org.bigcompany.service.OrgAnalyzerService;

import java.util.List;
import java.util.Map;

public class OrgAnalyzerController {

    public void run() {
        CsvEmployeeReader reader = new CsvEmployeeReader();
        List<Employee> employees = reader.read("employees.csv");

        OrgAnalyzerService service = new OrgAnalyzerService(employees);

        System.out.println("--- UNDERPAID MANAGERS ---");
        print(service.managersUnderpaid());

        System.out.println("\n--- OVERPAID MANAGERS ---");
        print(service.managersOverpaid());

        System.out.println("\n--- LONG REPORTING LINES (>4) ---");
        print(service.employeesWithTooLongReportingLines());
    }

    private void print(Map<?, ?> map) {
        if (map.isEmpty()) System.out.println("None");
        else map.forEach((k, v) -> System.out.println(k + " -> " + v));
    }
}