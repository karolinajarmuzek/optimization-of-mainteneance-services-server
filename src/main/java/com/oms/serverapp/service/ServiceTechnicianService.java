package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Repair;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.payload.ServiceTechnicianPayload;
import com.oms.serverapp.repository.ServiceTechnicianRepository;
import com.oms.serverapp.model.ServiceTechnician;
import com.oms.serverapp.util.RepairStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ServiceTechnicianService {

    private ServiceTechnicianRepository serviceTechnicianRepository;
    private SkillService skillService;
    private RepairService repairService;
    private PasswordEncoder encoder;
    private ReportService reportService;

    @Autowired
    public ServiceTechnicianService(ServiceTechnicianRepository serviceTechnicianRepository, SkillService skillService, RepairService repairService, PasswordEncoder encoder, ReportService reportService) {
        this.serviceTechnicianRepository = serviceTechnicianRepository;
        this.skillService = skillService;
        this.repairService = repairService;
        this.encoder = encoder;
        this.reportService = reportService;
    }

    public List<ServiceTechnicianPayload> getAllServiceTechnicians() {
        List<ServiceTechnician> serviceTechnicians = serviceTechnicianRepository.findAll();
        List<ServiceTechnicianPayload> serviceTechnicianResponses = new ArrayList<>();
        for (ServiceTechnician serviceTechnician : serviceTechnicians) {
            serviceTechnicianResponses.add(new ServiceTechnicianPayload(serviceTechnician));
        }
        return serviceTechnicianResponses;
    }

    public ServiceTechnicianPayload getServiceTechnicianById(Long id) throws NotFoundException {
        ServiceTechnician serviceTechnician = serviceTechnicianRepository.findById(id).orElse(null);
        if (serviceTechnician == null) {
            throw new NotFoundException(String.format("ServiceTechnician with id = %d not found.", id));
        }
        return new ServiceTechnicianPayload(serviceTechnician);
    }

    public ServiceTechnicianPayload getServiceTechnicianByUsername(String username) throws NotFoundException {
        ServiceTechnician serviceTechnician = serviceTechnicianRepository.findByUsername(username).orElse(null);
        if (serviceTechnician == null) {
            throw new NotFoundException(String.format("ServiceTechnician with username = %d not found.", username));
        }
        return new ServiceTechnicianPayload(serviceTechnician);
    }


    public List<RepairResponse> getAllActualReportsByServiceTechnicianUsername(String username) throws NotFoundException {
        ServiceTechnician serviceTechnician = serviceTechnicianRepository.findByUsername(username).orElse(null);
        if (serviceTechnician == null) {
            throw new NotFoundException(String.format("ServiceTechnician with username = %d not found.", username));
        }
        String repairDate;

        List<RepairResponse> repairResponses = new ArrayList<>();
        for (Repair repair : serviceTechnician.getRepairs()) {
            repairDate = new SimpleDateFormat("dd.MM.yyyy").format(repair.getDate());
            if (repair.getStatus() != RepairStatus.FINISHED){// && repairDate == todayDate) {
                repairResponses.add(new RepairResponse(repair, reportService.generateReportResponse(repair.getReport())));
            }
        }
        return  repairResponses;
    }

    public List<RepairResponse> getRepairsByServiceTechnicianUsername(String username, RepairStatus repairStatus, String date) throws NotFoundException, ParseException {
        ServiceTechnician serviceTechnician = serviceTechnicianRepository.findByUsername(username).orElse(null);
        if (serviceTechnician == null) {
            throw new NotFoundException(String.format("ServiceTechnician with username = %d not found.", username));
        }

        List<RepairResponse> repairResponses = new ArrayList<>();
        for (Repair repair : serviceTechnician.getRepairs()) {
            if (repairStatus != null && date != null) {
                String newDate = new SimpleDateFormat("dd.MM.yyyy").format(repair.getDate());
                if (repair.getStatus() == repairStatus && newDate.equals(date)) {
                    repairResponses.add(new RepairResponse(repair, reportService.generateReportResponse(repair.getReport())));
                }
            } else if (repairStatus != null && date == null && repair.getStatus() == repairStatus) {
                repairResponses.add(new RepairResponse(repair, reportService.generateReportResponse(repair.getReport())));
            } else if (repairStatus == null && date == null) {
                repairResponses.add(new RepairResponse(repair, reportService.generateReportResponse(repair.getReport())));
            }
        }
        return repairResponses;
    }

    public ResponseEntity<ServiceTechnician> addServiceTechnician(ServiceTechnicianPayload serviceTechnicianPayload) {
        ServiceTechnician savedServiceTechnician = serviceTechnicianRepository.save(new ServiceTechnician(serviceTechnicianPayload, encoder.encode(serviceTechnicianPayload.getPassword()), skillService.idsToSkills(serviceTechnicianPayload.getSkills()), repairService.idsToRepairs(serviceTechnicianPayload.getRepairs())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedServiceTechnician.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteServiceTechnician(Long id) {
        serviceTechnicianRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateServiceTechnician(ServiceTechnicianPayload serviceTechnicianPayload, Long id) {
        ServiceTechnician serviceTechnician = serviceTechnicianRepository.findById(id).orElse(null);
        if (serviceTechnician == null) {
            return ResponseEntity.notFound().build();
        }
        String password = serviceTechnicianPayload.getPassword() != null ? encoder.encode(serviceTechnicianPayload.getPassword()) : null;
        serviceTechnicianRepository.save(new ServiceTechnician(serviceTechnician, serviceTechnicianPayload, password, skillService.idsToSkills(serviceTechnicianPayload.getSkills()), repairService.idsToRepairs(serviceTechnicianPayload.getRepairs())));
        return ResponseEntity.ok().build();
    }

    public Set<ServiceTechnician> idsToServiceTechnician(Set<Long> serviceTechniciansIds) {
        Set<ServiceTechnician> serviceTechnicians = new HashSet<>();
        if (serviceTechniciansIds != null) {
            for (Long serviceTechnicianId : serviceTechniciansIds) {
                ServiceTechnician serviceTechnician = serviceTechnicianRepository.findById(serviceTechnicianId).orElse(null);
                if (serviceTechnician != null) serviceTechnicians.add(serviceTechnician);
            }
        }
        return serviceTechnicians;
    }
}
