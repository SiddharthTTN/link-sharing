package com.ttn.linksharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.Entity;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ttn.linksharing.repositories")
@EntityScan(basePackages = "com.ttn.linksharing.entity")
@EnableTransactionManagement
public class SpringLinkSharingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringLinkSharingApplication.class,args);
    }

}
