<template>
  <div class="onlineUserMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="登录地址">
        <a-input v-model="queryParams.onlineIP" placeholder="请输入登录地址"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="用户名称">
        <a-input v-model="queryParams.userName" placeholder="请输入用户名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery">
          搜索
        </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery">
          重置
        </a-button>
      </a-form-model-item>
    </a-form-model>
    <div id="tableDiv">
      <vxe-table :data="onlineUserListData" align="center" highlight-hover-row ref="onlineUserListRef" border>
        <vxe-table-column field="onlineName" title="登录名称"></vxe-table-column>
        <vxe-table-column field="department" title="部门名称"></vxe-table-column>
        <vxe-table-column field="departmentStatus" title="主机"></vxe-table-column>
        <vxe-table-column field="onLineAddress" title="登录地点"></vxe-table-column>
        <vxe-table-column field="browerName" title="浏览器"></vxe-table-column>
        <vxe-table-column field="systemName" title="操作系统"></vxe-table-column>
        <vxe-table-column field="createTime" title="登录时间"></vxe-table-column>
        <vxe-table-column field="operation" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="handleEdit(row)">
              强退
            </a-button>
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
export default {
  data() {
    return {
      queryParams: {
        onlineIP: '',
        userName: '',
        pageNum: 1,
        pageSize: 10,
      },
      onlineUserListData: [],
      paginationTotal: 0,
    };
  },
  created() {},
  methods: {
    handlePageChange() {},
    handleQuery() {},
    resetQuery() {},
    handleEdit(row) {}
  },
};
</script>

<style scoped>
.onlineUserMonitorTemplate {
  width: 100%;
  height: 100%;
  padding: 20px;
  font-family: Alibaba-PuHuiTi-Regular;
}
.queryForm {
  padding-top: 20px;
  height: 70px;
  background-color: #f2f2f2;
}
#tableDiv {
  margin-top: 20px;
}
</style>