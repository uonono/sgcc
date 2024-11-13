package com.sgcc.sgcc_mgr_bx.service;

import com.github.yitter.idgen.YitIdHelper;
import com.sgcc.sgcc_mgr_bx.entity.Account;
import com.sgcc.sgcc_mgr_bx.model.AccountModel;
import com.sgcc.sgcc_mgr_bx.model.UpdateAccountRequest;
import com.sgcc.sgcc_mgr_bx.repository.AccountRepository;
import com.sgcc.sgcc_mgr_bx.repository.TagRepository;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WxUserAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DatabaseClient databaseClient;

    /**
     * 创建新的账户记录
     * @param request 包含账户信息的 JSON 数据
     * @param authentication 用于获取当前用户的 openid
     * @return 保存后的账户信息
     */
    public Mono<AjaxResponse> createAccount(Authentication authentication, @RequestBody UpdateAccountRequest request) {
        // 获取字段值
        String detailAddress = request.getDetailAddress();
        String address = request.getAddress();
        String account = request.getAccount();
        Long tagId = request.getTagId();
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();

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
     * @param request JSON 对象，包含需要更新的字段
     * @return 操作结果
     */
    public Mono<Void> updateAccount(Authentication authentication, @RequestBody UpdateAccountRequest request) {
        String openid = authentication.getName();

        // 其它字段
        String address = request.getAddress();
        String detailAddress = request.getDetailAddress();
        String account = request.getAccount();
        Long tagId = request.getTagId();
        Double latitude = request.getLatitude();
        Double longitude = request.getLongitude();

        long id = request.getId() ;

        StringBuilder sql = new StringBuilder("UPDATE account_table SET ");
        List<Object> parameters = new ArrayList<>();

        if (address != null) {
            sql.append("address = ?, ");
            parameters.add(address);
        }
        if (detailAddress != null) {
            sql.append("detail_address = ?, ");
            parameters.add(detailAddress);
        }
        if (account != null) {
            sql.append("account = ?, ");
            parameters.add(account);
        }
        if (tagId != null) {
            sql.append("tag_id = ?, ");
            parameters.add(tagId);
        }
        if (latitude != null) {
            sql.append("latitude = ?, ");
            parameters.add(latitude);
        }
        if (longitude != null) {
            sql.append("longitude = ?, ");
            parameters.add(longitude);
        }

        // Remove the last comma and space
        if (!parameters.isEmpty()) {
            sql.setLength(sql.length() - 2);
        } else {
            return Mono.empty(); // No fields to update
        }

        sql.append(" WHERE id = ? AND openid = ?");
        parameters.add(id);
        parameters.add(openid);
        String sqlString = sql.toString();
        System.out.println(sqlString);
        // 使用 DatabaseClient 执行动态 SQL
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sqlString);
        for (int i = 0; i < parameters.size(); i++) {
            spec = spec.bind(i, parameters.get(i));
        }
        return spec.then();
//        return accountRepository.updateAccountByIdAndOpenid(id, openid, address, detailAddress, account, tagId, latitude, longitude)
//                .then(Mono.just(AjaxResponse.success("Account updated successfully")))
//                .onErrorResume(e -> Mono.just(AjaxResponse.error("An error occurred: " + e.getMessage())));
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
