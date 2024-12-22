package com.xunqi.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description: 自定义注解规则
 * @Created: with IntelliJ IDEA.
 * @author: 夏沫止水
 * @createTime: 2020-05-27 17:48
 **/

@Documented
//指定校验器，可以指定多个不同的校验器进行不同类型的校验
@Constraint(validatedBy = { ListValueConstraintValidator.class })
//标注在哪些位置
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface ListValue {
    //校验失败时显示的信息
    String message() default "{com.xunqi.common.valid.ListValue.message}";
    //校验时使用的分组
    Class<?>[] groups() default { };
    //额外的属性
    Class<? extends Payload>[] payload() default { };
    //值只能是这个指定的值
    int[] vals() default { };

}
