package com.sgcc.sgcc_mgr_bx.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sgcc.sgcc_mgr_bx.entity.UserInfo;
import com.sgcc.sgcc_mgr_bx.model.WorkerLocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

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
     * 通用List插入
     */
    @Bean
    public ReactiveRedisTemplate<String, List> reactiveRedisTemplateForList(ReactiveRedisConnectionFactory factory) {
        // 配置 ObjectMapper，让 Jackson 正确处理泛型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 支持 Java 8 时间类型
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 创建 Jackson2JsonRedisSerializer，传入 ObjectMapper 和泛型类型信息
        Jackson2JsonRedisSerializer<List> jacksonSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, List.class);

        // 设置序列化和反序列化配置
        RedisSerializationContext<String, List> serializationContext = RedisSerializationContext
                .<String, List>newSerializationContext(new StringRedisSerializer())
                .value(jacksonSerializer)
                .hashKey(new StringRedisSerializer())
                .hashValue(jacksonSerializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }


    /**
     * 配置 WorkerLocation 类型的 RedisTemplate
     */
    @Bean
    public ReactiveRedisTemplate<String, List<WorkerLocation>> reactiveRedisTemplateForWorkerLocationList(ReactiveRedisConnectionFactory factory) {
        // 配置 ObjectMapper，让 Jackson 正确处理泛型
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 支持 Java 8 时间类型
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 创建 Jackson2JsonRedisSerializer，传入 ObjectMapper 和泛型类型信息
        Jackson2JsonRedisSerializer<List<WorkerLocation>> jacksonSerializer =
                new Jackson2JsonRedisSerializer<>(
                        (Class<List<WorkerLocation>>) (Class<?>) List.class
                );

        // 设置序列化和反序列化配置
        RedisSerializationContext<String, List<WorkerLocation>> serializationContext = RedisSerializationContext
                .<String, List<WorkerLocation>>newSerializationContext(new StringRedisSerializer())
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
