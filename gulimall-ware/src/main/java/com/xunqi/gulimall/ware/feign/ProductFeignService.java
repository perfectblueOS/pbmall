package com.xunqi.gulimall.ware.feign;

import com.xunqi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
public interface ProductFeignService {
    /**
     * TODO 两重写法
     * 1.走网关+/api+gateway服务
     * 2.不走网关+本地服务
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
//    @RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);
}
