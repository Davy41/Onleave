package com.example.Onleave.controller;

import com.example.Onleave.model.Employee;
import com.example.Onleave.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // POST /api/employees
    // Body: {"firstName":"John","lastName":"Doe","email":"john.doe@example.com","department":"IT"}
    @PostMapping
    public ResponseEntity<Employee> save(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.saveEmployee(employee));
    }

    // GET /api/employees?page=0&size=10&sortBy=lastName
    @GetMapping
    public ResponseEntity<Page<Employee>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy) {
        return ResponseEntity.ok(employeeService.getAllEmployeesPaged(page, size, sortBy));
    }

    // GET /api/employees/province?code=KG&name=Kigali City
    @GetMapping("/province")
    public ResponseEntity<List<Employee>> getByProvince(
            @RequestParam(defaultValue = "") String code,
            @RequestParam(defaultValue = "") String name) {
        return ResponseEntity.ok(employeeService.getEmployeesByProvince(code, name));
    }

    // GET /api/employees/1
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getById(id));
    }
}
