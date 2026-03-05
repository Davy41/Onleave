package com.example.Onleave.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    // ── Self-referencing relationship ──────────────────────────────────────────
    // An Employee can have a manager, who is also an Employee.
    // manager_id is a FK that points back to the same employees table.
    // This models the approval chain: each employee knows who their manager is.
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    // The reverse side: one manager can manage many employees
    @OneToMany(mappedBy = "manager")
    private List<Employee> subordinates;
    // ──────────────────────────────────────────────────────────────────────────

    // Many-to-One with Location
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    // One-to-One with EmployeeProfile (mapped from the profile side)
    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private EmployeeProfile profile;

    // One-to-Many: one employee submits many leave applications
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<LeaveApplication> leaveApplications;

    // Many-to-Many: an employee can approve many leave applications
    @ManyToMany(mappedBy = "approvers")
    private List<LeaveApplication> applicationsToApprove;

    public Employee() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Employee getManager() { return manager; }
    public void setManager(Employee manager) { this.manager = manager; }

    public List<Employee> getSubordinates() { return subordinates; }
    public void setSubordinates(List<Employee> subordinates) { this.subordinates = subordinates; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public EmployeeProfile getProfile() { return profile; }
    public void setProfile(EmployeeProfile profile) { this.profile = profile; }

    public List<LeaveApplication> getLeaveApplications() { return leaveApplications; }
    public void setLeaveApplications(List<LeaveApplication> leaveApplications) { this.leaveApplications = leaveApplications; }

    public List<LeaveApplication> getApplicationsToApprove() { return applicationsToApprove; }
    public void setApplicationsToApprove(List<LeaveApplication> applicationsToApprove) { this.applicationsToApprove = applicationsToApprove; }
}
