package com.oms.serverapp.algorithms;

import com.oms.serverapp.model.*;
import com.oms.serverapp.util.RepairInfos;
import com.oms.serverapp.util.ReportStatus;

import java.io.*;
import java.util.*;

public class AntColony extends Algorithm {

    private double c = 1.0;             // initial value on each trail
    private double alpha = 1;           // pheromone importance
    private double beta = 5;            // distance priority
    private double evaporation = 0.5;   // pheromone evaporating
    private double antFactor = 0.9;     // number of ants use per city?
    private double randomFactor = 0.1;  // randomness
    private int maxIterations = 3;

    private int numberOfAnts;                                                   // number of ants
    private double[][][] trails;                                                // ant trails for each serviceTechnician
    private double[] probabilities;                                             // probability to choose each report (calculated dynamically)
    private List<Ant> ants = new ArrayList<>();                                 // list of ants
    private Map<Integer, List<Integer>> reportsAvailable = new HashMap<>();     // list of reports idx that each serviceTechnician can fix (has skill)

    private Ant bestSolution;                                                   // best found solution
    private Map<Long, Integer> sparePartsForBestSolution;                       // id and quantity of spare parts after best assignment
    private int[][] currentIndex;                                               // array of index of last scheduled report for each serviceTechnician
    private Map<Integer, Map<Long, Integer>> sparePartsForEachAntInColony;      // id and quantity of spare parts for each ant

    public AntColony(int scheduleInterval, int maxRepairTime) {
        super(scheduleInterval, maxRepairTime);
    }

    @Override
    void schedule() {
        numberOfAnts = (int) (getNumberOfReports() * antFactor);
        trails = new double[getNumberOfServiceTechnicians()][getNumberOfReports() + 1][getNumberOfReports() + 1];
        probabilities = new double[getNumberOfReports()];
        currentIndex = new int[numberOfAnts][getNumberOfServiceTechnicians()];
        sparePartsForEachAntInColony = new HashMap<>();
        sparePartsForBestSolution = new HashMap<>();

        ants = new ArrayList<>();
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant(getNumberOfServiceTechnicians(), getNumberOfReports()));
        }

        bestSolution = null;

        if (isShowMessages()) {
            System.out.println("number of reports " + getNumberOfReports());
            System.out.println("number of ants " + getNumberOfAnts());
            System.out.println("number of serviceTechnicians " + getNumberOfServiceTechnicians());
        }
        startAntOptimization();
    }

    private void startAntOptimization() {
        for (int i = 0; i < 3; i++) {
            if (isShowMessages()) System.out.println("Attempt #" + i);
            solve();
        }

        // check if solution exists
        if (bestSolution != null) {
            System.out.println("Best found solution profit " + bestSolution.profit);
            for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
                System.out.println("Service technician " + serviceTechnician + " total time " + bestSolution.repairTimes[serviceTechnician]);
                for (int report = 1; report < getNumberOfReports(); report++) {
                    if (bestSolution.trail[serviceTechnician][report] != -1) {
                        Report report1 = getReportsWithId().get(bestSolution.trail[serviceTechnician][report] - getNumberOfServiceTechnicians());
                        // update report status and create repair
                        report1.setStatus(ReportStatus.ASSIGNED);
                        updateReport(report1);

                        // TO DO
                        // if (!isTesting()) -> create repair
                        
                    } else {
                        break;
                    }
                }
            }
            setTotalProfit(getTotalProfit() + bestSolution.profit);
            updateServiceTechnicianRepairInfos(bestSolution);
            updateSparePartCountMap();
        } else {
            System.out.println("No correct solution found");
        }
    }

    private void solve() {
        setUpAnts();
        clearTrails();
        prepareSpareParts();
        for (int i = 0; i < maxIterations; i++) {
            if (isShowMessages()) System.out.println("Iteration #" + i);
            moveAnts(i);
            updateTrails();
            updateBest();
        }
        clearAnts();
    }

    private void setUpAnts() {
        for (Ant ant : ants) {
            ant.clear();
            reportsAvailable.clear();
            for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
                // set start position index (first schedule - start location; otherwise last report position)
                ant.visitRepair(serviceTechnician, -1, serviceTechnician, 0, 0);

                // reports available for each serviceTechnician
                List<Integer> reports = new ArrayList<>();
                for (Map.Entry<Integer, Report> entry : getReportsWithId().entrySet()) {
                    if (getServiceTechnicians().get(serviceTechnician).getOwnedSkills().stream().filter(skill -> skill.getId().equals(getReportsLoader().getReportSkillMap().get(entry.getValue()).getId())).findFirst().isPresent()) {
                        reports.add(entry.getKey());
                    }
                }
                reportsAvailable.put(serviceTechnician, reports);
            }
            int[] currentIdx = new int[getNumberOfServiceTechnicians()];
            Arrays.fill(currentIdx, 0);
            currentIndex[ants.indexOf(ant)] = currentIdx;
        }
    }

    private void moveAnts(int i) {
        //int intervalMinTime = 120; // change to getScheduleInterval
        for (Ant ant : ants) {
            int antId = ants.indexOf(ant);
            for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
                Long serviceTechnicianId = getServiceTechnicians().get(serviceTechnician).getId();
                int repairTimePreviously = getServiceTechniciansRepairInfos().get(serviceTechnicianId).getRepairsTime(); // in previous schedules
                int totalRepairsTime = ant.getRepairTimes()[serviceTechnician] + repairTimePreviously;  // in previous schedule and in actual schedule
                if (totalRepairsTime < (getScheduleInterval() * (getInterval() + 1))) { // check if serviceTechnician can handle more repairs
                    try {
                        boolean areSparePartsAvailable = true;
                        int report = selectNextReport(ant, serviceTechnician);

                        Skill skillNeeded = getReportsLoader().getReportSkillMap().get(getReportsWithId().get(report - getNumberOfServiceTechnicians()));
                        Set<SparePartNeeded> sparePartsNeeded = new HashSet<>();
                        if (!skillNeeded.getSparePartsNeeded().isEmpty()){
                            sparePartsNeeded = skillNeeded.getSparePartsNeeded();
                            for (SparePartNeeded sparePartNeeded : sparePartsNeeded) {
                                int sparePartCount = sparePartsForEachAntInColony.get(antId).get(sparePartNeeded.getSparePart().getId());
                                if (sparePartCount == -1 || sparePartCount < sparePartNeeded.getQuantity()) {
                                    areSparePartsAvailable = false;
                                    break;
                                }
                            }
                        }
                        if (areSparePartsAvailable) {
                            RepairInfos repairInfos = getRepairInfos(getReportsWithId().get(report - getNumberOfServiceTechnicians()), serviceTechnician, false);
                            int travelTime = (int) getDurationsInS()[ant.trail[serviceTechnician][currentIndex[antId][serviceTechnician]]][report] / 60;
                            int maxTime;
                            if ((getScheduleInterval() * (getInterval() + 1) * 1.3) < 480) {
                                maxTime = (int) (getScheduleInterval() * (getInterval() + 1) * 1.3);
                            } else {
                                maxTime = getMaxRepairTime() + getShiftTime();
                            }

                            if (((totalRepairsTime + repairInfos.getRepairTime() + travelTime) <= maxTime) || ((currentIndex[antId][serviceTechnician] == 0) && ((totalRepairsTime + repairInfos.getRepairTime() + travelTime) <= (getMaxRepairTime() + getShiftTime())))) { //check if fixing report will not exceed max time for current schedule
                                ant.visitRepair(serviceTechnician, currentIndex[antId][serviceTechnician], report, repairInfos.getRepairTime() + travelTime, repairInfos.getProfit());
                                currentIndex[antId][serviceTechnician] += 1;
                                Map<Long, Integer> spareParts = new HashMap<>(sparePartsForEachAntInColony.get(antId));
                                for (SparePartNeeded sparePartNeeded : sparePartsNeeded) {
                                    SparePart sparePart = sparePartNeeded.getSparePart();
                                    spareParts.put(sparePart.getId(), sparePartsForEachAntInColony.get(antId).get(sparePart.getId()) - sparePartNeeded.getQuantity());
                                }
                                sparePartsForEachAntInColony.put(antId, spareParts);
                            }
                        }
                    } catch (RuntimeException ex) {
                        System.out.println("err " + ex.getMessage());
                    }
                }
            }
        }
    }


    private int selectNextReport(Ant ant, int serviceTechnician) {
        List<Integer> availableReports = reportsAvailable.get(serviceTechnician);

        Random random = new Random();
        int randomReport = random.nextInt(availableReports.size());
        if (random.nextDouble() < randomFactor) {
            int randomIndex = reportsAvailable.get(serviceTechnician).get(randomReport) + getNumberOfServiceTechnicians();
            if (!ant.isVisited(randomIndex)) {
                if (isShowMessages()) System.out.println("report chosen randomly " + randomIndex);
                return randomIndex;
            }
        }

        calculateProbabilities(ant, serviceTechnician);

        double r = random.nextDouble();
        double total = 0;
        for (int i = 0; i < reportsAvailable.get(serviceTechnician).size(); i++) {
            int index = reportsAvailable.get(serviceTechnician).get(i);
            total += probabilities[index];
            int ind = index + getNumberOfServiceTechnicians();
            if (total >= r && !ant.isVisited(ind)) {
                if (isShowMessages()) System.out.println("report chosen based on probability " + ind);
                return ind;
            }
        }
        throw new RuntimeException("No cities available");
    }

    private void calculateProbabilities(Ant ant, int serviceTechnician) {
        int antId = ants.indexOf(ant);
        int globalIndex = ant.trail[serviceTechnician][currentIndex[antId][serviceTechnician]];
        int antIndex = 0;

        if (globalIndex > getNumberOfServiceTechnicians())
            antIndex = globalIndex - getNumberOfServiceTechnicians() + 1;

        List<Integer> availableReports = reportsAvailable.get(serviceTechnician);
        double pheromone = 0.0;

        for (int i = 0; i < availableReports.size(); i++) {
            int idx = reportsAvailable.get(serviceTechnician).get(i);
            if (!ant.isVisited(idx + getNumberOfServiceTechnicians()) && globalIndex != (idx + getNumberOfServiceTechnicians())) {
                RepairInfos repairInfos = getRepairInfos(getReportsWithId().get(idx), serviceTechnician, false);
                pheromone += Math.pow(trails[serviceTechnician][antIndex][idx + 1], alpha) * Math.pow(repairInfos.getProfit() / (repairInfos.getRepairTime() + (1.0 * 60.0 / getDurationsInS()[globalIndex][idx + getNumberOfServiceTechnicians()])), beta);
            }
        }

        for (int j = 0; j < getNumberOfReports(); j++) {
            probabilities[j] = 0.0;
        }
        for (int j = 0; j < availableReports.size(); j++) {
            int idx = reportsAvailable.get(serviceTechnician).get(j);
            if (!ant.isVisited(idx + getNumberOfServiceTechnicians()) && (globalIndex != (idx + getNumberOfServiceTechnicians()))) {
                RepairInfos repairInfos = getRepairInfos(getReportsWithId().get(idx), serviceTechnician, false);
                double numerator = Math.pow(trails[serviceTechnician][antIndex][idx + 1], alpha) * Math.pow(repairInfos.getProfit() / (repairInfos.getRepairTime() + (1.0 * 60.0 / getDurationsInS()[globalIndex][idx + getNumberOfServiceTechnicians()])), beta);
                probabilities[idx] = numerator / pheromone;
            }
        }
    }

    //evaporation
    private void updateTrails() {
        for (int i = 0; i < getNumberOfReports() + 1; i++) {
            for (int j = 0; j < getNumberOfReports() + 1; j++) {
                for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
                    trails[serviceTechnician][i][j] *= evaporation;
                }
            }
        }
        for (Ant ant : ants) {
            double contribution = ant.profit / (getScheduleInterval() * (getInterval() + 1));
            for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
                if (ant.trail[serviceTechnician][1] != -1) {
                    trails[serviceTechnician][ant.trail[serviceTechnician][0]][ant.trail[serviceTechnician][1] - getNumberOfServiceTechnicians() + 1] += contribution;
                    for (int i = 2; i < getNumberOfReports(); i++) {
                        if (ant.trail[serviceTechnician][i] != -1) {
                            trails[serviceTechnician][ant.trail[serviceTechnician][i - 1] - getNumberOfServiceTechnicians() + 1][ant.trail[serviceTechnician][i] - getNumberOfServiceTechnicians()] += contribution;
                        }
                    }
                }
            }
        }
    }

    private void updateBest() {

        for (Ant ant : ants) {
            boolean isCorrect = true;
            //for (int time : ant.repairTimes) {
            for (int serviceTechnician = 0; serviceTechnician < ant.repairTimes.length; serviceTechnician++){
                Long serviceTechnicianId = getServiceTechnicians().get(serviceTechnician).getId();
                int totalTime = ant.repairTimes[serviceTechnician] + getServiceTechniciansRepairInfos().get(serviceTechnicianId).getRepairsTime();
                int maxTime = getScheduleInterval() * (getInterval() + 1);
                if (maxTime == getMaxRepairTime()) maxTime = getMaxRepairTime() - getScheduleInterval();
                if (totalTime < maxTime) {
                    isCorrect = false;
                    break;
                }
            }

            if (isCorrect && (bestSolution == null || ant.profit > bestSolution.profit)) {
                //potrzeba id serwisanta
                for (Map.Entry<Long, Integer> entry : sparePartsForEachAntInColony.get(ants.indexOf(ant)).entrySet()) {
                    sparePartsForBestSolution.put(entry.getKey(), entry.getValue());
                }
                if (isShowMessages()) System.out.println("New best solution with profit " + ant.profit);
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
                    outputStrm.writeObject(ant);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                    ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
                    bestSolution = (Ant) objInputStream.readObject();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                }
            }
        }
    }

    private void clearTrails() {
        for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
            for (int i = 0; i < getNumberOfReports() + 1; i++) {
                for (int j = 0; j < getNumberOfReports() + 1; j++) {
                    trails[serviceTechnician][i][j] = c;
                }
            }
        }
    }

    private void updateServiceTechnicianRepairInfos(Ant bestAnt) {
        for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
            Long serviceTechnicianId = getServiceTechnicians().get(serviceTechnician).getId();
            int lastReportIndex = 0;
            for (int i = 1; i < getNumberOfReports(); i++) {
                if (bestAnt.trail[serviceTechnician][i] == -1) break;
                lastReportIndex = bestAnt.trail[serviceTechnician][i];
                getServiceTechniciansRepairInfos().get(serviceTechnicianId).getAssignedReports().add(getReportsWithId().get(bestAnt.trail[serviceTechnician][i] - getNumberOfServiceTechnicians()));

            }
            Report lastReport = getReportsWithId().get(lastReportIndex - getNumberOfServiceTechnicians());
            int totalTime = getServiceTechniciansRepairInfos().get(serviceTechnicianId).getRepairsTime() + bestAnt.getRepairTimes()[serviceTechnician];
            getServiceTechniciansRepairInfos().get(serviceTechnicianId).setRepairsTime(totalTime);
            if (lastReportIndex != 0) getServiceTechniciansRepairInfos().get(serviceTechnicianId).setLastReport(lastReport);
         }
    }

    private void clearAnts() {
        for (Ant ant : ants) {
            Arrays.fill(ant.visited, false);
            for (int serviceTechnician = 0; serviceTechnician < getNumberOfServiceTechnicians(); serviceTechnician++) {
                Arrays.fill(ant.trail[serviceTechnician], -1);
                Arrays.fill(ant.repairTimes, 0);
                ant.visitRepair(serviceTechnician, -1, serviceTechnician, 0, 0);
                ant.profit = 0;
                currentIndex[ants.indexOf(ant)][serviceTechnician] = 0;
            }
        }
    }

    private void prepareSpareParts() {
        Map<Long, Integer> spareParts = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : getSparePartCountMap().entrySet()) {
            spareParts.put(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < getNumberOfAnts(); i++) {
            sparePartsForEachAntInColony.put(i, spareParts);
        }
    }

    private void updateSparePartCountMap() {
        for (Map.Entry<Long, Integer> entry : sparePartsForBestSolution.entrySet()) {
            getSparePartCountMap().put(entry.getKey(), entry.getValue());
        }
    }

    public int getNumberOfAnts() {
        return numberOfAnts;
    }

}
