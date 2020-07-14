package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Repair;
import com.oms.serverapp.payload.RepairRequest;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(path = "/byToken")
    public List<RepairResponse> retrieveRepairByToken(Authentication authentication) throws NotFoundException {
        return repairService.getRepairsByUsername(authentication.getName());
    }

    @GetMapping(path = "{id}")
    public RepairResponse retrieveRepairById(@PathVariable("id") Long id) throws NotFoundException {
        return repairService.getRepairById(id);
    }

    @PostMapping
    public ResponseEntity<Repair> createRepair(@Valid @RequestBody RepairRequest repairRequest) {
        return repairService.addRepair(repairRequest);
    }

    @DeleteMapping(path = "{id}")
    public void deleteRepairById(@PathVariable("id") Long id) {
        repairService.deleteRepair(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateRepairById(@Valid @RequestBody RepairRequest repairRequest, @PathVariable("id") Long id) {
        return repairService.updateRepair(repairRequest, id);
    }
}
