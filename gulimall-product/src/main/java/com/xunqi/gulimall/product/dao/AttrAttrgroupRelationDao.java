package com.xunqi.gulimall.product.dao;

import com.xunqi.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    /**
     * 根据商品-商品分组关系列表批量删除商品与商品分组关系
     * @param entities
     */
    void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
