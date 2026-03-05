package com.example.Onleave.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_name", nullable = false, unique = true, length = 100)
    private String typeName; // e.g. Annual, Sick, Maternity, Paternity

    @Column(name = "max_days_per_year", nullable = false)
    private int maxDaysPerYear;

    @Column(name = "requires_document", nullable = false)
    private boolean requiresDocument = false;

    // One-to-Many: one leave type can appear in many applications
    @OneToMany(mappedBy = "leaveType", cascade = CascadeType.ALL)
    private List<LeaveApplication> leaveApplications;

    public LeaveType() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public int getMaxDaysPerYear() { return maxDaysPerYear; }
    public void setMaxDaysPerYear(int maxDaysPerYear) { this.maxDaysPerYear = maxDaysPerYear; }

    public boolean isRequiresDocument() { return requiresDocument; }
    public void setRequiresDocument(boolean requiresDocument) { this.requiresDocument = requiresDocument; }

    public List<LeaveApplication> getLeaveApplications() { return leaveApplications; }
    public void setLeaveApplications(List<LeaveApplication> leaveApplications) { this.leaveApplications = leaveApplications; }
}
