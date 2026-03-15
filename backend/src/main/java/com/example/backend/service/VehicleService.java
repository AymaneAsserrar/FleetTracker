package com.example.backend.service;

import com.example.backend.dto.VehicleDTO;
import com.example.backend.model.Vehicle;
import com.example.backend.model.enums.VehicleStatus;
import com.example.backend.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<VehicleDTO.Response> findAll() {
        return vehicleRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<VehicleDTO.Response> findByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    public VehicleDTO.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public VehicleDTO.Response create(VehicleDTO.Request request) {
        Vehicle vehicle = new Vehicle();
        applyRequest(vehicle, request);
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleDTO.Response update(Long id, VehicleDTO.Request request) {
        Vehicle vehicle = getOrThrow(id);
        applyRequest(vehicle, request);
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public VehicleDTO.Response updateLocation(Long id, VehicleDTO.LocationRequest request) {
        Vehicle vehicle = getOrThrow(id);
        vehicle.setLocation(toPoint(request.getLongitude(), request.getLatitude()));
        return toResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void delete(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    private Vehicle getOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));
    }

    private void applyRequest(Vehicle vehicle, VehicleDTO.Request request) {
        if (request.getLabel() != null) vehicle.setLabel(request.getLabel());
        if (request.getName() != null) vehicle.setName(request.getName());
        if (request.getLicensePlate() != null) vehicle.setLicensePlate(request.getLicensePlate());
        if (request.getStatus() != null) vehicle.setStatus(request.getStatus());
        if (request.getLatitude() != null && request.getLongitude() != null) {
            vehicle.setLocation(toPoint(request.getLongitude(), request.getLatitude()));
        }
    }

    private Point toPoint(double longitude, double latitude) {
        // JTS uses (x=longitude, y=latitude)
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public VehicleDTO.Response toResponse(Vehicle v) {
        VehicleDTO.Response r = new VehicleDTO.Response();
        r.setId(v.getId());
        r.setLabel(v.getLabel());
        r.setName(v.getName());
        r.setLicensePlate(v.getLicensePlate());
        r.setStatus(v.getStatus());
        r.setLastUpdated(v.getLastUpdated());
        if (v.getLocation() != null) {
            r.setLatitude(v.getLocation().getY());   // Y = latitude
            r.setLongitude(v.getLocation().getX());  // X = longitude
        }
        return r;
    }
}
