package com.xunqi.gulimall.product.vo;

import lombok.Data;

/**
 * 返回的商品响应数据中额外包含了
 * catelogName:所属分类名字
 * groupName：所属分组名字
 * 所以要额外创建vo对象来封装后端返回参数。
 * 继承AttrVo，并添加这两个额外的参数。
 */
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 商品所属分类名
     */
    private String catelogName;
    /**
     * 商品所属分组名
     */
    private String groupName;
}
