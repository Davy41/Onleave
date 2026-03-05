package com.example.Onleave.repository;

import com.example.Onleave.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // existsBy: checks if an employee with the given email already exists before saving
    // Prevents duplicate email registration without throwing a DB constraint error
    boolean existsByEmail(String email);

    // ── Retrieve employees by province code OR province name ──────────────────
    // Uses a JPQL JOIN to traverse: Employee → Location → province fields
    // The OR condition allows searching by either code (e.g. "KG") or name (e.g. "Kigali City")
    @Query("SELECT e FROM Employee e JOIN e.location l " +
           "WHERE l.provinceCode = :code OR LOWER(l.provinceName) = LOWER(:name)")
    List<Employee> findByProvinceCodeOrName(@Param("code") String code, @Param("name") String name);

    // ── Pagination + Sorting ───────────────────────────────────────────────────
    // Pageable carries both page number, page size, AND sort direction/field.
    // Spring Data JPA handles the LIMIT/OFFSET SQL automatically.
    // Example call: PageRequest.of(0, 10, Sort.by("lastName").ascending())
    Page<Employee> findAll(Pageable pageable);

    // Find employees by department with pagination and sorting
    Page<Employee> findByDepartment(String department, Pageable pageable);
}
