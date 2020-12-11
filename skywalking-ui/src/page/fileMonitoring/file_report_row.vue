<template>
  <div class="fileReportRowTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm" ref="queryForm">
      <a-form-model-item label="时间">
        <a-range-picker
          @change="onTimeChange"
          :show-time="{
            hideDisabledOptions: true,
            defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('11:59:59', 'HH:mm:ss')],
          }"
          format="YYYY-MM-DD HH:mm:ss"
        />
      </a-form-model-item>
      <a-form-model-item>
          <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
          <a-button type="primary" class="dcBtn" @click="exportEventXls"> 导出excel </a-button>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <vxe-table border ref="xTable" :merge-cells="mergeCells" :data="tableData" resizable stripe align="center">
        <vxe-table-column field="taskName" title="名称"></vxe-table-column>
        <vxe-table-column field="timestamp" title="时间" show-overflow>
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.timestamp) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="sumRealFileNum" title="准时到"></vxe-table-column>
        <vxe-table-column field="sumLateNum" title="晚到"></vxe-table-column>
        <vxe-table-column field="sumFileNum" title="应到"></vxe-table-column>
        <vxe-table-column field="sumRealFileSize" title="大小KB"></vxe-table-column>
        <vxe-table-column field="toQuoteRate" title="到报率"></vxe-table-column>
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
      mergeCells: [],
      headers: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        // beginTime: '',
        // endTime: '',
      },
      form: {},
      visible: false,
    };
  },
  created() {
    this.fetch();
  },
  mounted() {
    //this.findHeader();
  },
  computed: {},
  methods: {
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
      //this.queryParams.pageNum = 1;
      this.fetch();
    },
    resetQuery() {
      this.dateRange = [];
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    exportEventXls() {
      request({
        url: '/fileQReport/exportFileReportRow',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    fetch() {
      request({
        url: '/fileQReport/findFileReportRow',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.tableData = data.data.tableData;
        this.mergeCells = data.data.mergeCells;
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.dcBtn {
  margin-left: 10px;
}
</style>
