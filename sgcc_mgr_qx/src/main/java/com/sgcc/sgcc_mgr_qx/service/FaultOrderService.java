package com.sgcc.sgcc_mgr_qx.service;

import com.sgcc.sgcc_mgr_qx.entity.FaultOrder;
import com.sgcc.sgcc_mgr_qx.repository.FaultOrderRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class FaultOrderService {

    private final FaultOrderRepository faultOrderRepository;

    public FaultOrderService(FaultOrderRepository faultOrderRepository) {
        this.faultOrderRepository = faultOrderRepository;
    }

    public Flux<FaultOrder> findByPage(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;
        return faultOrderRepository.findByPage(pageSize, offset);
    }
}
