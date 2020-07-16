package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.payload.ReportResponse;
import com.oms.serverapp.payload.ServiceManPayload;
import com.oms.serverapp.service.ServiceManService;
import com.oms.serverapp.util.RepairStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
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

    @GetMapping(path = "byToken")
    public ServiceManPayload retrieveServiceManByToken(Authentication authentication) throws NotFoundException {
        return serviceManService.getServiceManByUsername(authentication.getName());
    }

    @GetMapping(path = "byToken/repair")
    public List<RepairResponse> retrieveRepairsByServiceManToken(Authentication authentication) throws NotFoundException, ParseException {
        return serviceManService.getRepairsByServiceManUsername(authentication.getName(), null, null);
    }

    @GetMapping(path = "byToken/repair/actual")
    public List<RepairResponse> retrieveAllActualReportsByServiceManToken(Authentication authentication) throws NotFoundException {
        return serviceManService.getAllActualReportsByServiceManUsername(authentication.getName());
    }

    @GetMapping(path = "byToken/repair/status={status}")
    public List<RepairResponse> retrieveRepairsByServiceManToken(Authentication authentication, @PathVariable("status") RepairStatus repairStatus) throws NotFoundException, ParseException {
        return serviceManService.getRepairsByServiceManUsername(authentication.getName(), repairStatus, null);
    }

    @GetMapping(path = "byToken/repair/status={status}/date={date}")
    public List<RepairResponse> retrieveRepairsByServiceManToken(Authentication authentication, @PathVariable("status") RepairStatus repairStatus, @PathVariable("date") String date) throws NotFoundException, ParseException {
        return serviceManService.getRepairsByServiceManUsername(authentication.getName(), repairStatus, date);
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
    public ResponseEntity<Object> updateServiceManById(@RequestBody ServiceManPayload serviceManPayload, @PathVariable("id") Long id) {
        return serviceManService.updateServiceMan(serviceManPayload, id);
    }

}
