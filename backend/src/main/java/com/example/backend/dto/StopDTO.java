package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class StopDTO {

    @Data
    public static class Request {
        private String name;
        private Double latitude;
        private Double longitude;
        private Integer sequenceOrder;
        private Long routeId;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;
        private Integer sequenceOrder;
        private Long routeId;
        private String routeName;
        private LocalDateTime createdAt;
    }
}
