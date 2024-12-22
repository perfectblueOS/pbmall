package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.AttrGroupDao;
import com.xunqi.gulimall.product.entity.AttrGroupEntity;
import com.xunqi.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
/*
extends ServiceImpl<AttrGroupDao, AttrGroupEntity>：
    AttrGroupServiceImpl 继承自mybatis ServiceImpl 泛型类，其中：
    AttrGroupDao 是数据访问对象（DAO），用于与数据库交互，处理 AttrGroupEntity 实体的增删改查操作。
    AttrGroupEntity 是实体类，表示属性组的数据模型。
    ServiceImpl 是 MyBatis-Plus 提供的一个实现类，它已经实现了许多常见的 CRUD 操作方法，因此不需要再手动编写这些基础方法。
    通过继承 ServiceImpl，可以直接使用这些现成的方法，并且可以在此基础上进行扩展。
 */
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 分页查询属性分组
     * @param params 前端传来的页面查询属性
     * @param catelogId 分类id
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
            /*
        IPage 是 MyBatis-Plus 框架提供的一个接口，用于封装分页查询的结果。它不仅包含当前页的数据列表，还包含了分页相关的元数据，如总记录数、总页数、当前页码等。
        具体来说，IPage 接口的主要功能和属性包括：
            数据列表：存储当前页的查询结果。
            分页参数：包括当前页码（current）、每页显示的记录数（size）、总记录数（total）、总页数（pages）等。
        这里的IPage<AttrGroupEntity> 用于存储分页查询后的 AttrGroupEntity 对象列表及其分页信息。
     */
        /*
        QueryWrapper 是 MyBatis-Plus 提供的一个条件构造器类，用于构建查询条件。
        它可以帮助你方便地构建复杂的 SQL 查询条件，而不需要手动拼接 SQL 语句。
        通过 QueryWrapper，你可以以链式调用的方式添加各种查询条件，从而提高代码的可读性和安全性。
        主要功能
            构建查询条件：可以方便地添加等于、不等于、大于、小于、模糊查询等条件。
            支持链式调用：可以通过连续调用多个方法来构建复杂的查询条件。
            防止 SQL 注入：MyBatis-Plus 内部会对参数进行处理，确保生成的 SQL 语句是安全的。
            支持多种数据库操作：不仅限于查询操作，还可以用于更新、删除等操作
         */
        if(catelogId==0){
            //如果是一级分类，则查询所有属性分组
            //new Query<AttrGroupEntity>().getPage(params)：按照参数中的分页信息创建分页对象
            //new QueryWrapper<AttrGroupEntity>()：创建一个没有附加任何条件的查询条件对象，即查询所有
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>());
            return new PageUtils(page);
        }
        else {
            //拿到前端传入的搜索关键字
            String key = (String) params.get("key");
            //创建一个条件查询对象，并附加条件：catelog_id=catelogId
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);
            //如果有查询关键字
            if(!StringUtils.isEmpty(key)){
                //那么添加查询条件：attr_group_id=key 或 attr_group_name like key
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),wrapper);
            return new PageUtils(page);
        }

    }

}