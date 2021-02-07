<template>
  <div class="onlineUserTemplate">
    <a-form-model layout="inline" :model="queryParams" ref="queryForm" class="queryForm">
      <a-form-model-item label="登录地址">
        <a-input v-model="queryParams.ipaddr" placeholder="请输入登录地址"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="用户名称">
        <a-input v-model="queryParams.userName" placeholder="请输入用户名称"> </a-input>
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
      <vxe-table :data="onlineUserListData" align="center" highlight-hover-row ref="onlineUserListRef" border>
        <vxe-table-column field="userName" title="登录名称"></vxe-table-column>
        <vxe-table-column field="deptName" title="部门名称"></vxe-table-column>
        <vxe-table-column field="ipaddr" title="主机"></vxe-table-column>
        <vxe-table-column field="loginLocation" title="登录地点"></vxe-table-column>
        <vxe-table-column field="browser" title="浏览器"></vxe-table-column>
        <vxe-table-column field="os" title="操作系统"></vxe-table-column>
        <vxe-table-column field="loginTime" title="登录时间">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.loginTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="operation" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="handleForceLogout(row)"> 强退 </a-button>
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
          onlineIP: undefined,
          userName: undefined,
          pageNum: 1,
          pageSize: 10,
        },
        onlineUserListData: [],
        paginationTotal: 0,
      };
    },
    created() {
      this.getOnlineUserList();
    },
    methods: {
      handleForceLogout(row) {
        this.$confirm({
          title: '是否确认强退名称为"' + row.userName + '"的数据项?',
          content: '',
          okText: '确定',
          okType: 'danger',
          cancelText: '取消',
          onOk: () => {
            hongtuConfig.forceLogout(row.tokenId).then((res) => {
              if (res.code == 200) {
                this.getOnlineUserList();
                this.$message.success('强退成功');
              }
            });
          },
          onCancel() {},
        });
      },
      handlePageChange({ currentPage, pageSize }) {
        this.queryParams.pageNum = currentPage;
        this.queryParams.pageSize = pageSize;
        this.getOnlineUserList();
      },
      handleQuery() {
        this.pageNum = 1;
        this.getOnlineUserList();
      },
      getOnlineUserList() {
        hongtuConfig.onlineUserList(this.queryParams).then((res) => {
          this.onlineUserListData = res.data.pageData;
          this.paginationTotal = res.data.totalCount;
        });
      },
      resetQuery() {
        this.queryParams = {
          onlineIP: undefined,
          userName: undefined,
          pageNum: 1,
          pageSize: 10,
        };
        this.handleQuery();
      },
    },
  };
</script>

<style scoped></style>
