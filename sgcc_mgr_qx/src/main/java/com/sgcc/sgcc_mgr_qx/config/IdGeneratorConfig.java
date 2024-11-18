package com.sgcc.sgcc_mgr_qx.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/** 
* @Author: cy
* @Date: 2024/11/5 10:21
* @Description:  初始化后，在任何需要生成ID的地方，调用以下方法：long newId = YitIdHelper.nextId();
*/
@Configuration
public class IdGeneratorConfig {

    /**
     * 全局的雪花id配置，在spring容器初始化后进行创建  machine范围0-63
     */
    @PostConstruct
    public void init() {
        // 创建 IdGeneratorOptions 对象并进行配置
        short machine = 55;
        IdGeneratorOptions options = new IdGeneratorOptions(machine);
        // 配置其他参数（如果需要）
        // options.WorkerIdBitLength = 10; // 默认6，最多支持64个节点
        // options.SeqBitLength = 6; // 默认6，如果需要更多ID速度，可以增大
        // options.BaseTime = Your_Base_Time; // 如果需要兼容老系统的BaseTime
        // 设置 ID 生成器
        YitIdHelper.setIdGenerator(options);
    }
}
