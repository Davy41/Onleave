package com.example.Onleave.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "leave_applications")
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    // ── One-to-Many (Many side): each application belongs to one employee ──────
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    // ──────────────────────────────────────────────────────────────────────────

    // One-to-Many (Many side): each application has one leave type
    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    // ── Many-to-Many: a leave application can have multiple approvers ──────────
    // A join table "application_approvers" is created automatically with two FK columns:
    //   application_id → leave_applications.id
    //   approver_id    → employees.id
    // One application can be approved by many employees (supervisor, HR manager, etc.)
    // One employee can approve many different applications.
    @ManyToMany
    @JoinTable(
        name = "application_approvers",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "approver_id")
    )
    private List<Employee> approvers;
    // ──────────────────────────────────────────────────────────────────────────

    public LeaveApplication() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public List<Employee> getApprovers() { return approvers; }
    public void setApprovers(List<Employee> approvers) { this.approvers = approvers; }
}
