package com.xunqi.gulimall.product.vo;

import lombok.Data;

/**
 * 接收前端传来的（待删除的）商品（id）与所属分组（id）的关系
 */
@Data
public class AttrGroupRelationVo {
    /**
     * 商品id
     */
    private Long attrId;
    /**
     * 商品分组id
     */
    private Long attrGroupId;
}
