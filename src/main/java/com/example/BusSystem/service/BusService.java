package com.example.BusSystem.service;

import com.example.BusSystem.domain.Bus.*;
import com.example.BusSystem.handler.BusTrackingHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class BusService {

    @Autowired
    private BusRepository repository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BusTrackingHandler busTrackingHandler;

    @Autowired
    private ObjectMapper objectMapper;

    public Bus create(BusDataInsert data) {
        Bus bus = new Bus(data);
        return repository.save(bus);
    }

    public Page<Bus> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Bus getOne(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    public Bus update(UUID id, BusDataUpdate data) {
        var bus = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        bus.updateBus(data);
        return repository.save(bus);
    }

    public void delete(UUID id) {
        var bus = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        repository.delete(bus);
    }

    public Bus updateLocalization(UUID id, BusLocalizationUpdate data){
        var bus = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        bus.updateLocalization(data);
        return repository.save(bus);
    }

    public Bus getLocation(UUID id){
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    public void updateBusLocation(UUID busId, BusLocalizationUpdate data ) {

        String key = "bus:" + busId.toString() + ":location";

        Map<String, Object> locationData = Map.of(
                "latitude", data.currentLatitude(),
                "longitude", data.currentLongitude(),
                "lastUpdate", LocalDateTime.now()
                // "next_stop",
                // "TEA",
        );

        redisTemplate.opsForHash().putAll(key, locationData);
        redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        try {
            String locationJson = objectMapper.writeValueAsString(locationData);
            busTrackingHandler.sendLocationUpdate(busId.toString(), locationJson);
        } catch (JsonProcessingException e) {
            System.out.println("erro ao enviar ao websocket "+e);
        }
    }

    public Optional<Map<String, Object>> getBusLocation(UUID busId) {
        String key = "bus:" + busId.toString() + ":location";

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Map<Object, Object> cachedData = redisTemplate.opsForHash().entries(key);
            //Adicionar logs aqui
            if (!cachedData.isEmpty()) {
                return Optional.of((Map<String, Object>) (Map<?, ?>) cachedData);
            }

        }
        //Adicionar logs aqui falando que buscou do banco
        return repository.findById(busId)
                .map(bus -> Map.of(
                        "latitude", bus.getCurrentLatitude(),
                        "longitude", bus.getCurrentLongitude()
                ));
    }

    public void persistLocationFromRedis(UUID busId) {
        Optional<Map<String, Object>> location = getBusLocation(busId);
        if (location.isPresent()) {
            var bus = repository.findById(busId)
                    .orElseThrow(() -> new EntityNotFoundException("Bus not found."));
            if (location.get().containsKey("lastUpdate") && location.get().get("lastUpdate") != null) {
                Object lastUpdateData = location.get().get("lastUpdate");

                if (lastUpdateData instanceof ArrayList) {
                    ArrayList<Integer> lastUpdateList = (ArrayList<Integer>) lastUpdateData;
                    LocalDateTime lastUpdate = LocalDateTime.of(
                            lastUpdateList.get(0), // ano
                            lastUpdateList.get(1), // mes
                            lastUpdateList.get(2), // dia
                            lastUpdateList.get(3), // hora
                            lastUpdateList.get(4), // minuto
                            lastUpdateList.get(5), // segundo
                            lastUpdateList.get(6)  // nanosegundo
                    );
                    bus.setLastLocationUpdate(lastUpdate);
                }
            } else {
                bus.setLastLocationUpdate(null);
            }
            bus.setCurrentLatitude((Double) location.get().get("latitude"));
            bus.setCurrentLongitude((Double) location.get().get("longitude"));

            repository.save(bus);
        }
    }
}