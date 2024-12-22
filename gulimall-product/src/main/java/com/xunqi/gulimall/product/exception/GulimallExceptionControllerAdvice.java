package com.xunqi.gulimall.product.exception;

import com.xunqi.common.exception.BizCodeEnum;
import com.xunqi.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理所有异常
 */
@Slf4j//日志
//@ResponseBody+
//@ControllerAdvice(basePackages = "com.xunqi.gulimall.product.controller")=
@RestControllerAdvice(basePackages = "com.xunqi.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class/*校验不合法异常*/)
    public R handleValidException(MethodArgumentNotValidException e){
        //打印日志
        log.error("数据校验异常{}，类型{}",e.getMessage(),e.getClass());
        //异常的map
        Map<String,String> errorMap = new HashMap<>();
        //获取校验结果
        BindingResult bindingResult = e.getBindingResult();
        //遍历结果添加到errorMap中
        bindingResult.getFieldErrors().forEach((fieldError)->{
            //错误的字段，以及错误的信息
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(), BizCodeEnum.VAILD_EXCEPTION.getMessage()).put("data",errorMap);
    }
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误{}，类型{}",throwable.getMessage(),throwable.getClass());
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMessage());
    }
    
}
