<template>
  <div class="roleMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="角色名称">
        <a-input v-model="queryParams.roleName" placeholder="请输入角色名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="权限字符">
        <a-input v-model="queryParams.roleKey" placeholder="请输入权限字符"> </a-input>
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
      <a-form-model-item label="创建时间">
        <a-range-picker />
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="getRoleList">
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
          <vxe-table-column field="roleName" title="角色名称"></vxe-table-column>
          <vxe-table-column field="roleKey" title="权限字符"></vxe-table-column>
          <vxe-table-column field="status" title="状态">
            <template v-slot="{ row }">
              <a-switch :checked="row.status == '0' ? true : false" @change="handleStatus(row)" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="createTime" title="创建时间"></vxe-table-column>
          <vxe-table-column title="操作">
            <template v-slot="{ row }">
              <a-button type="primary" icon="edit" @click="handleEdit(row)">
                修改
              </a-button>
              <a-button type="danger" icon="delete" @click="handleDelete(row)">
                删除
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
    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      @cancel="handleCancel"
      okText="确定"
      cancelText="取消"
      width="25%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        ref="formModel"
        :label-col="{ span: 6 }"
        :wrapperCol="{ span: 17 }"
        :rules="rules"
        :model="formDialog"
      >
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="角色名称" prop="roleName">
              <a-input v-model="formDialog.roleName" placeholder="请输入角色名称"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="权限字符" prop="roleKey">
              <a-input v-model="formDialog.roleKey" placeholder="请输入权限字符"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="角色顺序">
              <a-input-number :min="0" v-model="formDialog.roleSort" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="菜单权限">
              <a-tree
                v-model="checkedKeys"
                checkable
                :expanded-keys="expandedKeys"
                :auto-expand-parent="autoExpandParent"
                :selected-keys="selectedKeys"
                :tree-data="treeData"
                :replaceFields='replaceFields'
                @expand="onExpand"
                @select="onSelect"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="状态">
              <a-radio-group name="radioGroup" style="width:200px" v-model="formDialog.status">
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
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="备注">
              <a-textarea
                v-model="formDialog.remark"
                placeholder="请输入内容"
                :auto-size="{ minRows: 3, maxRows: 5 }"
              />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import hongtuConfig from '@/utils/services';
import request from '@/utils/request';
// const treeData = [
//   {
//     key: '0-0',
//     scopedSlots: {
//       title: 'title',
//     },
//     title: '0-0',
//     children: [
//       {
//         key: '0-0-0',
//         scopedSlots: {
//           title: 'title',
//         },
//         title: '0-0-0',
//       },
//       {
//         key: '0-0-1',
//         scopedSlots: {
//           title: 'title',
//         },
//         title: '0-0-1',
//       },
//       {
//         key: '0-0-2',
//         scopedSlots: {
//           title: 'title',
//         },
//         title: '0-0-2',
//       },
//     ],
//   },
//   {
//     key: '0-1',
//     scopedSlots: {
//       title: 'title',
//     },
//     title: '0-1',
//     children: [
//       {
//         key: '0-1-0',
//         scopedSlots: {
//           title: 'title',
//         },
//         title: '0-1-0',
//       },
//       {
//         key: '0-1-1',
//         scopedSlots: {
//           title: 'title',
//         },
//         title: '0-1-1',
//       },
//       {
//         key: '0-1-2',
//         scopedSlots: {
//           title: 'title',
//         },
//         title: '0-1-2',
//       },
//     ],
//   },
//   {
//     key: '0-2',
//     scopedSlots: {
//       title: '0-2',
//     },
//     title: '0-2',
//   },
// ];
export default {
  data() {
    return {
      queryParams: {
        roleName: '',
        roleKey: '',
        status: '',
        pageNum: 1,
        pageSize: 10,
      },
      roleListData: [],
      paginationTotal: 0,
      visibleModel: false,
      dialogTitle: '',
      formDialog: {
        roleName: '',
        roleKey: '',
        roleSort: 0,
        status: 0,
        menuIds: [],
        remark: '',
      },
      rules: {
        roleName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
        roleKey: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
      },
      checkedKeys: [],
      expandedKeys: [],
      autoExpandParent: true,
      selectedKeys: [],
      treeData: [],
      replaceFields: {
        children: 'children',
        title: 'label',
        key: 'id'
      }
    };
  },
  created() {
    this.getRoleList();
    this.getTreeSelect()
  },
  methods: {
    getRoleList() {
      hongtuConfig.roleConfigList(this.queryParams).then((res) => {
        console.log(res);
        this.roleListData = res.data.pageData;
        this.paginationTotal = res.data.totalCount;
      });
    },
    getTreeSelect(){
      hongtuConfig.menuConfigTree(this.queryParams).then((res) => {
        console.log(res)
        this.treeData = res.data
      })
    },
    handleQuery() {},
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        roleName: undefined,
        roleKey: undefined,
        status: undefined,
      };
      this.getRoleList();
    },
    handleSelectChange() {},
    handleAdd() {
      this.getTreeSelect()
      this.dialogTitle = '添加用户';
      this.visibleModel = true;
    },
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          if (!this.formDialog.id) {
            console.log(this.formDialog);
            hongtuConfig.roleConfigAdd(this.formDialog).then((res) => {
              if (res.code == 200) {
                this.$message.success(this.dialogTitle + '成功');
                this.visibleModel = false;
                this.getRoleList();
              }
            });
          } else {
            console.log(this.formDialog)
            this.formDialog.menuIds = []
            hongtuConfig.roleConfigEdit(this.formDialog).then((res) => {
              console.log(res);
              if (res.code == 200) {
                this.$message.success(this.dialogTitle + '成功');
                this.visibleModel = false;
                this.getRoleList();
              }
            });
          }
        }
      });
    },
    handleCancel() {},
    handleStatus(row) {
      row.status = !row.status;
    },
    onExpand() {},
    onSelect() {},
    handleEdit(row) {
      hongtuConfig.roleCofigSearchById(row.id).then((res) => {
        if (res.code == 200) {
          this.formDialog = res.data;
          this.visibleModel = true;
          this.dialogTitle = '编辑用户';
        }
      });
    },
    handleDelete(row) {
      let ids = [];
      let deletName = [];
      if (!row.id) {
        let cellsChecked = this.$refs.roleListRef.getCheckboxRecords();
        cellsChecked.forEach((element) => {
          ids.push(element.id);
          deletName.push(element.roleName);
        });
      } else {
        ids.push(row.id);
        deletName.push(row.roleName);
      }
      this.$confirm({
        title: '是否确认删除用户名称为' + deletName.join(',') + '的数据项',
        content: '',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          hongtuConfig.roleConfigDelete(ids.join(',')).then((response) => {
            if (response.code == 200) {
              this.$message.success('删除成功');
              this.resetQuery();
            }
          });
        },
        onCancel() {},
      });
    },
    handleUpload() {
      hongtuConfig.exportRole(this.queryParams).then((res) => {
        console.log(res)
        this.downloadfileCommon(res)
      })
    },
    // 分页事件
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.getRoleList();
    },
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
.dialogBox .ant-input {
  width: 100%;
}
.ant-input-number {
  width: 100%;
}
.ant-radio-group {
  padding-top: 0.14rem;
}
</style>