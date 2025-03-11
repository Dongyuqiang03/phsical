package com.shpes.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 邮箱校验注解
 */
@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    String message() default "邮箱格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 