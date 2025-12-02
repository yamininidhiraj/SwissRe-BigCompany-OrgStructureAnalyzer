package org.bigcompany.service;

import org.bigcompany.model.Employee;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class OrgAnalyzerServiceTest {

    private List<Employee> sampleEmployees() {
        return List.of(
                new Employee("123", "Joe", "Doe", new BigDecimal("60000"), null),
                new Employee("124", "Martin", "Chekov", new BigDecimal("45000"), "123"),
                new Employee("125", "Bob", "Ronstad", new BigDecimal("47000"), "123"),
                new Employee("300", "Alice", "Hasacat", new BigDecimal("50000"), "124"),
                new Employee("305", "Brett", "Hardleaf", new BigDecimal("34000"), "300"),
                new Employee("310", "Mark", "Zuckerberg", new BigDecimal("34000"), "305")
        );
    }

    @Test
    public void testUnderpaidManagers() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, BigDecimal> underpaid = svc.managersUnderpaid();

        assertNotNull(underpaid);
    }

    @Test
    public void testOverpaidManagers() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, BigDecimal> overpaid = svc.managersOverpaid();

        assertNotNull(overpaid);
    }

    @Test
    public void testReportingLines() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, Integer> tooLong = svc.employeesWithTooLongReportingLines();

        assertNotNull(tooLong);
        assertFalse(tooLong.containsKey("310"));
        assertTrue(tooLong.isEmpty());
    }
}
