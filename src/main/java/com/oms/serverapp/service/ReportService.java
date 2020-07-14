package com.oms.serverapp.service;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.*;
import com.oms.serverapp.payload.*;
import com.oms.serverapp.repository.*;
import com.oms.serverapp.util.Status;
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

    public ReportResponse generateReportResponse(Report report) {
        CustomerPayload customerPayload = new CustomerPayload(customerRepository.findById(report.getCustomer().getId()).orElse(null));
        FailurePayload failurePayload = new FailurePayload(failureRepository.findById(report.getFailure().getId()).orElse(null));
        DevicePayload devicePayload = new DevicePayload(deviceRepository.findById(report.getDevice().getId()).orElse(null));
        return new ReportResponse(report, customerPayload, failurePayload, devicePayload);
    }

    public Report generateReport(Report report, ReportRequest reportRequest) {
        Customer customer = customerRepository.findById(reportRequest.getCustomer()).orElse(report == null ? null : report.getCustomer());
        Failure failure = failureRepository.findById(reportRequest.getFailure()).orElse(report == null ? null : report.getFailure());
        Device device = deviceRepository.findById(reportRequest.getDevice()).orElse(report == null ? null : report.getDevice());
        if (report == null) {
            return new Report(reportRequest, customer, failure, device);
        } else {
            Repair repair = repairRepository.findById(reportRequest.getRepair()).orElse(report.getRepair());
             return new Report(report, reportRequest, customer, failure, device, repair);
        }
    }

    public List<ReportResponse> getAllReports() {
        List<Report> reports = reportRepository.findAll();
        List<ReportResponse> reportsResponse = new ArrayList<>();
        for (Report report: reports) {
            reportsResponse.add(generateReportResponse(report));
        }
        return reportsResponse;
    }

    public List<ReportResponse> getAllReportsByStatus(Status status) {
        List<Report> reports = reportRepository.findReportsByStatus(status);
        List<ReportResponse> reportsResponse = new ArrayList<>();
        for (Report report: reports) {
            reportsResponse.add(generateReportResponse(report));
        }
        return reportsResponse;
    }

    public ReportResponse getReportById(Long id) throws NotFoundException {
        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {
            throw new NotFoundException(String.format("Report with id = %d not found.", id));
        }
        return generateReportResponse(report);
    }

    public ResponseEntity<Report> addReport(ReportRequest reportRequest) {
        Report savedReport = reportRepository.save(generateReport(null, reportRequest));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedReport).toUri();
        return ResponseEntity.created(location).build();
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateReport(ReportRequest reportRequest, Long id) {
        Report report = reportRepository.findById(id).orElse(null);
        if (report == null) {    
            return ResponseEntity.notFound().build();
        }
        reportRepository.save(generateReport(report, reportRequest));
        return ResponseEntity.noContent().build();
    }
}
