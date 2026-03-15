package com.example.backend.repository;

import com.example.backend.model.LocationUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationUpdateRepository extends JpaRepository<LocationUpdate, Long> {
    List<LocationUpdate> findByVehicleIdOrderByRecordedAtDesc(Long vehicleId);
    Optional<LocationUpdate> findFirstByVehicleIdOrderByRecordedAtDesc(Long vehicleId);
}
