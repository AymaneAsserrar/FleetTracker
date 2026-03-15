package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class LocationUpdateDTO {

    @Data
    public static class Request {
        private Long vehicleId;
        private Double latitude;
        private Double longitude;
        private Double speed;
        private Double heading;
    }

    @Data
    public static class Response {
        private Long id;
        private Long vehicleId;
        private String vehicleName;
        private Double latitude;
        private Double longitude;
        private Double speed;
        private Double heading;
        private LocalDateTime recordedAt;
    }
}
