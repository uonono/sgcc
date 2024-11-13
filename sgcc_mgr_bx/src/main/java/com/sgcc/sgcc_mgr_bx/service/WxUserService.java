package com.sgcc.sgcc_mgr_bx.service;

import com.sgcc.sgcc_mgr_bx.entity.UserInfo;
import com.sgcc.sgcc_mgr_bx.repository.UserInfoRepository;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class WxUserService {

    private final UserInfoRepository userInfoRepository;

    public WxUserService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * 根据 OpenID 更新或新增用户信息
     * @param openid 当前用户的 OpenID
     * @param userInfo 用户信息
     * @return Mono<AjaxResponse>
     */
    public Mono<AjaxResponse> updateOrCreateUser(String openid, @RequestBody UserInfo userInfo) {
        // 获取字段值
        String unionid = userInfo.getUnionid();
        String headimgurl = userInfo.getHeadimgurl();
        String nickname = userInfo.getNickname();
        String phone = userInfo.getPhone();
        String email = userInfo.getEmail();
        String address = userInfo.getAddress();
        String accountNumber = userInfo.getAccountNumber();

        // 处理更新或创建用户的业务逻辑
        return userInfoRepository.findByOpenid(openid)  // 根据 openid 查找用户
                .flatMap(existingUser -> {
                    // 如果找到该用户，更新用户信息，只有字段不为空时才进行更新
                    if (nickname != null && !nickname.isEmpty()) {
                        existingUser.setNickname(nickname);
                    }
                    if (headimgurl != null && !headimgurl.isEmpty()) {
                        existingUser.setHeadimgurl(headimgurl);
                    }
                    if (phone != null && !phone.isEmpty()) {
                        existingUser.setPhone(phone);
                    }
                    if (email != null && !email.isEmpty()) {
                        existingUser.setEmail(email);
                    }
                    if (address != null && !address.isEmpty()) {
                        existingUser.setAddress(address);
                    }
                    if (accountNumber != null && !accountNumber.isEmpty()) {
                        existingUser.setAccountNumber(accountNumber);
                    }
                    // 保存更新后的用户
                    return userInfoRepository.updateByOpenid(existingUser.getOpenid(),existingUser)
                            .then(Mono.just(AjaxResponse.success("UserInfo updated successfully"))); // 返回更新后的用户数据
                })
                .switchIfEmpty(
                        // 如果用户未找到，则新增当前用户
                        Mono.defer(() -> {
                            // 新建一个 UserInfo 对象并填充信息
                            UserInfo newUser = new UserInfo();
                            newUser.setOpenid(openid);
                            if (nickname != null) {
                                newUser.setNickname(nickname);
                            }
                            if (headimgurl != null) {
                                newUser.setHeadimgurl(headimgurl);
                            }
                            if (phone != null) {
                                newUser.setPhone(phone);
                            }
                            if (email != null) {
                                newUser.setEmail(email);
                            }
                            if (address != null) {
                                newUser.setAddress(address);
                            }
                            if (accountNumber != null) {
                                newUser.setAccountNumber(accountNumber);
                            }
                            // 保存新用户
                            return userInfoRepository.save(newUser)
                                    .map(AjaxResponse::success);
                        })
                );
    }
}
