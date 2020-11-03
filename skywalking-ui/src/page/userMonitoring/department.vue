<template>
  <div class="departmentMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="部门名称">
        <a-input v-model="queryParams.departmentName" placeholder="请输入部门名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select default-value="部门状态" style="width: 240px" @change="handleSelectChange">
          <a-select-option value="jack">
            正常
          </a-select-option>
          <a-select-option value="lucy">
            停用
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery">
          搜索
        </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery">
          重置
        </a-button>
        <a-button type="primary" :style="{ marginLeft: '8px' }" @click="handleAdd">
          新增
        </a-button>
      </a-form-model-item>
    </a-form-model>
    <div id="tableDiv">
        <vxe-table :data="departmentListData" align="center" highlight-hover-row ref="departmentListRef" border>
          <vxe-table-column field="departmentName" title="部门名称" width="400"></vxe-table-column>
          <vxe-table-column field="order" title="排序"></vxe-table-column>
          <vxe-table-column field="departmentStatus" title="状态"></vxe-table-column>
          <vxe-table-column field="createTime" title="创建时间"></vxe-table-column>
          <vxe-table-column field="departOperation" title="操作">
              <template v-slot="{ row }">
                <a-button type="primary" icon="edit" @click="handleEdit(row)">
                  修改
                </a-button>
                <a-button type="danger" icon="plus" @click="handleDelete(row)">
                  新增
                </a-button>
                <a-button type="danger" icon="delete" @click="handleReset(row)">
                  删除
                </a-button>
              </template>
          </vxe-table-column>
        </vxe-table>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      queryParams: {
        departmentName: '',
      },
      departmentListData: []
    };
  },
  created() {},
  methods: {
    handleSelectChange() {},
    handleQuery() {},
    resetQuery() {},
    handleAdd() {},
  },
};
</script>

<style scoped>
.departmentMonitorTemplate {
  width: 100%;
  height: 100%;
  padding: 20px;
  font-family: Alibaba-PuHuiTi-Regular;
}
.queryForm {
  width: 100%;
  height: 70px;
  background-color: #f2f2f2;
  padding-top: 20px;
}
.ant-input {
  width: 240px;
  margin-right: 20px;
}
#tableDiv {
    padding: 20px 0;
}
</style>