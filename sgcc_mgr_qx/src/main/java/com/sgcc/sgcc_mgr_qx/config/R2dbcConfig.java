package com.sgcc.sgcc_mgr_qx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions.StoreConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@EnableTransactionManagement
@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = Arrays.asList(
                new ByteToBooleanConverter(),
                new ByteBufferToBooleanConverter(),
                new ZonedDateTimeToTimestampConverter() // 添加 ZonedDateTime 到 Timestamp 的转换器
        );
        return new R2dbcCustomConversions(StoreConversions.NONE, converters);
    }

    // Byte 到 Boolean 的转换器
    @ReadingConverter
    public static class ByteToBooleanConverter implements Converter<Byte, Boolean> {
        @Override
        public Boolean convert(Byte source) {
            return source != null && source == 1;
        }
    }

    // ByteBuffer 到 Boolean 的转换器
    @ReadingConverter
    public static class ByteBufferToBooleanConverter implements Converter<ByteBuffer, Boolean> {
        @Override
        public Boolean convert(ByteBuffer source) {
            return source.get(0) == 1;
        }
    }

    // ZonedDateTime 到 Timestamp 的转换器
    @ReadingConverter
    public static class ZonedDateTimeToTimestampConverter implements Converter<ZonedDateTime, Timestamp> {
        @Override
        public Timestamp convert(ZonedDateTime source) {
            return source != null ? Timestamp.from(source.toInstant()) : null;
        }
    }

}
