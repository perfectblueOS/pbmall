package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
//import io.renren.common.utils.PageUtils;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

