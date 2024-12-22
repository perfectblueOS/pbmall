package com.xunqi.gulimall.product.dao;

import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
