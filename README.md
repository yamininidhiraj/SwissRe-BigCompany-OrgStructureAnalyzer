# Org Structure Analyzer

This project analyzes the organizational structure of a large company using employee information stored in a CSV file. 

It identifies:

- Underpaid managers (earning <20% above avg subordinate salary)

- Overpaid managers (earning >50% above avg subordinate salary)

- Employees with reporting lines deeper than 4 levels

The application exposes REST APIs instead of printing results to the console.

## Requirements

- Java 17

- Maven 3.6.3

- Spring Boot 3.1.4

## Notes & Assumptions

- CSV file always has valid formatting

- Salaries are numeric (BigDecimal)

- At most 1000 employees

- Reporting chain is tree-like (no circular references)

- Controller returns clean JSON responses

## Build
```
mvn clean install
```

## Run Service locally

Run Service
```
mvn spring-boot:run
```
Spring Boot will automatically:

- Start an embedded Tomcat server 

- Load the CSV file

- Initialize service + controller beans

- Expose REST APIs 

## API Endpoints
- POST http://localhost:8080/api/load?path=<path-to-csv-file>
- GET http://localhost:8080/api/underpaid
- GET http://localhost:8080/api/overpaid
- GET http://localhost:8080/api/long-reporting-lines

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
           └── images/...
 └── test/
       └── OrgAnalyzerServiceTest.java  
```

### Example Input

- Default CSV - [employees.csv](https://github.com/yamininidhiraj/SwissRe-BigCompany-OrgStructureAnalyzer/blob/main/src/main/resources/employees.csv)
- Provide path using Post call

POST http://localhost:8080/api/load?path=<path-to-csv-file>
![path-to-csv.png](src/main/resources/images/post-path-to-csv.png)

### Example Output

GET http://localhost:8080/api/underpaid
![get-underpaid.png](src/main/resources/images/get-underpaid.png)
GET http://localhost:8080/api/overpaid
![get-overpaid.png](src/main/resources/images/get-overpaid.png)
GET http://localhost:8080/api/long-reporting-lines
![get-long-reporting-lines.png](src/main/resources/images/get-long-reporting-lines.png)

