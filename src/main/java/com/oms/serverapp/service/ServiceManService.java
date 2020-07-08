package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.payload.ServiceManPayload;
import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.model.ServiceMan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public ServiceManService(ServiceManRepository serviceManRepository, SkillService skillService, RepairService repairService) {
        this.serviceManRepository = serviceManRepository;
        this.skillService = skillService;
        this.repairService = repairService;
    }

    public List<ServiceManPayload> getAllServiceMen() {
        List<ServiceMan> serviceMen = serviceManRepository.findAll();
        List<ServiceManPayload> serviceMenResponse = new ArrayList<>();
        for (ServiceMan serviceMan: serviceMen) {
            serviceMenResponse.add(new ServiceManPayload(serviceMan.getId(), serviceMan.getFirstName(), serviceMan.getLastName(), serviceMan.getPhoneNumber(), serviceMan.getUsername(), serviceMan.getPassword(), serviceMan.getStartLocalization(), serviceMan.getExperience(), skillService.skillsToIds(serviceMan.getOwnedSkills()), repairService.repairsToIds(serviceMan.getRepairs())));
        }
        return serviceMenResponse;
    }

    public ServiceManPayload getServiceManById(Long id) throws NotFoundException {
        ServiceMan serviceMan = serviceManRepository.findById(id).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with id = %d not found.", id));
        }
        return new ServiceManPayload(serviceMan.getId(), serviceMan.getFirstName(), serviceMan.getLastName(), serviceMan.getPhoneNumber(), serviceMan.getUsername(), serviceMan.getPassword(), serviceMan.getStartLocalization(), serviceMan.getExperience(), skillService.skillsToIds(serviceMan.getOwnedSkills()), repairService.repairsToIds(serviceMan.getRepairs()));
    }

    public ResponseEntity<ServiceMan> addServiceMan(ServiceManPayload serviceManPayload) {
        ServiceMan savedServiceMan = serviceManRepository.save(new ServiceMan(serviceManPayload.getFirstName(), serviceManPayload.getLastName(), serviceManPayload.getPhoneNumber(), serviceManPayload.getUsername(), serviceManPayload.getPassword(), serviceManPayload.getStartLocalization(), serviceManPayload.getExperience(), skillService.idsToSkills(serviceManPayload.getSkills()), repairService.idsToRepairs(serviceManPayload.getRepairs())));
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
        serviceManRepository.save(new ServiceMan(serviceMan.getId(), serviceManPayload.getFirstName(), serviceManPayload.getLastName(), serviceManPayload.getPhoneNumber(), serviceManPayload.getUsername(), serviceManPayload.getPassword(), serviceManPayload.getStartLocalization(), serviceManPayload.getExperience(), skillService.idsToSkills(serviceManPayload.getSkills()), repairService.idsToRepairs(serviceManPayload.getRepairs())));
        return ResponseEntity.ok().build();
    }

    public Set<Long> serviceMenToIds(Set<ServiceMan> serviceMen) {
        Set<Long> serviceMenIds = new HashSet<>();
        if (serviceMen != null) {
            for (ServiceMan serviceMan : serviceMen) {
                serviceMenIds.add(serviceMan.getId());
            }
        }
        return serviceMenIds;
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
