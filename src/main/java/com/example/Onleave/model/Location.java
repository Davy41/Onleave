package com.example.Onleave.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Instead of a separate Province table, province info is stored as plain columns.
    // This keeps the schema minimal while still allowing queries by province code or name.
    @Column(name = "province_code", nullable = false, length = 10)
    private String provinceCode;

    @Column(name = "province_name", nullable = false, length = 100)
    private String provinceName;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "sector", nullable = false, length = 100)
    private String sector;

    // One Location has many Employees (One-to-Many)
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
    private List<Employee> employees;

    public Location() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProvinceCode() { return provinceCode; }
    public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }

    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }
}
