package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.RepairRecord;
import com.sgcc.sgcc_mgr_bx.model.RepairRecordWithCreatedAt;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RepairRecordRepository extends ReactiveCrudRepository<RepairRecord, Long> {


    /**
     * 自定义查询方法，根据 faultOrderId 获取 RepairRecord 列表
     * @param faultOrderId 工单id
     * @return 带评价创建时间的报修人员状态表
     */
    @Query("SELECT r.*, e.created_at " +
            "FROM repair_record r " +
            "LEFT JOIN evaluation e ON r.fault_order_id = e.fault_order_id " +
            "WHERE r.fault_order_id = :faultOrderId")
    Mono<RepairRecordWithCreatedAt> findByFaultOrderId(Long faultOrderId);


}
