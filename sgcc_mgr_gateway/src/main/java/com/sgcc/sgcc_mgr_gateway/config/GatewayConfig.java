package com.sgcc.sgcc_mgr_gateway.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class GatewayConfig {

//    @Bean
/*    public HttpClient customHttpClient() throws SSLException {
        // 使用InsecureTrustManagerFactory禁用证书验证
        SslProvider sslProvider = SslProvider.builder()
                .sslContext(SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build())
                .build();

        return HttpClient.create().secure(sslProvider);
    }*/

    /**
     * 自定义信任库 暂未实验
     * @return 成功加载信任库的webClient
     * @throws Exception 还是推荐去搞公网ip帮域名 然后CA证书
     */
    @Bean
    public HttpClient customHttpClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        // 使用 ClassPathResource 来获取 classpath 中的资源
        Resource resource = new ClassPathResource("mykeystore.jks");
        try (InputStream keyStoreStream = resource.getInputStream()) {
            keyStore.load(keyStoreStream, "changeit".toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(tmf)
                .build();

        return HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
    }


//    @Bean
    /*public HttpClientCustomizer httpClientCustomizer(HttpClient customHttpClient) {
        return httpClient -> customHttpClient;
    }*/
}
