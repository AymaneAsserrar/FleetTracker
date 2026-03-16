package com.example.backend.service;

import com.example.backend.dto.RouteDTO;
import com.example.backend.model.Route;
import com.example.backend.repository.RouteRepository;
import com.example.backend.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteService {

    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;

    public List<RouteDTO.Response> findAll() {
        return routeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<RouteDTO.Response> findActive() {
        return routeRepository.findByActive(true).stream().map(this::toResponse).toList();
    }

    public RouteDTO.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public RouteDTO.Response create(RouteDTO.Request request) {
        Route route = new Route();
        applyRequest(route, request);
        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteDTO.Response update(Long id, RouteDTO.Request request) {
        Route route = getOrThrow(id);
        applyRequest(route, request);
        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public void delete(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new EntityNotFoundException("Route not found with id: " + id);
        }
        long tripCount = tripRepository.findByRouteId(id).size();
        if (tripCount > 0) {
            throw new IllegalArgumentException(
                "Cannot delete route: it is used by " + tripCount + " trip(s). Remove or reassign those trips first."
            );
        }
        routeRepository.deleteById(id);
    }

    private Route getOrThrow(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + id));
    }

    private void applyRequest(Route route, RouteDTO.Request request) {
        if (request.getName() != null) route.setName(request.getName());
        if (request.getDescription() != null) route.setDescription(request.getDescription());
        if (request.getActive() != null) route.setActive(request.getActive());
    }

    public RouteDTO.Response toResponse(Route r) {
        RouteDTO.Response res = new RouteDTO.Response();
        res.setId(r.getId());
        res.setName(r.getName());
        res.setDescription(r.getDescription());
        res.setActive(r.getActive());
        res.setCreatedAt(r.getCreatedAt());
        res.setUpdatedAt(r.getUpdatedAt());
        return res;
    }
}
