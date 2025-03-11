package com.shpes.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 身份证号码校验注解
 */
@Documented
@Constraint(validatedBy = IdCardValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdCard {
    String message() default "身份证号码格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 