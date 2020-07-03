package com.oms.serverapp.service;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class RepairService {

    private RepairRepository repairRepository;

    @Autowired
    public RepairService(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    public List<Repair> getAllRepairs() {
        return repairRepository.findAll();
    }

    public Repair getRepairById(Long id) {
        Optional<Repair> repair = repairRepository.findById(id);

        return repair.get();
    }

    public ResponseEntity<Repair> addRepair(Repair repair) {
        Repair savedRepair = repairRepository.save(repair);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedRepair).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteRepair(Long id) {
        repairRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateRepair(Repair repair, Long id) {
        Optional<Repair> repairOptional = repairRepository.findById(id);

        if (!repairOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        repairRepository.save(repair);
        return ResponseEntity.noContent().build();
    }
}
