package com.example.Onleave.repository;

import com.example.Onleave.model.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {

    // existsBy: check if a profile already exists for this employee
    boolean existsByEmployeeId(Long employeeId);

    EmployeeProfile findByEmployeeId(Long employeeId);
}
