package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xunqi.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xunqi.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.AttrDao;
import com.xunqi.gulimall.product.entity.AttrEntity;
import com.xunqi.gulimall.product.service.AttrService;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        //Spring提供的包，将attr中的同名属性数据移动到attrEntity里，注意对应的属性名必须完全相同。
        BeanUtils.copyProperties(attr,attrEntity);
        //保存基本数据
        this.save(attrEntity);
        //保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        //AttrGroupId是AttrVo独有的属性，前端会传过来，由vo接收
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        //关联属性的id
        relationEntity.setAttrId(attrEntity.getAttrId());
    }

}