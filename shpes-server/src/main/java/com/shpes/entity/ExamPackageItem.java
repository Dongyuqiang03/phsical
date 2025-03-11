package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 体检套餐项目关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_package_item")
@ApiModel("体检套餐项目关联")
public class ExamPackageItem extends BaseEntity {

    @ApiModelProperty("套餐ID")
    private Long packageId;

    @ApiModelProperty("项目ID")
    private Long itemId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("备注")
    private String remark;
}