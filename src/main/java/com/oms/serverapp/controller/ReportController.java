package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.payload.ReportRequest;
import com.oms.serverapp.payload.ReportResponse;
import com.oms.serverapp.service.ReportService;
import com.oms.serverapp.util.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/report")
@RestController
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<ReportResponse> retrieveAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping(path = "status={status}")
    public List<ReportResponse> retrieveAllReportsByStatus(@PathVariable("status") ReportStatus reportStatus) {
        return reportService.getAllReportsByStatus(reportStatus);
    }

    @GetMapping(path = "{id}")
    public ReportResponse retrieveReportById(@PathVariable("id") Long id) throws NotFoundException {
        return reportService.getReportById(id);
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@Valid @RequestBody ReportRequest reportRequest) {
        return reportService.addReport(reportRequest);
    }

    @DeleteMapping(path = "{id}")
    public void deleteReportById(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateReportById(@Valid @RequestBody ReportRequest reportRequest, @PathVariable("id") Long id) {
        return reportService.updateReport(reportRequest, id);
    }
}
