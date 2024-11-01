/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sgcc.sgcc_mgr_auth;

import com.contest.auth.config.BasicAuthBlockFilter;
import com.contest.auth.config.CustomGrantedAuthoritiesConverter;
import com.contest.auth.config.CustomReactiveGrantedAuthoritiesConverter;
import com.contest.auth.config.CustomReactiveUserDetailsService;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Security configuration for the main application.
 *
 * @author Josh Cummings
 */
@Configuration
public class RestConfig {

	@Value("${jwt.public.key}")
	RSAPublicKey key;

	@Value("${jwt.private.key}")
	RSAPrivateKey priv;

	/**
	 * 自定义信任库 暂未实验
	 * @return 成功加载信任库的webClient
	 * @throws Exception 还是推荐去搞公网ip帮域名 然后CA证书
	 */
	@Bean
	public HttpClient customHttpClient() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");

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

	/*@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http
				.authorizeHttpRequests((authorize) -> authorize
						.anyRequest().authenticated()
				)
				.csrf((csrf) -> csrf.ignoringRequestMatchers("/token"))
				.httpBasic(Customizer.withDefaults())
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling((exceptions) -> exceptions
						.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler())
				);
		// @formatter:on
		return http.build();
	}*/

	// 注入 CustomAuthenticationManager

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
		// @formatter:off
		http
				.authorizeExchange(exchanges -> exchanges
						.anyExchange().authenticated()
				)
				.csrf(ServerHttpSecurity.CsrfSpec::disable)  // 禁用 CSRF 保护
				.httpBasic(Customizer.withDefaults())
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(customReactiveJwtAuthenticationConverter())) // 使用自定义的转换器
				).addFilterBefore(new BasicAuthBlockFilter(), SecurityWebFiltersOrder.AUTHENTICATION) // 添加自定义的过滤器，阻止 Basic 认证
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint((exchange, e) -> Mono.fromRunnable(() -> {
							exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // 自定义认证入口点
						}))
						.accessDeniedHandler((exchange, e) -> Mono.fromRunnable(() -> {
							exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN); // 自定义访问拒绝处理器
						}))
				);
		// @formatter:on
		return http.build();
	}


	@Bean
	public ReactiveJwtAuthenticationConverter customReactiveJwtAuthenticationConverter() {
		ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new CustomReactiveGrantedAuthoritiesConverter());
		return converter;
	}

	@Bean
	public ReactiveUserDetailsService reactiveUserDetailsService() {
		// 在这里可以配置从数据库或其他地方获取用户信息
		return username -> {
			// 可以根据用户名查询用户并返回
			return Mono.just(User.withUsername("user")
					.password("{noop}password") // 这里直接使用明文密码，实际中需要使用加密
					.roles("USER")
					.build());
		};
	}
/*	@Bean
	UserDetailsService users() {
		// @formatter:off
		return new InMemoryUserDetailsManager(
			User.withUsername("user")
				.password("{noop}password")
				.authorities("app")
				.build()
		);
		// @formatter:on
	}*/

	/*@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(this.key).build();
	}*/

	@Bean
	public ReactiveJwtDecoder jwtDecoder() {
		return NimbusReactiveJwtDecoder.withPublicKey(this.key).build();
	}

	@Bean
	JwtEncoder jwtEncoder() {
		JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwks);
	}

	/**
	* @Author: cy
	* @Date: 2024/8/2 上午10:41
	* @Description: 配置JwtAuthenticationConverter使用自定义的GrantedAuthoritiesConverter
	*/
	/*@Bean
	public JwtAuthenticationConverter customJwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		//原来那个自定义就是JwtAuthenticationConverter里面的方法 用Converter<Jwt, AbstractAuthenticationToken>反而没有，这样的话，不知道它到底在final个什么劲
		converter.setJwtGrantedAuthoritiesConverter(new CustomGrantedAuthoritiesConverter());
		return converter;
	}*/

	@Autowired
	private CustomReactiveUserDetailsService customReactiveUserDetailsService;

	// 配置使用 ReactiveUserDetailsService 的 AuthenticationManager
	@Bean
	public ReactiveAuthenticationManager authenticationManager() {
		// 使用 UserDetailsRepositoryReactiveAuthenticationManager 作为认证管理器
		UserDetailsRepositoryReactiveAuthenticationManager authManager =
				new UserDetailsRepositoryReactiveAuthenticationManager(customReactiveUserDetailsService);

		// 你可以配置密码加密器（如果需要）
		// authManager.setPasswordEncoder(passwordEncoder());

		return authManager;
	}

	@Bean
	public ReactiveJwtAuthenticationConverterAdapter customJwtAuthenticationConverter() {
		// 创建 JwtAuthenticationConverter 对象
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

		// 使用自定义的 JwtGrantedAuthoritiesConverter
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomGrantedAuthoritiesConverter());

		// 将 JwtAuthenticationConverter 包装为响应式的 ReactiveJwtAuthenticationConverterAdapter
		return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
	}
}
