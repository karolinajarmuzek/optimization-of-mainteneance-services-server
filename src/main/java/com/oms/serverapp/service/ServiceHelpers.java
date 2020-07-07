package com.oms.serverapp.service;

import com.oms.serverapp.model.Repair;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.model.ServiceMan;
import com.oms.serverapp.model.Skill;
import com.oms.serverapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class ServiceHelpers {

    private SkillRepository skillRepository;
    private ReportRepository reportRepository;
    private ServiceManRepository serviceManRepository;
    private RepairRepository repairRepository;

    @Autowired
    public ServiceHelpers(SkillRepository skillRepository, ReportRepository reportRepository, ServiceManRepository serviceManRepository, RepairRepository repairRepository) {
        this.skillRepository = skillRepository;
        this.reportRepository = reportRepository;
        this.serviceManRepository = serviceManRepository;
        this.repairRepository = repairRepository;
    }

    public ServiceHelpers() {
    }

    public Set<Long> serviceMenToIds(Set<ServiceMan> serviceMen) {
        Set<Long> serviceMenIds = new HashSet<>();
        if (serviceMen != null) {
            for (ServiceMan serviceMan : serviceMen) {
                serviceMenIds.add(serviceMan.getId());
            }
        }
        return serviceMenIds;
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

    public Set<Long> skillsToIds(Set<Skill> skills) {
        Set<Long> skillsIds = new HashSet<>();
        if (skills != null) {
            for (Skill skill : skills) {
                skillsIds.add(skill.getId());
            }
        }
        return skillsIds;
    }

    public Set<Skill> idsToSkills(Set<Long> skillsIds) {
        Set<Skill> skills = new HashSet<>();
        if (skillsIds != null) {
            for (Long skillId : skillsIds) {
                Skill skill = skillRepository.findById(skillId).orElse(null);
                if (skill != null) skills.add(skill);
            }
        }
        return skills;
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

    public Set<Long> repairsToIds(Set<Repair> repairs) {
        Set<Long> repairsIds = new HashSet<>();
        if (repairs != null) {
            for (Repair repair : repairs) {
                repairsIds.add(repair.getId());
            }
        }
        return repairsIds;
    }

    public Set<Repair> idsToRepairs(Set<Long> repairsIds) {
        Set<Repair> repairs = new HashSet<>();
        if (repairsIds != null) {
            for (Long repairId : repairsIds) {
                Repair repair = repairRepository.findById(repairId).orElse(null);
                if (repair != null) repairs.add(repair);
            }
        }
        return repairs;
    }

}
