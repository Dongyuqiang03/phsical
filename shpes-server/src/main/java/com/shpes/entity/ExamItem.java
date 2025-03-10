package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_item")
public class ExamItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String itemName;

    private String itemCode;

    private Long categoryId;

    private Long deptId;

    private String referenceLow;

    private String referenceHigh;

    private String unit;

    private String description;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}