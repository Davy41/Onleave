package com.example.Onleave.repository;

import com.example.Onleave.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // existsBy: checks if a location with the given district already exists
    // Spring Data JPA translates this method name into: SELECT COUNT(*) > 0 WHERE district = ?
    boolean existsByDistrict(String district);

    // Find all locations by province code (used to get employees in a province)
    List<Location> findByProvinceCode(String provinceCode);

    // Find all locations by province name (case-insensitive)
    List<Location> findByProvinceNameIgnoreCase(String provinceName);
}
