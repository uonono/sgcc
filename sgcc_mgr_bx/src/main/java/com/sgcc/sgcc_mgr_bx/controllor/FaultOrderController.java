package com.sgcc.sgcc_mgr_bx.controllor;

import com.github.yitter.idgen.YitIdHelper;
import com.sgcc.sgcc_mgr_bx.entity.Evaluation;
import com.sgcc.sgcc_mgr_bx.entity.FaultOrder;
import com.sgcc.sgcc_mgr_bx.model.EvaluationRequest;
import com.sgcc.sgcc_mgr_bx.model.WorkerLocation;
import com.sgcc.sgcc_mgr_bx.repository.EvaluationRepository;
import com.sgcc.sgcc_mgr_bx.repository.FaultOrderRepository;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/faultOrder")
public class FaultOrderController {

    @Autowired
    private FaultOrderRepository faultOrderRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private ReactiveRedisTemplate<String, WorkerLocation> reactiveRedisTemplate;

    /**
     * 接收并保存故障订单信息
     *
     * @param authentication openid
     * @param request           包含故障订单信息的 JSON 数据
     * @return 保存操作的结果
     */
    @PostMapping("/save")
    public Mono<AjaxResponse> faultOrder(Authentication authentication, @RequestBody FaultOrder request) {
        FaultOrder faultOrder = new FaultOrder();

        // 将请求数据解析并赋值给 FaultOrder 实体
        faultOrder.setAddressAreaName(request.getAddressAreaName());
        faultOrder.setAddress(request.getAddress());
        faultOrder.setAccount(request.getAccount());
        faultOrder.setContactName(request.getContactName());
        faultOrder.setContactPhone(request.getContactPhone());
        faultOrder.setContent(request.getContent());
        faultOrder.setOrderTime(request.getOrderTime());
        faultOrder.setLongitude(request.getLongitude());
        faultOrder.setLatitude(request.getLatitude());
        faultOrder.setId(YitIdHelper.nextId());
        faultOrder.setOpenid(authentication.getName());
        faultOrder.setProcCode(0);

        // 处理故障订单的业务逻辑
        // ...
        // 保存到数据库并返回结果
        return faultOrderRepository.save(faultOrder)
                .map(AjaxResponse::success)
                .onErrorResume(e -> Mono.just(AjaxResponse.error("Failed to save fault order: " + e.getMessage())));
    }

    /**
     * 获取提交的工单
     * @param id 工单id
     * @return 所有符合id的工单
     */
    @GetMapping("/get/{id}")
    public Mono<AjaxResponse> getFaultOrderById(Authentication authentication, @PathVariable Long id) {
        String openid = authentication.getName();
        return faultOrderRepository.findByOpenidAndId(openid, id)
                .map(AjaxResponse::success)
                .switchIfEmpty(Mono.just(AjaxResponse.error("Fault order not found with id: " + id + " and openid: " + openid)))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("An error occurred: " + e.getMessage())));
    }

    /**
     * 查询当前openid用户的所有提交的工单，到没到是一回事，报没报是另一回事，报了被接单是仍需要取消的
     * @param authentication 当前用户openid
     * @return 所有openid所属的报修工单
     */
    @PostMapping("/list")
    public Mono<AjaxResponse> listFaultOrdersByOpenid(Authentication authentication) {
        String openid = authentication.getName();
        return faultOrderRepository.findAllByOpenid(openid)
                .collectList()
                .map(AjaxResponse::success)
                .onErrorResume(e -> Mono.just(AjaxResponse.error("An error occurred while retrieving fault orders: " + e.getMessage())));
    }

    /**
     * 取消工单接口
     *
     * @param authentication 用户认证信息
     * @param id 工单 ID
     * @return 操作结果
     */
    @PostMapping("/cancel/{id}")
    public Mono<AjaxResponse> cancelOrder(Authentication authentication, @PathVariable Long id) {
        String openid = authentication.getName();
        return faultOrderRepository.deleteByOpenidAndId(openid, id)
                .then(Mono.just(AjaxResponse.success("工单已成功取消")))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("取消工单失败: " + e.getMessage())));
    }

    /**
     * 保存工单评价
     *
     * @param authentication 是我的单才能评价  openid
     * @param request        评价内容
     * @return 评价成功与否
     */
    @PostMapping("/evaluate/create")
    public Mono<AjaxResponse> createEvaluation(@RequestBody EvaluationRequest request, Authentication authentication) {
        String openid = authentication.getName();

        // 获取请求数据
        Long orderId = request.getOrderId();
        Integer attitudeScore = request.getAttitudeScore();
        Integer timelyScore = request.getTimelyScore();
        String content = request.getContent();

        // 处理评价创建的业务逻辑
        // ...
        return evaluationRepository.canEvaluate(openid, orderId)
                .flatMap(statusModel -> {
                    if (statusModel != null && statusModel.getStatus() == 1) { // 1 表示可以评价
                        Evaluation evaluation = new Evaluation();
                        evaluation.setId(YitIdHelper.nextId());
                        evaluation.setFaultOrderId(orderId);
                        evaluation.setServiceAttitude(attitudeScore);
                        evaluation.setRepairTimeliness(timelyScore);
                        evaluation.setComments(content);

                        // 在事务中同时执行保存评价和更新操作
                        return transactionalOperator.transactional(
                                evaluationRepository.save(evaluation)
                                        .then(databaseClient.sql("UPDATE fault_order SET proc_code = 7 WHERE id = ?")
                                                .bind(0, orderId)
                                                .then())
                        ).thenReturn(AjaxResponse.success("评价成功"));
                    } else {
                        return Mono.just(AjaxResponse.error("工单已评价或无权限评价该工单"));
                    }
                })
                .onErrorResume(e -> Mono.just(AjaxResponse.error("系统异常，请联系管理员: " + e.getMessage())));
    }

    /**
     * 根据id查询评价接口
     * @param id 评价id
     * @param authentication openid
     * @return id的评价信息
     */
    @GetMapping("evaluate/get/item/{id}")
    public Mono<AjaxResponse> getEvaluationByIdAndOpenid(@PathVariable String id, Authentication authentication) {
        String openid = authentication.getName();
        return evaluationRepository.findByIdAndOpenid(id, openid)
                .map(AjaxResponse::success)
                .defaultIfEmpty(AjaxResponse.error("Evaluation not found"));
    }

    /**
     * 获取历史报修记录
     * @param authentication openid
     * @return 历史报修记录信息
     */
    @GetMapping("addressHistory/list")
    public Mono<AjaxResponse> getAddressHistoryList(Authentication authentication) {
        String openid = authentication.getName();
        return faultOrderRepository.findByProcCodeAndOpenid(7, openid)
                .collectList()  // 将 Flux 转换为 Mono<List<FaultOrder>>
                .map(faultOrders -> {
                    if (faultOrders.isEmpty()) {
                        return AjaxResponse.error("AddressHistory not found");
                    } else {
                        return AjaxResponse.success(faultOrders);
                    }
                });
    }

    /**
     * 获取抢修人员位置 获取 WorkerLocation，封装成 AjaxResponse
     * @param id 工单id
     * @return 抢修人员当前位置
     */
    @GetMapping("/worker/location/{id}")
    public Mono<AjaxResponse> getWorkerLocation(@PathVariable String id) {
        // 假设 Redis 存储的键是工人的 id，例如：worker:location:{id}
        String redisKey = "worker:location:" + id;
        return reactiveRedisTemplate.opsForValue().get(redisKey)
                .flatMap(workerLocation -> {
                    // 如果找到工人的位置，返回成功的响应
                    return Mono.just(AjaxResponse.success(workerLocation));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // 如果未找到工人的位置，返回错误的响应
                    return Mono.just(AjaxResponse.error("Worker location not found"));
                }));
    }

    /**
     * 使用 ReactiveRedisTemplate 插入两个测试数据
     * @return 插入成功与否
     */
    @GetMapping("/worker/insert/test")
    public Mono<Void> insertTestData() {
        WorkerLocation worker1 = new WorkerLocation(39.9042, 116.4074);  // 北京位置
        WorkerLocation worker2 = new WorkerLocation(31.2304, 121.4737);  // 上海位置

        // 将数据存入 Redis，假设 ID 为 worker1 和 worker2
        return Mono.zip(
//                reactiveRedisTemplate.opsForHash().put("worker:location", "611391076756933", worker1),
//                reactiveRedisTemplate.opsForHash().put("worker:location", "611391076756933", worker2)
                reactiveRedisTemplate.opsForValue().set("worker:location:611391076756933", worker1),
                reactiveRedisTemplate.opsForValue().set("worker:location:611391076756933", worker2)
        ).then();
    }

}
