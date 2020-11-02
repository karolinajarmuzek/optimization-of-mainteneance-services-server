package com.oms.serverapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerAppApplication.class, args);

    }
}

// generate data
    /*
    Generator.generateData();
    ReportGenerator.generateReports(40);
    */

// parameters for testing
    /*
    private static boolean testing = true;
    private static Scheduler.algorithms algorithm = Scheduler.algorithms.ANT;
    private static int scheduleInterval = 120;
    private static int maxRepairTime = 480;
    private static String firstSchedule = "7:50:00";
    private static String startTime = "8:00:00";
     */

// run in testing mode
    /*
    if (testing) {
        Algorithm algo = null;
        if (algorithm == Scheduler.algorithms.GREEDY) {
            algo = new GreedyAlgorithm(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime, null);
        } else if (algorithm == Scheduler.algorithms.ANT) {
            algo = new AntColony(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime, null);
        }
        algo.exec();
    }
    */

    /*int[] intervals = new int[]{120};
    for (Scheduler.algorithms algo : Scheduler.algorithms.values()) {
        int numberOfTests = algo == Scheduler.algorithms.GREEDY ? 1 : 5;
        TestsGenerator testsGenerator = new TestsGenerator(numberOfTests);
        for (int interval : intervals) {
            testsGenerator.doTests(algo, interval, maxRepairTime, testing, firstSchedule, startTime);
        }
    }*/

// do tests - ANT COLONY ALGORITHM
    /*
    int[] intervals = new int[]{120, 240};
    double[] alphas = new double[]{1.0};
    double[] betas = new double[]{5.0};
    double[] evaporations = new double[]{0.5};
    double[] antFactors = new double[]{ 2.0};
    double[] randomFactor = new double[]{0.1, 0.2};
    int[] iterations = new int[]{2000};
    TestsGenerator testsGenerator = new TestsGenerator(5);
    for (int interval : intervals) {
        for (double alpha: alphas) {
            for (double beta : betas) {
                for (double evaporation : evaporations) {
                    for (double antFactor : antFactors) {
                        for (double factor : randomFactor) {
                            for (int iteration : iterations) {
                                System.out.println("a " + alpha + " b " + beta + " e " + evaporation + " af " + antFactor + " rf " + factor + " it " + iteration);
                                testsGenerator.setParams(alpha, beta, evaporation, antFactor, factor, iteration);
                                testsGenerator.doTests(Scheduler.algorithms.ANT, interval, maxRepairTime, testing, firstSchedule, startTime);
                            }
                        }
                    }
                }
            }
        }
    }
    */

// do tests - GREEDY ALGORITHM
    /*
    int[] intervals = new int[]{120, 240};
    for (int interval : intervals) {
        testsGenerator.doTests(Scheduler.algorithms.GREEDY, interval, maxRepairTime, testing, firstSchedule, startTime);
    }*/