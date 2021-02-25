<template>
  <div class="managerTemplate">
    <div class="hasHandleExportBox">
      <selectDate @changeDate="onTimeChange"></selectDate>
      <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" @click="exportEventXls"> 导出excel </a-button>
        </a-col>
      </a-row>
    </div>
    <div class="tableDateBox">
      <vxe-table border ref="xTable" :data="tableData" stripe align="center">
        <vxe-table-column field="timestamp" title="时间" >
            <template slot-scope="scope">
              <span v-if="scope.row.timestamp == '合计'">{{ scope.row.timestamp }}</span>
              <span v-if="scope.row.timestamp !== '合计'">{{ parseTime(scope.row.timestamp, '{y}-{m}-{d}') }}</span>
            </template>
        </vxe-table-column>
        <vxe-table-column  v-for="(item, index) in headers" :key="index" :title="item.title" :field="item.taskId + '_toQuoteRate'">
          <!--  <vxe-table-column :field="item.taskId + '_sumRealFileNum'" title="准时到"></vxe-table-column>
           <vxe-table-column :field="item.taskId + '_sumLateNum'" title="晚到"></vxe-table-column>
           <vxe-table-column :field="item.taskId + '_sumFileNum'" title="应到"></vxe-table-column>
           <vxe-table-column :field="item.taskId + '_sumRealFileSize'" title="大小KB"></vxe-table-column>
          <vxe-table-column :field="item.taskId + '_toQuoteRate'" title="到报率"></vxe-table-column>-->
        </vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
import moment from 'moment';
export default {
  data() {
    return {
      tableData: [],
      headers: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        beginTime: '',
        endTime: '',
      },
      form: {},
      visible: false,
    };
  },
  created() {
    this.fetch();
  },
  mounted() {
    this.findHeader();
  },
  computed: {},
  methods: {
    findHeader() {
      request({
        url: '/fileQReport/findHeader',
        method: 'get',
      }).then((data) => {
        this.headers = data.data;
      });
    },
    statusFormat(status) {
      return this.selectDictLabel(this.statusOptions, status);
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.fetch();
    },
    resetQuery() {
      this.dateRange = [];
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    exportEventXls() {
      request({
        url: '/fileQReport/exportFileReport',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    moment,
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },
    onTimeChange(value, dateString) {
      this.queryParams.beginTime = dateString[0];
      this.queryParams.endTime = dateString[1];
    },
    fetch() {
      request({
        url: '/fileQReport/findFileReport',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.tableData = data.data;
      });
    },
  },
};
</script>
<style scoped></style>
