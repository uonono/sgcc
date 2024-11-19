package com.sgcc.sgcc_mgr_qx.repository;

import com.sgcc.sgcc_mgr_qx.entity.FaultOrder;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaultOrderRepository extends ReactiveCrudRepository<FaultOrder, Long> {

    /**
     * 分页查所有（不过要是知道了这个接口，用户端的openid限制查询没有意义）
     * @param pageSize 分页大小
     * @param offset 偏移量
     * @return 分页后的数据
     */
    @Query("SELECT * FROM fault_order ORDER BY create_time DESC LIMIT :pageSize OFFSET :offset")
    Flux<FaultOrder> findByPage(int pageSize, int offset);

}
