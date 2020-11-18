<template>
  <div class="userMonitorTemplate">
    <div class="left">
      <a-input-search style="margin-bottom: 8px" placeholder="请输入部门名称" @change="onChange" />
      <a-tree
        :expanded-keys="expandedKeys"
        :auto-expand-parent="autoExpandParent"
        :tree-data="deptOptions"
        :replaceFields="replaceFields"
        @expand="onExpand"
      >
        <template slot="title" slot-scope="{ title }">
          <span v-if="title.indexOf(searchValue) > -1">
            {{ title.substr(0, title.indexOf(searchValue)) }}
            <span style="color: #f50">{{ searchValue }}</span>
            {{ title.substr(title.indexOf(searchValue) + searchValue.length) }}
          </span>
          <span v-else>{{ title }}</span>
        </template>
      </a-tree>
    </div>
    <div class="right">
      <div class="searchBox">
        <a-form-model layout="inline" :model="queryParams" ref="queryForm" class="queryForm">
          <a-form-model-item label="用户名称">
            <a-input v-model="queryParams.userName" placeholder="请输入用户名称"> </a-input>
          </a-form-model-item>
          <a-form-model-item label="手机号码">
            <a-input v-model="queryParams.phonenumber" placeholder="请输入手机号码"> </a-input>
          </a-form-model-item>
          <a-form-model-item label="状态">
            <a-select v-model="queryParams.status" style="width: 240px">
              <a-select-option v-for="item in statusOptions" :key="item.dictValue">{{
                item.dictLabel
              }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="创建时间">
            <a-range-picker
              style="width: 295px"
              v-model="queryParams.dateRange"
              @change="onTimeChange"
              :show-time="{
                hideDisabledOptions: true,
                defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('11:59:59', 'HH:mm:ss')],
              }"
              format="YYYY-MM-DD HH:mm:ss"
            />
          </a-form-model-item>
          <a-form-model-item>
            <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
            <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
          </a-form-model-item>
        </a-form-model>
      </div>

      <div id="linkUser_content">
        <a-row type="flex" class="rowToolbar">
          <a-col :span="1.5">
            <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
            <a-button type="primary" :disabled="single" icon="edit" @click="handleUpdate"> 修改 </a-button>
            <a-button type="danger" :disabled="single" icon="delete" @click="handleDelete"> 删除 </a-button>
            <a-button type="primary" icon="vertical-align-bottom" @click="handleExport"> 导出 </a-button>
          </a-col>
        </a-row>
        <div id="tableDiv">
          <vxe-table :data="userListData" align="center" @checkbox-change="selectBox" highlight-hover-row ref="userListRef" border>
            <vxe-table-column type="checkbox" width="50"></vxe-table-column>
            <vxe-table-column field="userName" width="80" title="用户名称"></vxe-table-column>
            <vxe-table-column field="nickName" title="用户昵称"></vxe-table-column>
            <vxe-table-column field="dept.deptName" title="部门"></vxe-table-column>
            <vxe-table-column field="phonenumber" title="手机号码"></vxe-table-column>
            <vxe-table-column width="70" title="状态">
              <template v-slot="{ row }">
                <a-switch :checked="row.status == '0' ? true : false" @change="handleStatus(row)" />
              </template>
            </vxe-table-column>
            <vxe-table-column field="createTime" title="创建时间">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.createTime) }}</span>
              </template>
            </vxe-table-column>
            <vxe-table-column field="userOperation" title="操作" width="240">
              <template v-slot="{ row }">
                <a-button type="primary" icon="edit" @click="handleUpdate(row)"> 修改 </a-button>
                <a-button type="danger" icon="delete" @click="handleDelete(row)"> 删除 </a-button>
                <a-button type="danger" icon="setting" @click="handleResetPwd(row)"> 重置 </a-button>
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
    </div>
    <!-- 添加或修改对话框 -->
    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      @cancel="handleCancel"
      okText="确定"
      cancelText="取消"
      width="45%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 8 }"
        :wrapperCol="{ span: 15 }"
        :model="formDialog"
        ref="formModel"
        :rules="rules"
      >
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="用户昵称" prop="nickName">
              <a-input v-model="formDialog.nickName" placeholder="请输入用户昵称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="归属部门">
              <treeselect v-model.trim="formDialog.deptId" :options="deptOptions" placeholder="请选择归属部门" />
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="手机号码">
              <a-input v-model="formDialog.phonenumber" placeholder="请输入手机号码"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="邮箱">
              <a-input v-model="formDialog.eamil" placeholder="请输入邮箱"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="用户名称" prop="userName">
              <a-input v-model="formDialog.userName" placeholder="请输入用户名称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="用户密码">
              <a-input-password v-model="formDialog.password" placeholder="请输入密码"> </a-input-password>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="用户性别">
              <a-select default-value="请选择" v-model="formDialog.sex">
                <a-select-option v-for="item in sexOptions" :key="item.dictValue">
                  {{ item.dictLabel }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="状态">
              <a-radio-group name="radioGroup" v-model="formDialog.status">
                <a-radio v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue"> {{ dict.dictLabel }} </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="角色">
              <a-select mode="multiple" v-model="formDialog.roleIds" placeholder="请选择">
                <a-select-option v-for="item in roleOptions" :key="item.id" :disabled="item.status == 1">
                  {{ item.roleName }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
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
    <!-- 重置密码对话框 -->
    <a-modal
      title="提示"
      :visible="visible"
      :maskClosable="false"
      okText="确定"
      cancelText="取消"
      @ok="handlePsd"
      @cancel="handleCancelPsd"
    >
    <div>
      <span>请输入"{{this.name}}"的新密码 </span>
      <a-input v-model="psdForm.password"></a-input>
    </div>
    </a-modal>
  </div>
</template>

<script>
// 接口地址
import hongtuConfig from '@/utils/services';
import moment from 'moment';
import Treeselect from '@riophae/vue-treeselect';
import '@riophae/vue-treeselect/dist/vue-treeselect.css';
const gData = [
  {
    key: '0-0',
    scopedSlots: {
      title: 'title',
    },
    title: '0-0',
    children: [
      {
        key: '0-0-0',
        scopedSlots: {
          title: 'title',
        },
        title: '0-0-0',
      },
      {
        key: '0-0-1',
        scopedSlots: {
          title: 'title',
        },
        title: '0-0-1',
      },
      {
        key: '0-0-2',
        scopedSlots: {
          title: 'title',
        },
        title: '0-0-2',
      },
    ],
  },
  {
    key: '0-1',
    scopedSlots: {
      title: 'title',
    },
    title: '0-1',
    children: [
      {
        key: '0-1-0',
        scopedSlots: {
          title: 'title',
        },
        title: '0-1-0',
      },
      {
        key: '0-1-1',
        scopedSlots: {
          title: 'title',
        },
        title: '0-1-1',
      },
      {
        key: '0-1-2',
        scopedSlots: {
          title: 'title',
        },
        title: '0-1-2',
      },
    ],
  },
  {
    key: '0-2',
    scopedSlots: {
      title: '0-2',
    },
    title: '0-2',
  },
];

const dataList = [];
const generateList = (data) => {
  for (let i = 0; i < data.length; i++) {
    const node = data[i];
    const key = node.key;
    dataList.push({ key, title: key });
    if (node.children) {
      generateList(node.children);
    }
  }
};
generateList(gData);

const getParentKey = (key, tree) => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};
export default {
  components: { Treeselect },
  data() {
    return {
      expandedKeys: [],
      searchValue: '',
      autoExpandParent: true,
      gData,
      queryParams: {
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined,
        pageNum: 1,
        pageSize: 10,
      },
      statusOptions: [],
      userListData: [],
      paginationTotal: 0,
      visibleModel: false, // 弹出框
      dialogTitle: '',
      deptOptions: [], // 部门下拉框数组
      replaceFields: {
        children: 'children',
        title: 'label',
        key: 'id',
      },
      roleOptions: [],
      ids: [],
      formDialog: {},
      sexOptions: [],
      rules: {
        userName: [{ required: true, message: '请输入用户昵称', trigger: 'blur' }],
        nickName: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
      },
      single: true,
      visible: false,
      psdForm: {},
      name: '',
    };
  },
  created() {
    this.getUserList();
    this.getTreeselect();
    hongtuConfig.getDicts('sys_normal_disable').then((res) => {
      if (res.code == 200) {
        this.statusOptions = res.data;
      }
      console.log(this.statusOptions);
    });
    hongtuConfig.getDicts('sys_user_sex').then((res) => {
      if (res.code == 200) {
        this.sexOptions = res.data;
      }
    });
  },
  methods: {
    selectBox(selection) {
      // console.log(selection.selection)
      this.single = selection.selection.length > 0 ? false: true
    },
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
      this.autoExpandParent = false;
    },
    onChange(e) {
      const value = e.target.value;
      const expandedKeys = dataList
        .map((item) => {
          if (item.title.indexOf(value) > -1) {
            return getParentKey(item.key, gData);
          }
          return null;
        })
        .filter((item, i, self) => item && self.indexOf(item) === i);
      Object.assign(this, {
        expandedKeys,
        searchValue: value,
        autoExpandParent: true,
      });
    },
    // 获取表格数据
    getUserList() {
      if (this.queryParams.dateRange) {
        this.dateRange = this.queryParams.dateRange;
      } else {
        this.dateRange = [];
      }
      hongtuConfig.userCofigList(this.addDateRange(this.queryParams, this.dateRange)).then((response) => {
        console.log(response);
        this.userListData = response.data.pageData;
        this.paginationTotal = response.data.totalCount;
      });
    },
    // 用户状态修改
    handleStatus(row) {
      row.status = row.status == '0' ? '1' : '0';
      let text = row.status === '0' ? '启用' : '停用';
      let statusData = {
        id: row.id,
        status: row.status,
      };
      this.$confirm({
        title: '确认要"' + text + '""' + row.userName + '"用户吗?',
        content: '',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          console.log(statusData);
          hongtuConfig.editUserStatus(statusData).then((res) => {
            if (res.code == 200) {
              this.$message.success('修改状态成功');
              this.getUserList();
            }
          });
        },
        onCancel() {},
      });
    },
    // 获取角色列表
    getRoles() {
      hongtuConfig.roleConfigList(this.queryParams).then((res) => {
        this.roleOptions = res.data.pageData;
        console.log(res);
      });
    },
    // 获取部门下拉树列表
    getTreeselect() {
      hongtuConfig.treeselect(this.formDialog).then((res) => {
        this.deptOptions = res.data;
      });
    },
    moment,
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },
    onTimeChange(value, dataString) {
      this.queryParams.beginTime = dataString[0];
      this.queryParams.endTime = dataString[1];
      console.log(this.queryParams);
    },
    // 表单重置
    reset() {
      this.formDialog = {
        id: undefined,
        deptId: undefined,
        userName: undefined,
        nickName: undefined,
        password: undefined,
        phonenumber: undefined,
        email: undefined,
        sex: undefined,
        status: '0',
        remark: undefined,
        postIds: [],
        roleIds: [],
      };
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getUserList();
    },
    // 分页事件
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.getUserList();
    },
    // 新增
    handleAdd() {
      this.reset();
      this.getTreeselect();
      this.getRoles();
      this.dialogTitle = '添加用户';
      // this.formDialog.password = this.initPassword
      this.visibleModel = true;
    },
    // 修改
    handleUpdate(row) {
      if (!row.id) {
        let cellsChecked = this.$refs.userListRef.getCheckboxRecords();
        cellsChecked.forEach((element) => {
          this.ids.push(element.id);
        });
      }
      this.reset();
      this.getTreeselect();
      this.getRoles();
      const id = row.id || this.ids;
      // this.ids = [];
      // this.ids.push(id);
      hongtuConfig.userCofigSearchById(id).then((response) => {
        if (response.code == 200) {
          console.log(response);
          this.formDialog = response.data;
          this.formDialog.postIds = response.data.postIds;
          this.formDialog.roleIds = response.data.roleIds;
          this.visibleModel = true;
          this.dialogTitle = '修改用户';
          this.formDialog.password = '';
        }
      });
    },
    handleOk() {
      console.log(this.formDialog);
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          if (!this.formDialog.id) {
            hongtuConfig.userCofigAdd(this.formDialog).then((response) => {
              console.log(response);
              if (response.code == 200) {
                this.$message.success(this.dialogTitle + '成功');
                this.visibleModel = false;
                this.getUserList();
              }
            });
          } else {
            hongtuConfig.userCofigEdit(this.formDialog).then((response) => {
              if (response.code == 200) {
                this.$message.success(this.dialogTitle + '成功');
                this.visibleModel = false;
                this.getUserList();
              }
            });
          }
        }
      });
    },
    handleCancel() {
      this.$refs.formModel.resetFields();
    },
    handleSelect() {},
    // 删除
    handleDelete(row) {
      // console.log(row)
      let ids = [];
      let deletName = [];
      if (!row.id) {
        let cellsChecked = this.$refs.userListRef.getCheckboxRecords();
        cellsChecked.forEach((element) => {
          ids.push(element.id);
          deletName.push(element.userName);
        });
      } else {
        ids.push(row.id);
        deletName.push(row.userName);
      }
      this.$confirm({
        title: '是否确认删除用户名称为' + deletName.join(',') + '的数据项',
        content: '',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          hongtuConfig.userCofigDelete(ids.join(',')).then((response) => {
            if (response.code == 200) {
              this.$message.success('删除成功');
              this.resetQuery();
            }
          });
        },
        onCancel() {},
      });
    },
    // 重置
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        beginTime: undefined,
        endTime: undefined,
      };
      this.getUserList();
    },
    handleResetPwd(row) {
      this.name = row.userName
      this.psdForm = row
      console.log(this.psdForm)
      this.psdForm.password = ''
      this.visible = true
    },
    handlePsd() {
      let data = {
        id: this.psdForm.id,
        password:this.psdForm.password
      }
      hongtuConfig.resetUserPwd(data).then((res) => {
        if(res.code == 200) {
          this.$message.success('重置密码成功！')
        }
      })
      this.visible = false
    },
    handleCancelPsd() {
      this.visible = false
    },
    handleExport() {
      hongtuConfig.exportUser(this.queryParams).then((res) => {
        this.downloadfileCommon(res);
      });
    },
  },
};
</script>

<style lang='scss' scoped>
.userMonitorTemplate {
  display: flex;
  width: 100%;
  height: 100%;
  font-family: Alibaba-PuHuiTi-Regular;
  padding: 0.375rem 0.25rem;
  box-shadow: $plane_shadow;

  .left {
    width: 15%;
    height: 100%;
    padding: 0 0.125rem;
    border-right: 1px solid #ece;
  }

  .right {
    padding-right: 0.25rem;

    .searchBox {
      margin-left: 0.25rem;
      height: 1.5rem;
      width: 100%;
      background: #f2f2f2;
      padding: 0.1875rem;
      border-radius: 0.1rem;

      .ant-input {
        width: 3rem;
        padding: 0 0.375rem 0 0.1875rem;
        margin-bottom: 0.45rem;
      }

      .ant-calendar-range-picker-input {
        height: 0.4rem;
      }

      .ant-btn {
        width: 1rem;
      }
    }

    #linkUser_content {
      margin: 0.25rem --0.1875rem 0.25rem 0.25rem;

      .ant-btn {
        width: 1rem;
        margin: 0 0.125rem 0.125rem 0;
      }
    }
  }
}
</style>
<style scoped>
.ant-radio-group {
  padding-top: 0.13rem;
}
</style>