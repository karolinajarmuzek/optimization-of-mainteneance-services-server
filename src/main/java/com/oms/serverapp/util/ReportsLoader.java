package com.oms.serverapp.util;

import com.oms.serverapp.model.Report;
import com.oms.serverapp.model.Skill;

import java.util.List;
import java.util.Map;

public class ReportsLoader {

    private final List<Report> reportsToSchedule;
    private final Map<Report, Skill> reportSkillMap;

    public ReportsLoader(List<Report> reportsToSchedule, Map<Report, Skill> reportSkillMap) {
        this.reportsToSchedule = reportsToSchedule;
        this.reportSkillMap = reportSkillMap;
    }

    public List<Report> getReportsToSchedule() {
        return reportsToSchedule;
    }

    public Map<Report, Skill> getReportSkillMap() {
        return reportSkillMap;
    }
}
