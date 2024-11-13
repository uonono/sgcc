package com.sgcc.sgcc_mgr_bx.config;

import com.sgcc.sgcc_mgr_bx.entity.UserInfo;
import com.sgcc.sgcc_mgr_bx.model.WorkerLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ReactiveRedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
                .<String, Object>newSerializationContext(new StringRedisSerializer())
                .hashKey(new StringRedisSerializer())
                .hashValue(new StringRedisSerializer())
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    /**
     * 配置 WorkerLocation 类型的 RedisTemplate
     */
    @Bean
    public ReactiveRedisTemplate<String, WorkerLocation> reactiveRedisTemplateForWorkerLocation(ReactiveRedisConnectionFactory factory) {
        // 使用 Jackson2JsonRedisSerializer 来序列化 WorkerLocation 类
        Jackson2JsonRedisSerializer<WorkerLocation> jacksonSerializer = new Jackson2JsonRedisSerializer<>(WorkerLocation.class);

        // 设置序列化和反序列化配置
        RedisSerializationContext<String, WorkerLocation> serializationContext = RedisSerializationContext
                .<String, WorkerLocation>newSerializationContext(new StringRedisSerializer())
                .value(jacksonSerializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(jacksonSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    /**
     * 配置 UserInfo 类型的 RedisTemplate
     */
    @Bean
    public ReactiveRedisTemplate<String, UserInfo> reactiveRedisTemplateForUserInfo(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserInfo> jacksonSerializer = new Jackson2JsonRedisSerializer<>(UserInfo.class);

        RedisSerializationContext<String, UserInfo> serializationContext = RedisSerializationContext
                .<String, UserInfo>newSerializationContext(new StringRedisSerializer())
                .value(jacksonSerializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(jacksonSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}
