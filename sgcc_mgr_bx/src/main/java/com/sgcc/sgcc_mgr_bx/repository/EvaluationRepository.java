package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.Evaluation;
import com.sgcc.sgcc_mgr_bx.model.StatusModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EvaluationRepository extends ReactiveCrudRepository<Evaluation, Long> {

    // 自定义查询方法，检查是否满足保存评价的条件
    @Query("""
    SELECT COUNT(1) > 0 AS status FROM repair_record rr
    JOIN fault_order fo ON rr.fault_order_id = fo.id
    LEFT JOIN evaluation ev ON ev.fault_order_id = fo.id
    WHERE rr.fault_order_id = :orderId AND fo.openid = :openid AND ev.fault_order_id IS NULL
    """)
    Mono<StatusModel> canEvaluate(String openid, Long orderId);

}
