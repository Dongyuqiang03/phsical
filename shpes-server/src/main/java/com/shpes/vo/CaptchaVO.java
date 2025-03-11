package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 验证码返回值对象
 */
@Data
@ApiModel(description = "验证码返回值对象")
public class CaptchaVO {
    
    @ApiModelProperty("验证码key")
    private String key;
    
    @ApiModelProperty("验证码图片（Base64编码）")
    private String image;
    
    public CaptchaVO(String key, String image) {
        this.key = key;
        this.image = image;
    }
} 