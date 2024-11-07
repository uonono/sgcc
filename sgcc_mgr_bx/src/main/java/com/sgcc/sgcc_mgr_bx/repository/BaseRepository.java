package com.sgcc.sgcc_mgr_bx.repository;

import reactor.core.publisher.Mono;

public interface BaseRepository<T, ID> {
    Mono<T> saveWithId(T entity);
}
