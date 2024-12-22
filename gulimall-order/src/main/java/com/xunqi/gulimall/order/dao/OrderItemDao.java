package com.xunqi.gulimall.order.dao;

import com.xunqi.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:12:22
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
