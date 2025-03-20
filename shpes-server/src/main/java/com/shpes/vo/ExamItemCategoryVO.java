package com.shpes.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "体检项目分类VO")
public class ExamItemCategoryVO {
    @ApiModelProperty("分类ID")
    private Long id;
    
    @ApiModelProperty("分类名称")
    private String name;
    
    @ApiModelProperty("分类编码")
    private String code;
    
    @ApiModelProperty("状态(0:禁用 1:启用)")
    private Integer status;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 