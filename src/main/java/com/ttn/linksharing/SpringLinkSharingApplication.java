package com.ttn.linksharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.Entity;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ttn.linksharing.repositories")
@EntityScan(basePackages = "com.ttn.linksharing.entity")
@EnableTransactionManagement
@EnableAsync
public class SpringLinkSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringLinkSharingApplication.class,args);
    }

}
