package com.oms.serverapp;

import com.oms.serverapp.model.*;
import com.oms.serverapp.repository.ReportRepository;
import com.oms.serverapp.repository.ServiceTechnicianRepository;
import com.oms.serverapp.repository.SkillRepository;
import com.oms.serverapp.repository.SparePartRepository;
import com.oms.serverapp.util.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptimizationServices {

    private static ReportRepository reportRepository;
    private static ServiceTechnicianRepository serviceTechnicianRepository;
    private static SkillRepository skillRepository;
    private static SparePartRepository sparePartRepository;

    @Autowired
    public OptimizationServices(ReportRepository reportRepository, ServiceTechnicianRepository serviceTechnicianRepository, SkillRepository skillRepository, SparePartRepository sparePartRepository) {
        this.reportRepository = reportRepository;
        this.serviceTechnicianRepository = serviceTechnicianRepository;
        this.skillRepository = skillRepository;
        this.sparePartRepository = sparePartRepository;
    }

    public static List<ServiceTechnician> loadServiceTechnicians() {
        return serviceTechnicianRepository.findAll();
     }

     public static List<Report> loadReports() {
        return reportRepository.findAll();
     }

     public static Skill getSkillByDeviceAndFailure(Device device, Failure failure) {
        return skillRepository.findByDeviceAndFailure(device, failure);
     }

     public static void updateReport(Report report) {
        reportRepository.save(report);
     }

     public static List<Report> loadReportsWithStatus(ReportStatus status) {
        return reportRepository.findReportsByStatus(status);
     }

    public static List<SparePart> loadSpareParts() {
        return sparePartRepository.findAll();
    }

    public static void updateSparePart(SparePart sparePart) {
        sparePartRepository.save(sparePart);
    }

    public static SparePart getSparePartById(Long id) {
        return sparePartRepository.findById(id).orElse(null);
    }

}
