package com.sgcc.sgcc_mgr_eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class SgccMgrEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgccMgrEurekaApplication.class, args);
    }

}
