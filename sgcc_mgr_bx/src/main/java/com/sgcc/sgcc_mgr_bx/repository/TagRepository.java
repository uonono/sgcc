package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.Tag;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TagRepository extends ReactiveCrudRepository<Tag, Long>,BaseRepository<Tag,Long> {

    // 根据 openid 查询所有符合条件的标签
    Flux<Tag> findByOpenid(String openid);

    // 自定义删除方法，根据 id 和 openid 删除标签
    @Query("DELETE FROM tag_table WHERE id = :id AND openid = :openid")
    Mono<Void> deleteByIdAndOpenid(Long id, String openid);

    // 根据 id 和 openid 查询标签
    @Query("SELECT * FROM tag_table WHERE id = :id AND openid = :openid")
    Mono<Tag> findByIdAndOpenid(Long id, String openid);
}
