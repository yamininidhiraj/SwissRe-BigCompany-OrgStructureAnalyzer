package org.bigcompany.util;

import org.bigcompany.model.Employee;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CsvEmployeeReader {

    public List<Employee> read(String filePath) {
        List<Employee> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                CsvEmployeeReader.class.getClassLoader().getResourceAsStream(filePath)))) {
            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");

                String id = p[0].trim();
                String first = p[1].trim();
                String last = p[2].trim();
                BigDecimal salary = new BigDecimal(p[3].trim());
                String managerId = p.length > 4 ? p[4].trim() : null;

                list.add(new Employee(id, first, last, salary, managerId));
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to read CSV file", e);
        }
        return list;
    }
}