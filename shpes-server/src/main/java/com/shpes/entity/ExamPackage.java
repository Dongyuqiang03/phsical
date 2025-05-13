package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 体检套餐实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_package")
@ApiModel("体检套餐")
public class ExamPackage extends BaseEntity {

    @ApiModelProperty("套餐名称")
    private String name;

    @ApiModelProperty("套餐编码")
    private String code;

    @ApiModelProperty("套餐描述")
    private String description;

    @ApiModelProperty("套餐价格（分）")
    private Integer price=0;

    @ApiModelProperty("原价（分）")
    private Integer originalPrice=0;

    @ApiModelProperty("适用性别：0-不限，1-男，2-女")
    private Integer gender=0;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}