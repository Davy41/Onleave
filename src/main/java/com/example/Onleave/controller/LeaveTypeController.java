package com.example.Onleave.controller;

import com.example.Onleave.model.LeaveType;
import com.example.Onleave.service.LeaveTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @PostMapping
    public ResponseEntity<LeaveType> save(@RequestBody LeaveType leaveType) {
        return ResponseEntity.ok(leaveTypeService.saveLeaveType(leaveType));
    }

    @GetMapping
    public ResponseEntity<List<LeaveType>> getAll() {
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes());
    }
}
