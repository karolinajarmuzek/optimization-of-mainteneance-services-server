package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.payload.ServiceManPayload;
import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.model.ServiceMan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ServiceManService {

    private ServiceManRepository serviceManRepository;
    private SkillService skillService;
    private RepairService repairService;
    private PasswordEncoder encoder;

    @Autowired
    public ServiceManService(ServiceManRepository serviceManRepository, SkillService skillService, RepairService repairService, PasswordEncoder encoder) {
        this.serviceManRepository = serviceManRepository;
        this.skillService = skillService;
        this.repairService = repairService;
        this.encoder = encoder;
    }

    public List<ServiceManPayload> getAllServiceMen() {
        List<ServiceMan> serviceMen = serviceManRepository.findAll();
        List<ServiceManPayload> serviceMenResponse = new ArrayList<>();
        for (ServiceMan serviceMan: serviceMen) {
            serviceMenResponse.add(new ServiceManPayload(serviceMan));
        }
        return serviceMenResponse;
    }

    public ServiceManPayload getServiceManById(Long id) throws NotFoundException {
        ServiceMan serviceMan = serviceManRepository.findById(id).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with id = %d not found.", id));
        }
        return new ServiceManPayload(serviceMan);
    }

    public ServiceManPayload getServiceManByUsername(String username) throws NotFoundException {
        ServiceMan serviceMan = serviceManRepository.findByUsername(username).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with username = %d not found.", username));
        }
        return new ServiceManPayload(serviceMan);
    }

    public ResponseEntity<ServiceMan> addServiceMan(ServiceManPayload serviceManPayload) {
        ServiceMan savedServiceMan = serviceManRepository.save(new ServiceMan(serviceManPayload, encoder.encode(serviceManPayload.getPassword()), skillService.idsToSkills(serviceManPayload.getSkills()), repairService.idsToRepairs(serviceManPayload.getRepairs())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedServiceMan.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteServiceMan(Long id) {
        serviceManRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateServiceMan(ServiceManPayload serviceManPayload, Long id) {
        ServiceMan serviceMan = serviceManRepository.findById(id).orElse(null);
        if (serviceMan == null) {
            return ResponseEntity.notFound().build();
        }
        String password = serviceManPayload.getPassword() != null ? encoder.encode(serviceManPayload.getPassword()) : null;
        serviceManRepository.save(new ServiceMan(serviceMan, serviceManPayload, password, skillService.idsToSkills(serviceManPayload.getSkills()), repairService.idsToRepairs(serviceManPayload.getRepairs())));
        return ResponseEntity.ok().build();
    }

    public Set<ServiceMan> idsToServiceMen(Set<Long> serviceMenIds) {
        Set<ServiceMan> serviceMen = new HashSet<>();
        if (serviceMenIds != null) {
            for (Long serviceManId : serviceMenIds) {
                ServiceMan serviceMan = serviceManRepository.findById(serviceManId).orElse(null);
                if (serviceMan != null) serviceMen.add(serviceMan);
            }
        }
        return serviceMen;
    }
}
