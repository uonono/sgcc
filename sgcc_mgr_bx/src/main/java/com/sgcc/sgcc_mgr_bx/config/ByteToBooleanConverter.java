package com.sgcc.sgcc_mgr_bx.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@ReadingConverter
public class ByteToBooleanConverter implements Converter<ByteBuffer, Boolean> {

    @Override
    public Boolean convert(ByteBuffer source) {
        // 假设 1 表示 true, 0 表示 false
        return source.get(0) == 1;
    }
}
