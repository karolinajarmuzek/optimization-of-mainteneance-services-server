package com.oms.serverapp.service;

import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.model.ServiceMan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceManService {

    private ServiceManRepository serviceManRepository;

    @Autowired
    public ServiceManService(ServiceManRepository serviceManRepository) {
        this.serviceManRepository = serviceManRepository;
    }

    public List<ServiceMan> getAllServiceMen() {
        return serviceManRepository.findAll();
    }

    //????
    public ServiceMan getServiceManById(Long id) {
        Optional<ServiceMan> serviceMan = serviceManRepository.findById(id);

        /*if (!serviceMan.isPresent()) {
            throw new ServiceManNotFoundException("id-" + id);
        }*/

        return serviceMan.get();
    }

    public ResponseEntity<ServiceMan> addServiceMan(ServiceMan serviceMan) {
        ServiceMan savedServiceMan = serviceManRepository.save(serviceMan);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedServiceMan.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteServiceMan(Long id) {
        serviceManRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateServiceMan(ServiceMan serviceMan, Long id) {
        Optional<ServiceMan> serviceManOptional = serviceManRepository.findById(id);

        if (!serviceManOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        serviceMan.setId(id);
        serviceManRepository.save(serviceMan);

        return ResponseEntity.ok().build();
    }
}
