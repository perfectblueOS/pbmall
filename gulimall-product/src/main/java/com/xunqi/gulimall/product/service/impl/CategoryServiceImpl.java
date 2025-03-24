package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.service.CategoryBrandRelationService;
import com.xunqi.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.CategoryDao;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有的分类，并以树形结构返回
     * @return List<CategoryEntity>
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        // 1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2、找到所有的一级分类（父分类为0），并将这些分类的子分类设置为null
        List<CategoryEntity> level1Menus = entities.stream()
                .filter(item -> item.getParentCid() == 0)// 父分类为0
                .map(item -> {
                    item.setChildren(getChildrens(item, entities));
                    return item;
                })// 对每一个最高分类递归设置子分类，使用封装的递归算法getChildrens()
                //按照预设好的排列顺序进行排列
                .sorted((menu1, menu2) -> (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()))
                .collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单是否被别的地方引用(TODO表示将做未做)

        //逻辑删除：使用某一个字段标识其是否被删除，不会真正删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        //初始化空的path列表
        List<Long> paths = new ArrayList<>();
        //使用递归算法查找所有父节点
        List<Long> parentPath = findParentPath(catelogId, paths);
        //查找的父节点路径是从大到小排列的，需要反转
        return paths.toArray(new Long[parentPath.size()]);
    }

    /**
     * 更新类别（同步更新原表和关联表冗余数据）
     * @param category 更新的类别
     */
    @Transactional//开启事务
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parend_cid",0));
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //查所有一级分类
        List<CategoryEntity> level1Categories = getLevel1Categories();
        Map<String, List<Catelog2Vo>> collect2 = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查二级分类
            List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> collect = null;
            if (entities != null) {

                collect = entities.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
                    List<CategoryEntity> entities3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    if (entities3 != null) {
                        List<Catelog2Vo.Category3Vo> collect1 = entities3.stream().map(l3 -> {
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect1);

                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return collect;
        }));
        return collect2;
    }

    /**
     * 递归查询父节点，生成路径的方法
     * @param catelogId 当前节点
     * @param paths 生成的路径
     * @return
     */
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //先加入当前节点id
        paths.add(catelogId);
        //拿到当前节点的数据
        CategoryEntity byId = this.getById(catelogId);
        //如果该节点还有父节点
        if(byId.getParentCid()!=0){
            //递归查询该节点的父节点
            findParentPath(byId.getParentCid(), paths);
        }
        //返回路径的逆序列表
        return paths;
    }

    private static List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2)-> (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort()))
                .collect(Collectors.toList());
        return children;
    }

}
