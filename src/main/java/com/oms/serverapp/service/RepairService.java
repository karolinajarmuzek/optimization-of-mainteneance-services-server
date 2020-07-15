package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.*;
import com.oms.serverapp.payload.RepairRequest;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.payload.ReportRequest;
import com.oms.serverapp.payload.ReportResponse;
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
    private ReportService reportService;

    @Autowired
    public RepairService(RepairRepository repairRepository, ServiceManRepository serviceManRepository, ReportRepository reportRepository, ReportService reportService) {
        this.repairRepository = repairRepository;
        this.serviceManRepository = serviceManRepository;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
    }

    public List<RepairResponse> getAllRepairs() throws NotFoundException {
        List<Repair> repairs = repairRepository.findAll();
        List<RepairResponse> repairsResponse = new ArrayList<>();
        for (Repair repair: repairs) {
            ReportResponse reportResponse = reportService.getReportById(repair.getReport().getId());
            repairsResponse.add(new RepairResponse(repair, reportResponse));
        }
        return repairsResponse;
    }

    public RepairResponse getRepairById(Long id) throws NotFoundException {
        Repair repair = repairRepository.findById(id).orElse(null);
        if (repair == null) {
            throw new NotFoundException(String.format("Repair with id=%d not found", id));
        }
        ReportResponse reportResponse = reportService.getReportById(repair.getReport().getId());
        return new RepairResponse(repair, reportResponse);
    }

    public ResponseEntity<Repair> addRepair(RepairRequest repairRequest) {
        ServiceMan serviceMan = serviceManRepository.findById(repairRequest.getServiceMan()).orElse(null);
        Report report = reportRepository.findById(repairRequest.getReport()).orElse(null);
        Repair savedRepair = repairRepository.save(new Repair(repairRequest, serviceMan, report));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedRepair).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteRepair(Long id) {
        repairRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateRepair(RepairRequest repairRequest, Long id) {
        Repair repair = repairRepository.findById(id).orElse(null);
        if (repair == null) {
            return ResponseEntity.notFound().build();
        }
        ServiceMan serviceMan = repair.getServiceMan();
        if (repairRequest.getServiceMan() != null) {
            serviceMan = serviceManRepository.findById(repairRequest.getServiceMan()).orElse(repair.getServiceMan());
        }
        Report report = repair.getReport();
        if (repairRequest.getReport() != null) {
            report = reportRepository.findById(repairRequest.getReport()).orElse(repair.getReport()); //throw Exception -> report must be not null
        }
        repairRepository.save(new Repair(repair, repairRequest, serviceMan, report));
        return ResponseEntity.noContent().build();
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
