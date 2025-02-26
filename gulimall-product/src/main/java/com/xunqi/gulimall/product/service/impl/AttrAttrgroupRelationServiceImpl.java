package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.xunqi.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xunqi.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xunqi.gulimall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存属性分组关联关系
     *
     * 将传入的视图对象集合转换为实体对象集合后，调用基类批量保存方法
     *
     * @param vos 属性分组关联关系视图对象集合，包含需要保存的关联关系数据。
     *            每个视图对象应包含属性ID和属性组ID的对应关系
     *
     */
    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {
        // 将视图对象集合转换为实体对象集合
        // 使用流处理逐个转换视图对象为实体对象，保持属性字段的对应关系
        List<AttrAttrgroupRelationEntity> entities = vos.stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());

        // 调用基类批量保存方法持久化实体集合
        this.saveBatch(entities);
    }


}
