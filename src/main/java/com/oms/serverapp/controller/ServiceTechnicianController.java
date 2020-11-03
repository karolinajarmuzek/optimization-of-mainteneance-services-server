package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.ServiceTechnician;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.payload.ServiceTechnicianPayload;
import com.oms.serverapp.service.ServiceTechnicianService;
import com.oms.serverapp.util.RepairStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@RequestMapping("api/serviceTechnician")
@RestController
public class ServiceTechnicianController {

    private final ServiceTechnicianService serviceTechnicianService;

    @Autowired
    public ServiceTechnicianController(ServiceTechnicianService serviceTechnicianService) {
        this.serviceTechnicianService = serviceTechnicianService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<ServiceTechnicianPayload> retrieveAllServiceTechnicians() {
        return serviceTechnicianService.getAllServiceTechnicians();
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(path = "{id}")
    public ServiceTechnicianPayload retrieveServiceTechnicianById(@PathVariable("id") Long id) throws NotFoundException {
        return serviceTechnicianService.getServiceTechnicianById(id);
    }

    @GetMapping(path = "byToken")
    public ServiceTechnicianPayload retrieveServiceTechnicianByToken(Authentication authentication) throws NotFoundException {
        return serviceTechnicianService.getServiceTechnicianByUsername(authentication.getName());
    }

    @GetMapping(path = "byToken/repair")
    public List<RepairResponse> retrieveRepairsByServiceTechnicianToken(Authentication authentication) throws NotFoundException, ParseException {
        return serviceTechnicianService.getRepairsByServiceTechnicianUsername(authentication.getName(), null, null);
    }

    @GetMapping(path = "byToken/repair/actual")
    public List<RepairResponse> retrieveAllActualReportsByServiceTechnicianToken(Authentication authentication) throws NotFoundException {
        return serviceTechnicianService.getAllActualReportsByServiceTechnicianUsername(authentication.getName());
    }

    @GetMapping(path = "byToken/repair/status={status}")
    public List<RepairResponse> retrieveRepairsByServiceTechnicianToken(Authentication authentication, @PathVariable("status") RepairStatus repairStatus) throws NotFoundException, ParseException {
        return serviceTechnicianService.getRepairsByServiceTechnicianUsername(authentication.getName(), repairStatus, null);
    }

    @GetMapping(path = "byToken/repair/status={status}/date={date}")
    public List<RepairResponse> retrieveRepairsByServiceTechnicianToken(Authentication authentication, @PathVariable("status") RepairStatus repairStatus, @PathVariable("date") String date) throws NotFoundException, ParseException {
        return serviceTechnicianService.getRepairsByServiceTechnicianUsername(authentication.getName(), repairStatus, date);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<ServiceTechnician> createServiceTechnician(@Valid @RequestBody ServiceTechnicianPayload serviceTechnicianPayload) {
        return serviceTechnicianService.addServiceTechnician(serviceTechnicianPayload);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "{id}")
    public void deleteServiceTechnicianById(@PathVariable("id") Long id) {
        serviceTechnicianService.deleteServiceTechnician(id);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateServiceTechnicianById(@RequestBody ServiceTechnicianPayload serviceTechnicianPayload, @PathVariable("id") Long id) {
        return serviceTechnicianService.updateServiceTechnician(serviceTechnicianPayload, id);
    }

}
