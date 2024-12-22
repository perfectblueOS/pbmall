package com.xunqi.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:06:24
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

