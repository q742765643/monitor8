<template>
  <div class="departmentMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="部门名称">
        <a-input v-model="queryParams.deptName" placeholder="请输入部门名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select v-model="queryParams.status" style="width: 240px" @change="handleSelectChange">
          <a-select-option v-for="dict in statusOptions" :key="dict.dictValue">
            {{ dict.dictLabel }}
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
        <vxe-table-column field="deptName" title="部门名称" width="400"></vxe-table-column>
        <vxe-table-column field="orderNum" title="排序"></vxe-table-column>
        <vxe-table-column field="status" title="状态" >
          <template v-slot="{ row }">
              <span v-if="row.status == 0">正常 </span>
              <span v-if="row.status == 1">停用 </span>
            </template>
        </vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间">
          <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
        </vxe-table-column>
        <vxe-table-column title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="handleUpdate(row)">
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
    <!-- 添加或修改部门对话框 -->
    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      @cancel="handleCancel"
      okText="确定"
      cancelText="取消"
      width="40%"
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
            <a-form-model-item label="上级部门" prop="parentId">
              <treeselect
                v-model.trim="formDialog.parentId"
                :options="deptOptions"
                placeholder="选择上级部门"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="部门名称" prop="deptName">
              <a-input v-model="formDialog.deptName" placeholder="请输入部门名称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="显示顺序" prop="orderNum">
              <a-input-number :min="0" v-model="formDialog.orderNum" style="width: 100%" />
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
                <a-radio v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue">
                  {{ dict.dictLabel }}
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
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  components: { Treeselect },
  data() {
    return {
      queryParams: {
        deptName: undefined,
        status: undefined,
      },
      departmentListData: [],
      dialogTitle: '',
      visibleModel: false,
      deptOptions: [],
      // title: undefined,
      // replaceFields: {
      //   children: 'children',
      //   title: 'label',
      //   key: 'id',
      //   value: 'id',
      // },
      formDialog: {},
      rules: {
        parentId: [{ required: true, message: '上级部门不能为空', trigger: 'blur' }],
        deptName: [{ required: true, message: '部门名称不能为空', trigger: 'blur' }],
        orderNum: [{ required: true, message: '菜单顺序不能为空', trigger: 'blur' }],
      },
      statusOptions: [],
    };
  },
  watch: {
    // title(value) {
    //   console.log(value);
    // },
  },
  created() {
    this.getDeptList();
    hongtuConfig.getDicts('sys_normal_disable').then((res) => {
      if (res.code == 200) {
        this.statusOptions = res.data;
      }
      console.log(this.statusOptions);
    });
  },
  methods: {
    getDeptList() {
      hongtuConfig.deptList(this.queryParams).then((res) => {
        this.departmentListData = res.data;
      });
    },
    // 查询部门下拉树结构
    getTreeselect() {
      hongtuConfig.treeselect(this.formDialog).then((res) => {
        console.log(res);
        this.deptOptions = [];
        const dept = { id: 0, label: '主类目', children: [] };
        dept.children = res.data;
        this.deptOptions.push(dept);
        console.log(this.deptOptions);
      });
    },
    handleSelectChange() {},
    handleQuery() {
      this.getDeptList();
    },
    handleSelect() {
      console.log(this.formDialog.parentId);
    },
    resetQuery() {
      this.queryParams = {
        deptName: undefined,
        status: undefined,
      };
      this.getDeptList();
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
      this.formDialog.parentId = undefined;
      this.formDialog.status = undefined;
    },
    handleAdd(row) {
      this.reset();
      console.log(this.deptOptions);
      this.getTreeselect();
      console.log(this.deptOptions);
      if (row != undefined) {
        this.formDialog.parentId = row.id;
      }
      this.visibleModel = true;
      this.dialogTitle = '添加部门';
    },
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      hongtuConfig.getDept(row.id).then((res) => {
        this.formDialog = res.data;
        if(this.formDialog.parentId == 100) {
          this.formDialog.parentId =0
        }
        this.visibleModel = true;
        this.dialogTitle = '修改部门';
      });
    },
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          if (this.formDialog.id == undefined) {
            hongtuConfig.addDept(this.formDialog).then((res) => {
              if (res.code == 200) {
                this.$message.success('新增成功');
                this.visibleModel = false;
                this.getDeptList();
              }
            });
          } else {
            hongtuConfig.editDept(this.formDialog).then((res) => {
              if (res.code == 200) {
                this.$message.success('修改成功');
                this.visibleModel = false;
                this.getDeptList();
              }
            });
          }
        }
      });
    },
    handleCancel() {
      this.visibleModel = false;
      this.reset();
    },
    handleDelete(row) {
      this.$confirm({
        title: '是否确认删除部门名称为' + row.deptName + '的数据项',
        content: '',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          hongtuConfig.deleteDept(row.id).then((response) => {
            if (response.code == 200) {
              this.$message.success('删除成功');
              this.getDeptList();
            }
          });
        },
        onCancel() {},
      });
    },
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
  /* width: 240px; */
  margin-right: 20px;
}
#tableDiv {
  padding: 20px 0;
}
.ant-radio-group {
  padding-top: 0.14rem;
}
</style>