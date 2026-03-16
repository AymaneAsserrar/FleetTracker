package com.example.backend.scheduler;

import com.example.backend.model.Trip;
import com.example.backend.model.enums.TripStatus;
import com.example.backend.repository.TripRepository;
import com.example.backend.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripScheduler {

    private static final double RADIUS_METERS = 20.0;

    private final TripRepository tripRepository;
    private final AlertService alertService;

    @Scheduled(fixedDelay = 60_000) // runs every 60 seconds
    @Transactional
    public void checkOverdueTrips() {
        List<Trip> overdueTrips = tripRepository.findByStatusAndEndTimeBefore(
                TripStatus.IN_PROGRESS, LocalDateTime.now());

        for (Trip trip : overdueTrips) {
            if (trip.getEndLatitude() == null || trip.getEndLongitude() == null
                    || trip.getVehicle() == null || trip.getVehicle().getLocation() == null) {
                // Cannot determine position — raise late alert
                alertService.createLateAlertIfNotExists(trip);
                continue;
            }

            double vehicleLat = trip.getVehicle().getLocation().getY();
            double vehicleLon = trip.getVehicle().getLocation().getX();
            double distance = haversineMeters(vehicleLat, vehicleLon,
                    trip.getEndLatitude(), trip.getEndLongitude());

            if (distance <= RADIUS_METERS) {
                trip.setStatus(TripStatus.COMPLETED);
                tripRepository.save(trip);
                log.info("Trip {} auto-completed: vehicle within {}m of destination", trip.getId(), distance);
            } else {
                alertService.createLateAlertIfNotExists(trip);
                log.info("Trip {} overdue: vehicle {}m from destination, alert created", trip.getId(), distance);
            }
        }
    }

    /**
     * Haversine formula — returns distance in meters between two lat/lon points.
     */
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6_371_000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
