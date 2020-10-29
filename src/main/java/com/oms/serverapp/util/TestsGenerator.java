package com.oms.serverapp.util;

import com.oms.serverapp.OptimizationServices;
import com.oms.serverapp.algorithms.*;
import com.oms.serverapp.model.Report;
import com.oms.serverapp.model.ServiceTechnician;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TestsGenerator {
    private final String CSV_FILE_PATH = "src/main/java/com/oms/serverapp/tests/";
    private final String MATRIX_FILE = "src/main/java/com/oms/serverapp/tests/matrix.txt";

    private int numberOfTests;
    private double[][] durations;

    private double alpha;
    private double beta;
    private double evaporation;
    private double antFactor;
    private double randomFactor;
    private int iterations;
    private boolean areParamsToSet = false;

    public TestsGenerator(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }

    public void setParams(double alpha, double beta, double evaporation, double antFactor, double randomFactor, int iterations) {
        this.alpha = alpha;
        this.beta = beta;
        this.evaporation = evaporation;
        this.antFactor = antFactor;
        this.iterations = iterations;
        this.randomFactor = randomFactor;
        this.areParamsToSet = true;
    }

    public void doTests(Scheduler.algorithms algorithmName, int scheduleInterval, int maxRepairTime, boolean testing, String firstSchedule, String startTime ) {
        //prepareAllDurationsForTesting();
        readMatrix();
        CSVGenerator csvGenerator = new CSVGenerator();
        int numberOfServiceTechnicians = OptimizationServices.loadServiceTechnicians().size() - 1;
        int numberOfReports = OptimizationServices.loadReports().size();
        String fileName = " ";
        List<String[]> data = new ArrayList<>();

        for (int i = 0; i < numberOfTests; i++) {
            List<String[]> serviceTechnicians = new ArrayList<>();
            SolutionDetails details = null;

            if (algorithmName == Scheduler.algorithms.GREEDY) {
                GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime, durations);
                details = greedyAlgorithm.tests();
            } else if (algorithmName == Scheduler.algorithms.ANT) {
                AntColony antColony = new AntColony(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime, durations);
                if (areParamsToSet)
                    antColony.setParams(alpha, beta, evaporation, antFactor, randomFactor, iterations);
                details = antColony.tests();
            }

            if (details != null) {
                for (Map.Entry<Long, ServiceTechnicianRepairInfos> entry : details.getServiceTechnicianRepairInfosMap().entrySet()) {
                    List<Long> reportsId = details.getServiceTechnicianRepairInfosMap().get(entry.getKey()).getAssignedReports().stream().map(report -> report.getId()).collect(Collectors.toList());
                    serviceTechnicians.add(new String[]{String.valueOf(entry.getKey()), String.valueOf(details.getServiceTechnicianRepairInfosMap().get(entry.getKey()).getRepairsTime()), String.valueOf(reportsId).replaceAll(", ", "-")});
                }
                data.add(new String[]{String.valueOf(i + 1), String.valueOf(details.getProfit())});

                fileName = CSV_FILE_PATH + algorithmName + "_I" + scheduleInterval + "_S" + numberOfServiceTechnicians + "_R" + numberOfReports;
                if (algorithmName == Scheduler.algorithms.ANT) fileName += "_af" + AntColony.getAntFactor() + "_it" + AntColony.getMaxIterations() + "_rf" + AntColony.getRandomFactor() + "_alpha" + AntColony.getAlpha() + "_beta" + AntColony.getBeta() + "_e" + AntColony.getEvaporation();

                System.out.println("filename " + fileName);
                csvGenerator.generateCSV(fileName + "_" + (i + 1) + ".csv", serviceTechnicians);
            } else {
                data.add(new String[]{"Incorrect solution"});
            }
        }
        csvGenerator.generateCSV(fileName + ".csv", data);
    }


    private void prepareAllDurationsForTesting() {
        List<Report> reports = OptimizationServices.loadReports();
        List<ServiceTechnician> serviceTechnicians = OptimizationServices.loadServiceTechnicians();
        ServiceTechnician admin = serviceTechnicians.stream().filter(serviceTechnician -> serviceTechnician.getFirstName().equals("Admin")).findFirst().orElse(null);
        serviceTechnicians.remove(admin);
        double[][] locations = new double[reports.size() + serviceTechnicians.size()][2];
        for (int i = 0; i < serviceTechnicians.size(); i++) {
            locations[i][0] = Double.parseDouble(serviceTechnicians.get(i).getLongitude());
            locations[i][1] = Double.parseDouble(serviceTechnicians.get(i).getLatitude());
        }
        for (int i = 0; i < reports.size(); i++) {
            locations[i + serviceTechnicians.size()][0] = Double.parseDouble(reports.get(i).getLongitude());
            locations[i + serviceTechnicians.size()][1] = Double.parseDouble(reports.get(i).getLatitude());
        }
        DurationsMatrix durationsMatrix = new DurationsMatrix(serviceTechnicians.size() + reports.size(), locations);
        durations = durationsMatrix.getDurations();
        writeMatrix();
    }

    private void writeMatrix() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(MATRIX_FILE));

            for (int i = 0; i < durations.length; i++) {
                for (int j = 0; j < durations[i].length; j++) {
                    bw.write(durations[i][j] + ((j == durations[i].length-1) ? "" : ","));
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {}
    }

    private void readMatrix() {
        try {
            Scanner sc = new Scanner(new BufferedReader(new FileReader(MATRIX_FILE)));
            int reportsNumber = OptimizationServices.loadReports().size();
            int serviceTechniciansNumber = OptimizationServices.loadServiceTechnicians().size() - 1;
            double [][] myArray = new double[reportsNumber + serviceTechniciansNumber][reportsNumber + serviceTechniciansNumber];
            while(sc.hasNextLine()) {
                for (int i=0; i<myArray.length; i++) {
                    String[] line = sc.nextLine().trim().split(",");
                    for (int j=0; j<line.length; j++) {
                        myArray[i][j] = Double.parseDouble(line[j]);
                    }
                }
            }
            durations = myArray;
            System.out.println("ra " + reportsNumber);
            System.out.println("st " + serviceTechniciansNumber);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
