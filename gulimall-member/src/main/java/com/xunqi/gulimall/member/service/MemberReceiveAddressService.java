package com.xunqi.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:06:23
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

