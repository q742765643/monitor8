<template>
  <div class="loginLogTemplate">
    <a-form-model layout="inline" :model="queryParams" ref="queryForm" class="queryForm">
      <a-form-model-item label="登录地址">
        <a-input v-model="queryParams.ipaddr" placeholder="请输入登录地址"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="用户名称">
        <a-input v-model="queryParams.userName" placeholder="请输入用户名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select v-model="queryParams.status" style="width: 240px">
          <a-select-option v-for="dict in statusOptions" :key="dict.dictValue">
            {{ dict.dictLabel }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="登录时间">
        <a-range-picker v-model.trim="queryParams.dateRange" />
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" :disabled="single" @click="handleDelete"> 删除 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" @click="handleClean"> 清空 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="primary" icon="vertical-align-bottom" @click="handleExport"> 导出 </a-button>
        </a-col>
      </a-row>
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons> <p style="text-align: right; margin-bottom: 0">列选择</p> </template>
        </vxe-toolbar>
      </div>
      <vxe-table
        :data="loginListData"
        @checkbox-change="selectBox"
        align="center"
        highlight-hover-row
        ref="loginListRef"
        border
      >
        <vxe-table-column type="checkbox" width="50"></vxe-table-column>
        <vxe-table-column field="userName" title="用户名称"></vxe-table-column>
        <vxe-table-column field="ipaddr" title="登录地址" show-overflow></vxe-table-column>
        <vxe-table-column field="loginLocation" title="登录地点"></vxe-table-column>
        <vxe-table-column field="browser" title="浏览器"></vxe-table-column>
        <vxe-table-column field="os" title="操作系统"></vxe-table-column>
        <vxe-table-column field="status" title="登录状态">
          <template v-slot="{ row }">
            <span> {{ statusFormat(statusOptions, row.status) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="msg" title="操作信息"></vxe-table-column>
        <vxe-table-column field="loginTime" width="160" title="登录日期">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.loginTime) }}</span>
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
  export default {
    data() {
      return {
        queryParams: {
          loginLocation: undefined,
          userName: undefined,
          status: undefined,
          pageNum: 1,
          pageSize: 10,
          params: {
            orderBy: {
              loginTime: 'desc',
            },
          },
        },
        loginListData: [],
        paginationTotal: 0,
        statusOptions: [],
        dateRange: [],
        single: true,
      };
    },
    created() {
      this.getLoginList();
      hongtuConfig.getDicts('sys_common_status').then((res) => {
        if (res.code == 200) {
          this.statusOptions = res.data;
        }
      });
    },
    methods: {
      selectBox(selection) {
        // console.log(selection.selection)
        this.single = selection.selection.length > 0 ? false : true;
      },
      // 导出
      handleExport() {
        hongtuConfig.exportLogininfor(this.queryParams).then((res) => {
          this.downloadfileCommon(res);
        });
      },
      getLoginList() {
        if (this.queryParams.dateRange) {
          this.dateRange = this.queryParams.dateRange;
        } else {
          this.dateRange = [];
        }
        hongtuConfig.getLogin(this.addDateRange(this.queryParams, this.dateRange)).then((res) => {
          //   console.log(res)
          this.loginListData = res.data.pageData;
          this.paginationTotal = res.data.totalCount;
        });
      },
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getLoginList();
      },
      handleClean() {
        this.$confirm({
          title: '是否确认清空所有登录日志数据项?',
          content: '',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          onOk: () => {
            hongtuConfig.cleanLogininfor().then((res) => {
              if (res.code == 200) {
                this.getLoginList();
                this.$message.success('清空成功');
              }
            });
          },
          onCancel() {},
        });
      },
      statusFormat(list, text) {
        return hongtuConfig.formatterselectDictLabel(list, text);
      },
      resetQuery() {
        this.dateRange = [];
        this.queryParams = {
          loginLocation: undefined,
          userName: undefined,
          status: undefined,
          params: {
            orderBy: {
              loginTime: 'desc',
            },
          },
          pageNum: 1,
          pageSize: 10,
        };
        this.getLoginList();
      },
      handleDelete() {
        let ids = [];
        let cellsChecked = this.$refs.loginListRef.getCheckboxRecords();
        cellsChecked.forEach((element) => {
          ids.push(element.id);
        });
        this.$confirm({
          title: '是否确认删除访问编号为"' + ids + '"的数据项?',
          content: '',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          onOk: () => {
            hongtuConfig.delLogininfor(ids).then((res) => {
              if (res.code == 200) {
                this.getLoginList();
                this.single = true;
                this.$message.success('删除成功');
              }
            });
          },
          onCancel() {},
        });
      },
      // 分页事件
      handlePageChange({ currentPage, pageSize }) {
        this.queryParams.pageNum = currentPage;
        this.queryParams.pageSize = pageSize;
        this.getLoginList();
      },
    },
  };
</script>

<style scoped></style>
