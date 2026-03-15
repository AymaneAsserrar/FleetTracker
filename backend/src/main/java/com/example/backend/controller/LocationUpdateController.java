package com.example.backend.controller;

import com.example.backend.dto.LocationUpdateDTO;
import com.example.backend.service.LocationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location-updates")
@RequiredArgsConstructor
@Tag(name = "Location Updates", description = "Track real-time vehicle locations")
public class LocationUpdateController {

    private final LocationUpdateService locationUpdateService;

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get location history for a vehicle", description = "Returns all location updates ordered newest first")
    public ResponseEntity<List<LocationUpdateDTO.Response>> getHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(locationUpdateService.findByVehicle(vehicleId));
    }

    @GetMapping("/vehicle/{vehicleId}/latest")
    @Operation(summary = "Get the latest location for a vehicle")
    public ResponseEntity<LocationUpdateDTO.Response> getLatest(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(locationUpdateService.findLatest(vehicleId));
    }

    @PostMapping
    @Operation(summary = "Record a new location update", description = "Records vehicle position and also updates the vehicle's current location")
    public ResponseEntity<LocationUpdateDTO.Response> create(@RequestBody LocationUpdateDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationUpdateService.create(request));
    }
}
