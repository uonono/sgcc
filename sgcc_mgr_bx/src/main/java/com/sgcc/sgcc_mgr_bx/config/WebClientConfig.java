package com.sgcc.sgcc_mgr_bx.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient createWebClient() throws Exception {
        // 加载自定义信任库
        KeyStore keyStore = KeyStore.getInstance("JKS");
//        try (FileInputStream keyStoreStream = new FileInputStream("C:\\Users\\PC-00088\\IdeaProjects\\contest\\contest_mgr_gateway\\src\\main\\resources\\mykeystore.jks")) {
        try (InputStream keyStoreStream = new ClassPathResource("cacerts").getInputStream() ){
            keyStore.load(keyStoreStream, "changeit".toCharArray());
        }

        // 初始化TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // 使用Netty的SslContextBuilder创建SslContext
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(tmf)
                .build();

        // 配置HttpClient使用自定义的SslContext
        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

        // 使用配置好的HttpClient构建WebClient
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
