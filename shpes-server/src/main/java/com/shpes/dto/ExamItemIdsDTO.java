package com.shpes.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 体检项目ID列表数据传输对象
 */
@ApiModel(description = "体检项目ID列表")
public class ExamItemIdsDTO {
    
    @ApiModelProperty(value = "体检项目ID列表", required = true)
    private List<Long> itemIds;
    
    public List<Long> getItemIds() {
        return itemIds;
    }
    
    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }
} 