<template>
  <div id="untreatAlarm">
    <div>
      <planeTitle titleName="未处理告警"> </planeTitle>
    </div>
    <div id="alarmContnet">
      <vxe-table :height="table_height" :data="tableData" stripe>
        <!-- border="none" -->
        <vxe-table-column field="number" title="序号"></vxe-table-column>
        <vxe-table-column field="level" title="告警级别"></vxe-table-column>
        <vxe-table-column field="firm" title="告警业务" show-overflow></vxe-table-column>
        <vxe-table-column field="startData" title="开始时间" show-overflow></vxe-table-column>
        <vxe-table-column field="sustainTime" title="持续时间" show-overflow></vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
  import planeTitle from '@/components/titile/planeTitle.vue';
  // 接口地址
  import hongtuConfig from '@/utils/services';
  export default {
    data() {
      return {
        table_height: 220,
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
            level: '2',
            firm: 'CRM客户管理系统',
            startData: '2020-04-18 12:25:36',
            sustainTime: '101天12小时25分钟',
          },
          {
            number: '4',
            level: '2',
            firm: 'CRM客户管理系统',
            startData: '2020-04-18 12:25:36',
            sustainTime: '101天12小时25分钟',
          },
          {
            number: '5',
            level: '2',
            firm: 'CRM客户管理系统',
            startData: '2020-04-18 12:25:36',
            sustainTime: '101天12小时25分钟',
          },
          {
            number: '6',
            level: '2',
            firm: 'CRM客户管理系统',
            startData: '2020-04-18 12:25:36',
            sustainTime: '101天12小时25分钟',
          },
          {
            number: '7',
            level: '2',
            firm: 'CRM客户管理系统',
            startData: '2020-04-18 12:25:36',
            sustainTime: '101天12小时25分钟',
          },
        ],
        columns: [
          {
            title: '序号',
            dataIndex: 'number',
          },
          {
            title: '告警级别',
            dataIndex: 'level',
          },
          {
            title: '告警业务',
            dataIndex: 'firm',
            ellipsis: true,
          },
          {
            title: '开始时间',
            dataIndex: 'startData',
            ellipsis: true,
          },
          {
            title: '持续时间',
            dataIndex: 'sustainTime',
            ellipsis: true,
          },
        ],
      };
    },
    name: 'untreatAlarm',
    components: { planeTitle },
    mounted() {
      hongtuConfig.getAlarm().then((res) => {
        if (res.status == 200 && res.data.code == 200) {
          this.tableData = res.data.data;
        }
      });
      this.$nextTick(() => {
        this.setTableHeight();
      });

      window.addEventListener('resize', () => {
        setTimeout(() => {
          this.setTableHeight();
        }, 500);
      });
    },
    methods: {
      setTableHeight() {
        let h = document.getElementById('alarmContnet').clientHeight;
        let computedStyle = getComputedStyle(document.getElementById('alarmContnet'), null);
        this.table_height = h - 2 * parseInt(computedStyle.paddingTop);
      },
    },
  };
</script>

<style lang="scss" scoped>
  #untreatAlarm {
    height: 4.375rem;
    width: 100%;
    margin-top: 0.375rem;
    box-shadow: $plane_shadow;

    #alarmContnet {
      padding: 0.125rem 0.0625rem 0.125rem 0.0625rem;
      width: 100%;
      height: calc(4.375rem - 0.75rem);
    }
  }
</style>
