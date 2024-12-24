package com.xunqi.gulimall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.R;
//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.xunqi.common.valid.AddGroup;
import com.xunqi.common.valid.UpdateGroup;
import com.xunqi.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xunqi.gulimall.product.entity.BrandEntity;
import com.xunqi.gulimall.product.service.BrandService;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
//    @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:brand:save")
//    标@Valid启用校验
    //在@Valid后紧跟BindingResult，可以对校验的结果做处理
    //不启用这种校验方式是因为重复代码过多，重用性较差，所以我们用全局异常处理GulimallExceptionControllerAdvice
    //@Validated是Spring提供的注解，可以实现对特定分组的校验进行分组校验
    //@Validated如果指定了分组，没指定分组的字段校验不会生效，
    //相反，如果没有指定分组，指定分组的字段校验不会生效
    //@Valid没有其他属性，无法实现校验分组
    public R save(/*@Valid*/@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
//        if(result.hasErrors()){
//            Map<String,String> map = new HashMap<>();
//            //获取校验的错误结果
//            result.getFieldErrors().forEach((item)->{
//                //获取错误提示
//                String message = item.getDefaultMessage();
//                //获取错误的属性名
//                String field = item.getField();
//                map.put(field,message);
//            });
//            R.error(400,"提交数据非法").put("data",map);
//        }
//        else {
//            brandService.save(brand);
//        }
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
//    @RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
