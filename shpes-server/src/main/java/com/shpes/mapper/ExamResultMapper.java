package com.shpes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shpes.entity.ExamResult;
import com.shpes.vo.ExamResultVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 体检结果Mapper接口
 */
@Mapper
public interface ExamResultMapper extends BaseMapper<ExamResult> {

    /**
     * 根据记录ID查询结果
     */
    List<ExamResult> selectResultsByRecordId(@Param("recordId") Long recordId);

    /**
     * 分页查询体检结果
     */
    Page<ExamResult> selectResultPage(Page<ExamResult> page,
                                     @Param("recordId") Long recordId,
                                     @Param("itemId") Long itemId,
                                     @Param("resultType") Integer resultType,
                                     @Param("reviewStatus") Integer reviewStatus);

    /**
     * 查询用户的体检记录ID列表
     */
    @Select("SELECT id FROM exam_record WHERE user_id = #{userId}")
    List<Long> selectUserRecordIds(@Param("userId") Long userId);
}