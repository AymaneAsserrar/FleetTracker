package com.example.backend.service;

import com.example.backend.dto.TripDTO;
import com.example.backend.model.Driver;
import com.example.backend.model.Route;
import com.example.backend.model.Trip;
import com.example.backend.model.Vehicle;
import com.example.backend.model.enums.TripStatus;
import com.example.backend.repository.DriverRepository;
import com.example.backend.repository.RouteRepository;
import com.example.backend.repository.TripRepository;
import com.example.backend.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;

    public List<TripDTO.Response> findAll() {
        return tripRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<TripDTO.Response> findByVehicle(Long vehicleId) {
        return tripRepository.findByVehicleId(vehicleId).stream().map(this::toResponse).toList();
    }

    public List<TripDTO.Response> findByStatus(TripStatus status) {
        return tripRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    public TripDTO.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public TripDTO.Response create(TripDTO.Request request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + request.getVehicleId()));
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.getRouteId()));

        Trip trip = new Trip();
        trip.setVehicle(vehicle);
        trip.setRoute(route);
        if (request.getDriverId() != null) {
            trip.setDriver(driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + request.getDriverId())));
        }
        trip.setStartLatitude(request.getStartLatitude());
        trip.setStartLongitude(request.getStartLongitude());
        trip.setEndLatitude(request.getEndLatitude());
        trip.setEndLongitude(request.getEndLongitude());
        trip.setStartTime(request.getStartTime());
        trip.setEndTime(request.getEndTime());
        if (request.getStatus() != null) trip.setStatus(request.getStatus());

        return toResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripDTO.Response updateStatus(Long id, TripStatus status) {
        Trip trip = getOrThrow(id);
        trip.setStatus(status);
        return toResponse(tripRepository.save(trip));
    }

    @Transactional
    public TripDTO.Response update(Long id, TripDTO.Request request) {
        Trip trip = getOrThrow(id);
        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + request.getVehicleId()));
            trip.setVehicle(vehicle);
        }
        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.getRouteId()));
            trip.setRoute(route);
        }
        if (request.getDriverId() != null) {
            trip.setDriver(driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + request.getDriverId())));
        } else {
            trip.setDriver(null);
        }
        trip.setStartLatitude(request.getStartLatitude());
        trip.setStartLongitude(request.getStartLongitude());
        trip.setEndLatitude(request.getEndLatitude());
        trip.setEndLongitude(request.getEndLongitude());
        if (request.getStartTime() != null) trip.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) trip.setEndTime(request.getEndTime());
        if (request.getStatus() != null) trip.setStatus(request.getStatus());
        return toResponse(tripRepository.save(trip));
    }

    @Transactional
    public void delete(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new EntityNotFoundException("Trip not found with id: " + id);
        }
        tripRepository.deleteById(id);
    }

    private Trip getOrThrow(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + id));
    }

    public TripDTO.Response toResponse(Trip t) {
        TripDTO.Response r = new TripDTO.Response();
        r.setId(t.getId());
        r.setVehicleId(t.getVehicle().getId());
        r.setVehicleName(t.getVehicle().getName());
        r.setRouteId(t.getRoute().getId());
        r.setRouteName(t.getRoute().getName());
        if (t.getDriver() != null) {
            r.setDriverId(t.getDriver().getId());
            r.setDriverName(t.getDriver().getName());
        }
        r.setStartLatitude(t.getStartLatitude());
        r.setStartLongitude(t.getStartLongitude());
        r.setEndLatitude(t.getEndLatitude());
        r.setEndLongitude(t.getEndLongitude());
        r.setStartTime(t.getStartTime());
        r.setEndTime(t.getEndTime());
        r.setStatus(t.getStatus());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }
}
