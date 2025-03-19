/**
 * 日期时间格式化工具函数
 */

/**
 * 格式化后端返回的日期时间数组
 * @param {Array|String} dateTime 可以是日期时间数组 [year, month, day, hour, minute, second] 或普通日期字符串
 * @param {String} format 格式化模板，默认为 'YYYY-MM-DD HH:mm:ss'
 * @returns {String} 格式化后的日期时间字符串
 */
export function formatDateTime(dateTime, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!dateTime) return '未知时间';
  
  let year, month, day, hour, minute, second;
  
  // 处理数组格式 [2025, 3, 18, 22, 42, 3]
  if (Array.isArray(dateTime)) {
    [year, month, day, hour, minute, second] = dateTime;
  } 
  // 处理Date对象
  else if (dateTime instanceof Date) {
    year = dateTime.getFullYear();
    month = dateTime.getMonth() + 1;
    day = dateTime.getDate();
    hour = dateTime.getHours();
    minute = dateTime.getMinutes();
    second = dateTime.getSeconds();
  } 
  // 处理普通字符串或其他情况
  else {
    return dateTime;
  }
  
  // 格式化成两位数
  const pad = (num) => String(num).padStart(2, '0');
  
  // 替换格式字符串
  format = format
    .replace('YYYY', year)
    .replace('MM', pad(month))
    .replace('DD', pad(day))
    .replace('HH', pad(hour))
    .replace('mm', pad(minute))
    .replace('ss', pad(second));
    
  return format;
}

/**
 * 格式化日期字符串
 * @param {String} date 日期字符串，如 '2025-03-18'
 * @param {String} format 格式化模板，默认为 'YYYY-MM-DD'
 * @returns {String} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  
  // 如果已经是字符串格式，直接返回
  if (typeof date === 'string') {
    return date;
  }
  
  return formatDateTime(date, format);
} 