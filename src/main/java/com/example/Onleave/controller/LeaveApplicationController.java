package com.example.Onleave.controller;

import com.example.Onleave.model.LeaveApplication;
import com.example.Onleave.service.LeaveApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-applications")
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
    }

    @PostMapping
    public ResponseEntity<LeaveApplication> apply(@RequestBody LeaveApplication application) {
        return ResponseEntity.ok(leaveApplicationService.applyForLeave(application));
    }

    // PUT /api/leave-applications/{id}/approve?approverId=2
    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveApplication> approve(
            @PathVariable Long id,
            @RequestParam Long approverId) {
        return ResponseEntity.ok(leaveApplicationService.approveLeave(id, approverId));
    }

    // GET /api/leave-applications?status=PENDING&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<LeaveApplication>> getByStatus(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(leaveApplicationService.getByStatusPaged(status, page, size));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveApplication>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveApplicationService.getByEmployee(employeeId));
    }
}
