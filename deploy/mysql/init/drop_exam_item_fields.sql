-- 删除exam_item表中的参考值相关冗余字段
-- 只保留referenceValue字段，删除unit、lowerLimit和upperLimit字段

ALTER TABLE exam_item
DROP COLUMN lower_limit,
DROP COLUMN upper_limit,
DROP COLUMN unit; 