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
      <span>告警设备</span>
      <span>告警信息</span>
      <span>告警时间</span>
      <span>告警IP</span>
      <!--   <span>持续时间</span> -->
    </div>
    <div class="cell">
      <div class="data" v-for="(item, index) in tableData" :key="index">
        <span>{{ index }}</span>
        <span>{{ item.level | toLevelName }}</span>
        <span>{{ item.deviceType | toDeviceName }}</span>
        <span>{{ item.message }}</span>
        <span>{{ item.timestamp | toDate }}</span>
        <span>{{ item.ip }}</span>

        <!--   <span>{{item.sustainTime}}</span> -->
      </div>
    </div>
  </div>
</template>

<script>
  import moment from 'moment';
  import services from '@/utils/services';
  export default {
    data() {
      return {
        tableData: [],
      };
    },
    created() {
      services.getAlarm().then((res) => {
        if ((res.status == 200) & (res.data.code == 200)) {
          this.tableData = res.data.data;
        }
      });
      // this.tableData = [];
    },
    filters: {
      toDate: function(timestamp) {
        return moment(parseInt(timestamp)).format('YYYY-MM-DD HH:mm:ss');
      },
      toDeviceName: function(deviceType) {
        //0 服务器 1网络设备 2进程 3文件
        switch (parseInt(deviceType)) {
          case 0:
            return '服务器';
            break;
          case 1:
            return '网络设备';
            break;
          case 2:
            return '进程';
            break;
          case 3:
            return '文件';
            break;

          default:
            break;
        }
      },
      toLevelName: function(level) {
        // 0 一般 1危险 2故障 3 正常
        switch (parseInt(level)) {
          case 0:
            return '一般';
            break;
          case 1:
            return '危险';
            break;
          case 2:
            return '故障';
            break;
          case 3:
            return '正常';
            break;

          default:
            break;
        }
      },
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
      span:nth-child(-n + 3) {
        flex: 1;
      }
      span:nth-child(n + 3) {
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

<style>
  .el-table th,
  .el-table td {
    text-align: center !important;
  }
</style>
