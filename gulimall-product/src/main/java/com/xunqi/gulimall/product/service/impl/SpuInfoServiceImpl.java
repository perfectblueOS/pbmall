package com.xunqi.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.xunqi.common.constant.ProductConstant;
import com.xunqi.common.es.SkuEsModel;
import com.xunqi.common.to.SkuHasStockVo;
import com.xunqi.common.to.SkuReductionTo;
import com.xunqi.common.to.SpuBoundTo;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.common.utils.R;
import com.xunqi.gulimall.product.entity.*;
import com.xunqi.gulimall.product.feign.CouponFeignService;
import com.xunqi.gulimall.product.feign.SearchFeignService;
import com.xunqi.gulimall.product.feign.WareFeignService;
import com.xunqi.gulimall.product.service.*;
import com.xunqi.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoServiceImpl skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //保存spu基本信息
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);
        //保存spu描述图片
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //保存spu图片集
        List<String> images = vo.getImages();
        spuImagesService.saveImages(infoEntity.getId(),images);

        //保存spu规格参数
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(attr.getAttrValues());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()!=0){
            log.error("远程保存spu积分信息失败");
        }

        //保存spu对应的所有的sku信息
        //sku基本信息
        List<Skus> skuses = vo.getSkus();
        if(skuses!=null&& skuses.size()>0){
            skuses.forEach(item->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if(image.getDefaultImg()==1){
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSpuId(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);


                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img ->
                {
                    SkuImagesEntity entity = new SkuImagesEntity();
                    entity.setSkuId(skuId);
                    entity.setImgUrl(img.getImgUrl());
                    entity.setDefaultImg(img.getDefaultImg());
                    return entity;
                }).filter(entity->{
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //TODO 没有图片路径的无需保存
                skuImagesService.saveBatch(imagesEntities);


                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(items -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(items, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item,skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount()>0||skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode()!=0){
                        log.error("远程保存spu积分信息失败");
                    }
                }

            });

        }

        //sku图片信息
        //sku销售属性信息
        //sku优惠满减信息


    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String)params.get("key");
        //TODO 这里的and不会在开头出现
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }
        String status = (String)params.get("status");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("publish_status",status);
        }
        String brandId = (String)params.get("brandId");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String)params.get("catelogId");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("catelog_id",catelogId);
        }


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
        //组装需要的数据
        SkuEsModel skuEsModel = new SkuEsModel();
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        List<ProductAttrValueEntity> baseAttrlistforspu = productAttrValueService.baseAttrlistforspu(spuId);

        List<Long> collect = baseAttrlistforspu.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrs(collect);
        Set<Long> idSet = new HashSet<>(searchAttrIds);
        List<SkuEsModel.Attrs> attrs = new ArrayList<>();
        List<SkuEsModel.Attrs> collect1 = baseAttrlistforspu.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item->{
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item,attrs1);
            return attrs1;
        }).collect(Collectors.toList());
        Map<Long,Boolean> stockMap = null;
        try{
            R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>(){};
            stockMap = skuHasStock.getData(typeReference).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        }
        catch (Exception e){
            log.error("库存服务查询异常，原因{}",e);
        }


        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> esModels = skuInfoEntities.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }

            //热度评分
            esModel.setHotScore(0L);

            BrandEntity brand = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());
            esModel.setAttrs(collect1);
            return esModel;
        }).collect(Collectors.toList());
        R r = searchFeignService.productStatusUp(esModels);
        if(r.getCode() == 0){
            //远程调用成功
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP);
        }
        else{

        }

    }


}
