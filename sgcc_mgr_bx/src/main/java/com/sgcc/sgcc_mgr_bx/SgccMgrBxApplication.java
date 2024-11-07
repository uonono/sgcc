package com.sgcc.sgcc_mgr_bx;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
@EnableR2dbcRepositories
@SpringBootApplication
public class SgccMgrBxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgccMgrBxApplication.class, args);
    }

}
