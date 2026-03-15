package com.example.backend.service;

import com.example.backend.dto.LocationUpdateDTO;
import com.example.backend.model.LocationUpdate;
import com.example.backend.model.Vehicle;
import com.example.backend.repository.LocationUpdateRepository;
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
public class LocationUpdateService {

    private final LocationUpdateRepository locationUpdateRepository;
    private final VehicleRepository vehicleRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<LocationUpdateDTO.Response> findByVehicle(Long vehicleId) {
        return locationUpdateRepository.findByVehicleIdOrderByRecordedAtDesc(vehicleId)
                .stream().map(this::toResponse).toList();
    }

    public LocationUpdateDTO.Response findLatest(Long vehicleId) {
        return locationUpdateRepository.findFirstByVehicleIdOrderByRecordedAtDesc(vehicleId)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("No location updates for vehicle: " + vehicleId));
    }

    @Transactional
    public LocationUpdateDTO.Response create(LocationUpdateDTO.Request request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + request.getVehicleId()));

        LocationUpdate update = new LocationUpdate();
        update.setVehicle(vehicle);
        update.setLatitude(request.getLatitude());
        update.setLongitude(request.getLongitude());
        update.setSpeed(request.getSpeed());
        update.setHeading(request.getHeading());

        // Also update vehicle's current location
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        vehicle.setLocation(point);
        vehicleRepository.save(vehicle);

        return toResponse(locationUpdateRepository.save(update));
    }

    public LocationUpdateDTO.Response toResponse(LocationUpdate lu) {
        LocationUpdateDTO.Response r = new LocationUpdateDTO.Response();
        r.setId(lu.getId());
        r.setVehicleId(lu.getVehicle().getId());
        r.setVehicleName(lu.getVehicle().getName());
        r.setLatitude(lu.getLatitude());
        r.setLongitude(lu.getLongitude());
        r.setSpeed(lu.getSpeed());
        r.setHeading(lu.getHeading());
        r.setRecordedAt(lu.getRecordedAt());
        return r;
    }
}
