package com.oms.serverapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerAppApplication.class, args);
        //Generator.generateData();
        //ReportGenerator.generateReports(16 * 5);
        //RepairOptimization.scheduler();
        Optimization.schedule();
        //RepairOptimization.api();
        /*StreetsCreator streetsCreator = new StreetsCreator();
        streetsCreator.generateAddress();*/
    }
}
