package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.RepairRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RepairRecordRepository extends ReactiveCrudRepository<RepairRecord, Long> {


    // 自定义查询方法，根据 faultOrderId 获取 RepairRecord 列表
    Mono<RepairRecord> findByFaultOrderId(Long aLong);


}
