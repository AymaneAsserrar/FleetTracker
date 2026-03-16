package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class AuthDTO {

    @Data
    @Schema(name = "LoginRequest")
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @Schema(name = "LoginResponse")
    public static class LoginResponse {
        private String token;
        private Long driverId;
        private String name;
        private String username;
        private Boolean isManager;
    }

    @Data
    @Schema(name = "RegisterRequest")
    public static class RegisterRequest {
        private String name;
        private Long age;
        private String licenceNumber;
        private String contactPhone;
        private String username;
        private String password;
    }
}
