package com.xunqi.gulimall.order.dao;

import com.xunqi.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:12:22
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
