package com.xunqi.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description:自定义校验器
 * @Created: with IntelliJ IDEA.
 * @author: perfect-blue
 *
 **/
//校验器必须实现ConstraintValidator接口，第一个泛型是校验注解，第二个是校验的数据类型
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化方法，获取vals里的值添加到set中
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] vals = constraintAnnotation.vals();

        for (int val : vals) {
            set.add(val);
        }

    }

    /**
     * 判断是否校验成功
     * @param value 需要效验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        //判断是否有包含的值
        boolean contains = set.contains(value);

        return contains;
    }

}
