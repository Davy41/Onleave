package com.example.Onleave.repository;

import com.example.Onleave.model.LeaveApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    // Get all applications for a specific employee
    List<LeaveApplication> findByEmployeeId(Long employeeId);

    // Get all applications with a specific status (PENDING, APPROVED, REJECTED)
    // with pagination and sorting support
    Page<LeaveApplication> findByStatus(String status, Pageable pageable);

    // existsBy: check if an employee already has a PENDING application
    boolean existsByEmployeeIdAndStatus(Long employeeId, String status);
}
