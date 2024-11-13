package com.sgcc.sgcc_mgr_bx.controllor;

import com.github.yitter.idgen.YitIdHelper;
import com.sgcc.sgcc_mgr_bx.entity.Tag;
import com.sgcc.sgcc_mgr_bx.entity.UserInfo;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_bx.model.TagCreateRequest;
import com.sgcc.sgcc_mgr_bx.repository.TagRepository;
import com.sgcc.sgcc_mgr_bx.repository.UserInfoRepository;
import com.sgcc.sgcc_mgr_bx.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/wxUser")
public class WxUserController {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private WxUserService wxUserService;

    @Autowired
    private TagRepository tagRepository;

    /**
     * 根据jwt查询当前用户信息
     * @param authentication jwt封装的用户名
     * @return 对应的当前用户信息
     */
    @GetMapping("/currentUser")
    public Mono<AjaxResponse> currentUser(Authentication authentication) {
        return userInfoRepository.findByOpenid(authentication.getName())
                .map(AjaxResponse::success)
                .defaultIfEmpty(AjaxResponse.error("用户未找到")); // 返回用户未找到的错误信息
    }

    /**
     * 根据当前用户的openid更新用户信息
     * @param authentication 当前认证的用户
     * @param userInfo 更新的用户信息
     * @return 更新后的用户信息或者错误信息
     */
    @PostMapping("/updateWxUser")
    public Mono<AjaxResponse> updateWxUser(Authentication authentication, @RequestBody UserInfo userInfo) {
        // 获取当前用户的 openid
        String openid = authentication.getName();  // JWT 中存储的 openid
        // 调用服务层的方法，传递 openid 和 userInfo 进行更新或新增
        return wxUserService.updateOrCreateUser(openid, userInfo);
    }


    /**
     * 创建标签
     * @param request 包含 tagName 的 JSON 数据
     * @param authentication 认证的openid
     * @return 保存后的标签信息
     */
    @PostMapping("/tag/create")
    public Mono<AjaxResponse> tagCreate(Authentication authentication, @RequestBody TagCreateRequest request) {
        // 获取标签名称
        String tagName = request.getTagName();

        // 创建标签对象并设置标签名
        Tag tag = new Tag();
        tag.setOpenid(authentication.getName());
        tag.setTagName(tagName);
        tag.setId(YitIdHelper.nextId());

        // 保存标签到数据库
        return tagRepository.save(tag)
                .map(AjaxResponse::success)
                .defaultIfEmpty(AjaxResponse.error("Failed to create tag"));
    }

    /**
     * 获取当前用户的所有标签列表
     * @param authentication 用于获取当前用户的 openid
     * @return 符合条件的标签列表
     */
    @PostMapping("/tag/list")
    public Mono<AjaxResponse> tagList(Authentication authentication) {
        String openid = authentication.getName();  // 从 Authentication 获取当前用户的 openid

        // 查询符合 openid 的所有标签数据
        Flux<Tag> tags = tagRepository.findByOpenid(openid);

        // 返回结果
        return tags.collectList()  // 将 Flux<Tag> 转换为 List<Tag>
                .map(AjaxResponse::success)
                .defaultIfEmpty(AjaxResponse.error("No tags found for the specified openid"));
    }

    /**
     * 删除指定 ID 的标签（仅限所属用户）
     * @param id 标签 ID
     * @param authentication 用于获取当前用户的 openid
     * @return 删除操作的结果
     */
    @PostMapping("/tag/remove/{id}")
    public Mono<AjaxResponse> tagRemove(Authentication authentication, @PathVariable Long id) {
        String openid = authentication.getName();  // 获取当前用户的 openid

        // 删除符合条件的标签（id 和 openid）
        return tagRepository.deleteByIdAndOpenid(id, openid)
                .then(Mono.just(AjaxResponse.success("Tag deleted successfully")))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("Failed to delete tag: " + e.getMessage())));
    }
}
