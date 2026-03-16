package com.example.backend.controller;

import com.example.backend.dto.DriverDTO;
import com.example.backend.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Manage fleet drivers")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "List all drivers", description = "Returns all drivers")
    public ResponseEntity<List<DriverDTO.Response>> getAll() {
        return ResponseEntity.ok(driverService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID")
    public ResponseEntity<DriverDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new driver")
    public ResponseEntity<DriverDTO.Response> create(@RequestBody DriverDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing driver")
    public ResponseEntity<DriverDTO.Response> update(
            @PathVariable Long id,
            @RequestBody DriverDTO.Request request) {
        return ResponseEntity.ok(driverService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
