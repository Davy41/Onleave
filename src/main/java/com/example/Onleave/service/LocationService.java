package com.example.Onleave.service;

import com.example.Onleave.model.Location;
import com.example.Onleave.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location saveLocation(Location location) {
        // existsBy check: avoid saving duplicate districts
        if (locationRepository.existsByDistrict(location.getDistrict())) {
            throw new RuntimeException("Location with district '" + location.getDistrict() + "' already exists.");
        }
        return locationRepository.save(location);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public List<Location> getByProvinceCode(String code) {
        return locationRepository.findByProvinceCode(code);
    }

    public List<Location> getByProvinceName(String name) {
        return locationRepository.findByProvinceNameIgnoreCase(name);
    }
}
