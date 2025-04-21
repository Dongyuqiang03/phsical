package com.shpes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 体检套餐返回值对象
 */
@Data
@ApiModel(description = "体检套餐返回值对象")
public class ExamPackageVO {
    
    @ApiModelProperty("套餐ID")
    private Long id;
    
    @ApiModelProperty("套餐名称")
    private String name;
    
    @ApiModelProperty("套餐编码")
    private String code;
    
    @ApiModelProperty("套餐类型")
    private String type;
    
    @ApiModelProperty("适用性别：0-不限，1-男，2-女")
    private Integer gender;
    
    @ApiModelProperty("套餐价格")
    private BigDecimal price;
    
    @ApiModelProperty("原价")
    private BigDecimal originalPrice;
    
    @ApiModelProperty("折扣")
    private BigDecimal discount;
    
    @ApiModelProperty("套餐描述")
    private String description;
    
    @ApiModelProperty("注意事项")
    private String notice;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("项目数量")
    private Integer itemCount;
    
    @ApiModelProperty("体检项目列表")
    private List<ExamItemVO> items;
    
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 