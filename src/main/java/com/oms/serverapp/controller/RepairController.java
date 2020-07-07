package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Repair;
import com.oms.serverapp.payload.RepairPayload;
import com.oms.serverapp.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/repair")
@RestController
public class RepairController {

    private final RepairService reportService;

    @Autowired
    public RepairController(RepairService repairService) {
        this.reportService = repairService;
    }

    @GetMapping
    public List<RepairPayload> retrieveAllRepairs() {
        return reportService.getAllRepairs();
    }

    @GetMapping(path = "{id}")
    public RepairPayload retrieveRepairById(@PathVariable("id") Long id) throws NotFoundException {
        return reportService.getRepairById(id);
    }

    @PostMapping
    public ResponseEntity<Repair> createRepair(@Valid @RequestBody RepairPayload repairPayload) {
        return reportService.addRepair(repairPayload);
    }

    @DeleteMapping(path = "{id}")
    public void deleteRepairById(@PathVariable("id") Long id) {
        reportService.deleteRepair(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateRepairById(@Valid @RequestBody RepairPayload repairPayload, @PathVariable("id") Long id) {
        return reportService.updateRepair(repairPayload, id);
    }
}
