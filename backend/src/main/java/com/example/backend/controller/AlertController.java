package com.example.backend.controller;

import com.example.backend.dto.AlertDTO;
import com.example.backend.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Trip delay alerts")
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    @Operation(summary = "List all alerts")
    public ResponseEntity<List<AlertDTO.Response>> getAll() {
        return ResponseEntity.ok(alertService.findAll());
    }

    @GetMapping("/unresolved")
    @Operation(summary = "List unresolved alerts")
    public ResponseEntity<List<AlertDTO.Response>> getUnresolved() {
        return ResponseEntity.ok(alertService.findUnresolved());
    }

    @PatchMapping("/{id}/resolve")
    @Operation(summary = "Mark an alert as resolved")
    public ResponseEntity<AlertDTO.Response> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.resolve(id));
    }
}
