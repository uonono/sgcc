package com.sgcc.sgcc_mgr_qx.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import reactor.netty.http.server.HttpServer;
/**
* @Author: cy
* @Date: 2024/10/10 09:25
* @Description: 这个是http的配置，当启用了这个配置时，https会失效
*/
//@Configuration
public class ServerConfig {

//    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> nettyServerCustomizer() {
        return factory -> factory.addServerCustomizers(httpServer -> 
            HttpServer.create()
                      .port(8768) // HTTP 端口
        );
    }
}
