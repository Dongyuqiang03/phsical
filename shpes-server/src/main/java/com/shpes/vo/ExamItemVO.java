package com.shpes.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer categoryId;
    
    @ApiModelProperty("分类名称")
    private String categoryName;
    
    @ApiModelProperty("执行科室ID")
    private Long deptId;
    
    @ApiModelProperty("执行科室名称")
    private String deptName;
    
    @ApiModelProperty("参考值描述")
    private String referenceValue;
    
    @ApiModelProperty("价格（分）")
    private Integer price;
    
    @ApiModelProperty("项目说明")
    private String remark;
    
    @ApiModelProperty("状态：0-禁用，1-启用")
    private Integer status;
    
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 