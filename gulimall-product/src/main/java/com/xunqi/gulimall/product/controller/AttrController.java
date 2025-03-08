package com.xunqi.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.R;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.xunqi.gulimall.product.entity.ProductAttrValueEntity;
import com.xunqi.gulimall.product.service.ProductAttrValueService;
import com.xunqi.gulimall.product.vo.AttrGroupRelationVo;
import com.xunqi.gulimall.product.vo.AttrRespVo;
import com.xunqi.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.xunqi.gulimall.product.entity.AttrEntity;
import com.xunqi.gulimall.product.service.AttrService;




/**
 * 商品属性
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:46
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;

    /**

     */
    @GetMapping("/{attrType}/list/{spuId}")
    public R baseAttrlistforspu(@PathVariable Long spuId){
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrlistforspu(spuId);
        return R.ok().put("data",entities);
    }

    /**
     * 商品信息查询
     * @param params 前端传的查询参数
     * @param catelogId 类别id
     * @param type 属性类型（销售属性/基本属性）
     * @return
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String,Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String type){
        PageUtils page = attrService.queryBaseAttrPage(params,catelogId,type);
        return R.ok().put("page",page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
//    @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
        //弃用，与前端需要的不一致
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo respVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    @RequestMapping("/update/{spuId}")
//    @RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                    @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
