package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.payload.ServiceManPayload;
import com.oms.serverapp.service.ServiceManService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/serviceman")
@RestController
public class ServiceManController {

    private final ServiceManService serviceManService;

    @Autowired
    public ServiceManController(ServiceManService serviceManService) {
        this.serviceManService = serviceManService;
    }

    @GetMapping
    public List<ServiceManPayload> retrieveAllServiceMen() {
        return serviceManService.getAllServiceMen();
    }

    @GetMapping(path = "{id}")
    public ServiceManPayload retrieveServiceManById(@PathVariable("id") Long id) throws NotFoundException {
        return serviceManService.getServiceManById(id);
    }

    @PostMapping
    public ResponseEntity<ServiceMan> createServiceMan(@Valid @RequestBody ServiceManPayload serviceManPayload) {
        return serviceManService.addServiceMan(serviceManPayload);
    }

    @DeleteMapping(path = "{id}")
    public void deleteServiceManById(@PathVariable("id") Long id) {
        serviceManService.deleteServiceMan(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateServiceManById(@Valid @RequestBody ServiceManPayload serviceManPayload, @PathVariable("id") Long id) {
        return serviceManService.updateServiceMan(serviceManPayload, id);
    }

}
