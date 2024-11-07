package com.sgcc.sgcc_mgr_bx.repository;

import com.sgcc.sgcc_mgr_bx.entity.UserInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserInfoRepository extends ReactiveCrudRepository<UserInfo, Long>, BaseRepository<UserInfo, Long> {

    Mono<UserInfo> findByOpenid(String openid);

    @Query("UPDATE user_info SET nickname = :#{#userInfo.nickname}, headimgurl = :#{#userInfo.headimgurl}, " +
            "phone = :#{#userInfo.phone}, email = :#{#userInfo.email}, address = :#{#userInfo.address}, " +
            "account_number = :#{#userInfo.accountNumber} WHERE openid = :id")
    Mono<Void> updateByOpenid(String id, UserInfo userInfo);
}
