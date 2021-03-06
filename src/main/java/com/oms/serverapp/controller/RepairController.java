package com.oms.serverapp.controller;

import com.oms.serverapp.util.OptimizationServices;
import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Repair;
import com.oms.serverapp.payload.RepairRequest;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/repair")
@RestController
public class RepairController {

    private final RepairService repairService;

    @Autowired
    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    @GetMapping
    public List<RepairResponse> retrieveAllRepairs() throws NotFoundException {
        return repairService.getAllRepairs();
    }

    @GetMapping(path = "{id}")
    public RepairResponse retrieveRepairById(@PathVariable("id") Long id) throws NotFoundException {
        return repairService.getRepairById(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<Repair> createRepair(@Valid @RequestBody RepairRequest repairRequest) {
        return repairService.addRepair(repairRequest);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping(path = "{id}")
    public void deleteRepairById(@PathVariable("id") Long id) {
        repairService.deleteRepair(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateRepairById(@RequestBody RepairRequest repairRequest, @PathVariable("id") Long id) {
        return repairService.updateRepair(repairRequest, id);
    }

    @PutMapping(path = "close/{id}")
    public void closeRepair(Authentication authentication, @PathVariable("id") Long id) {
        OptimizationServices.closeRepair(id, authentication.getName());
    }
}
