package com.sgcc.sgcc_mgr_bx.repository;

import com.github.yitter.idgen.YitIdHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;

public class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {

    private final ReactiveCrudRepository<T, ID> repository;

    public BaseRepositoryImpl(@Qualifier("tagRepository") @Lazy ReactiveCrudRepository<T, ID> repository) {
        this.repository = repository;
    }
    @Override
    public Mono<T> saveWithId(T entity) {
        // 使用反射检查和设置 ID 字段
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);

            // 如果 id 字段为空，则生成新的 ID
            if (idField.get(entity) == null) {
                long newId = YitIdHelper.nextId();
                idField.set(entity, newId);  // 设置 ID
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return Mono.error(new RuntimeException("Failed to set ID for entity", e));
        }

        return repository.save(entity);
    }
}
