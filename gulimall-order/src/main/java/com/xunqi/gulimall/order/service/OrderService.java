package com.xunqi.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.order.entity.OrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:12:22
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

