package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 体检项目实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_item")
@ApiModel("体检项目")
public class ExamItem extends BaseEntity {

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目编码")
    private String code;

    @ApiModelProperty("项目分类")
    private Integer category;

    @ApiModelProperty("执行科室ID")
    private Long departmentId;

    @ApiModelProperty("参考值描述")
    private String referenceValue;

    @ApiModelProperty("价格（分）")
    private Integer price;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}