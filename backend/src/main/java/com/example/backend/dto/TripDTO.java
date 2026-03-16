package com.example.backend.dto;

import com.example.backend.model.enums.TripStatus;
import lombok.Data;

import java.time.LocalDateTime;

public class TripDTO {

    @Data
    public static class Request {
        private Long vehicleId;
        private Long routeId;
        private Long driverId;
        private Double startLatitude;
        private Double startLongitude;
        private Double endLatitude;
        private Double endLongitude;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private TripStatus status;
    }

    @Data
    public static class Response {
        private Long id;
        private Long vehicleId;
        private String vehicleName;
        private Long routeId;
        private String routeName;
        private Long driverId;
        private String driverName;
        private Double startLatitude;
        private Double startLongitude;
        private Double endLatitude;
        private Double endLongitude;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private TripStatus status;
        private LocalDateTime createdAt;
    }
}
