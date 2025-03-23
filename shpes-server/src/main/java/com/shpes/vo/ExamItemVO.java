package com.shpes.vo;

import com.shpes.common.enums.ExamCategoryEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 体检项目返回值对象
 */
@Data
@ApiModel(description = "体检项目返回值对象")
public class ExamItemVO {
    
    @ApiModelProperty("项目ID")
    private Long id;
    
    @ApiModelProperty("项目名称")
    private String name;
    
    @ApiModelProperty("项目编码")
    private String code;
    
    @ApiModelProperty("项目分类")
    private Integer category;
    
    @ApiModelProperty("分类名称")
    private String categoryName;
    
    @ApiModelProperty("执行科室ID")
    private Long departmentId;
    
    @ApiModelProperty("执行科室名称")
    private String departmentName;
    
    @ApiModelProperty("参考值描述")
    private String referenceValue;
    
    @ApiModelProperty("价格（分）")
    private Integer price;
    
    @ApiModelProperty("项目说明")
    private String remark;
    
    @ApiModelProperty("排序")
    private Integer sort;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 