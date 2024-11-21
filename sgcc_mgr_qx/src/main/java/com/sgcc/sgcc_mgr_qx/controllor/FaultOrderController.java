package com.sgcc.sgcc_mgr_qx.controllor;

import com.github.yitter.idgen.YitIdHelper;
import com.sgcc.sgcc_mgr_qx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_qx.repository.FaultOrderRepository;
import com.sgcc.sgcc_mgr_qx.repository.RepairRecordRepository;
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
    private RepairRecordRepository repairRecordRepository;

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

    /**
     * 接单接口
     * @param authentication 当前用户认证信息
     * @param orderId 工单ID
     * @return 接单结果
     */
    @GetMapping("/accept/{orderId}")
    public Mono<AjaxResponse> acceptOrder(Authentication authentication, @PathVariable Long orderId) {
        String repairUserPhone = authentication.getName(); // 获取当前用户手机号
        Long id = YitIdHelper.nextId();
        //联合主键本身带检查，不知道这里查询是否多余（emm多余的是否影响性能）
        return repairRecordRepository.faultOrderIdAndRepairUserPhoneIsNull(orderId, repairUserPhone)
                .flatMap(exists -> {
                    if (exists.isStatusOne()) {
                        return Mono.just(AjaxResponse.error("接单失败：您已接过此工单！"));
                    }
                    return repairRecordRepository
                            .insertRepairRecord(id, orderId, repairUserPhone)
                            .then(Mono.just(AjaxResponse.success("接单成功！")));
                })
                .onErrorResume(e -> Mono.just(AjaxResponse.error("接单失败：" + e.getMessage())));
    }

    /**
     * 获取进行中的订单
     * @param authentication 用户手机号
     * @return 进行中的订单
     */
    @GetMapping("/inProgress")
    public Mono<AjaxResponse> getInProgressOrders(Authentication authentication) {
        String repairUserPhone = authentication.getName(); // 获取当前登录用户手机号
        return repairRecordRepository.findInProgressOrdersByPhone(repairUserPhone)
                .collectList()
                .flatMap(inProgressOrders -> {
                    if (inProgressOrders.isEmpty()) {
                        return Mono.just(AjaxResponse.error("没有进行中的工单"));
                    } else {
                        return Mono.just(AjaxResponse.success(inProgressOrders));
                    }
                });
    }
}
