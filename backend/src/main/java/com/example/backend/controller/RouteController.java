package com.example.backend.controller;

import com.example.backend.dto.RouteDTO;
import com.example.backend.dto.StopDTO;
import com.example.backend.service.RouteService;
import com.example.backend.service.StopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Routes", description = "Manage fleet routes")
public class RouteController {

    private final RouteService routeService;
    private final StopService stopService;

    @GetMapping
    @Operation(summary = "List all routes", description = "Returns all routes, optionally only active ones")
    public ResponseEntity<List<RouteDTO.Response>> getAll(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(routeService.findActive());
        }
        return ResponseEntity.ok(routeService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<RouteDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.findById(id));
    }

    @GetMapping("/{id}/stops")
    @Operation(summary = "Get all stops for a route, ordered by sequence")
    public ResponseEntity<List<StopDTO.Response>> getStops(@PathVariable Long id) {
        return ResponseEntity.ok(stopService.findByRoute(id));
    }

    @PostMapping
    @Operation(summary = "Create a new route")
    public ResponseEntity<RouteDTO.Response> create(@RequestBody RouteDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing route")
    public ResponseEntity<RouteDTO.Response> update(
            @PathVariable Long id,
            @RequestBody RouteDTO.Request request) {
        return ResponseEntity.ok(routeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a route")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
