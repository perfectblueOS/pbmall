package com.xunqi.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-12-04 09:18:26
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

