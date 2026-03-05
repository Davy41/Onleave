package com.example.Onleave.service;

import com.example.Onleave.model.LeaveType;
import com.example.Onleave.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeService(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public LeaveType saveLeaveType(LeaveType leaveType) {
        if (leaveTypeRepository.existsByTypeName(leaveType.getTypeName())) {
            throw new RuntimeException("Leave type '" + leaveType.getTypeName() + "' already exists.");
        }
        return leaveTypeRepository.save(leaveType);
    }

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }
}
