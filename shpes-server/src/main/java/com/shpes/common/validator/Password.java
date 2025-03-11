package com.shpes.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 密码强度校验注解
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "密码不符合规则要求";
    
    /**
     * 最小长度，默认6
     */
    int minLength() default 6;
    
    /**
     * 最大长度，默认20
     */
    int maxLength() default 20;
    
    /**
     * 是否需要包含数字，默认true
     */
    boolean requireDigit() default true;
    
    /**
     * 是否需要包含小写字母，默认true
     */
    boolean requireLowercase() default true;
    
    /**
     * 是否需要包含大写字母，默认true
     */
    boolean requireUppercase() default true;
    
    /**
     * 是否需要包含特殊字符，默认true
     */
    boolean requireSpecialChar() default true;
    
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 