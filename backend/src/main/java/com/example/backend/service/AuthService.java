package com.example.backend.service;

import com.example.backend.config.JwtUtil;
import com.example.backend.dto.AuthDTO;
import com.example.backend.model.Driver;
import com.example.backend.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthDTO.LoginResponse register(AuthDTO.RegisterRequest request) {
        if (driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        Driver driver = new Driver();
        driver.setName(request.getName());
        driver.setAge(request.getAge());
        driver.setLicenceNumber(request.getLicenceNumber());
        driver.setContactPhone(request.getContactPhone());
        driver.setUsername(request.getUsername());
        driver.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        driver.setIsManager(false);
        driver = driverRepository.save(driver);

        String token = jwtUtil.generate(driver.getId(), driver.getUsername(), false);
        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse();
        response.setToken(token);
        response.setDriverId(driver.getId());
        response.setName(driver.getName());
        response.setUsername(driver.getUsername());
        response.setIsManager(false);
        return response;
    }

    /** Re-reads the driver from DB and returns a fresh token — used on app startup. */
    public AuthDTO.LoginResponse me(String username) {
        Driver driver = driverRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));
        String token = jwtUtil.generate(driver.getId(), driver.getUsername(), driver.getIsManager());
        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse();
        response.setToken(token);
        response.setDriverId(driver.getId());
        response.setName(driver.getName());
        response.setUsername(driver.getUsername());
        response.setIsManager(driver.getIsManager());
        return response;
    }

    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Driver driver = driverRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), driver.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generate(driver.getId(), driver.getUsername(), driver.getIsManager());

        AuthDTO.LoginResponse response = new AuthDTO.LoginResponse();
        response.setToken(token);
        response.setDriverId(driver.getId());
        response.setName(driver.getName());
        response.setUsername(driver.getUsername());
        response.setIsManager(driver.getIsManager());
        return response;
    }
}
