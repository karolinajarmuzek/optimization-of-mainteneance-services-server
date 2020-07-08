package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.*;
import com.oms.serverapp.payload.ReportPayload;
import com.oms.serverapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class ReportService {

    private ReportRepository reportRepository;
    private CustomerRepository customerRepository;
    private DeviceRepository deviceRepository;
    private FailureRepository failureRepository;
    private RepairRepository repairRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository, CustomerRepository customerRepository, DeviceRepository deviceRepository, FailureRepository failureRepository, RepairRepository repairRepository) {
        this.reportRepository = reportRepository;
        this.customerRepository = customerRepository;
        this.deviceRepository = deviceRepository;
        this.failureRepository = failureRepository;
        this.repairRepository = repairRepository;
    }

    public List<ReportPayload> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        List<ReportPayload> reportsResponse = new ArrayList<>();
        for (Report report: reports) {
            reportsResponse.add(new ReportPayload(report.getId(), report.getCustomer().getId(), report.getFailure().getId(), report.getDevice().getId(), report.getDate(), report.getLocation(), report.getDescription(), report.getStatus(), report.getRepair().getId()));
        }
        return reportsResponse;
    }

    public ReportPayload getReportById(Long id) throws NotFoundException {
        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            throw new NotFoundException(String.format("Report with id = %d not found.", id));
        }
        return new ReportPayload(report.getId(), report.getCustomer().getId(), report.getFailure().getId(), report.getDevice().getId(), report.getDate(), report.getLocation(), report.getDescription(), report.getStatus(), report.getRepair().getId());
    }

    public ResponseEntity<Report> addReport(ReportPayload reportPayload) {
        Customer customer = customerRepository.findById(reportPayload.getCustomer()).orElse(null);
        Failure failure = failureRepository.findById(reportPayload.getFailure()).orElse(null);
        Device device = deviceRepository.findById(reportPayload.getDevice()).orElse(null);
        Repair repair = repairRepository.findById(reportPayload.getRepair()).orElse(null);
        Report savedReport = reportRepository.save(new Report(customer, failure, device, reportPayload.getDate(), reportPayload.getLocation(), reportPayload.getDescription(), reportPayload.getStatus(), repair));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedReport).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateReport(ReportPayload reportPayload, Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        Customer customer = customerRepository.findById(reportPayload.getCustomer()).orElse(null);
        Failure failure = failureRepository.findById(reportPayload.getFailure()).orElse(null);
        Device device = deviceRepository.findById(reportPayload.getDevice()).orElse(null);
        Repair repair = repairRepository.findById(reportPayload.getRepair()).orElse(null);
        reportRepository.save(new Report(repair.getId(), customer, failure, device, reportPayload.getDate(), reportPayload.getLocation(), reportPayload.getDescription(), reportPayload.getStatus(), repair));
        return ResponseEntity.noContent().build();
    }

    public Set<Long> reportsToIds(Set<Report> reports) {
        Set<Long> reportsIds = new HashSet<>();
        if (reports != null) {
            for (Report report : reports) {
                reportsIds.add(report.getId());
            }
        }
        return reportsIds;
    }

    public Set<Report> idsToReports(Set<Long> reportsIds) {
        Set<Report> reports = new HashSet<>();
        if (reportsIds != null) {
            for (Long reportId : reportsIds) {
                Report report = reportRepository.findById(reportId).orElse(null);
                if (report != null) reports.add(report);
            }
        }
        return reports;
    }

}
