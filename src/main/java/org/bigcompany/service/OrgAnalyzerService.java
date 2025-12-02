package org.bigcompany.service;

import org.bigcompany.model.Employee;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrgAnalyzerService {

    private final List<Employee> employees;

    public OrgAnalyzerService(List<Employee> employees) {
        this.employees = employees;
    }

    // Compute avg subordinate salary per manager
    private Map<String, BigDecimal> computeAverageSubordinateSalary() {
        Map<String, List<Employee>> byManager =
                employees.stream()
                        .filter(e -> e.getManagerId() != null)
                        .collect(Collectors.groupingBy(Employee::getManagerId));

        Map<String, BigDecimal> avg = new HashMap<>();

        for (var en : byManager.entrySet()) {
            BigDecimal total = en.getValue().stream()
                    .map(Employee::getSalary)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal average = total.divide(BigDecimal.valueOf(en.getValue().size()), 2, RoundingMode.HALF_UP);
            avg.put(en.getKey(), average);
        }
        return avg;
    }

    /** Managers earning less than 20% above average subordinate salary */
    public Map<String, BigDecimal> managersUnderpaid() {
        Map<String, BigDecimal> avg = computeAverageSubordinateSalary();
        Map<String, BigDecimal> result = new HashMap<>();

        for (Employee e : employees) {
            if (!avg.containsKey(e.getId())) continue;

            BigDecimal requiredMin = avg.get(e.getId()).multiply(BigDecimal.valueOf(1.2));
            if (e.getSalary().compareTo(requiredMin) < 0) {
                result.put(e.getId(), requiredMin.subtract(e.getSalary()));
            }
        }
        return result;
    }

    /** Managers earning more than 50% above average subordinate salary */
    public Map<String, BigDecimal> managersOverpaid() {
        Map<String, BigDecimal> avg = computeAverageSubordinateSalary();
        Map<String, BigDecimal> result = new HashMap<>();

        for (Employee e : employees) {
            if (!avg.containsKey(e.getId())) continue;

            BigDecimal maxAllowed = avg.get(e.getId()).multiply(BigDecimal.valueOf(1.5));
            if (e.getSalary().compareTo(maxAllowed) > 0) {
                result.put(e.getId(), e.getSalary().subtract(maxAllowed));
            }
        }
        return result;
    }

    /** Reporting line too long (>4 levels) */
    public Map<String, Integer> employeesWithTooLongReportingLines() {
        Map<String, Employee> map = employees.stream()
                .collect(Collectors.toMap(Employee::getId, x -> x));

        Map<String, Integer> result = new HashMap<>();

        for (Employee e : employees) {
            int depth = 0;
            String cur = e.getManagerId();

            while (cur != null && map.containsKey(cur)) {
                depth++;
                cur = map.get(cur).getManagerId();

                if (depth > 4) {
                    result.put(e.getId(), depth - 4);
                    break;
                }
            }
        }
        return result;
    }
}