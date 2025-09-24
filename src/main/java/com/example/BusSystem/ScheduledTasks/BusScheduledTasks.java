package com.example.BusSystem.ScheduledTasks;

import com.example.BusSystem.domain.Bus.BusRepository;
import com.example.BusSystem.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Component
public class BusScheduledTasks {
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private BusService busService;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void persistAllBusLocations() {
        busRepository.findAll().forEach(bus -> {
            Optional<Map<String, Object>> location = busService.getBusLocation(bus.getId());

            if (location.isPresent()) {
                System.out.println("Dados encontrados no Redis para o ônibus: " + bus.getId());
                System.out.println("Dados do Redis: " + location.get());

                try {
                    busService.persistLocationFromRedis(bus.getId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //adicionar Logs  aqui
                System.out.println("AVISO: Nenhum dado de localização encontrado para o ônibus: " + bus.getId());
            }
        });
    }
}
