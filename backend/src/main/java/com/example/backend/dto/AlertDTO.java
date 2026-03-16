package com.example.backend.dto;

import com.example.backend.model.enums.AlertType;
import lombok.Data;

import java.time.LocalDateTime;

public class AlertDTO {

    @Data
    public static class Response {
        private Long id;
        private Long tripId;
        private String vehicleName;
        private String driverName;
        private String routeName;
        private AlertType type;
        private String message;
        private Boolean resolved;
        private LocalDateTime createdAt;
    }
}
