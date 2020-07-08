package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.*;
import com.oms.serverapp.payload.RepairPayload;
import com.oms.serverapp.repository.RepairRepository;
import com.oms.serverapp.repository.ReportRepository;
import com.oms.serverapp.repository.ServiceManRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class RepairService {

    private RepairRepository repairRepository;
    private ServiceManRepository serviceManRepository;
    private ReportRepository reportRepository;

    @Autowired
    public RepairService(RepairRepository repairRepository, ServiceManRepository serviceManRepository, ReportRepository reportRepository) {
        this.repairRepository = repairRepository;
        this.serviceManRepository = serviceManRepository;
        this.reportRepository = reportRepository;
    }

    public List<RepairPayload> getAllRepairs() {
        List<Repair> repairs = repairRepository.findAll();
        List<RepairPayload> repairsResponse = new ArrayList<>();
        for (Repair repair: repairs) {
            repairsResponse.add(new RepairPayload(repair.getId(), repair.getServiceMan().getId(), repair.getDate(), repair.getTime(), repair.getStatus(), repair.getReport().getId()));
        }
        return repairsResponse;
    }

    public RepairPayload getRepairById(Long id) throws NotFoundException {
        Repair repair = repairRepository.findById(id).orElse(null);
        if (repair == null) {
            throw new NotFoundException(String.format("Repair with id=%d not found", id));
        }
        return new RepairPayload(repair.getId(), repair.getServiceMan().getId(), repair.getDate(), repair.getTime(), repair.getStatus(), repair.getReport().getId());
    }

    public ResponseEntity<Repair> addRepair(RepairPayload repairPayload) {
        ServiceMan serviceMan = serviceManRepository.findById(repairPayload.getServiceMan()).orElse(null);
        Report report = reportRepository.findById(repairPayload.getReport()).orElse(null);
        Repair savedRepair = repairRepository.save(new Repair(serviceMan, repairPayload.getDate(), repairPayload.getTime(), repairPayload.getStatus(), report));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedRepair).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteRepair(Long id) {
        repairRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateRepair(RepairPayload repairPayload, Long id) {
        Repair repair = repairRepository.findById(id).orElse(null);
        if (repair == null) {
            return ResponseEntity.notFound().build();
        }
        ServiceMan serviceMan = serviceManRepository.findById(repairPayload.getServiceMan()).orElse(null);
        Report report = reportRepository.findById(repairPayload.getReport()).orElse(null);
        repairRepository.save(new Repair(repair.getId(), serviceMan, repairPayload.getDate(), repairPayload.getTime(), repairPayload.getStatus(), report));
        return ResponseEntity.noContent().build();
    }

    public Set<Long> repairsToIds(Set<Repair> repairs) {
        Set<Long> repairsIds = new HashSet<>();
        if (repairs != null) {
            for (Repair repair : repairs) {
                repairsIds.add(repair.getId());
            }
        }
        return repairsIds;
    }

    public Set<Repair> idsToRepairs(Set<Long> repairsIds) {
        Set<Repair> repairs = new HashSet<>();
        if (repairsIds != null) {
            for (Long repairId : repairsIds) {
                Repair repair = repairRepository.findById(repairId).orElse(null);
                if (repair != null) repairs.add(repair);
            }
        }
        return repairs;
    }
}
