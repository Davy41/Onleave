package com.example.Onleave.controller;

import com.example.Onleave.model.Location;
import com.example.Onleave.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<Location> save(@RequestBody Location location) {
        return ResponseEntity.ok(locationService.saveLocation(location));
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAll() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/province/code/{code}")
    public ResponseEntity<List<Location>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(locationService.getByProvinceCode(code));
    }

    @GetMapping("/province/name/{name}")
    public ResponseEntity<List<Location>> getByName(@PathVariable String name) {
        return ResponseEntity.ok(locationService.getByProvinceName(name));
    }
}
