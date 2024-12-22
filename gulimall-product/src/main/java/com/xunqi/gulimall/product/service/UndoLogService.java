package com.xunqi.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
//import io.renren.common.utils.PageUtils;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.product.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author perfectblue
 * @email 1608306542@qq.com
 * @date 2024-11-30 12:04:45
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

