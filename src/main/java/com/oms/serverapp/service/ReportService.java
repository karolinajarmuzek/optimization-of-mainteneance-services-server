package com.oms.serverapp.service;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository repository) {
        this.reportRepository = repository;
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        Optional<Report> report = reportRepository.findById(id);

        return report.get();
    }

    public ResponseEntity<Report> addReport(Report report) {
        Report savedReport = reportRepository.save(report);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedReport).toUri();

        return ResponseEntity.created(location).build();
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    public ResponseEntity<Object> updateReport(Report report, Long id) {
        Optional<Report> reportOptional = reportRepository.findById(id);

        if (!reportOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        reportRepository.save(report);
        return ResponseEntity.noContent().build();
    }

}
