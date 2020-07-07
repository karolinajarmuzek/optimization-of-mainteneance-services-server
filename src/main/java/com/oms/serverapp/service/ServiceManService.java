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
import java.util.List;

@Service
public class ServiceManService {

    private ServiceManRepository serviceManRepository;
    private ServiceHelpers serviceHelpers = new ServiceHelpers();

    @Autowired
    public ServiceManService(ServiceManRepository serviceManRepository) {
        this.serviceManRepository = serviceManRepository;
    }

    public List<ServiceManPayload> getAllServiceMen() {
        List<ServiceMan> serviceMen = serviceManRepository.findAll();
        List<ServiceManPayload> serviceMenResponse = new ArrayList<>();
        for (ServiceMan serviceMan: serviceMen) {
            serviceMenResponse.add(new ServiceManPayload(serviceMan.getId(), serviceMan.getFirstName(), serviceMan.getLastName(), serviceMan.getPhoneNumber(), serviceMan.getUsername(), serviceMan.getPassword(), serviceMan.getStartLocalization(), serviceMan.getExperience(), serviceHelpers.skillsToIds(serviceMan.getOwnedSkills()), serviceHelpers.repairsToIds(serviceMan.getRepairs())));
        }
        return serviceMenResponse;
    }

    public ServiceManPayload getServiceManById(Long id) throws NotFoundException {
        ServiceMan serviceMan = serviceManRepository.findById(id).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with id = %d not found.", id));
        }
        return new ServiceManPayload(serviceMan.getId(), serviceMan.getFirstName(), serviceMan.getLastName(), serviceMan.getPhoneNumber(), serviceMan.getUsername(), serviceMan.getPassword(), serviceMan.getStartLocalization(), serviceMan.getExperience(), serviceHelpers.skillsToIds(serviceMan.getOwnedSkills()), serviceHelpers.repairsToIds(serviceMan.getRepairs()));
    }

    public ResponseEntity<ServiceMan> addServiceMan(ServiceManPayload serviceManPayload) {
        ServiceMan savedServiceMan = serviceManRepository.save(new ServiceMan(serviceManPayload.getFirstName(), serviceManPayload.getLastName(), serviceManPayload.getPhoneNumber(), serviceManPayload.getUsername(), serviceManPayload.getPassword(), serviceManPayload.getStartLocalization(), serviceManPayload.getExperience(), serviceHelpers.idsToSkills(serviceManPayload.getSkills()), serviceHelpers.idsToRepairs(serviceManPayload.getRepairs())));
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
        serviceManRepository.save(new ServiceMan(serviceManPayload.getFirstName(), serviceManPayload.getLastName(), serviceManPayload.getPhoneNumber(), serviceManPayload.getUsername(), serviceManPayload.getPassword(), serviceManPayload.getStartLocalization(), serviceManPayload.getExperience(), serviceHelpers.idsToSkills(serviceManPayload.getSkills()), serviceHelpers.idsToRepairs(serviceManPayload.getRepairs())));
        return ResponseEntity.ok().build();
    }
}
