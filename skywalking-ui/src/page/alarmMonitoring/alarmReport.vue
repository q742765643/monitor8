<template>
  <div class="managerTemplate">
    <div class="hasHandleExportBox">
      <selectDate @changeDate="changeDate"></selectDate>
      <a-button type="primary" @click="exportEventXls" style="margin-right: 4px"> 导出excel </a-button>
    </div>
    <div class="tableDateBox">
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons> <p style="text-align: right; margin-bottom: 0">列选择</p> </template>
        </vxe-toolbar>
      </div>
      <vxe-table show-overflow :data="tableData" align="center" highlight-hover-row ref="tablevxe">
        <vxe-table-column field="deviceType" title="类型" show-overflow></vxe-table-column>
        <vxe-table-column field="general" title="一般" show-overflow></vxe-table-column>
        <vxe-table-column field="danger" title="危险" show-overflow></vxe-table-column>
        <vxe-table-column field="fault" title="故障" show-overflow></vxe-table-column>
        <vxe-table-column field="processed" title="已处理" show-overflow></vxe-table-column>
        <vxe-table-column field="unprocessed" title="未处理" show-overflow></vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
// 接口地址
import hongtuConfig from '@/utils/services';
import request from '@/utils/request';
import selectDate from '@/components/date/select.vue';
import Qs from 'qs';
export default {
  data() {
    return {
      queryParams: {},
      tableData: [], //表格
      dateRange: [],
    };
  },
  components: { selectDate },
  created() {
    this.queryTable();
  },
  mounted() {
    this.$nextTick(() => {
      this.setTableHeight();
    });
    window.addEventListener('resize', () => {
      this.setTableHeight();
    });
  },
  methods: {
    exportEventXls() {
      let params = this.addDateRange(this.queryParams, this.dateRange);
      request({
        url: '/alarmQReport/exportExcel',
        method: 'post',
        params: params,
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    changeDate(data) {
      this.dateRange = data;
      this.queryTable();
    },
    /* table方法 */
    queryTable() {
      let params = this.addDateRange(this.queryParams, this.dateRange);
      request({
        url: '/alarmQReport/getAlarmReport',
        method: 'get',
        params: params,
      }).then((res) => {
        this.tableData = res.data;
      });
    },
    setTableHeight() {
      let h = document.getElementById('tablediv').offsetHeight;
      let padding = getComputedStyle(document.getElementById('linkManger_content'), false)['paddingTop'];
      let h_page = document.getElementById('page_table').offsetHeight;

      // let chartHeight = document.getElementById("chartdiv").clientHeight;
      this.tableheight = h + parseInt(padding) * 2 - h_page - 1;
    },
  },
};
</script>

<style lang="scss" scoped>
.managerTemplate {
}
</style>
