<template>
  <span>{{ formattedValue }}</span>
</template>

<script>
export default {
  name: 'DateTimeFormat',
  props: {
    value: {
      type: [String, Date],
      required: true
    },
    // 格式类型：date(年月日)、time(时分)、datetime(年月日时分秒)
    type: {
      type: String,
      default: 'date',
      validator: function(value) {
        return ['date', 'time', 'datetime'].indexOf(value) !== -1
      }
    }
  },
  computed: {
    formattedValue() {
      if (!this.value) return '';
      
      switch (this.type) {
        case 'date':
          return this.formatDate(this.value);
        case 'time':
          return this.formatTime(this.value);
        case 'datetime':
          return this.formatDateTime(this.value);
        default:
          return this.value;
      }
    }
  },
  methods: {
    formatDate(value) {
      return value;  // 后端已格式化为 yyyy-MM-dd
    },
    formatTime(value) {
      return value;  // 后端已格式化为 HH:mm
    },
    formatDateTime(value) {
      return value;  // 后端已格式化为 yyyy-MM-dd HH:mm:ss
    }
  }
}
</script>