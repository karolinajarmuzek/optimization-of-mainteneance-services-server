package com.oms.serverapp.controller;

import com.oms.serverapp.exception.NotFoundException;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.service.ReportService;
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
    public List<Report> retrieveAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping(path = "{id}")
    public Report retrieveReportById(@PathVariable("id") Long id) throws NotFoundException {
        return reportService.getReportById(id);
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
        return reportService.addReport(report);
    }

    @DeleteMapping(path = "{id}")
    public void deleteReportById(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<Object> updateReportById(@Valid @RequestBody Report report, @PathVariable("id") Long id) {
        return reportService.updateReport(report, id);
    }
}
