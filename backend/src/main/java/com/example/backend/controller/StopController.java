package com.example.backend.controller;

import com.example.backend.dto.StopDTO;
import com.example.backend.service.StopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stops")
@RequiredArgsConstructor
@Tag(name = "Stops", description = "Manage route stops")
public class StopController {

    private final StopService stopService;

    @GetMapping
    @Operation(summary = "List all stops")
    public ResponseEntity<List<StopDTO.Response>> getAll() {
        return ResponseEntity.ok(stopService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stop by ID")
    public ResponseEntity<StopDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stopService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new stop")
    public ResponseEntity<StopDTO.Response> create(@RequestBody StopDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stopService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing stop")
    public ResponseEntity<StopDTO.Response> update(
            @PathVariable Long id,
            @RequestBody StopDTO.Request request) {
        return ResponseEntity.ok(stopService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a stop")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
