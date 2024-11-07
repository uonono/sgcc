package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.FaultOrder;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaultOrderRepository extends ReactiveCrudRepository<FaultOrder, Long> {

    // 自定义查询方法，根据openid和id查找FaultOrder
    @Query("SELECT * FROM fault_order WHERE openid = :openid AND id = :id")
    Mono<FaultOrder> findByOpenidAndId(String openid, Long id);

    /**
     * 查询指定 openid 的所有工单
     * @param openid 用户的微信 openid
     * @return 符合条件的 FaultOrder 列表
     */
    @Query("SELECT * FROM fault_order WHERE openid = :openid")
    Flux<FaultOrder> findAllByOpenid(String openid);

    /**
     * 根据 openid 和 id 删除对应的工单
     *
     * @param openid 用户的 openid
     * @param id 工单 ID
     * @return 删除结果
     */
    @Query("DELETE FROM fault_order WHERE openid = :openid AND id = :id")
    Mono<Void> deleteByOpenidAndId(String openid, Long id);
}