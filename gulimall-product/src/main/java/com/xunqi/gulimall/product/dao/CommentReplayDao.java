package com.xunqi.gulimall.product.dao;

import com.xunqi.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
