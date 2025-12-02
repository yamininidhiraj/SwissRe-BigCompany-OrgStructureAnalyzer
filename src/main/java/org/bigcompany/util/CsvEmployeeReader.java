package org.bigcompany.util;

import org.bigcompany.model.Employee;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvEmployeeReader {

    public List<Employee> read(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) {
                System.out.println("No file provided. Reading default employees.csv from resources...");
                return readFromResource("employees.csv");
            } else {
                return readFromDisk(filePath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file: " + e.getMessage(), e);
        }
    }

    private List<Employee> readFromDisk(String path) throws IOException {
        return parse(Files.newBufferedReader(new File(path).toPath()));
    }

    private List<Employee> readFromResource(String name) throws IOException {
        ClassPathResource resource = new ClassPathResource(name);
        return parse(new BufferedReader(new InputStreamReader(resource.getInputStream())));
    }

    private List<Employee> parse(BufferedReader reader) throws IOException {
        List<Employee> list = new ArrayList<>();
        String line;
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) continue;

            String[] parts = line.split(",");

            String id = parts[0];
            String firstName = parts[1];
            String lastName = parts[2];
            BigDecimal salary = new BigDecimal(parts[3]);
            String managerId = parts.length > 4 && !parts[4].isBlank() ? parts[4] : null;

            list.add(new Employee(id, firstName, lastName, salary, managerId));
        }
        return list;
    }
}