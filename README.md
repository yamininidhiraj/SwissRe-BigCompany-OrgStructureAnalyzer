# Org Structure Analyzer

A Java program that:

- Reads a CSV file.

- Builds an in-memory representation of the org chart.

- For each manager:

  1. Compute subordinate salary average.
  2. Check if salary is below required minimum (20% rule).
  3. Check if salary is above allowed maximum (50% rule).

- For each employee:
  1. Compute the number of managers between them and the CEO.
  2. Print if this number > 4.
  3. Print results clearly.

## Tech Stack

- Java SE 17

- Maven for build

- JUnit 5 for testing

## Build
```
mvn clean install
```

## Run locally

Run 
```
mvn spring-boot:run
```

## Project Structure

```
src/
 └── main/
     └── java/org/bigcompany/
         ├── controller/
         │     └── OrgAnalyzerController.java
         ├── model/
         │     └── Employee.java
         ├── service/
         │     └── OrgAnalyzerService.java  
         ├── util/
         │     └── CsvEmployeeReader.java
     └── resources/
           └── employees.csv
 └── test/
       └── OrgAnalyzerServiceTest.java  
```

## High-Level Architecture

### CsvEmployeeReader

- Responsible for reading CSV using BufferedReader.

- Converts each line into an Employee object.

- Returns a List<Employee>.

### OrgAnalyzerService

- Performs the core logic:

- Computes average subordinate salaries.

- Identifies underpaid and overpaid managers.

- Detects long reporting lines.

### OrgAnalyzerController

- Entry point.

- Loads the CSV.

- Calls service methods.

- Prints formatted results to the console.

### Example CSV

| Id  | firstName | lastName | salary |  managerId |  
|-----|---|---|--------|---|
| 123 | Joe | Doe | 60000  |
| 124 | Martin | Chekov | 45000  | 123
| 125 | Bob | Ronstad | 47000  | 123
| 300 | Alice | Hasacat | 50000  | 124
| 305 | Brett | Hardleaf | 34000  | 300

### Example Output

```
--- UNDERPAID MANAGERS ---
124 -> 15000.000

--- OVERPAID MANAGERS ---
None

--- LONG REPORTING LINES (>4) ---
None
```
