package com.xunqi.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:02:31
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

