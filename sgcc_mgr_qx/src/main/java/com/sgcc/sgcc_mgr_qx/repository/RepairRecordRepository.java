package com.sgcc.sgcc_mgr_qx.repository;


import com.sgcc.sgcc_mgr_qx.entity.RepairRecord;
import com.sgcc.sgcc_mgr_qx.model.StatusModel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RepairRecordRepository extends ReactiveCrudRepository<RepairRecord, Long> {


    /**
     * 检查是否已存在指定工单和用户的记录
     * @param faultOrderId 工单ID
     * @param repairUserPhone 维修用户手机号
     * @return 记录是否存在
     */
    @Query("SELECT COUNT(1) > 0 AS status FROM repair_record WHERE fault_order_id = :faultOrderId AND repair_user_phone = :repairUserPhone")
    Mono<StatusModel> faultOrderIdAndRepairUserPhoneIsNull(@Param("faultOrderId") Long faultOrderId, @Param("repairUserPhone") String repairUserPhone);


    /**
     * 自定义插入维修记录
     * @param id 维修记录ID
     * @param faultOrderId 工单ID
     * @param repairUserPhone 维修用户手机号
     * @return 插入操作结果
     */
    @Modifying
    @Query("INSERT INTO repair_record (id, fault_order_id, repair_user_phone, receive_time) " +
            "VALUES (:id, :faultOrderId, :repairUserPhone, CURRENT_TIMESTAMP)")
    Mono<Integer> insertRepairRecord(@Param("id") Long id,
                                     @Param("faultOrderId") Long faultOrderId,
                                     @Param("repairUserPhone") String repairUserPhone);

    /**
     * 获取进行中的订单
     * @param repairUserPhone 用户手机号
     * @return
     */
    @Query("SELECT fr.* FROM fault_order fo " +
            "JOIN repair_record rr ON fo.id = rr.fault_order_id " +
            "WHERE rr.repair_user_phone = :repairUserPhone " +
            "AND fo.status = '进行中'")
    Flux<RepairRecord> findInProgressOrdersByPhone(@Param("repairUserPhone") String repairUserPhone);

}
