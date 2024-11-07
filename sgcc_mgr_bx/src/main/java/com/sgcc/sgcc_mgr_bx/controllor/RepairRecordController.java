package com.sgcc.sgcc_mgr_bx.controllor;

import com.sgcc.sgcc_mgr_bx.entity.RepairRecord;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_bx.repository.RepairRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/repairRecords")
public class RepairRecordController {

    private final RepairRecordRepository repairRecordRepository;

    @Autowired
    public RepairRecordController(RepairRecordRepository repairRecordRepository) {
        this.repairRecordRepository = repairRecordRepository;
    }

    /**
     * 根据工单ID（faultOrderId）获取RepairRecord列表，并使用AjaxResponse进行封装
     * @param faultOrderId 工单ID
     * @return 封装的AjaxResponse对象
     */
    @GetMapping("/get/{id}")
    public Mono<AjaxResponse> getRepairRecordByFaultOrderId(@PathVariable("id") Long faultOrderId) {
        return repairRecordRepository.findByFaultOrderId(faultOrderId)
                .map(AjaxResponse::success) // 如果找到记录，封装成功响应
                .defaultIfEmpty(AjaxResponse.error("No repair record found for the given faultOrderId")); // 如果没有找到记录，返回错误响应
    }
}
