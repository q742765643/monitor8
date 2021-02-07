<template>
  <div class="roleTemplate">
    <div class="timerSelect">
      <a-form-model layout="inline" :model="queryParams" ref="queryForm" class="queryForm">
        <a-form-model-item label="任务名称">
          <a-input v-model="queryParams.taskName" placeholder="请输入任务名称"> </a-input>
        </a-form-model-item>
        <a-form-model-item label="执行状态" prop="handleCode">
          <a-select style="width: 120px" v-model="queryParams.handleCode" placeholder="执行状态">
            <a-select-option key="0" value="0">
              一般
            </a-select-option>
            <a-select-option key="1" value="1">
              危险
            </a-select-option>
            <a-select-option key="2" value="2">
              故障
            </a-select-option>
            <a-select-option key="3" value="3">
              正常
            </a-select-option>
            <a-select-option key="4" value="4">
              未执行
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="时间">
          <selectDate
            class="selectDate"
            @changeDate="changeDate"
            :HandleDateRange="dateRange"
            ref="selectDateRef"
          ></selectDate>
        </a-form-model-item>
        <a-form-model-item>
          <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
        </a-form-model-item>
      </a-form-model>
    </div>

    <div class="tableDateBox">
      <!-- <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="primary" icon="edit" :disabled="single" @click="handleUpdate"> 修改 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" :disabled="single" @click="handleDelete"> 删除 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="primary" icon="vertical-align-bottom" @click="handleUpload"> 导出 </a-button>
        </a-col>
      </a-row> -->
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons> <p style="text-align: right; margin-bottom: 0">列选择</p> </template>
        </vxe-toolbar>
      </div>
      <vxe-table
        :data="fileListData"
        @checkbox-change="selectBox"
        align="center"
        highlight-hover-row
        ref="roleListRef"
        border
      >
        <vxe-table-column type="checkbox" width="50"></vxe-table-column>
        <vxe-table-column field="taskName" title="任务名称"></vxe-table-column>
        <vxe-table-column field="fileNum" title="应到文件数量"></vxe-table-column>
        <vxe-table-column field="realFileNum" title="实到文件数量"></vxe-table-column>
        <vxe-table-column field="lateNum" title="晚到数量"></vxe-table-column>
        <vxe-table-column field="status" title="当前状态">
          <template v-slot="{ row }">
            <span v-if="row.status == 0">一般 </span>
            <span v-if="row.status == 1">危险 </span>
            <span v-if="row.status == 2">故障 </span>
            <span v-if="row.status == 3">正常 </span>
            <span v-if="row.status == 4">未执行 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="ddataTime" title="资料时次">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.ddataTime) }}</span>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        id="page_table"
        perfect
        :current-page.sync="queryParams.pageNum"
        :page-size.sync="queryParams.pageSize"
        :total="paginationTotal"
        :page-sizes="[10, 20, 100]"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      >
      </vxe-pager>
    </div>
  </div>
</template>

<script>
import hongtuConfig from '@/utils/services';
import request from '@/utils/request';
import moment from 'moment';
import selectDate from '@/components/date/select.vue';
export default {
  data() {
    return {
      queryParams: {
        taskName: '',
        pageNum: 1,
        pageSize: 10,
      },
      dateRange: [],
      fileListData: [],
      paginationTotal: 0,
      values: undefined,
    };
  },
  components: { selectDate },
  created() {
    this.getFileList();
  },
  methods: {
    changeDate(data) {
      this.dateRange = data;
    },
    moment,
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },

    getFileList() {
      console.log(this.dateRange);
      hongtuConfig.fileDetail(this.addDateRange(this.queryParams, this.dateRange)).then((res) => {
        console.log(res);
        this.fileListData = res.data.pageData;
        this.paginationTotal = res.data.totalCount;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getFileList();
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        taskName: '',
      };
      this.$refs.selectDateRef.changeDate('resetQuery');
      this.getFileList();
    },

    selectBox(selection) {
      console.log(selection.selection);
      this.single = selection.selection.length > 0 ? false : true;
    },

    // 分页事件
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.getFileList();
    },
  },
};
</script>

<style scoped></style>
