package com.example.backend.service;

import com.example.backend.dto.DriverDTO;
import com.example.backend.model.Driver;
import com.example.backend.repository.DriverRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;

    public List<DriverDTO.Response> findAll() {
        return driverRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DriverDTO.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public DriverDTO.Response create(DriverDTO.Request request) {
        Driver driver = new Driver();
        applyRequest(driver, request);
        return toResponse(driverRepository.save(driver));
    }

    @Transactional
    public DriverDTO.Response update(Long id, DriverDTO.Request request) {
        Driver driver = getOrThrow(id);
        applyRequest(driver, request);
        return toResponse(driverRepository.save(driver));
    }

    @Transactional
    public void delete(Long id) {
        if (!driverRepository.existsById(id)) {
            throw new EntityNotFoundException("Driver not found with id: " + id);
        }
        driverRepository.deleteById(id);
    }

    private Driver getOrThrow(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + id));
    }

    private void applyRequest(Driver driver, DriverDTO.Request request) {
        if (request.getName() != null)
            driver.setName(request.getName());
        if (request.getAge() != null)
            driver.setAge(request.getAge());
        if (request.getIsManager() != null)
            driver.setIsManager(request.getIsManager());
        if (request.getLicenceNumber() != null)
            driver.setLicenceNumber(request.getLicenceNumber());
        if (request.getContactPhone() != null)
            driver.setContactPhone(request.getContactPhone());
    }

    public DriverDTO.Response toResponse(Driver d) {
        DriverDTO.Response r = new DriverDTO.Response();
        r.setId(d.getId());
        r.setName(d.getName());
        r.setAge(d.getAge());
        r.setIsManager(d.getIsManager());
        r.setLicenceNumber(d.getLicenceNumber());
        r.setContactPhone(d.getContactPhone());
        return r;
    }
}
