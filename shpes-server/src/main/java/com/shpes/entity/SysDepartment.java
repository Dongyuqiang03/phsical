package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
@ApiModel("部门")
public class SysDepartment extends BaseEntity {

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("部门编码")
    private String code;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}