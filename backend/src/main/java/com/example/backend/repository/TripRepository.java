package com.example.backend.repository;

import com.example.backend.model.Trip;
import com.example.backend.model.enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByVehicleId(Long vehicleId);
    List<Trip> findByRouteId(Long routeId);
    List<Trip> findByStatus(TripStatus status);
}
