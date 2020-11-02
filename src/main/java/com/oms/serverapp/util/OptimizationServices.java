package com.oms.serverapp.util;

import com.oms.serverapp.model.*;
import com.oms.serverapp.repository.*;
import com.oms.serverapp.util.RepairStatus;
import com.oms.serverapp.util.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptimizationServices {

    private static ReportRepository reportRepository;
    private static ServiceTechnicianRepository serviceTechnicianRepository;
    private static SkillRepository skillRepository;
    private static SparePartRepository sparePartRepository;
    private static RepairRepository repairRepository;

    @Autowired
    public OptimizationServices(ReportRepository reportRepository, ServiceTechnicianRepository serviceTechnicianRepository, SkillRepository skillRepository, SparePartRepository sparePartRepository, RepairRepository repairRepository) {
        this.reportRepository = reportRepository;
        this.serviceTechnicianRepository = serviceTechnicianRepository;
        this.skillRepository = skillRepository;
        this.sparePartRepository = sparePartRepository;
        this.repairRepository = repairRepository;
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

    public static Repair getRepairByServiceTechnician(Long serviceTechnicianId) {
        return repairRepository.findFirstByServiceTechnicianIdOrderByDateDesc(serviceTechnicianId);
    }

    public static List<Repair> getRepairsNotFinished(ServiceTechnician serviceTechnician) {
        return repairRepository.findByServiceTechnicianAndStatusNot(serviceTechnician, RepairStatus.FINISHED);
    }

    public static void addRepair(Repair repair) {
        repairRepository.save(repair);
    }

    public static void updateRepair(Repair repair) {
        repairRepository.save(repair);
    }

    public static void closeRepair(Long repairId, String username) {
        Repair repair = repairRepository.findById(repairId).orElse(null);
        if (repair != null) {
            repair.setStatus(RepairStatus.FINISHED);
            Report report = repair.getReport();
            report.setStatus(ReportStatus.FINISHED);
            repairRepository.save(repair);
            reportRepository.save(report);
            ServiceTechnician serviceTechnician = serviceTechnicianRepository.findByUsername(username).orElse(null);
            Repair newRepair = repairRepository.findFirstByServiceTechnicianIdAndStatusOrderByDateAsc(serviceTechnician.getId(), RepairStatus.PENDING);
            newRepair.setStatus(RepairStatus.REPAIRING);
            repairRepository.save(newRepair);
        }
    }
}
