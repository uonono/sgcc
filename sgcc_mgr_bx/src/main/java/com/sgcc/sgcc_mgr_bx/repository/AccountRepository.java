package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> ,BaseRepository<Account,Long> {

    // 根据 openid 查询所有符合条件的账户
    Flux<Account> findByOpenid(String openid);

    /**
     * 根据 openid 和 id 查询 Account
     *
     * @param openid 用户的 openid
     * @param id 账号 ID
     * @return Account 实体
     */
    @Query("SELECT * FROM account_table WHERE openid = :openid AND id = :id")
    Mono<Account> findByOpenidAndId(String openid, Long id);

    // 自定义更新方法，根据 id 和 openid 更新账户信息
    @Query("UPDATE account_table SET address = :address, detail_address = :detailAddress, account = :account, " +
            "tag_id = :tagId, latitude = :latitude, longitude = :longitude " +
            "WHERE id = :id AND openid = :openid")
    Mono<Void> updateAccountByIdAndOpenid(Long id, String openid, String address, String detailAddress,
                                             String account, Long tagId, Double latitude, Double longitude);

    // 自定义删除方法，根据 openid 和 id 删除账户
    @Query("DELETE FROM account_table WHERE id = :id AND openid = :openid")
    Mono<Void> deleteByOpenidAndId(Long id, String openid);
}
