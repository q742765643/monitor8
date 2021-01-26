<template>
  <a-row class="queryForm">
    <!-- <el-date-picker
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
    </el-date-picker> -->
    <el-date-picker
      type="daterange"
      v-model="dateRange"
      :picker-options="pickerOptions"
      range-separator="至"
      value-format="yyyy-MM-dd"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      @blur="changeDate"
      align="right"
      style="width: 370px"
    >
    </el-date-picker>
  </a-row>
</template>
<style lang="scss" scoped></style>
<script>
import { parseDate } from '@/components/util';
export default {
  name: 'selectDate',
  props: {
    handleRange: {
      type: Number,
    },
  },
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
              picker.$emit('pick', [parseDate(start) + ' 00:00:00', parseDate(end) + ' 23:59:59']);
              // picker.$emit('pick', [start, end]);
            },
          },
          {
            text: '最近一个月',
            onClick(picker) {
              const end = parseDate(new Date()) + ' 00:00:00';
              const start = parseDate(new Date()) + ' 00:00:00';
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [parseDate(start) + ' 00:00:00', parseDate(end) + ' 23:59:59']);
            },
          },
          {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [parseDate(start) + ' 00:00:00', parseDate(end) + ' 23:59:59']);
            },
          },
        ],
      },
      dateRange: [parseDate(new Date()) + ' 00:00:00', parseDate(new Date()) + ' 23:59:59'],
      // dateRange: [this.parseTime(new Date().getTime() - 3600 * 1000 * 24), this.parseTime(new Date())],
    };
  },
  created() {
    console.log(this.handleRange);
    if (this.handleRange) {
      const end = new Date();
      const start = new Date();
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
      this.dateRange = [parseDate(start) + ' 00:00:00', parseDate(end) + ' 23:59:59'];
    }
    this.changeDate();
    // console.log(this.dateRange)
  },
  methods: {
    changeDate() {
      console.log('123');
      if (this.dateRange[0].indexOf('00:00:00') == '-1') {
        this.dateRange[0] += ' 00:00:00';
        this.dateRange[1] += ' 23:59:59';
      }
      console.log(this.dateRange);
      this.$emit('changeDate', this.dateRange);
    },
  },
};
</script>
