package com.sgcc.sgcc_mgr_qx.controllor;

import com.sgcc.sgcc_mgr_qx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_qx.repository.FaultOrderRepository;
import com.sgcc.sgcc_mgr_qx.service.FaultOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/faultOrder")
public class FaultOrderController {

    @Autowired
    private FaultOrderRepository faultOrderRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private FaultOrderService faultOrderService;


    /**
     * 获取提交的工单
     * @param id 工单id
     * @return 所有符合id的工单
     */
    @GetMapping("/get/{id}")
    public Mono<AjaxResponse> getFaultOrderById(Authentication authentication, @PathVariable Long id) {
        String openid = authentication.getName();
        return faultOrderRepository.findById(id)
                .map(AjaxResponse::success)
                .switchIfEmpty(Mono.just(AjaxResponse.error("Fault order not found with id: " + id + " and openid: " + openid)))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("An error occurred: " + e.getMessage())));
    }

    /**
     * 查询当前报修用户的所有提交的工单
     * @return 所有openid所属的报修工单
     */
    @GetMapping("/findByPage")
    public Mono<AjaxResponse> findByPage(@RequestParam(defaultValue = "1") int pageNumber,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        return faultOrderService.findByPage(pageNumber, pageSize).collectList().map(faultOrders -> {
            if (faultOrders.isEmpty()) {
                return AjaxResponse.error("FaultOrders not found");
            } else {
                return AjaxResponse.success(faultOrders);
            }
        });
    }

}
