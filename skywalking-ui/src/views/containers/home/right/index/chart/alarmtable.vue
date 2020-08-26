<template>
  <div id="alarmTab">
    <!-- <el-table :data="tableData" size="mini" stripe style="width: 100%" align="center">
      <el-table-column prop="number" label="序号" width="50"></el-table-column>
      <el-table-column prop="level" label="告警级别" width="100"></el-table-column>
      <el-table-column prop="firm" label="告警业务"></el-table-column>
      <el-table-column prop="startData" label="开始时间"></el-table-column>
      <el-table-column prop="sustainTime" label="持续时间"></el-table-column>
    </el-table>-->
    <div class="title">
      <span>序号</span>
      <span>告警级别</span>
      <span>告警业务</span>
      <span>开始时间</span>
      <span>持续时间</span>
    </div>
    <div class="cell">
      <div class="data" v-for="(item,index) in  tableData" :key="index">
        <span>{{item.number}}</span>
        <span>{{item.level}}</span>
        <span>{{item.firm}}</span>
        <span>{{item.startData}}</span>
        <span>{{item.sustainTime}}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [
        {
          number: '1',
          level: '1',
          firm: 'CRM客户管理系统',
          startData: '2020-04-18 12:25:36',
          sustainTime: '101天12小时25分钟',
        },
        {
          number: '2',
          level: '2',
          firm: 'CRM客户管理系统',
          startData: '2020-04-18 12:25:36',
          sustainTime: '101天12小时25分钟',
        },
        {
          number: '3',
          level: '3',
          firm: 'CRM客户管理系统',
          startData: '2020-04-18 12:25:36',
          sustainTime: '101天12小时25分钟',
        },
        {
          number: '4',
          level: '4',
          firm: 'CRM客户管理系统',
          startData: '2020-04-18 12:25:36',
          sustainTime: '101天12小时25分钟',
        },
        {
          number: '5',
          level: '5',
          firm: 'CRM客户管理系统',
          startData: '2020-04-18 12:25:36',
          sustainTime: '101天12小时25分钟',
        },
        {
          number: '6',
          level: '6',
          firm: 'CRM客户管理系统',
          startData: '2020-04-18 12:25:36',
          sustainTime: '101天12小时25分钟',
        },
      ],
    };
  },
};
</script>

<style lang="scss" scoped>
#alarmTab {
  padding: 0.1rem 0.2rem;
  height: 3.625rem;
  width: 9.5875rem;
  display: block;
  overflow: hidden;

  .table {
    width: 9.5875rem;
    width: 100%;
    display: flex;
    span {
      color: '#000';

      font-size: 0.2rem;
      /*  font-weight: 600; */
      display: block;
      text-align: center;
    }
    span:nth-child(-n + 2) {
      flex: 1;
    }
    span:nth-child(n + 2) {
      flex: 2;
    }
  }

  .title {
    @extend .table;
    font-weight: 600;
    line-height: 0.4rem;
    background: rgba(201, 199, 199, 0.2);
    span {
      height: 0.4rem;
    }
  }
  .cell {
    div:nth-child(2n) {
      background: #eef5fd;
    }
    .data {
      &:hover {
        background: rgba(201, 199, 199, 0.2);
      }
      line-height: 0.4rem;
      @extend .table;
      span {
        line-height: 0.5rem;
      }
    }
  }
}
</style>

<style >
.el-table th,
.el-table td {
  text-align: center !important;
}
</style>

