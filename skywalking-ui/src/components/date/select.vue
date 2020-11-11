<template>
<a-row class="seletDate" type="flex" justify="center" align="middle" >
    <el-date-picker
            type="datetimerange"
            v-model="dateRange"
            :picker-options="pickerOptions"
            value-format="yyyy-MM-dd HH:mm:ss"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @blur ="changeDate"
            align="right">
    </el-date-picker>
</a-row>
</template>
<style lang="scss" scoped>
  .seletDate {
    z-index: 1;
    width: 100%;
    height: 1.0rem;
    // line-height: 1.25rem;
    font-size: 0.2rem;
    //box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    box-shadow: $plane_shadow;
    margin-bottom: 0.1rem;
  }
</style>
<script>
  export default {
    name: 'selectDate',
    data() {
      return {
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        dateRange: [this.parseTime(new Date().getTime()-3600 * 1000 * 24),this.parseTime(new Date())]
      };
    },
      created() {
        this.changeDate();
      },
      methods:{
      changeDate(){
        this.$emit('changeDate', this.dateRange)
      },
    }
  }
</script>

