package com.sgcc.sgcc_mgr_qx.config;

import com.sgcc.sgcc_mgr_qx.config.Http3AltSvcFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebFilterConfig {

    @Bean
    public WebFilter http3AltSvcFilter() {
        return new Http3AltSvcFilter();
    }
}
