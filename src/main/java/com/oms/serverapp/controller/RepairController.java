package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Repair;
import com.oms.serverapp.service.RepairService;
import com.oms.serverapp.service.ReportService;
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
    public List<Repair> retrieveAllRepairs() {
        return reportService.getAllRepairs();
    }

    @GetMapping(path = "{id}")
    public Repair retrieveRepairById(@PathVariable("id") Long id) throws NotFoundException {
        return reportService.getRepairById(id);
    }

    @PostMapping
    public ResponseEntity<Repair> createRepair(@Valid @RequestBody Repair repair) {
        return reportService.addRepair(repair);
    }

    @DeleteMapping(path = "{id}")
    public void deleteRepairById(@PathVariable("id") Long id) {
        reportService.deleteRepair(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateRepairById(@Valid @RequestBody Repair repair, @PathVariable("id") Long id) {
        return reportService.updateRepair(repair, id);
    }
}
