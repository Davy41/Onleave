# Onleave Application

## Running the Application

1. Ensure PostgreSQL is running
2. Configure database in `application.properties`
3. Run: `mvn spring-boot:run`
4. Application starts at `http://localhost:8080`

## Testing Endpoints

### Employee Endpoints

#### Create Employee
```bash
curl -X POST http://localhost:8080/api/employees -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"john.doe@example.com","department":"IT"}'
```

#### Get All Employees (Paginated)
```bash
curl http://localhost:8080/api/employees?page=0&size=10&sortBy=lastName
```

#### Get Employees by Province
```bash
curl "http://localhost:8080/api/employees/province?code=KG&name=Kigali City"
```

#### Get Employee by ID
```bash
curl http://localhost:8080/api/employees/1
```

### Location Endpoints

#### Create Location
```bash
curl -X POST http://localhost:8080/api/locations -H "Content-Type: application/json" -d '{"provinceCode":"KG","provinceName":"Kigali City","district":"Gasabo","sector":"Remera"}'
```

#### Get All Locations
```bash
curl http://localhost:8080/api/locations
```

#### Get Locations by Province Code
```bash
curl http://localhost:8080/api/locations/province/code/KG
```

#### Get Locations by Province Name
```bash
curl http://localhost:8080/api/locations/province/name/Kigali%20City
```

### Leave Type Endpoints

#### Create Leave Type
```bash
curl -X POST http://localhost:8080/api/leave-types -H "Content-Type: application/json" -d '{"typeName":"Annual","maxDaysPerYear":21,"requiresDocument":false}'
```

#### Get All Leave Types
```bash
curl http://localhost:8080/api/leave-types
```

### Leave Application Endpoints

#### Apply for Leave
```bash
curl -X POST http://localhost:8080/api/leave-applications -H "Content-Type: application/json" -d '{"startDate":"2024-02-01","endDate":"2024-02-05","reason":"Family vacation","employee":{"id":1},"leaveType":{"id":1}}'
```

#### Approve Leave
```bash
curl -X PUT http://localhost:8080/api/leave-applications/1/approve?approverId=2
```

#### Get Leave Applications by Status (Paginated)
```bash
curl "http://localhost:8080/api/leave-applications?status=PENDING&page=0&size=10"
```

#### Get Leave Applications by Employee
```bash
curl http://localhost:8080/api/leave-applications/employee/1
```
