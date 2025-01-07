package com.xunqi.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.xunqi.gulimall.product.dao.AttrGroupDao;
import com.xunqi.gulimall.product.dao.CategoryDao;
import com.xunqi.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.xunqi.gulimall.product.entity.AttrGroupEntity;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.service.CategoryService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;

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
        //TODO 这里忘记了将关联对象持久化
        //将关联对象持久化
        relationDao.insert(relationEntity);
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

    /**
     * 查询商品属性
     * @param attrId 商品id
     * @return 商品属性
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //根据商品id查到对应的商品属性，将查到的商品属性封装到传回给前端的Vo中
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,respVo);

        //设置分组信息
        //根据商品属性id查询到对应的属性属性和分组关联对象
        AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attrId));
        if(attrgroupRelation!=null){//注意查询值的非空判断，避免报NPE
            //从关联对象中查询到商品所属的分组id，并设置到返回对象中
            respVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
            //通过查询到的分组id查询到对应的分组对象
            //TODO 是否可以简化，将attrgroupRelation.getAttrGroupId()封装起来
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
            if(attrGroupEntity!=null){//非空判断
                //将查询到的分组对象的分组名设置到返回对象中
                respVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }

        //设置分类信息
        //拿到商品属性中的商品分类id
        Long catelogId = attrEntity.getCatelogId();
        //根据商品分类id，查到分类路径
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        //将分类路径设置到返回对象中
        respVo.setCatelogPath(catelogPath);
        //根据商品分类id查到对应的分类信息
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity!=null) {//非空判断
            //将分类的名字设置到返回对象中
            respVo.setCatelogName(categoryEntity.getName());
        }

        return respVo;
    }

    /**
     * 对商品属性进行更新
     * @param attr 前端传的更新信息
     */
    @Transactional//开启事务，当两个写操作有一个及以上执行失败时回滚
    @Override
    public void updateAttr(AttrVo attr) {
        //创建商品属性对象，将前端传过来的商品更改信息（vo）复制到这个对象里
        //新建的原因是updateById(attrEntity)操作只能接受attrEntity
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        //写操作1
        this.updateById(attrEntity);

        //创建商品及分组关联关系对象，对商品和商品分组的id进行赋值
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationEntity.setAttrGroupId(attr.getAttrGroupId());

        //验证该商品的类别是否存在（商品是否已被分类）
        Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        if(count>0){//已被分类
            //写操作2
            //找到该商品的关系记录，并使用relationEntity中的字段值来更新商品和分组记录。
            relationDao.update(relationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",attr.getAttrId()));
        }
        else {//还未被分类
            //写操作2
            //加入商品与分组的关联关系
            relationDao.insert(relationEntity);
        }
    }

}