<template>
  <div id="untreatAlarm">
    <div>
      <planeTitle titleName="未处理告警"> </planeTitle>
    </div>
    <div id="alarmContnet">
      <vxe-table :height="table_height" :data="tableData" stripe class="alarmTable">
        <!-- border="none" -->
        <vxe-table-column field="number" type="index" title="序号" width="10%"></vxe-table-column>
        <vxe-table-column field="level" title="告警级别" :formatter="formatAlarmLevel" width="15%"></vxe-table-column>
        <vxe-table-column field="message" title="告警业务" show-overflow></vxe-table-column>
        <vxe-table-column field="timestamp" title="开始时间" show-overflow>
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.timestamp) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="duration" title="持续时间" show-overflow></vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
import planeTitle from '@/components/titile/planeTitle.vue';
import request from '@/utils/request';
export default {
  data() {
    return {
      table_height: 220,
      tableData: [],
      alarmLevelOptions: [],
    };
  },
  name: 'untreatAlarm',
  components: { planeTitle },
  created() {
    this.getDicts('alarm_level').then((response) => {
      this.alarmLevelOptions = response.data;
    });
  },
  mounted() {
    setTimeout(() => {
      this.fetch();
      this.setTableHeight();
    }, 500);
    window.addEventListener('resize', () => {
      setTimeout(() => {
        this.setTableHeight();
      }, 500);
    });
  },
  methods: {
    formatAlarmLevel({ cellValue }) {
      return this.selectDictLabel(this.alarmLevelOptions, cellValue);
    },
    fetch() {
      request({
        url: '/main/getAlarm',
        method: 'get',
      }).then((data) => {
        this.tableData = data.data;
      });
    },
    setTableHeight() {
      let h = document.getElementById('alarmContnet').clientHeight;
      this.table_height = h;
    },
  },
};
</script>

<style lang="scss" scoped>
#untreatAlarm {
  width: 100%;
  height: calc(34% - 30px);
  margin-top: 20px;
  box-shadow: $plane_shadow;

  #alarmContnet {
    padding: 0 5px;
    width: 100%;
    height: calc(100% - 86px);
    ::-webkit-scrollbar {
      width: 3px;
      background-color: #f5f5f5;
    }
    ::-webkit-scrollbar-thumb {
      // background-color: #5aa6ee;
    }
    ::-webkit-scrollbar-track {
      box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.3);
      background-color: #f5f5f5;
    }
  }
}
</style>
