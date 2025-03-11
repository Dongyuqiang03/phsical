package com.shpes.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 通用分页数据封装
 */
@Data
@ApiModel(description = "通用分页数据封装")
public class CommonPage<T> {
    
    @ApiModelProperty("总记录数")
    private long total;
    
    @ApiModelProperty("当前页码")
    private long pageNum;
    
    @ApiModelProperty("每页记录数")
    private long pageSize;
    
    @ApiModelProperty("数据列表")
    private List<T> records;
    
    /**
     * 将MyBatis Plus分页结果转化为通用结果
     */
    public static <T> CommonPage<T> restPage(Page<T> pageResult) {
        CommonPage<T> result = new CommonPage<>();
        result.setTotal(pageResult.getTotal());
        result.setPageNum(pageResult.getCurrent());
        result.setPageSize(pageResult.getSize());
        result.setRecords(pageResult.getRecords());
        return result;
    }
    
    /**
     * 将数据集合转化为分页结果
     */
    public static <T> CommonPage<T> restPage(List<T> list, long total, long pageNum, long pageSize) {
        CommonPage<T> result = new CommonPage<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRecords(list);
        return result;
    }
} 