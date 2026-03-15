package com.example.backend.repository;

import com.example.backend.model.Vehicle;
import com.example.backend.model.enums.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatus(VehicleStatus status);
}
