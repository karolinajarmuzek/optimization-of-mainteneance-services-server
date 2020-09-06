package com.oms.serverapp;

import com.oms.serverapp.algorithms.AntColony;
import com.oms.serverapp.algorithms.GreedyAlgorithm;
import com.oms.serverapp.algorithms.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerAppApplication {
    private static boolean testing = false;
    private static Scheduler.algorithms algorithm = Scheduler.algorithms.GREEDY;
    private static int scheduleInterval = 120;
    private static int maxRepairTime = 480;
    private static String firstSchedule = "7:50:00";
    private static String startTime = "8:00:00";

    public static void main(String[] args) {
        SpringApplication.run(ServerAppApplication.class, args);
        //Generator.generateData();
        //ReportGenerator.generateReports(20);

        System.out.println("STARTED!");
        if (testing) {
            if (algorithm == Scheduler.algorithms.GREEDY) {
                GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime);
                greedyAlgorithm.exec();
            } else if (algorithm == Scheduler.algorithms.ANT) {
                AntColony antColony = new AntColony(scheduleInterval, maxRepairTime, testing, firstSchedule, startTime);
                antColony.exec();
            }
        }
    }
}
