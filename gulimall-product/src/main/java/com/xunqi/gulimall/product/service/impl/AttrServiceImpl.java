package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xunqi.gulimall.product.dao.AttrGroupDao;
import com.xunqi.gulimall.product.dao.CategoryDao;
import com.xunqi.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xunqi.gulimall.product.entity.AttrGroupEntity;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.vo.AttrRespVo;
import com.xunqi.gulimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.AttrDao;
import com.xunqi.gulimall.product.entity.AttrEntity;
import com.xunqi.gulimall.product.service.AttrService;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;

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

    /**
     * 分页查询商品信息
     * @param params 前端传的各种参数
     * @param catelogId 指定类别查询
     * @return
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        //新建查询对象
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        //前端传入的catelogId=0表示全部查询
        //如果不等于0，那么按照指定的类别查询
        if(catelogId!=0){
            queryWrapper.eq("catelog_id",catelogId);
        }
        //拿到前端传回的关键字key
        String key = (String) params.get("key");
        //如果关键字不为空，进行id的精确查询和name的模糊查询
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper)->{
                wrapper.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        //封装页面对象
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        //前端还要求额外的两个属性，categoryName和GroupName，需要查询后拿到
        //先拿到查询后封装的AttrEntity结果集
        List<AttrEntity> records = page.getRecords();
        //对每一个AttrEntity进行处理，转换成前端需要的AttrRespVo
        List<AttrRespVo> respVos = records
                .stream()//利用流式处理
                .map((attrEntity) -> {//对每一个attrEntity进行处理
            AttrRespVo attrRespVo = new AttrRespVo();
            //将attrEntity与attrRespVo的每一个相同属性（也就是除了categoryName和GroupName）拷贝到attrRespVo中
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //接下来添加剩余的两个属性
            //从attr和attrGroup的关联表中拿到关联信息
            AttrAttrgroupRelationEntity attrId = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (attrId != null) { //找到对应信息
                //从拿到的关联信息中取得attr对应的attrGroupId信息
                Long attrGroupId = attrId.getAttrGroupId();
                //根据attrGroupId拿到对应的分组信息
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                //从对应的分组信息中拿到组名，并在attrRespVo中设置
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            //根据前端传过来的商品属性拿到所属商品类别id，再根据id拿到对应的分类信息
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {//成功拿到
                //获取分类名并设置到attrRespVo中
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());//转换为List
        //设置页面要展示的商品属性列表
        pageUtils.setList(respVos);
        return pageUtils;
    }

}