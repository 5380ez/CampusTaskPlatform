package com.wuyanteam.campustaskplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.wuyanteam.campustaskplatform.mapper")
public class CampusTaskPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusTaskPlatformApplication.class, args);
    }

}
