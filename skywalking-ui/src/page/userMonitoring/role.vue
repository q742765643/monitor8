<template>
  <div class="roleMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="角色名称">
        <a-input v-model="queryParams.roleName" placeholder="请输入角色名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="权限字符">
        <a-input v-model="queryParams.roleName" placeholder="请输入权限字符"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select default-value="用户状态" style="width: 240px" @change="handleSelectChange">
          <a-select-option value="jack">
            正常
          </a-select-option>
          <a-select-option value="lucy">
            停用
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="创建时间">
        <a-range-picker />
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
    <div id="linkrole_content">
      <a-row type="flex" class="rowToolbar">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd">
            新增
          </a-button>
          <a-button type="primary" icon="edit" @click="handleEdit">
            修改
          </a-button>
          <a-button type="danger" icon="delete" @click="handleDelete">
            删除
          </a-button>
          <a-button type="primary" icon="vertical-align-bottom" @click="handleUpload">
            导出
          </a-button>
        </a-col>
      </a-row>

      <div id="tableDiv">
        <vxe-table :data="roleListData" align="center" highlight-hover-row ref="roleListRef" border>
          <vxe-table-column type="checkbox" width="50"></vxe-table-column>
          <vxe-table-column field="userName" title="用户名称"></vxe-table-column>
          <vxe-table-column field="userName2" title="用户昵称"></vxe-table-column>
          <vxe-table-column field="departmentName" title="部门"></vxe-table-column>
          <vxe-table-column field="userPhone" title="手机号码"></vxe-table-column>
          <vxe-table-column field="userStatus" title="状态"></vxe-table-column>
          <vxe-table-column field="userCreateTime" title="创建时间"></vxe-table-column>
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
  </div>
</template>

<script>
export default {
  data() {
    return {
      queryParams: {
        roleName: '',
        pageNum: 1,
        pageSize: 10,
      },
      roleListData: [],
      paginationTotal: 0,
    };
  },
  methods: {
    handleQuery() {},
    resetQuery() {},
    handleSelectChange() {},
    handleAdd() {},
    handleEdit() {},
    handleDelete() {},
    handleUpload() {},
    handlePageChange() {},
  },
};
</script>

<style scoped>
.roleMonitorTemplate {
  width: 100%;
  height: 100%;
  padding: 20px;
  font-family: Alibaba-PuHuiTi-Regular;
}
.queryForm {
  width: 100%;
  height: 80px;
  background: #f2f2f2;
  border-radius: 8px;
  padding-top: 20px;
}
.ant-input {
  width: 240px;
  margin-right: 20px;
}
#linkrole_content {
  padding: 20px 0;
}
#tableDiv {
  margin-top: 20px;
}
</style>