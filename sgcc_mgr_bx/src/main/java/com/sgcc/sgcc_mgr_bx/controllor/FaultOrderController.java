package com.sgcc.sgcc_mgr_bx.controllor;

import com.github.yitter.idgen.YitIdHelper;
import com.sgcc.sgcc_mgr_bx.entity.FaultOrder;
import com.sgcc.sgcc_mgr_bx.repository.FaultOrderRepository;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/faultOrder")
public class FaultOrderController {

    @Autowired
    private FaultOrderRepository faultOrderRepository;

    /**
     * 接收并保存故障订单信息
     *
     * @param authentication openid
     * @param data           包含故障订单信息的 JSON 数据
     * @return 保存操作的结果
     */
    @PostMapping("/save")
    public Mono<AjaxResponse> faultOrder(Authentication authentication, @RequestBody Map<String, Object> data) {
        FaultOrder faultOrder = new FaultOrder();
        // 将请求数据解析并赋值给 FaultOrder 实体
        faultOrder.setAddressAreaName((String) data.get("addressAreaName"));
        faultOrder.setAddress((String) data.get("address"));
        faultOrder.setAccount((String) data.get("account"));
        faultOrder.setContactName((String) data.get("contactName"));
        faultOrder.setContactPhone((String) data.get("contactPhone"));
        faultOrder.setContent((String) data.get("content"));
        faultOrder.setOrderTime((String) data.get("orderTime"));
        faultOrder.setLongitude(Double.valueOf(data.get("longitude").toString()));
        faultOrder.setLatitude(Double.valueOf(data.get("latitude").toString()));
        faultOrder.setId(YitIdHelper.nextId());
        faultOrder.setOpenid(authentication.getName());
        faultOrder.setProcCode(0);
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
}
