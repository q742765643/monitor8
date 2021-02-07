<template>
  <div class="unAlarmTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="告警事件类型">
        <a-select style="width: 120px" v-model="queryParams.monitorType" placeholder="请选择">
          <a-select-option v-for="dict in alarmTypeOptions" :key="dict.dictValue" :value="dict.dictValue">
            {{ dict.dictLabel }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="告警事件IP">
        <a-input v-model="queryParams.ip" placeholder="请输入IP"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="告警级别">
        <a-select style="width: 120px" v-model="queryParams.level" placeholder="请选择">
          <a-select-option v-for="dict in alarmLevelOptions" :key="dict.dictValue" :value="dict.dictValue">
            {{ dict.dictLabel }}
          </a-select-option>
        </a-select>
      </a-form-model-item>

      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons> <p style="text-align: right; margin-bottom: 0">列选择</p> </template>
        </vxe-toolbar>
      </div>
      <vxe-table border ref="xTable" :data="tableData" stripe align="center">
        <vxe-table-column field="monitorType" title="告警事件类型" :formatter="formatMonitorType"></vxe-table-column>
        <vxe-table-column field="告警事件IP" title="ip">
          <template slot-scope="scope">
            <span v-if="scope.row.ip && scope.row.ip != 'null'">{{ scope.row.ip }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="level" title="告警级别" :formatter="formatAlarmLevel"></vxe-table-column>
        <vxe-table-column field="alarmNum" title="告警次数"></vxe-table-column>
        <vxe-table-column field="message" title="详细" show-overflow></vxe-table-column>
        <vxe-table-column field="timestamp" title="开始时间">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.timestamp) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="endTime" title="最新时间">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.endTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="duration" title="持续时间"></vxe-table-column>
      </vxe-table>
      <vxe-pager
        id="page_table"
        perfect
        :current-page.sync="queryParams.pageNum"
        :page-size.sync="queryParams.pageSize"
        :total="paginationTotal"
        :page-sizes="[10, 20, 100]"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="queryTable"
      >
      </vxe-pager>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request';
export default {
  data() {
    return {
      alarmLevelOptions: [],
      alarmTypeOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      tableData: [], //表格
      paginationTotal: 0,
    };
  },
  created() {
    this.getDicts('alarm_level').then((response) => {
      this.alarmLevelOptions = response.data;
    });
    this.getDicts('alarm_type').then((response) => {
      this.alarmTypeOptions = response.data;
    });
    this.queryTable();
  },
  mounted() {},
  methods: {
    /* 查询 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryTable();
    },
    /* 重置 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        level: '',
        ip: '',
      };
      this.queryTable();
    },
    /* table方法 */
    queryTable() {
      request({
        url: '/alarmUn/list',
        method: 'get',
        params: this.queryParams,
      }).then((data) => {
        this.tableData = data.data.pageData;
        this.paginationTotal = data.data.totalCount;
      });
    },
    /* 翻页 */
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.queryTable();
    },
    formatAlarmLevel({ cellValue }) {
      return this.selectDictLabel(this.alarmLevelOptions, cellValue);
    },
    formatMonitorType({ cellValue }) {
      return this.selectDictLabel(this.alarmTypeOptions, cellValue);
    },
  },
};
</script>
