package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.product.entity.AttrEntity;
import com.xunqi.gulimall.product.vo.AttrRespVo;
import com.xunqi.gulimall.product.vo.AttrVo;

import java.util.Map;

/**
 * 商品属性
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:46
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);
}

