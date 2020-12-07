<template>
  <a-row class="queryForm">
    <el-date-picker
      type="datetimerange"
      v-model="dateRange"
      :picker-options="pickerOptions"
      value-format="yyyy-MM-dd HH:mm:ss"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      @blur="changeDate"
      align="right"
    >
    </el-date-picker>
  </a-row>
</template>
<style lang="scss" scoped></style>
<script>
  export default {
    name: 'selectDate',
    data() {
      return {
        pickerOptions: {
          shortcuts: [
            {
              text: '最近一周',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
                picker.$emit('pick', [start, end]);
              },
            },
            {
              text: '最近一个月',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
                picker.$emit('pick', [start, end]);
              },
            },
            {
              text: '最近三个月',
              onClick(picker) {
                const end = new Date();
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
                picker.$emit('pick', [start, end]);
              },
            },
          ],
        },
        dateRange: [this.parseTime(new Date().getTime() - 3600 * 1000 * 24), this.parseTime(new Date())],
      };
    },
    created() {
      this.changeDate();
    },
    methods: {
      changeDate() {
        this.$emit('changeDate', this.dateRange);
      },
    },
  };
</script>
