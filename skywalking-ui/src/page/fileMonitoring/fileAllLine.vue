<template>
  <div class="dataAllLineTem">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="资料名称">
        <a-select style="width: 120px" v-model="queryParams.dataName" placeholder="请选择">
          <a-select-option v-for="dict in dataOptions" :key="dict.dictValue" :value="dict.dictValue">
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
        <vxe-table-column field="monitorType" title="资料名称"></vxe-table-column>
        <vxe-table-column field="timestamp" title="最新时间（北京时）">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.timestamp) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="alarmNum" title="缺收" width="80"></vxe-table-column>
        <vxe-table-column field="message" title="实收" width="80"></vxe-table-column>
        <vxe-table-column field="duration" title="应收" width="80"></vxe-table-column>
        <vxe-table-column field="jishilv" title="及时率">
          <template slot-scope="scope">
            <el-progress
              :text-inside="true"
              :stroke-width="20"
              :percentage="scope.row.jishilv"
              status="success"
            ></el-progress>
          </template>
        </vxe-table-column>
        <vxe-table-column field="daobaolv" title="到报率">
          <template slot-scope="scope">
            <el-progress
              :text-inside="true"
              :stroke-width="20"
              :percentage="scope.row.daobaolv"
              status="success"
            ></el-progress>
          </template>
        </vxe-table-column>
        <vxe-table-column field="duration" title="目的路径"></vxe-table-column>
        <vxe-table-column field="duration" title="最新采集时间"></vxe-table-column>
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
      dataOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      tableData: [{ jishilv: 70, daobaolv: 40 }], //表格
      paginationTotal: 0,
    };
  },
  created() {
    //this.queryTable();
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
      };
      this.queryTable();
    },
    /* table方法 */
    queryTable() {
      request({
        url: '',
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
  },
};
</script>
