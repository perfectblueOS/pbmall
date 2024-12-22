package com.xunqi.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:02:31
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

