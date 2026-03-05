package com.example.Onleave.repository;

import com.example.Onleave.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    // existsBy: prevent duplicate leave type names
    boolean existsByTypeName(String typeName);
}
