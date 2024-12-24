package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.CategoryDao;
import com.xunqi.gulimall.product.entity.CategoryEntity;
import com.xunqi.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

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