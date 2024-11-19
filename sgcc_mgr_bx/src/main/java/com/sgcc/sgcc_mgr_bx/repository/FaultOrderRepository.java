package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.FaultOrder;
import com.sgcc.sgcc_mgr_bx.model.FaultOrderResponse;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaultOrderRepository extends ReactiveCrudRepository<FaultOrder, Long> {

    // 自定义查询方法，根据 openid 和 id 查询 FaultOrderWithNickname
    @Query("""
        SELECT fo.*, CASE
        WHEN ev.created_at IS NOT NULL AND rr.deal_with_time IS NOT NULL THEN 7
        WHEN rr.deal_with_time IS NOT NULL THEN 6
        WHEN rr.survey_time IS NOT NULL THEN 5
        WHEN rr.arrive_time IS NOT NULL THEN 4
        WHEN rr.receive_time IS NOT NULL THEN 3
        ELSE 0
        END AS proc_code,qui.nickname AS worker_name,qui.phone AS worker_phone FROM fault_order fo
        LEFT JOIN repair_record rr ON fo.id = rr.fault_order_id
        LEFT JOIN evaluation ev ON fo.id = ev.fault_order_id
        LEFT JOIN qx_user_info qui ON qui.phone = rr.repair_user_phone WHERE fo.openid = :openid AND fo.id = :id
    """)
    Mono<FaultOrderResponse> findByOpenidAndId(String openid, Long id);

    /**
     * 查询指定 openid 的所有工单
     * @param openid 用户的微信 openid
     * @return 符合条件的 FaultOrder 列表
     */
    @Query("""
        SELECT fo.* ,CASE
        WHEN ev.created_at IS NOT NULL AND rr.deal_with_time IS NOT NULL THEN 7
        WHEN rr.deal_with_time IS NOT NULL THEN 6
        WHEN rr.survey_time IS NOT NULL THEN 5
        WHEN rr.arrive_time IS NOT NULL THEN 4
        WHEN rr.receive_time IS NOT NULL THEN 3
        ELSE 0
        END AS proc_code FROM fault_order fo
        LEFT JOIN repair_record rr ON fo.id = rr.fault_order_id
        LEFT JOIN evaluation ev ON fo.id = ev.fault_order_id
                 WHERE openid = :openid
        """)
    Flux<FaultOrderResponse> findAllByOpenid(String openid);

    /**
     * 根据 openid 和 id 删除对应的工单
     *
     * @param openid 用户的 openid
     * @param id 工单 ID
     * @return 删除结果
     */
    @Query("DELETE FROM fault_order WHERE openid = :openid AND id = :id")
    Mono<Void> deleteByOpenidAndId(String openid, Long id);


    @Query("""
        SELECT *
        FROM fault_order fo
        LEFT JOIN repair_record rr ON fo.id = rr.fault_order_id
        LEFT JOIN evaluation ev ON fo.id = ev.fault_order_id
        WHERE fo.openid = :openid
          AND ev.created_at IS NOT NULL AND rr.deal_with_time IS NOT NULL;
    """)
    Flux<FaultOrder> findByProcCodeAndOpenid(String openid);

}
