package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Repair;
import com.oms.serverapp.payload.RepairResponse;
import com.oms.serverapp.payload.ReportResponse;
import com.oms.serverapp.payload.ServiceManPayload;
import com.oms.serverapp.repository.ServiceManRepository;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.util.RepairStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ServiceManService {

    private ServiceManRepository serviceManRepository;
    private SkillService skillService;
    private RepairService repairService;
    private PasswordEncoder encoder;
    private ReportService reportService;

    @Autowired
    public ServiceManService(ServiceManRepository serviceManRepository, SkillService skillService, RepairService repairService, PasswordEncoder encoder, ReportService reportService) {
        this.serviceManRepository = serviceManRepository;
        this.skillService = skillService;
        this.repairService = repairService;
        this.encoder = encoder;
        this.reportService = reportService;
    }

    public List<ServiceManPayload> getAllServiceMen() {
        List<ServiceMan> serviceMen = serviceManRepository.findAll();
        List<ServiceManPayload> serviceMenResponse = new ArrayList<>();
        for (ServiceMan serviceMan: serviceMen) {
            serviceMenResponse.add(new ServiceManPayload(serviceMan));
        }
        return serviceMenResponse;
    }

    public ServiceManPayload getServiceManById(Long id) throws NotFoundException {
        ServiceMan serviceMan = serviceManRepository.findById(id).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with id = %d not found.", id));
        }
        return new ServiceManPayload(serviceMan);
    }

    public ServiceManPayload getServiceManByUsername(String username) throws NotFoundException {
        ServiceMan serviceMan = serviceManRepository.findByUsername(username).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with username = %d not found.", username));
        }
        return new ServiceManPayload(serviceMan);
    }

    public List<RepairResponse> getRepairsByServiceManUsername(String username, RepairStatus repairStatus, String date) throws NotFoundException, ParseException {
        ServiceMan serviceMan = serviceManRepository.findByUsername(username).orElse(null);
        if (serviceMan == null) {
            throw new NotFoundException(String.format("Serviceman with username = %d not found.", username));
        }

        List<RepairResponse> repairResponses = new ArrayList<>();
        for (Repair repair : serviceMan.getRepairs()) {
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

    public ResponseEntity<ServiceMan> addServiceMan(ServiceManPayload serviceManPayload) {
        ServiceMan savedServiceMan = serviceManRepository.save(new ServiceMan(serviceManPayload, encoder.encode(serviceManPayload.getPassword()), skillService.idsToSkills(serviceManPayload.getSkills()), repairService.idsToRepairs(serviceManPayload.getRepairs())));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedServiceMan.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteServiceMan(Long id) {
        serviceManRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateServiceMan(ServiceManPayload serviceManPayload, Long id) {
        ServiceMan serviceMan = serviceManRepository.findById(id).orElse(null);
        if (serviceMan == null) {
            return ResponseEntity.notFound().build();
        }
        String password = serviceManPayload.getPassword() != null ? encoder.encode(serviceManPayload.getPassword()) : null;
        serviceManRepository.save(new ServiceMan(serviceMan, serviceManPayload, password, skillService.idsToSkills(serviceManPayload.getSkills()), repairService.idsToRepairs(serviceManPayload.getRepairs())));
        return ResponseEntity.ok().build();
    }

    public Set<ServiceMan> idsToServiceMen(Set<Long> serviceMenIds) {
        Set<ServiceMan> serviceMen = new HashSet<>();
        if (serviceMenIds != null) {
            for (Long serviceManId : serviceMenIds) {
                ServiceMan serviceMan = serviceManRepository.findById(serviceManId).orElse(null);
                if (serviceMan != null) serviceMen.add(serviceMan);
            }
        }
        return serviceMen;
    }
}
