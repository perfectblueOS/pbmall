package com.xunqi.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.service.CategoryBrandRelationService;
import com.xunqi.gulimall.product.vo.Catelog2Vo;
import jodd.util.StringUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    StringRedisTemplate redisTemplate;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    RedissonClient redissonClient;

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
        //TODO 检查当前删除的菜单是否被别的地方引用

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
//    失效模式
    @CacheEvict(value = "category",allEntries = true)
//    @Caching(evict = {
//            @CacheEvict(value = {"category"},key = "'getLevel1Categories'"),
//            @CacheEvict(value = {"category"},key = "'getCatalogJson'")
//    } )
//    @CachePut双写模式
    @Transactional//开启事务
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",0));
    }

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));
        return parentCid;
    }

    //TODO lettuce堆外内存溢出
    //默认使用xms指定的堆外内存，使用netty进行连接
    //由于bug，导致netty对外内存溢出
    //解决方法：升级lettuce，或使用jedis
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(StringUtil.isEmpty(catalogJSON)){
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithLocalLock();
            return catalogJsonFromDb;


        }
        Map<String,List<Catelog2Vo>> result = JSON.parseObject(catalogJSON,new TypeReference<Map<String,List<Catelog2Vo>>>(){});
        return result;

    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //锁标识
//        String uuid = UUID.randomUUID().toString();
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,20,TimeUnit.SECONDS);
//        if(Boolean.TRUE.equals(lock)){
//            redisTemplate.expire("lock",10,TimeUnit.SECONDS);非原子
        //锁的粒度越细越快
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();
            Map<String, List<Catelog2Vo>> dataFromDb;
            try{
                dataFromDb = getDataFromDb();
            }
            finally {
//                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script,Long.class),Arrays.asList("lock"),uuid);
                lock.unlock();
            }
            return dataFromDb;

//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(lockValue)){
//                redisTemplate.delete("lock");
//            }

//        }
//        else {
//            try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
//            //重试
//            return getCatalogJsonFromDbWithLocalLock();
//        }

    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if(StringUtil.isEmpty(catalogJSON)){
            Map<String,List<Catelog2Vo>> result = JSON.parseObject(catalogJSON,new TypeReference<Map<String,List<Catelog2Vo>>>(){});
            return result;
        }
        //TODO 优化方式：只进行一次数据库的查询，在代码内进行处理，将数据库查询的时间转化为cpu消耗。
        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        String s = JSON.toJSONString(parentCid);
        redisTemplate.opsForValue().set("catalogJSON",s,1, TimeUnit.DAYS);
        return parentCid;
    }

    //优化前
//        //查所有一级分类
//        List<CategoryEntity> level1Categories = getLevel1Categories();
//        Map<String, List<Catelog2Vo>> collect2 = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            //查二级分类
//            List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//            List<Catelog2Vo> collect = null;
//            if (entities != null) {
//
//                collect = entities.stream().map(item -> {
//                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
//                    List<CategoryEntity> entities3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
//                    if (entities3 != null) {
//                        List<Catelog2Vo.Category3Vo> collect1 = entities3.stream().map(l3 -> {
//                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                            return category3Vo;
//                        }).collect(Collectors.toList());
//                        catelog2Vo.setCatalog3List(collect1);
//
//                    }
//                    return catelog2Vo;
//                }).collect(Collectors.toList());
//            }
//            return collect;
//        }));
//        return collect2;

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        //TODO 本地锁只对当前微服务加锁
        synchronized (this){
            return getDataFromDb();

        }

        //优化前
//        //查所有一级分类
//        List<CategoryEntity> level1Categories = getLevel1Categories();
//        Map<String, List<Catelog2Vo>> collect2 = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//            //查二级分类
//            List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//            List<Catelog2Vo> collect = null;
//            if (entities != null) {
//
//                collect = entities.stream().map(item -> {
//                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());
//                    List<CategoryEntity> entities3 = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
//                    if (entities3 != null) {
//                        List<Catelog2Vo.Category3Vo> collect1 = entities3.stream().map(l3 -> {
//                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                            return category3Vo;
//                        }).collect(Collectors.toList());
//                        catelog2Vo.setCatalog3List(collect1);
//
//                    }
//                    return catelog2Vo;
//                }).collect(Collectors.toList());
//            }
//            return collect;
//        }));
//        return collect2;
    }
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
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
