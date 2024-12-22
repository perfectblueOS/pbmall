package com.xunqi.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:02:31
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

