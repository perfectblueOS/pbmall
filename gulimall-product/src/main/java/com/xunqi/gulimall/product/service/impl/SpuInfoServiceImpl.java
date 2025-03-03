package com.xunqi.gulimall.product.service.impl;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.product.entity.*;
import com.xunqi.gulimall.product.service.*;
import com.xunqi.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import io.renren.common.utils.PageUtils;
//import io.renren.common.utils.Query;

import com.xunqi.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


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
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                List<Attr> attrs = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrs.stream().map(items -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(items, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
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


}
