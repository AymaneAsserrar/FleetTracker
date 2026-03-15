package com.example.backend.service;

import com.example.backend.dto.StopDTO;
import com.example.backend.model.Route;
import com.example.backend.model.Stop;
import com.example.backend.repository.RouteRepository;
import com.example.backend.repository.StopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StopService {

    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;

    public List<StopDTO.Response> findAll() {
        return stopRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<StopDTO.Response> findByRoute(Long routeId) {
        return stopRepository.findByRouteIdOrderBySequenceOrder(routeId).stream().map(this::toResponse).toList();
    }

    public StopDTO.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public StopDTO.Response create(StopDTO.Request request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.getRouteId()));
        Stop stop = new Stop();
        stop.setRoute(route);
        applyRequest(stop, request);
        return toResponse(stopRepository.save(stop));
    }

    @Transactional
    public StopDTO.Response update(Long id, StopDTO.Request request) {
        Stop stop = getOrThrow(id);
        if (request.getRouteId() != null) {
            Route route = routeRepository.findById(request.getRouteId())
                    .orElseThrow(() -> new EntityNotFoundException("Route not found with id: " + request.getRouteId()));
            stop.setRoute(route);
        }
        applyRequest(stop, request);
        return toResponse(stopRepository.save(stop));
    }

    @Transactional
    public void delete(Long id) {
        if (!stopRepository.existsById(id)) {
            throw new EntityNotFoundException("Stop not found with id: " + id);
        }
        stopRepository.deleteById(id);
    }

    private Stop getOrThrow(Long id) {
        return stopRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stop not found with id: " + id));
    }

    private void applyRequest(Stop stop, StopDTO.Request request) {
        if (request.getName() != null) stop.setName(request.getName());
        if (request.getLatitude() != null) stop.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) stop.setLongitude(request.getLongitude());
        if (request.getSequenceOrder() != null) stop.setSequenceOrder(request.getSequenceOrder());
    }

    public StopDTO.Response toResponse(Stop s) {
        StopDTO.Response r = new StopDTO.Response();
        r.setId(s.getId());
        r.setName(s.getName());
        r.setLatitude(s.getLatitude());
        r.setLongitude(s.getLongitude());
        r.setSequenceOrder(s.getSequenceOrder());
        r.setRouteId(s.getRoute().getId());
        r.setRouteName(s.getRoute().getName());
        r.setCreatedAt(s.getCreatedAt());
        return r;
    }
}
