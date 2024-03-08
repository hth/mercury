package com.github.hth.dataconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class DataConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataConsumerApplication.class, args);
    }

}
