package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class RouteDTO {

    @Data
    public static class Request {
        private String name;
        private String description;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private Boolean active;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
