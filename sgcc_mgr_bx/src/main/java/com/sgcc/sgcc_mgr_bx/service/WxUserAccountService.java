package com.sgcc.sgcc_mgr_bx.service;

import com.github.yitter.idgen.YitIdHelper;
import com.sgcc.sgcc_mgr_bx.entity.Account;
import com.sgcc.sgcc_mgr_bx.model.AccountModel;
import com.sgcc.sgcc_mgr_bx.repository.AccountRepository;
import com.sgcc.sgcc_mgr_bx.repository.TagRepository;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class WxUserAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * 创建新的账户记录
     * @param data 包含账户信息的 JSON 数据
     * @param authentication 用于获取当前用户的 openid
     * @return 保存后的账户信息
     */
    public Mono<AjaxResponse> createAccount(Authentication authentication, Map<String, Object> data) {
        String detailAddress = (String) data.get("detailAddress");
        String address = (String) data.get("address");
        String account = (String) data.get("account");
        Long tagId = Long.valueOf(data.get("tagId").toString());
        Double latitude = Double.valueOf(data.get("latitude").toString());
        Double longitude = Double.valueOf(data.get("longitude").toString());

        Account accountEntity = new Account();
        accountEntity.setDetailAddress(detailAddress);
        accountEntity.setAddress(address);
        accountEntity.setAccount(account);
        accountEntity.setTagId(tagId);
        accountEntity.setLatitude(latitude);
        accountEntity.setLongitude(longitude);
        accountEntity.setOpenid(authentication.getName());
        accountEntity.setId(YitIdHelper.nextId());

        return accountRepository.save(accountEntity)
                .map(AjaxResponse::success)
                .onErrorResume(e -> Mono.just(AjaxResponse.error("Failed to create account: " + e.getMessage())));
    }

    /**
     * 查询当前用户的所有户号记录，并返回标签名称
     * @param openid 用于获取当前用户的 openid
     * @return 当前用户的所有户号记录
     */
    public Mono<AjaxResponse> listAccountsByOpenid(String openid) {
        return accountRepository.findByOpenid(openid)
                .flatMap(account -> {
                    AccountModel accountModel = new AccountModel();
                    accountModel.setId(account.getId());
                    accountModel.setOpenid(account.getOpenid());
                    accountModel.setAddress(account.getAddress());
                    accountModel.setDetailAddress(account.getDetailAddress());
                    accountModel.setAccount(account.getAccount());
                    accountModel.setTagId(account.getTagId());
                    accountModel.setLatitude(account.getLatitude());
                    accountModel.setLongitude(account.getLongitude());

                    return tagRepository.findByIdAndOpenid(account.getTagId(), openid)
                            .map(tag -> {
                                accountModel.setTagName(tag.getTagName());
                                return accountModel;
                            })
                            .defaultIfEmpty(accountModel);
                })
                .collectList()
                .map(AjaxResponse::success)
                .defaultIfEmpty(AjaxResponse.error("No accounts found for the specified openid"));
    }

    /**
     * 根据 id 和 openid 查询用户账户信息，并包含标签名称
     *
     * @param openid 用户认证信息
     * @param id 账号 ID
     * @return AjaxResponse 包装的查询结果
     */
    public Mono<AjaxResponse> getAccountById(Long id, String openid) {
        return accountRepository.findByOpenidAndId(openid, id)
                .flatMap(account -> {
                    AccountModel accountModel = new AccountModel();
                    accountModel.setId(account.getId());
                    accountModel.setOpenid(account.getOpenid());
                    accountModel.setAddress(account.getAddress());
                    accountModel.setDetailAddress(account.getDetailAddress());
                    accountModel.setAccount(account.getAccount());
                    accountModel.setTagId(account.getTagId());
                    accountModel.setLatitude(account.getLatitude());
                    accountModel.setLongitude(account.getLongitude());

                    return tagRepository.findByIdAndOpenid(account.getTagId(), openid)
                            .map(tag -> {
                                accountModel.setTagName(tag.getTagName());
                                return accountModel;
                            })
                            .defaultIfEmpty(accountModel);
                })
                .map(AjaxResponse::success)
                .switchIfEmpty(Mono.just(AjaxResponse.error("Account not found with id: " + id + " and openid: " + openid)))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("An error occurred: " + e.getMessage())));
    }

    /**
     * 更新账户信息接口
     *
     * @param authentication 用户认证信息
     * @param data JSON 对象，包含需要更新的字段
     * @return 操作结果
     */
    public Mono<AjaxResponse> updateAccount(Authentication authentication, Map<String, Object> data) {
        String openid = authentication.getName();

        Long id = Long.valueOf((String) data.get("id"));
        String address = (String) data.get("address");
        String detailAddress = (String) data.get("detailAddress");
        String account = (String) data.get("account");
        Long tagId = Long.valueOf(data.get("tagId").toString());
        Double latitude = Double.valueOf(data.get("latitude").toString());
        Double longitude = Double.valueOf(data.get("longitude").toString());

        return accountRepository.updateAccountByIdAndOpenid(id, openid, address, detailAddress, account, tagId, latitude, longitude)
                .flatMap(result ->
                         Mono.just(AjaxResponse.success(result)))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("An error occurred: " + e.getMessage())));
    }

    /**
     * 根据 openid 和 id 删除账户信息
     *
     * @param id 账号 ID
     * @param openid 用户 openid
     * @return 删除结果
     */
    public Mono<AjaxResponse> deleteAccountByIdAndOpenid(Long id, String openid) {
        return accountRepository.deleteByOpenidAndId(id, openid)
                .then(Mono.just(AjaxResponse.success("Account deleted successfully")))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("Failed to delete account: " + e.getMessage())));
    }
}
