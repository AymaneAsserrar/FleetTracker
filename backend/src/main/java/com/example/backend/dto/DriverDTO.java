package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public class DriverDTO {

    @Data
    @Schema(name = "DriverRequest")
    public static class Request {
        private String name;
        private Long age;
        private Boolean isManager;
        private String licenceNumber;
    }

    @Data
    @Schema(name = "DriverResponse")
    public static class Response {
        private Long id;
        private String name;
        private Long age;
        private Boolean isManager;
        private String licenceNumber;
    }
}
