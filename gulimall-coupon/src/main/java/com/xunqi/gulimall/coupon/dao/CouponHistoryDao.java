package com.xunqi.gulimall.coupon.dao;

import com.xunqi.gulimall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:02:31
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
