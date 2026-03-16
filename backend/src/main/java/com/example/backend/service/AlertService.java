package com.example.backend.service;

import com.example.backend.dto.AlertDTO;
import com.example.backend.model.Alert;
import com.example.backend.model.Trip;
import com.example.backend.model.enums.AlertType;
import com.example.backend.repository.AlertRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;

    public List<AlertDTO.Response> findAll() {
        return alertRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public List<AlertDTO.Response> findUnresolved() {
        return alertRepository.findByResolvedFalseOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void createLateAlertIfNotExists(Trip trip) {
        if (alertRepository.existsByTripIdAndType(trip.getId(), AlertType.LATE)) return;
        Alert alert = new Alert();
        alert.setTrip(trip);
        alert.setType(AlertType.LATE);
        String driverName = trip.getDriver() != null ? trip.getDriver().getName() : "Unknown driver";
        String routeName = trip.getRoute() != null ? trip.getRoute().getName() : "Unknown route";
        alert.setMessage("Driver " + driverName + " is late on route " + routeName + ".");
        alertRepository.save(alert);
    }

    @Transactional
    public AlertDTO.Response resolve(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alert not found with id: " + id));
        alert.setResolved(true);
        return toResponse(alertRepository.save(alert));
    }

    private AlertDTO.Response toResponse(Alert a) {
        AlertDTO.Response r = new AlertDTO.Response();
        r.setId(a.getId());
        r.setType(a.getType());
        r.setMessage(a.getMessage());
        r.setResolved(a.getResolved());
        r.setCreatedAt(a.getCreatedAt());
        if (a.getTrip() != null) {
            r.setTripId(a.getTrip().getId());
            if (a.getTrip().getVehicle() != null) r.setVehicleName(a.getTrip().getVehicle().getName());
            if (a.getTrip().getDriver() != null) r.setDriverName(a.getTrip().getDriver().getName());
            if (a.getTrip().getRoute() != null) r.setRouteName(a.getTrip().getRoute().getName());
        }
        return r;
    }
}
