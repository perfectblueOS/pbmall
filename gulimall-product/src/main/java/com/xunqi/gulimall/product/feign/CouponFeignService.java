package com.xunqi.gulimall.product.feign;

import com.xunqi.common.to.SkuReductionTo;
import com.xunqi.common.to.SpuBoundTo;
import com.xunqi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * TODO
     * RequestBody将对象转为json
     * 找到gulimall-coupon，发送请求，将上一步的json放在请求体位置
     * 对方服务收到请求，将请求体的json转为entity（有一一对应关系就可以，与类型无关）
     * 即：json数据格式兼容，双方无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @PostMapping("coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
