package com.sgcc.sgcc_mgr_bx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions.StoreConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

@Configuration
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        StoreConversions storeConversions = R2dbcCustomConversions.STORE_CONVERSIONS;
        List<Converter<?, ?>> converters = Arrays.asList(new ByteToBooleanConverter());
        return new R2dbcCustomConversions(storeConversions, converters);
    }

    @ReadingConverter
    public static class ByteToBooleanConverter implements Converter<ByteBuffer, Boolean> {
        @Override
        public Boolean convert(ByteBuffer source) {
            // 自定义转换逻辑，这里假设 1 是 true，0 是 false
            return source.get(0) == 1;
        }
    }
}
