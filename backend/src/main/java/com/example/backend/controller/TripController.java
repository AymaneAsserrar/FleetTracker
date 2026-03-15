package com.example.backend.controller;

import com.example.backend.dto.TripDTO;
import com.example.backend.model.enums.TripStatus;
import com.example.backend.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Manage vehicle trips")
public class TripController {

    private final TripService tripService;

    @GetMapping
    @Operation(summary = "List all trips", description = "Returns all trips, optionally filtered by status")
    public ResponseEntity<List<TripDTO.Response>> getAll(
            @RequestParam(required = false) TripStatus status) {
        if (status != null) {
            return ResponseEntity.ok(tripService.findByStatus(status));
        }
        return ResponseEntity.ok(tripService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get trip by ID")
    public ResponseEntity<TripDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.findById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(summary = "Get all trips for a specific vehicle")
    public ResponseEntity<List<TripDTO.Response>> getByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(tripService.findByVehicle(vehicleId));
    }

    @PostMapping
    @Operation(summary = "Create a new trip")
    public ResponseEntity<TripDTO.Response> create(@RequestBody TripDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a trip")
    public ResponseEntity<TripDTO.Response> update(
            @PathVariable Long id,
            @RequestBody TripDTO.Request request) {
        return ResponseEntity.ok(tripService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update trip status only")
    public ResponseEntity<TripDTO.Response> updateStatus(
            @PathVariable Long id,
            @RequestParam TripStatus status) {
        return ResponseEntity.ok(tripService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a trip")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
