package com.oms.serverapp.util;

import com.oms.serverapp.OptimizationServices;
import com.oms.serverapp.algorithms.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestsGenerator {
    private final String CSV_FILE_PATH = "src/main/java/com/oms/serverapp/TESTS/";

    private int numberOfTests;

    public TestsGenerator(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }

    public void doTests(Scheduler.algorithms algorithmName, int scheduleInterval, int maxRepairTime, boolean testing, String firstSchedule, String startTime ) {
        CSVGenerator csvGenerator = new CSVGenerator();
        int numberOfServiceTechnicians = OptimizationServices.loadServiceTechnicians().size() - 1;
        int numberOfReports = OptimizationServices.loadReports().size();
        String fileName = CSV_FILE_PATH + algorithmName + "_I" + scheduleInterval + "_S" + numberOfServiceTechnicians + "_R" + numberOfReports;
        if (algorithmName == Scheduler.algorithms.ANT) fileName += "_af" + AntColony.getAntFactor() + "_it" + AntColony.getMaxIterations() + "_rf" + AntColony.getRandomFactor() + "_alpha" + AntColony.getAlpha() + "_beta" + AntColony.getBeta() + "_e" + AntColony.getEvaporation();
        List<String[]> data = new ArrayList<>();

        for (int i = 0; i < numberOfTests; i++) {
            List<String[]> serviceTechnicians = new ArrayList<>();
            Algorithm algorithm = null;

            if (algorithmName == Scheduler.algorithms.GREEDY)
                algorithm = new GreedyAlgorithm(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime);
            else if (algorithmName == Scheduler.algorithms.ANT)
                algorithm = new AntColony(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime);

            SolutionDetails details = algorithm.tests();
            if (details != null) {
                for (Map.Entry<Long, ServiceTechnicianRepairInfos> entry : details.getServiceTechnicianRepairInfosMap().entrySet()) {
                    List<Long> reportsId = details.getServiceTechnicianRepairInfosMap().get(entry.getKey()).getAssignedReports().stream().map(report -> report.getId()).collect(Collectors.toList());
                    serviceTechnicians.add(new String[]{String.valueOf(entry.getKey()), String.valueOf(details.getServiceTechnicianRepairInfosMap().get(entry.getKey()).getRepairsTime()), String.valueOf(reportsId).replaceAll(", ", "-")});
                }
                data.add(new String[]{String.valueOf(i + 1), String.valueOf(details.getProfit())});
                csvGenerator.generateCSV(fileName + "_" + (i + 1) + ".csv", serviceTechnicians);
            } else {
                data.add(new String[]{"Incorrect solution"});
            }
        }
        csvGenerator.generateCSV(fileName + ".csv", data);
    }
}
