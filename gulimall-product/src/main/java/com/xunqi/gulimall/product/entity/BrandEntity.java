package com.xunqi.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.xunqi.common.valid.AddGroup;
import com.xunqi.common.valid.ListValue;
import com.xunqi.common.valid.UpdateGroup;
import com.xunqi.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 * @description 分组校验
 *
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增不可指定品牌id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
//	@NotNull//非空
	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class, UpdateGroup.class})//不能为null且至少有一个非空格字符
//	@NotEmpty//不能为null且不能为空串
	private String name;
	/**
	 * 品牌logo地址
	 */
	//必须符合url格式
	//可以不携带，但是一旦带了就必须合法
	@NotEmpty(message = "logo必须提交",groups = {AddGroup.class})
	@URL(message = "logo必须是合法的url地址",groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ListValue(vals = {0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	@NotNull(message = "显示状态必须指定",groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "检索首字母必须提交",groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是字母",groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序必须提交",groups = {AddGroup.class})//@NotEmpty只能接受String，集合等
	@Min(value = 0,message = "排序必须>=0",groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
