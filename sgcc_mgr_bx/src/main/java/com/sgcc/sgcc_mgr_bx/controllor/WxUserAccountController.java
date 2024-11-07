package com.sgcc.sgcc_mgr_bx.controllor;

import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_bx.service.WxUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;
/**
* @Author: cy
* @Date: 2024/11/5 16:26
* @Description: 用户户号接口
*/
@RestController
@RequestMapping("/wxUserAccount")
public class WxUserAccountController {

    @Autowired
    private WxUserAccountService wxUserAccountService;

    /**
     * 创建新的账户记录
     * @param data 包含账户信息的 JSON 数据
     * @param authentication 用于获取当前用户的 openid
     * @return 保存后的账户信息
     */
    @PostMapping("/create")
    public Mono<AjaxResponse> create(Authentication authentication, @RequestBody Map<String, Object> data) {
        return wxUserAccountService.createAccount(authentication, data);
    }

    /**
     * 查询当前用户的所有户号记录，并返回标签名称
     * @param authentication 用于获取当前用户的 openid
     * @return 当前用户的所有户号记录
     */
    @GetMapping("/list")
    public Mono<AjaxResponse> list(Authentication authentication) {
        String openid = authentication.getName();
        return wxUserAccountService.listAccountsByOpenid(openid);
    }

    /**
     * 根据 id 和 openid 查询用户账户信息，并包含标签名称
     *
     * @param authentication 用户认证信息
     * @param id 账号 ID
     * @return AjaxResponse 包装的查询结果
     */
    @GetMapping("/get/{id}")
    public Mono<AjaxResponse> getAccountById(Authentication authentication, @PathVariable Long id) {
        String openid = authentication.getName();
        return wxUserAccountService.getAccountById(id, openid);
    }

    /**
     * 更新账户信息接口
     *
     * @param authentication 用户认证信息
     * @param data JSON 对象，包含需要更新的字段
     * @return 操作结果
     */
    @PostMapping("/update")
    public Mono<AjaxResponse> updateAccount(Authentication authentication, @RequestBody Map<String, Object> data) {
        return wxUserAccountService.updateAccount(authentication, data);
    }

    /**
     * 删除账户信息接口
     *
     * @param authentication 用户认证信息
     * @param id 账号 ID
     * @return 操作结果
     */
    @PostMapping("/delete/{id}")
    public Mono<AjaxResponse> deleteAccount(Authentication authentication, @PathVariable Long id) {
        String openid = authentication.getName();
        return wxUserAccountService.deleteAccountByIdAndOpenid(id, openid);
    }
}
