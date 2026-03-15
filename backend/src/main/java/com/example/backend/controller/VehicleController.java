package com.example.backend.controller;

import com.example.backend.dto.VehicleDTO;
import com.example.backend.model.enums.VehicleStatus;
import com.example.backend.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Manage fleet vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "List all vehicles", description = "Returns all vehicles, optionally filtered by status")
    public ResponseEntity<List<VehicleDTO.Response>> getAll(
            @RequestParam(required = false) VehicleStatus status) {
        if (status != null) {
            return ResponseEntity.ok(vehicleService.findByStatus(status));
        }
        return ResponseEntity.ok(vehicleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<VehicleDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new vehicle")
    public ResponseEntity<VehicleDTO.Response> create(@RequestBody VehicleDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing vehicle")
    public ResponseEntity<VehicleDTO.Response> update(
            @PathVariable Long id,
            @RequestBody VehicleDTO.Request request) {
        return ResponseEntity.ok(vehicleService.update(id, request));
    }

    @PutMapping("/{id}/location")
    @Operation(summary = "Update vehicle's current location")
    public ResponseEntity<VehicleDTO.Response> updateLocation(
            @PathVariable Long id,
            @RequestBody VehicleDTO.LocationRequest request) {
        return ResponseEntity.ok(vehicleService.updateLocation(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vehicle")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
