<template>
  <div class="managerTemplate">
    <div class="hasHandleExportBox">
      <a-row>
        <a-col :span="24" class="flexColClass">
          <span class="spanLabel"> 资料时间 </span>
          <selectDate @changeDate="onTimeChange" :handleDiffRange="7"></selectDate>
        </a-col>
        <a-col :span="24" class="flexColClass">
          <span class="spanLabel"> 资料任务 </span>
          <a-select
            style="min-width: 370px"
            mode="multiple"
            class="marSelect"
            v-model="queryParams.taskIds"
            placeholder="资料任务"
          >
            <a-select-option v-for="host in taskIds" :key="host.taskId">
              {{ host.title }}
            </a-select-option>
          </a-select>
        </a-col>
      </a-row>
      <div>
        <a-button type="primary" html-type="submit" style="margin-right: 10px" @click="handleQuery"> 搜索 </a-button>
        <a-button type="primary" @click="exportEventXls" style="margin-right: 10px"> 导出excel </a-button>
        <a-button type="primary" @click="exportEventPdf" style="margin-right: 10px"> 导出pdf </a-button>
      </div>
    </div>
    <div class="tableDateBox">
      <div class="fileTitleClass">{{ fileTitle }}</div>
      <vxe-table border ref="xTable" :data="tableData" stripe align="center" :merge-cells="mergeCells">
        <vxe-table-column field="timestamp" title="时间">
          <template slot-scope="scope">
            <span v-if="scope.row.timestamp == '(日期)平均' || scope.row.timestamp == '结论'">{{
              scope.row.timestamp
            }}</span>
            <span v-if="scope.row.timestamp !== '(日期)平均' && scope.row.timestamp != '结论'">{{
              parseTime(scope.row.timestamp, '{y}-{m}-{d}')
            }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column v-for="(item, index) in headers" :key="index" :title="item.title" :field="item.taskId">
        </vxe-table-column>
        <vxe-table-column field="hjC" title="(类别)平均" show-overflow> </vxe-table-column>
      </vxe-table>
    </div>

    <div class="tableDateBox">
      <div class="fileTitleClass">{{ infoTitle }}</div>
      <vxe-table border ref="xTable" :data="tableDataDetail" stripe align="center">
        <vxe-table-column field="taskName" title="资料名称"></vxe-table-column>
        <vxe-table-column field="ddataTime" title="资料时间">
          <template slot-scope="scope">
            <span v-if="null != scope.row.ddataTime">{{ parseTime(scope.row.ddataTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="startTimeA" title="采集时间">
          <template slot-scope="scope">
            <span v-if="null != scope.row.startTimeA">{{ parseTime(scope.row.startTimeA) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="fileNum" title="应到(个)" width="80"></vxe-table-column>
        <vxe-table-column field="realFileNum" title="实到(个)" width="80"> </vxe-table-column>
        <vxe-table-column field="lateNum" title="迟到(个)" width="80"></vxe-table-column>
        <vxe-table-column field="fileSize" title="应到大小(KB)" width="80"></vxe-table-column>
        <vxe-table-column field="realFileSize" title="实到大小(KB)" width="80"></vxe-table-column>
        <vxe-table-column field="errorReason" title="原因" width="80" show-overflow></vxe-table-column>
        <vxe-table-column field="remark" title="值班员备注" width="80" show-overflow></vxe-table-column>
        <vxe-table-column width="120" field="date" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="handleEdit(row)"> 编辑 </a-button>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <a-modal
      v-model="visibleModel"
      title="修改"
      @ok="handleOk"
      okText="确定"
      cancelText="取消"
      width="45%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox fileMonitoringdialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 5 }"
        :wrapperCol="{ span: 18 }"
        :model="formDialog"
        ref="formModel"
      >
        <a-row>
          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }" label="原因" prop="errorReason">
              <a-input type="textarea" v-model="formDialog.errorReason" style="width: 100%"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }" label="值班员备注" prop="remark">
              <a-input type="textarea" v-model="formDialog.remark" style="width: 100%"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import request from '@/utils/request';
import moment from 'moment';
import selectDate from '@/components/date/select.vue';
var XEUtils = require('xe-utils');
export default {
  data() {
    return {
      tableData: [],
      headers: [],
      taskIds: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        startTime: '',
        endTime: '',
        taskIds: [],
      },
      form: {},
      tableDataDetail: [],
      mergeCells: [],
      visible: false,
      visibleModel: false,
      formDialog: {
        id: '',
        errorReason: '',
        remark: '',
      },
      fileTitle: '',
      infoTitle: '',
    };
  },
  created() {
    //this.fetch();
  },
  mounted() {
    this.findHeader();
  },
  components: { selectDate },
  methods: {
    findHeader() {
      request({
        url: '/fileQReport/findHeader',
        method: 'get',
      }).then((data) => {
        this.taskIds = data.data;
      });
    },
    statusFormat(status) {
      return this.selectDictLabel(this.statusOptions, status);
    },
    handleQuery() {
      this.headers = [];
      let that = this;
      for (let i = 0; i < this.queryParams.taskIds.length; i++) {
        let taskId = this.queryParams.taskIds[i];
        that.taskIds.forEach(function (item, index) {
          if (taskId == item.taskId) {
            that.headers.push(item);
          }
        });
      }

      this.fetch();
      this.selectPageListDetail();
      let start = this.queryParams.startTime.slice(0, 10);
      let end = this.queryParams.endTime.slice(0, 10);
      start = start.replace(/-/g, '/');
      end = end.replace(/-/g, '/');
      this.fileTitle = '(' + start + '至' + end + ')' + ' 资料监视文件到报率报告';
      this.infoTitle = '(' + start + '至' + end + ')' + ' 资料缺报详情报告';
    },
    handleEdit(row) {
      this.formDialog.id = row.id;
      this.formDialog.remark = row.remark;
      this.formDialog.errorReason = row.errorReason;
      this.visibleModel = true;
    },
    handleOk() {
      request({
        url: '/fileQReport/updateDetail',
        method: 'post',
        data: this.formDialog,
      }).then((res) => {
        this.$message.success('修改成功');
        this.selectPageListDetail();
        this.visibleModel = false;
      });
    },
    resetQuery() {
      this.dateRange = [];
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    exportEventXls() {
      request({
        url: '/fileQReport/exportFileReport',
        method: 'post',
        data: this.addDateRange(this.queryParams, this.dateRange),
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    exportEventPdf() {
      request({
        url: '/fileQReport/exportFileReportPdf',
        method: 'post',
        data: this.addDateRange(this.queryParams, this.dateRange),
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
    onTimeChange(value) {
      this.queryParams.startTime = value[0];
      this.queryParams.endTime = value[1];
    },
    selectPageListDetail() {
      request({
        url: '/fileQReport/selectPageListDetail',
        method: 'post',
        data: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.tableDataDetail = data.data.tableData;
      });
    },
    fetch() {
      request({
        url: '/fileQReport/findFileReport',
        method: 'post',
        data: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.mergeCells = data.data.mergeCells;
        this.tableData = data.data.tableData;
      });
    },
    /* colspanMethod({ _rowIndex, _columnIndex }) {
     
    }, */
  },
};
</script>
<style scoped >
.fileTitleClass {
  text-align: center;
  font-size: 18px;
  font-weight: 500;
}
.marSelect {
  max-width: calc(100% - 80px);
}
.hasHandleExportBox .ant-row {
  width: 70%;
}
</style>
