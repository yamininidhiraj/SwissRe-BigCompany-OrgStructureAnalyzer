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
                new Employee("124", "Martin", "Chekov", new BigDecimal("145000"), "123"),
                new Employee("125", "Bob", "Ronstad", new BigDecimal("47000"), "123"),
                new Employee("300", "Alice", "Hasacat", new BigDecimal("50000"), "124"),
                new Employee("305", "Brett", "Hardleaf", new BigDecimal("34000"), "300"),
                new Employee("310", "Mark", "Zuckerberg", new BigDecimal("34000"), "305"),
                new Employee("313","Bill","Gates",new BigDecimal("20000"),"310")
        );
    }

    @Test
    public void testUnderpaidManagers() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, BigDecimal> underpaid = svc.managersUnderpaid();

        assertTrue(underpaid.containsKey("123"));
        assertTrue(underpaid.containsKey("305"));

        assertEquals(new BigDecimal("55200.000"), underpaid.get("123"));
        assertEquals(new BigDecimal("6800.000"), underpaid.get("305"));
    }

    @Test
    public void testOverpaidManagers() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, BigDecimal> overpaid = svc.managersOverpaid();

        assertTrue(overpaid.containsKey("124"));
        assertTrue(overpaid.containsKey("310"));

        assertEquals(new BigDecimal("70000.000"), overpaid.get("124"));
        assertEquals(new BigDecimal("4000.000"), overpaid.get("310"));
    }

    @Test
    public void testReportingLines() {
        OrgAnalyzerService svc = new OrgAnalyzerService(sampleEmployees());

        Map<String, Integer> tooLong = svc.employeesWithTooLongReportingLines();

        assertNotNull(tooLong);
        assertTrue(tooLong.containsKey("313"));
        assertEquals(5, tooLong.get("313"));
    }
}
