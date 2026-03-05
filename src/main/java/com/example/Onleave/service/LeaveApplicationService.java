package com.example.Onleave.service;

import com.example.Onleave.model.Employee;
import com.example.Onleave.model.LeaveApplication;
import com.example.Onleave.repository.EmployeeRepository;
import com.example.Onleave.repository.LeaveApplicationRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository,
                                   EmployeeRepository employeeRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.employeeRepository = employeeRepository;
    }

    public LeaveApplication applyForLeave(LeaveApplication application) {
        // existsBy: block duplicate PENDING applications for same employee
        if (leaveApplicationRepository.existsByEmployeeIdAndStatus(
                application.getEmployee().getId(), "PENDING")) {
            throw new RuntimeException("Employee already has a pending leave application.");
        }
        return leaveApplicationRepository.save(application);
    }

    // Approve a leave application and add the approver to the Many-to-Many list
    public LeaveApplication approveLeave(Long applicationId, Long approverId) {
        LeaveApplication app = leaveApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        Employee approver = employeeRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));

        app.getApprovers().add(approver);
        app.setStatus("APPROVED");
        return leaveApplicationRepository.save(app);
    }

    public List<LeaveApplication> getByEmployee(Long employeeId) {
        return leaveApplicationRepository.findByEmployeeId(employeeId);
    }

    // Paginated list of applications by status, sorted by startDate
    public Page<LeaveApplication> getByStatusPaged(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return leaveApplicationRepository.findByStatus(status, pageable);
    }

    public List<LeaveApplication> getAll() {
        return leaveApplicationRepository.findAll();
    }
}
