package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
//import io.renren.common.utils.PageUtils;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

