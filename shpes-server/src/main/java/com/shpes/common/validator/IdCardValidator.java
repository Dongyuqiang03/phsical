package com.shpes.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 身份证号码校验器
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    @Override
    public void initialize(IdCard constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!ID_CARD_PATTERN.matcher(value).matches()) {
            return false;
        }
        // 校验身份证号码的校验码
        return checkIdCardVerifyCode(value);
    }

    /**
     * 校验身份证号码的校验码
     */
    private boolean checkIdCardVerifyCode(String idCard) {
        char[] chars = idCard.toUpperCase().toCharArray();
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] verifyCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (chars[i] - '0') * weights[i];
        }
        
        int mod = sum % 11;
        return chars[17] == verifyCode[mod];
    }
} 