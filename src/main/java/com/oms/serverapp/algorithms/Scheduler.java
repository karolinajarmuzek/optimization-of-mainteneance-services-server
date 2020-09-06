package com.oms.serverapp.algorithms;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    public enum algorithms { GREEDY, ANT}
    public static final String FIRST_SCHEDULE = "7:50:00";
    public static final String START_TIME = "8:00:00";
    public static final String TWO_H_INTERVAL = "0 50 7,9,11,13 ? * MON-FRI";
    public static final String FOUR_H_INTERVAL = "0 50 7,11 ? * MON-FRI";
    public static final int MAX_REPAIR_TIME = 480; //in minutes

    private static final Scheduler.algorithms algorithm = algorithms.GREEDY;
    private static final String interval = TWO_H_INTERVAL;

    @Scheduled(cron = interval)
    public void schedule() {
        int scheduleInterval = 0;
        if (interval == TWO_H_INTERVAL)
            scheduleInterval = 120;
        else if (interval == FOUR_H_INTERVAL)
            scheduleInterval = 240;

        if (algorithm == algorithms.GREEDY) {
            GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm(scheduleInterval, MAX_REPAIR_TIME, false, FIRST_SCHEDULE, START_TIME);
            greedyAlgorithm.exec();
        } else if (algorithm == algorithms.ANT) {
            AntColony antColony = new AntColony(scheduleInterval, MAX_REPAIR_TIME, false, FIRST_SCHEDULE, START_TIME);
            antColony.exec();
        }
    }
}
