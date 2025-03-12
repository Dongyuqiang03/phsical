package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码返回值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "验证码返回信息")
public class CaptchaVO {
    
    @ApiModelProperty("验证码key")
    private String key;
    
    @ApiModelProperty("验证码图片（Base64编码）")
    private String image;
    
    public static CaptchaVO of(String key, String image) {
        return new CaptchaVO(key, image);
    }
} 