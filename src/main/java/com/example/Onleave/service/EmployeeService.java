package com.example.Onleave.service;

import com.example.Onleave.model.Employee;
import com.example.Onleave.repository.EmployeeRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        // existsBy: prevent duplicate emails before inserting
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("Email '" + employee.getEmail() + "' is already registered.");
        }
        return employeeRepository.save(employee);
    }

    // ── Pagination + Sorting ───────────────────────────────────────────────────
    // PageRequest.of(page, size, sort) builds a Pageable object.
    // - page: 0-indexed page number
    // - size: number of records per page (improves performance by limiting data loaded)
    // - Sort: defines order (e.g. by lastName ascending)
    // The returned Page<Employee> contains the data + metadata (totalPages, totalElements)
    public Page<Employee> getAllEmployeesPaged(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return employeeRepository.findAll(pageable);
    }

    public Page<Employee> getByDepartmentPaged(String department, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        return employeeRepository.findByDepartment(department, pageable);
    }
    // ──────────────────────────────────────────────────────────────────────────

    // Retrieve employees by province code OR province name
    public List<Employee> getEmployeesByProvince(String code, String name) {
        return employeeRepository.findByProvinceCodeOrName(code, name);
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
}
