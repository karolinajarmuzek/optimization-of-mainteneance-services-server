package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.payload.ReportPayload;
import com.oms.serverapp.service.ReportService;
import com.oms.serverapp.util.Status;
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
    public List<ReportPayload> retrieveAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping(path = "status={status}")
    public List<ReportPayload> retrieveAllReportsByStatus(@PathVariable("status") Status status) {
        return reportService.getAllReportsByStatus(status);
    }

    @GetMapping(path = "{id}")
    public ReportPayload retrieveReportById(@PathVariable("id") Long id) throws NotFoundException {
        return reportService.getReportById(id);
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@Valid @RequestBody ReportPayload reportPayload) {
        return reportService.addReport(reportPayload);
    }

    @DeleteMapping(path = "{id}")
    public void deleteReportById(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateReportById(@Valid @RequestBody ReportPayload reportPayload, @PathVariable("id") Long id) {
        return reportService.updateReport(reportPayload, id);
    }
}
