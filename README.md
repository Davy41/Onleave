# Staff Leave & HR Request Management System

**Student:** Ibambasi Ganza Pierre Davy
**ID:** 26427
**University:** Adventist University of Central Africa
**Course:** Web Technology and Internet
**Date:** February 20, 2026

---

## Project Overview

A Spring Boot REST API backed by PostgreSQL that manages employee leave requests, approval chains, and leave balances. The system allows staff to submit leave applications which are routed through a manager approval chain, with leave balances tracked per employee.

**Tech Stack:** Java 21 · Spring Boot 4.0 · Spring Data JPA · PostgreSQL · Maven

---

## How to Run

1. Create a PostgreSQL database named `Onleave`
2. Update credentials in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Onleave
spring.datasource.username=postgres
spring.datasource.password=davy
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

3. Run the application:

```bash
./mvnw spring-boot:run
```

Hibernate will auto-create all tables on first run via `ddl-auto=update`.

---

## 1. Entity Relationship Diagram (ERD)

### Tables

The system uses **5 tables** covering all required relationship types.

```
+------------------+          +---------------------+
|    locations     |          |      employees      |
+------------------+          +---------------------+
| PK id            | 1------N | PK id               |
| province_code    |          | first_name          |
| province_name    |          | last_name           |
| district         |          | email (UNIQUE)      |
| sector           |          | department          |
+------------------+          | FK location_id      |
                               | FK manager_id ──┐  |  <- Self-referencing
                               +─────────────────┼──+
                                                 |
                                    (manager is also an Employee)

+---------------------+        +----------------------+
|  employee_profiles  |        |    leave_types       |
+---------------------+        +----------------------+
| PK id               |        | PK id                |
| phone_number        |        | type_name (UNIQUE)   |
| date_of_birth       |        | max_days_per_year    |
| gender              |        | requires_document    |
| hire_date           |        +----------+-----------+
| FK employee_id(UNIQ)|                   | 1
+----------+----------+                   |
           | 1-to-1                       | N
           |                              v
+----------+----------+        +----------+-----------+
|      employees      |        |  leave_applications  |
+---------------------+ 1---N  +----------------------+
                               | PK id                |
                               | start_date           |
                               | end_date             |
                               | status               |
                               | reason               |
                               | FK employee_id       |
                               | FK leave_type_id     |
                               +----------+-----------+
                                          |
                                     N    |    M
                                          v
                               +----------+-----------+
                               | application_approvers| <- Join Table (Many-to-Many)
                               +----------------------+
                               | FK application_id    |
                               | FK approver_id       |
                               +----------------------+
```

### Relationship Summary

| Relationship | Type | Tables Involved |
|---|---|---|
| Employee -> manager (self) | Self-referencing (Many-to-One) | employees.manager_id -> employees.id |
| Employee -> EmployeeProfile | One-to-One | employee_profiles.employee_id (UNIQUE) |
| Location -> Employee | One-to-Many | employees.location_id -> locations.id |
| Employee -> LeaveApplication | One-to-Many | leave_applications.employee_id |
| LeaveType -> LeaveApplication | One-to-Many | leave_applications.leave_type_id |
| LeaveApplication <-> Employee | Many-to-Many | application_approvers join table |

---

## 2. Saving a Location

**Logic:** The `Location` entity stores province information as plain columns (`province_code`, `province_name`) alongside `district` and `sector`. This avoids a separate Province table while still supporting province-based queries. Before saving, `existsByDistrict()` checks for duplicates.

**Entity — `Location.java`:**
```java
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "province_code", nullable = false, length = 10)
    private String provinceCode;   // e.g. "KG"

    @Column(name = "province_name", nullable = false, length = 100)
    private String provinceName;   // e.g. "Kigali City"

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "sector", nullable = false, length = 100)
    private String sector;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Employee> employees;
}
```

**Service — `LocationService.java`:**
```java
public Location saveLocation(Location location) {
    if (locationRepository.existsByDistrict(location.getDistrict())) {
        throw new RuntimeException("Location with district '"
            + location.getDistrict() + "' already exists.");
    }
    return locationRepository.save(location);
}
```

**Endpoint:** `POST /api/locations`

**Sample Request Body:**
```json
{
  "provinceCode": "KG",
  "provinceName": "Kigali City",
  "district": "Gasabo",
  "sector": "Kimironko"
}
```

---

## 3. Sorting and Pagination

**Logic:** Spring Data JPA's `Pageable` interface handles both sorting and pagination in a single object. `PageRequest.of(page, size, Sort)` builds the `Pageable`. Spring translates this into SQL `ORDER BY ... LIMIT ... OFFSET ...` automatically, which improves performance by only loading a subset of data rather than fetching all rows at once.

**Service — `EmployeeService.java`:**
```java
public Page<Employee> getAllEmployeesPaged(int page, int size, String sortBy) {
    // PageRequest.of builds a Pageable with:
    //   page  = 0-indexed page number (0 = first page)
    //   size  = number of records per page
    //   Sort  = field and direction to sort by
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
    return employeeRepository.findAll(pageable);
}
```

**Repository — `EmployeeRepository.java`:**
```java
// Spring Data JPA automatically generates the SQL with LIMIT/OFFSET
Page<Employee> findAll(Pageable pageable);

// Filtered + paged: only employees of a given department
Page<Employee> findByDepartment(String department, Pageable pageable);
```

**Controller — `EmployeeController.java`:**
```java
// GET /api/employees?page=0&size=5&sortBy=lastName
@GetMapping
public ResponseEntity<Page<Employee>> getAllPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "lastName") String sortBy) {
    return ResponseEntity.ok(employeeService.getAllEmployeesPaged(page, size, sortBy));
}
```

**Why pagination improves performance:** Without pagination, a query like `SELECT * FROM employees` returns every row in the table. With pagination, only `size` rows are returned per request. As the dataset grows, this dramatically reduces memory usage and response time.

**Sample Response (Page metadata included):**
```json
{
  "content": [ ... ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

---

## 4. Many-to-Many Relationship

**Logic:** A `LeaveApplication` can be reviewed by multiple approvers (e.g. direct manager, then HR manager). Equally, one `Employee` can approve many different applications. This is a Many-to-Many relationship. JPA manages it via a **join table** called `application_approvers` which holds two foreign key columns linking the two entities.

**Entity — `LeaveApplication.java`:**
```java
// @JoinTable defines the join table name and its two FK columns:
//   joinColumns        -> the FK pointing TO this entity (LeaveApplication)
//   inverseJoinColumns -> the FK pointing TO the other entity (Employee)
@ManyToMany
@JoinTable(
    name = "application_approvers",
    joinColumns = @JoinColumn(name = "application_id"),
    inverseJoinColumns = @JoinColumn(name = "approver_id")
)
private List<Employee> approvers;
```

**Entity — `Employee.java` (inverse side):**
```java
// mappedBy tells JPA that LeaveApplication owns the join table,
// so Employee is the non-owning (inverse) side — no duplicate table is created
@ManyToMany(mappedBy = "approvers")
private List<LeaveApplication> applicationsToApprove;
```

**Generated join table in PostgreSQL:**
```sql
CREATE TABLE application_approvers (
    application_id BIGINT REFERENCES leave_applications(id),
    approver_id    BIGINT REFERENCES employees(id),
    PRIMARY KEY (application_id, approver_id)
);
```

**Service — approve a leave application:**
```java
public LeaveApplication approveLeave(Long applicationId, Long approverId) {
    LeaveApplication app = leaveApplicationRepository.findById(applicationId)
        .orElseThrow(() -> new RuntimeException("Application not found"));
    Employee approver = employeeRepository.findById(approverId)
        .orElseThrow(() -> new RuntimeException("Approver not found"));

    app.getApprovers().add(approver);   // inserts a row into application_approvers
    app.setStatus("APPROVED");
    return leaveApplicationRepository.save(app);
}
```

**Endpoint:** `PUT /api/leave-applications/{id}/approve?approverId=2`

---

## 5. One-to-Many Relationship

**Logic:** One `Employee` can submit many `LeaveApplication` records over time. The foreign key `employee_id` lives on the `leave_applications` table (the "many" side). JPA maps this with `@OneToMany` on Employee and `@ManyToOne` on LeaveApplication.

**Entity — `Employee.java` (one side):**
```java
// mappedBy = "employee" tells JPA the foreign key is managed by
// the "employee" field in LeaveApplication, not a separate column here
@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
private List<LeaveApplication> leaveApplications;
```

**Entity — `LeaveApplication.java` (many side):**
```java
// @JoinColumn places the FK column "employee_id" in the leave_applications table
@ManyToOne
@JoinColumn(name = "employee_id", nullable = false)
private Employee employee;
```

**The same pattern applies to LeaveType -> LeaveApplication:**
```java
// In LeaveApplication.java
@ManyToOne
@JoinColumn(name = "leave_type_id", nullable = false)
private LeaveType leaveType;
```

---

## 6. One-to-One Relationship

**Logic:** Each `Employee` has exactly one `EmployeeProfile` containing personal details (phone, DOB, gender, hire date). The `employee_id` column in `employee_profiles` is both a foreign key and has a `UNIQUE` constraint — this is what enforces the One-to-One: no two profiles can reference the same employee.

**Entity — `EmployeeProfile.java` (owning side):**
```java
// @OneToOne + @JoinColumn = this entity holds the FK column
// unique = true enforces the one-to-one constraint at the DB level
@OneToOne
@JoinColumn(name = "employee_id", unique = true, nullable = false)
private Employee employee;
```

**Entity — `Employee.java` (inverse side):**
```java
// mappedBy = "employee" means EmployeeProfile owns the FK
// cascade = ALL: saving/deleting Employee also affects the profile
@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
private EmployeeProfile profile;
```

**Generated SQL:**
```sql
CREATE TABLE employee_profiles (
    id            BIGINT PRIMARY KEY,
    phone_number  VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender        VARCHAR(10) NOT NULL,
    hire_date     DATE NOT NULL,
    employee_id   BIGINT UNIQUE NOT NULL REFERENCES employees(id)
    --                   ^^^^^^ enforces one-to-one
);
```

---

## 7. Self-Referencing Relationship

**Logic:** An `Employee` can have a manager who is also an `Employee`. The `manager_id` column in the `employees` table is a foreign key that references the same table's `id` column. This models the approval hierarchy — an employee's leave is approved by their manager, who may also need approval from their own manager. Top-level employees (e.g. CEO) have `manager_id = NULL`.

**Entity — `Employee.java`:**
```java
// manager_id FK points back to employees.id (same table)
@ManyToOne
@JoinColumn(name = "manager_id")
private Employee manager;

// Reverse: one manager supervises many employees
@OneToMany(mappedBy = "manager")
private List<Employee> subordinates;
```

**Generated SQL:**
```sql
CREATE TABLE employees (
    id          BIGINT PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) UNIQUE NOT NULL,
    department  VARCHAR(100) NOT NULL,
    location_id BIGINT REFERENCES locations(id),
    manager_id  BIGINT REFERENCES employees(id)  -- self-reference
);
```

---

## 8. existsBy() Method

**Logic:** `existsBy()` is a Spring Data JPA derived query method. Spring reads the method name and generates a `SELECT COUNT(*) > 0 WHERE field = ?` query automatically — no SQL or JPQL needed. It returns `true` if at least one matching record exists. This is used throughout the project to prevent duplicates before saving.

**Examples across repositories:**

```java
// EmployeeRepository.java
// Prevents saving two employees with the same email
boolean existsByEmail(String email);

// LocationRepository.java
// Prevents saving two locations with the same district
boolean existsByDistrict(String district);

// LeaveTypeRepository.java
// Prevents duplicate leave type names (e.g. two "Annual" types)
boolean existsByTypeName(String typeName);

// EmployeeProfileRepository.java
// Ensures an employee does not get two profiles
boolean existsByEmployeeId(Long employeeId);

// LeaveApplicationRepository.java
// Prevents submitting a new application if one is already PENDING
boolean existsByEmployeeIdAndStatus(Long employeeId, String status);
```

**Usage in service:**
```java
public Employee saveEmployee(Employee employee) {
    if (employeeRepository.existsByEmail(employee.getEmail())) {
        throw new RuntimeException("Email already registered.");
    }
    return employeeRepository.save(employee);
}
```

---

## 9. Retrieve Employees by Province Code OR Province Name

**Logic:** Since province data is stored as plain columns on the `Location` table, we use a JPQL query that joins `Employee` to `Location` and filters by either `provinceCode` or `provinceName`. The `OR` condition means only one of the two parameters needs to match. `LOWER()` on both sides makes the name search case-insensitive.

**Repository — `EmployeeRepository.java`:**
```java
// JPQL traverses the Employee -> Location relationship using the entity field name,
// not the column name. "e.location" refers to the @ManyToOne field in Employee.
@Query("SELECT e FROM Employee e JOIN e.location l " +
       "WHERE l.provinceCode = :code OR LOWER(l.provinceName) = LOWER(:name)")
List<Employee> findByProvinceCodeOrName(
    @Param("code") String code,
    @Param("name") String name
);
```

**Service — `EmployeeService.java`:**
```java
public List<Employee> getEmployeesByProvince(String code, String name) {
    return employeeRepository.findByProvinceCodeOrName(code, name);
}
```

**Controller — `EmployeeController.java`:**
```java
// GET /api/employees/province?code=KG
// GET /api/employees/province?name=Kigali City
// GET /api/employees/province?code=KG&name=Kigali City
@GetMapping("/province")
public ResponseEntity<List<Employee>> getByProvince(
        @RequestParam(defaultValue = "") String code,
        @RequestParam(defaultValue = "") String name) {
    return ResponseEntity.ok(employeeService.getEmployeesByProvince(code, name));
}
```

---

## Full API Endpoint Reference

### Locations
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/locations` | Save a new location |
| GET | `/api/locations` | Get all locations |
| GET | `/api/locations/{id}` | Get location by ID |
| GET | `/api/locations/province/code/{code}` | Get locations by province code |
| GET | `/api/locations/province/name/{name}` | Get locations by province name |

### Employees
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/employees` | Save a new employee |
| GET | `/api/employees?page=0&size=10&sortBy=lastName` | Get all employees (paged + sorted) |
| GET | `/api/employees/{id}` | Get employee by ID |
| GET | `/api/employees/province?code=KG&name=Kigali City` | Get employees by province |

### Leave Types
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/leave-types` | Save a new leave type |
| GET | `/api/leave-types` | Get all leave types |
| GET | `/api/leave-types/{id}` | Get leave type by ID |

### Leave Applications
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/leave-applications` | Submit a leave application |
| GET | `/api/leave-applications?status=PENDING&page=0&size=10` | Get applications by status (paged) |
| GET | `/api/leave-applications/{id}` | Get application by ID |
| GET | `/api/leave-applications/employee/{employeeId}` | Get all applications for an employee |
| PUT | `/api/leave-applications/{id}/approve?approverId=2` | Approve a leave application |

---

## Sample Request Bodies

**Create Location:**
```json
{
  "provinceCode": "KG",
  "provinceName": "Kigali City",
  "district": "Gasabo",
  "sector": "Kimironko"
}
```

**Create Employee:**
```json
{
  "firstName": "Patrick",
  "lastName": "Dushimimana",
  "email": "patrick@auca.ac.rw",
  "department": "IT",
  "location": { "id": 1 },
  "manager": { "id": 2 }
}
```

**Create Leave Type:**
```json
{
  "typeName": "Annual",
  "maxDaysPerYear": 21,
  "requiresDocument": false
}
```

**Submit Leave Application:**
```json
{
  "startDate": "2026-03-01",
  "endDate": "2026-03-05",
  "reason": "Family event",
  "employee": { "id": 1 },
  "leaveType": { "id": 1 }
}
```

---

## Project Structure

```
src/main/java/com/example/Onleave/
├── OnleaveApplication.java
├── controller/
│   ├── EmployeeController.java
│   ├── LeaveApplicationController.java
│   ├── LeaveTypeController.java
│   └── LocationController.java
├── model/
│   ├── Employee.java               <- Self-referencing, One-to-One, One-to-Many, Many-to-Many
│   ├── EmployeeProfile.java        <- One-to-One with Employee
│   ├── LeaveApplication.java       <- Many-to-Many with Employee (approvers)
│   ├── LeaveType.java              <- One-to-Many with LeaveApplication
│   └── Location.java               <- One-to-Many with Employee
├── repository/
│   ├── EmployeeRepository.java     <- existsBy, province query, pagination
│   ├── EmployeeProfileRepository.java
│   ├── LeaveApplicationRepository.java
│   ├── LeaveTypeRepository.java
│   └── LocationRepository.java
└── service/
    ├── EmployeeService.java
    ├── LeaveApplicationService.java
    ├── LeaveTypeService.java
    └── LocationService.java
```
