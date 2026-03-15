package com.example.backend.dto;

import com.example.backend.model.enums.VehicleStatus;
import lombok.Data;

import java.time.LocalDateTime;

public class VehicleDTO {

    @Data
    public static class Request {
        private String label;
        private String name;
        private String licensePlate;
        private VehicleStatus status;
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class Response {
        private Long id;
        private String label;
        private String name;
        private String licensePlate;
        private Double latitude;
        private Double longitude;
        private VehicleStatus status;
        private LocalDateTime lastUpdated;
    }

    @Data
    public static class LocationRequest {
        private Double latitude;
        private Double longitude;
    }
}
