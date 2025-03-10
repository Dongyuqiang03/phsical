package com.shpes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_package_item")
public class ExamPackageItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long packageId;

    private Long itemId;

    private LocalDateTime createTime;
}