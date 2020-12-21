<template>
  <div class="managerTemplate">
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
        <a-col :span="24">
          <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
          <a-button type="primary" @click="exportEventXls"> 导出excel </a-button>
        </a-col>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <vxe-table border ref="xTable" :data="tableData" stripe align="center">
        <vxe-table-column field="timestamp" title="时间" show-overflow>
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.timestamp) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-colgroup v-for="(item, index) in headers" :key="index" :title="item.title">
          <vxe-table-column :field="item.taskId + '_sumRealFileNum'" title="准时到"></vxe-table-column>
          <vxe-table-column :field="item.taskId + '_sumLateNum'" title="晚到"></vxe-table-column>
          <vxe-table-column :field="item.taskId + '_sumFileNum'" title="应到"></vxe-table-column>
          <vxe-table-column :field="item.taskId + '_sumRealFileSize'" title="大小KB"></vxe-table-column>
          <vxe-table-column :field="item.taskId + '_toQuoteRate'" title="到报率"></vxe-table-column>
        </vxe-table-colgroup>
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
