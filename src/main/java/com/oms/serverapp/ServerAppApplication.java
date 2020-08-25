package com.oms.serverapp;

import com.oms.serverapp.algorithms.AntColony;
import com.oms.serverapp.algorithms.GreedyAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerAppApplication.class, args);
        //Generator.generateData();
        //ReportGenerator.generateReports(20);

        /*GreedyAlgorithm greedyAlgorithm = new GreedyAlgorithm(120, 480);
        greedyAlgorithm.exec("7:50:00");*/

        AntColony antColony = new AntColony(120,480);
        antColony.exec("7:50:00");
    }
}
