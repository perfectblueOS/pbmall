package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

