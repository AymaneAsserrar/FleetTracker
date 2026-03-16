package com.example.backend.repository;

import com.example.backend.model.Alert;
import com.example.backend.model.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    boolean existsByTripIdAndType(Long tripId, AlertType type);
    List<Alert> findAllByOrderByCreatedAtDesc();
    List<Alert> findByResolvedFalseOrderByCreatedAtDesc();
}
