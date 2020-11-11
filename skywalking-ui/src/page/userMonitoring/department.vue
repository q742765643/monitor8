<template>
  <div class="departmentMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="部门名称">
        <a-input v-model="queryParams.departmentName" placeholder="请输入部门名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select v-model="queryParams.status" style="width: 240px" @change="handleSelectChange">
          <a-select-option :value="0">
            正常
          </a-select-option>
          <a-select-option :value="1">
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
            <a-button type="danger" icon="plus" @click="handleAdd(row)">
              新增
            </a-button>
            <a-button type="danger" icon="delete" @click="handleDelete(row)">
              删除
            </a-button>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      @cancel="handleCancel"
      okText="确定"
      cancelText="取消"
      width="30%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 6 }"
        :wrapperCol="{ span: 17 }"
        :model="formDialog"
        ref="formModel"
        :rules="rules"
      >
        <a-row>
          <a-col :span="24" v-if="formDialog.parentId !== '0'">
            <a-form-model-item label="上级部门">
              <a-tree-select
                v-model="formDialog.parentId"
                show-search
                style="width: 100%"
                :treeData="deptOptions"
                :replaceFields='replaceFields'
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                placeholder="请选择上级部门"
                allow-clear
              >
              </a-tree-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="部门名称">
              <a-input v-model="formDialog.deptName" placeholder="请输入部门名称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="显示顺序">
              <a-input-number :min="0" v-model="formDialog.orderNum" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="负责人">
              <a-input v-model="formDialog.leader" placeholder="请输入负责人"> </a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="12">
            <a-form-model-item label="联系电话">
              <a-input v-model="formDialog.phone" placeholder="请输入联系电话"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="邮箱">
              <a-input-password v-model="formDialog.eamil" placeholder="请输入邮箱"> </a-input-password>
            </a-form-model-item>
          </a-col>

          <a-col :span="12">
            <a-form-model-item label="状态">
              <a-radio-group name="radioGroup" v-model="formDialog.status">
                <a-radio :value="0">
                  正常
                </a-radio>
                <a-radio :value="1">
                  停用
                </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import hongtuConfig from '@/utils/services';
export default {
  data() {
    return {
      queryParams: {
        departmentName: '',
        status: 0,
      },
      departmentListData: [],
      dialogTitle: '',
      visibleModel: false,
      deptOptions: [],
      replaceFields: {
        children: 'children',
        title: 'label',
        key: 'id'
      },
      formDialog: {},
      // formDialog: {
      //   deptName: '',
      //   leader: '',
      //   orderNum: 0,
      //   phone: '',
      //   eamil: '',
      //   status: ''
      // },
      rules: {},
    };
  },
  created() {},
  methods: {
    handleSelectChange() {},
    handleQuery() {},
    resetQuery() {},
    // 查询部门下拉树结构
    getTreeselect() {
      hongtuConfig.treeselect(this.formDialog).then((res) => {
        // console.log(res)
        this.deptOptions = []
        const dept = { id: 0, label: "主类目", children: [] };
        dept.children = res.data;
        this.deptOptions.push(dept);
      })
    },
    // 表单重置
    reset() {
      this.formDialog = {
        id: undefined,
        parentId: 100,
        deptName: undefined,
        orderNum: undefined,
        leader: undefined,
        phone: undefined,
        email: undefined,
        status: '0',
      };
      this.$refs.formModel.resetFields();
    },
    handleAdd(row) {
      this.reset()
      this.getTreeselect()
      console.log(this.deptOptions)
      if(row != undefined) {
        this.formDialog.parentId = row.id
      }
      this.visibleModel = true
      this.dialogTitle = '添加部门'
    },
    handleOk() {},
    handleCancel() {},
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