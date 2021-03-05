<template lang="html">
<el-dialog
      title="Cron表达式"
      :visible.sync="cronDialogVisible"
      :append-to-body="true"
      width="660px"
      max-height="500px"
      top="1vh"
      :close-on-click-modal="false"
      :show-close="false"
    >
  <div class="cron selScrollBar" :val="value_">
        <second-and-minute v-model="sVal" lable="秒"></second-and-minute >
        <second-and-minute ref="mChild" v-model="mVal" lable="分"></second-and-minute >
        <hour ref="hChild" v-model="hVal" lable="时"></hour>
        <day ref="dChild" v-model="dVal" lable="日"></day>
        <month ref="monthChild" v-model="monthVal" lable="月"></month>
        <week ref="weekChild" v-model="weekVal" lable="周"></week>
        <year v-model="yearVal" lable="年"></year>
    <!-- table -->
     <div v-if="false" class="cronVal"><label>当前cron表达式：</label><span>{{value_}}</span></div>
     <!-- <el-input class="cronVal" :readonly="true" size="small" v-model="value_" auto-complete="off"></el-input> -->
  </div>
  <div class="el-icon-info" style="color:#E6A23C;">注：【间隔】只能选中一个</div>
  <el-table
       :data="tableData"
       size="mini"
       border
       class="cronTable">
       <el-table-column
         prop="sVal"
         label="秒"
         width="70">
       </el-table-column>
       <el-table-column
         prop="mVal"
         label="分"
         width="70">
       </el-table-column>
       <el-table-column
         prop="hVal"
         label="时"
         width="70">
       </el-table-column>
       <el-table-column
         prop="dVal"
         label="日"
         width="70">
       </el-table-column>
       <el-table-column
         prop="monthVal"
         label="月"
         width="70">
       </el-table-column>
       <el-table-column
         prop="weekVal"
         label="周"
         width="70">
       </el-table-column>
       <el-table-column
         prop="yearVal"
         label="年">
       </el-table-column>
     </el-table>
  <div class="dialogFooter">
    <el-button type="primary" size="small" @click="cronSure">确定</el-button>
    <el-button size="small" @click="cronCancel">取消</el-button>
  </div>
  </el-dialog>
</template>

<script>
import SecondAndMinute from './secondAndMinute';
import hour from './hour';
import day from './day';
import month from './month';
import week from './week';
import year from './year';
import hongtuConfig from '@/utils/services';

export default {
  props: {
    oldCron: {
      type: String,
    },
    value: {
      type: String,
    },
    cronDialogVisible: {
      type: Boolean,
    },
  },
  data() {
    return {
      sVal: '',
      mVal: '',
      hVal: '',
      dVal: '',
      monthVal: '',
      weekVal: '',
      yearVal: '',
      cronExpression: '',
    };
  },
  watch: {
    value(a, b) {
      this.updateVal();
    },
  },
  computed: {
    tableData() {
      return [
        {
          sVal: this.sVal,
          mVal: this.mVal,
          hVal: this.hVal,
          dVal: this.dVal,
          monthVal: this.monthVal,
          weekVal: this.weekVal,
          yearVal: this.yearVal,
        },
      ];
    },
    value_() {
      if (!this.dVal && !this.weekVal) {
        return '';
      }
      // if (this.dVal !== "?" && this.weekVal !== "?") {
      //   this.$notify({
      //     title: "提示",
      //     type: "warning",
      //     message: "日期与星期必须有一个为“不指定”"
      //   });
      //   return;
      // }
      //秒  选择了间隔   其他的时间单位不可选择间隔
      let timeArr = ['s', 'm', 'h', 'd', 'month', 'week'];
      let intervalArr = [];
      if (this.sVal.includes('/')) {
        intervalArr.push(0);
      }
      if (this.mVal.includes('/')) {
        intervalArr.push(1);
      }
      if (this.hVal.includes('/')) {
        intervalArr.push(2);
      }
      if (this.dVal.includes('/')) {
        intervalArr.push(3);
      }
      if (this.monthVal.includes('/')) {
        intervalArr.push(4);
      }
      if (this.weekVal.includes('-')) {
        intervalArr.push(5);
      }
      //选择了两个间隔
      if (intervalArr.length >= 2) {
        this.$notify({
          title: '提示',
          type: 'warning',
          message: '已存在间隔',
        });
        this.$refs[timeArr[intervalArr[1]] + 'Child'].setChange();
        return;
      }
      let v = `${this.sVal} ${this.mVal} ${this.hVal} ${this.dVal} ${this.monthVal} ${this.weekVal} ${this.yearVal}`;
      if (v !== this.value) {
        this.$emit('input', v);
      }
      this.cronExpression = v;
      return v;
    },
  },
  methods: {
    updateVal() {
      if (!this.value) {
        return;
      }
      let arrays = this.value.split(' ');
      this.sVal = arrays[0];
      this.mVal = arrays[1];
      this.hVal = arrays[2];
      this.dVal = arrays[3];
      this.monthVal = arrays[4];
      this.weekVal = arrays[5];
      this.yearVal = arrays[6] === 'undefined' ? '' : arrays[6];
    },
    // cron表达式确定
    cronSure() {
      //对特殊字符进行编码
      hongtuConfig
        .getNextTime({
          cronExpression: this.cronExpression.split(' ?')[0] + ' ?',
        })
        .then(({ data }) => {
          if (data && data.length > 0) {
            // this.$emit('closeCronPopover', this.cronExpression);
            let fiveHtml = '<ul>';
            const resData = data;
            resData.forEach((single) => {
              fiveHtml += '<li>' + single + '</li>';
            });
            fiveHtml += '</ul>';
            this.$alert(fiveHtml, 'cron表达式的5次执行时间', {
              dangerouslyUseHTMLString: true,
              closeOnClickModal: false,
            })
              .then(() => {
                //  设置cron表达式的值   关闭弹出层
                this.$emit('setCron', this.cronExpression);
                this.$message.success('保存成功');
              })
              .catch(() => {
                this.$notify({
                  title: '提示',
                  type: 'info',
                  message: '重新选择',
                });
              });
          } else {
            this.$notify({
              title: '提示',
              type: 'error',
              message: '选择的cron表达式不存在五次执行时间，请重新选择',
            });
          }
        })
        .catch((error) => {
          console.log(error);
        });
    },
    // cron表达式取消
    cronCancel() {
      this.$emit('closeCron');
    },
  },
  created() {
    this.updateVal();
  },
  components: {
    SecondAndMinute,
    hour,
    day,
    month,
    week,
    year,
  },
};
</script>

<style lang="scss">
.cron {
  text-align: left;
  padding: 10px;
  background: #fff;
  border: 1px solid #dcdfe6;
  height: 386px;
  overflow-y: auto;
  margin-bottom: 10px;
  .el-tabs__content {
    line-height: 30px;
  }
  .el-checkbox {
    margin-right: 0px;
  }
  .el-table th {
    padding: 4px 0;
    line-height: 0px;
  }
  .singleCon {
    margin-bottom: 10px;
    border-bottom: 1px dashed #ccc4c4;
    border: 1px solid #ddd;
    .timeTitle {
      background: #c9eef9;
      padding: 5px;
    }
    .timeCon {
      padding: 10px;
    }
  }
}
.cronTable {
  width: 100%;
  margin-bottom: 10px;
  th,
  td {
    padding: 2px 0;
  }
}
</style>
