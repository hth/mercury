/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.datacsv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataCsvApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataCsvApplication.class, args);
    }

}
