<template>
  <div class="userMonitorTemplate">
    <!-- <div class="childLeftAside">
      <a-input-search
        style="margin-bottom: 8px"
        allow-clear
        v-model="searchStr"
        placeholder="请输入部门名称"
        @change="onChange"
      />
      <el-scrollbar>
        <a-tree
          v-model="checkedKeys"
          :selectedKeys="selectedKeys"
          :expanded-keys="expandedKeys"
          :auto-expand-parent="autoExpandParent"
          :tree-data="deptOptions"
          :replaceFields="replaceFields"
          @select="onSelect"
          @expand="onExpand"
        >
          <template slot="title" slot-scope="{ label }">
            <span
              v-html="label.replace(new RegExp(searchValue, 'g'), '<span style=color:#f50>' + searchValue + '</span>')"
            ></span>
          </template>
        </a-tree>
      </el-scrollbar>
    </div> -->
    <el-scrollbar>
      <div class="timerSelect">
        <a-form-model layout="inline" :model="queryParams" ref="queryForm" class="queryForm">
          <a-form-model-item label="用户名称">
            <a-input v-model="queryParams.userName" placeholder="请输入用户名称" style="width: 120px"> </a-input>
          </a-form-model-item>

          <a-form-model-item label="状态">
            <a-select v-model="queryParams.status" style="width: 70px">
              <a-select-option v-for="item in statusOptions" :key="item.dictValue">{{
                item.dictLabel
              }}</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="创建时间">
            <!-- <el-date-picker
                type="datetimerange"
                v-model="dateRange"
                value-format="yyyy-MM-dd HH:mm:ss"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="onTimeChange"
                align="right"
              >
              </el-date-picker> -->

            <selectDate
              class="selectDate"
              @changeDate="changeDate"
              :HandleDateRange="dateRange"
              ref="selectDateRef"
            ></selectDate>

            <!--
                v-model="queryParams.dateRange"  -->
          </a-form-model-item>
          <a-form-model-item>
            <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
            <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
          </a-form-model-item>
        </a-form-model>
      </div>
      <div class="tableDateBox">
        <a-row type="flex" class="rowToolbar" :gutter="10">
          <a-col :span="1.5">
            <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
          </a-col>
          <a-col :span="1.5">
            <a-button type="primary" :disabled="single" icon="edit" @click="handleUpdate"> 修改 </a-button>
          </a-col>
          <a-col :span="1.5">
            <a-button type="danger" :disabled="single" icon="delete" @click="handleDelete"> 删除 </a-button>
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
          :data="userListData"
          align="center"
          @checkbox-change="selectBox"
          highlight-hover-row
          ref="userListRef"
          border
        >
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
          <vxe-table-column field="createTime" title="创建时间" width="180">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="userOperation" title="操作" width="260">
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
    </el-scrollbar>
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
          <a-col :span="12">
            <a-form-model-item label="手机号码">
              <a-input v-model="formDialog.phonenumber" placeholder="请输入手机号码"> </a-input>
            </a-form-model-item>
          </a-col>

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
          <a-col :span="12">
            <a-form-model-item label="状态">
              <a-radio-group name="radioGroup" v-model="formDialog.status">
                <a-radio v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue">
                  {{ dict.dictLabel }}
                </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="角色">
              <a-select mode="multiple" v-model="formDialog.roleIds" placeholder="请选择">
                <a-select-option v-for="item in roleOptions" :key="item.id" :disabled="item.status == 1">
                  {{ item.roleName }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="备注" :label-col="{ span: 4 }" :wrapperCol="{ span: 20 }">
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
        <span>请输入"{{ this.name }}"的新密码 </span>
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
import selectDate from '@/components/date/select.vue';
export default {
  components: { Treeselect, selectDate },
  data() {
    return {
      expandedKeys: [],
      backupsExpandedKeys: [],
      searchValue: '',
      autoExpandParent: false,
      checkedKeys: [],
      selectedKeys: [],
      searchStr: '',
      replaceFields: {
        children: 'children',
        title: 'label',
        key: 'id',
      },
      dateRange: [],
      queryParams: {
        userName: undefined,
        phonenumber: undefined,
        status: undefined,
        pageNum: 1,
        pageSize: 10,
      },
      statusOptions: [],
      userListData: [],
      paginationTotal: 0,
      visibleModel: false, // 弹出框
      dialogTitle: '',
      deptOptions: [], // 部门下拉框数组
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
      dateRange: [],
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
    changeDate(data) {
      this.dateRange = data;
    },
    selectBox(selection) {
      // console.log(selection.selection)
      this.single = selection.selection.length > 0 ? false : true;
    },
    onChange() {
      var vm = this;
      //添加这行代码是为了只点击搜索才触发
      vm.searchValue = vm.searchStr;
      //如果搜索值为空，则不展开任何项，expandedKeys置为空
      //如果搜索值不位空，则expandedKeys的值要为搜索值的父亲及其所有祖先
      if (vm.searchValue === '') {
        vm.expandedKeys = [];
      } else {
        //首先将展开项与展开项副本置为空
        vm.expandedKeys = [];
        vm.backupsExpandedKeys = [];
        //获取所有存在searchValue的节点
        let candidateKeysList = vm.getkeyList(vm.searchValue, vm.deptOptions, []);
        //遍历满足条件的所有节点
        candidateKeysList.map((item) => {
          //获取每个节点的母亲节点
          var key = vm.getParentKey(item, vm.deptOptions);
          //当item是最高一级，父key为undefined，将不放入到数组中
          //如果母亲已存在于数组中，也不放入到数组中
          if (key && !vm.backupsExpandedKeys.some((item) => item === key)) vm.backupsExpandedKeys.push(key);
        });
        let length = this.backupsExpandedKeys.length;
        for (let i = 0; i < length; i++) {
          vm.getAllParentKey(vm.backupsExpandedKeys[i], vm.deptOptions);
        }
        vm.expandedKeys = vm.backupsExpandedKeys.slice();
      }
    },
    //获取节点中含有value的所有id集合
    getkeyList(value, tree, keyList) {
      //遍历所有同一级的树
      for (let i = 0; i < tree.length; i++) {
        let node = tree[i];
        //如果该节点存在value值则push
        if (node.label.indexOf(value) > -1) {
          keyList.push(node.id);
        }
        //如果拥有孩子继续遍历
        if (node.children) {
          this.getkeyList(value, node.children, keyList);
        }
      }
      //因为是引用类型，所有每次递归操作的都是同一个数组
      return keyList;
    },
    //该递归主要用于获取key的父亲节点的key值
    getParentKey(key, tree) {
      let parentKey, temp;
      //遍历同级节点
      for (let i = 0; i < tree.length; i++) {
        const node = tree[i];
        if (node.children) {
          //如果该节点的孩子中存在该key值，则该节点就是我们要找的父亲节点
          //如果不存在，继续遍历其子节点
          if (node.children.some((item) => item.id === key)) {
            parentKey = node.id;
          } else if ((temp = this.getParentKey(key, node.children))) {
            //parentKey = this.getParentKey(key,node.children)
            //改进，避免二次遍历
            parentKey = temp;
          }
        }
      }
      return parentKey;
    },
    //获取该节点的所有祖先节点
    getAllParentKey(key, tree) {
      var parentKey;
      if (key) {
        //获得父亲节点
        parentKey = this.getParentKey(key, tree);
        if (parentKey) {
          //如果父亲节点存在，判断是否已经存在于展开列表里，不存在就进行push
          if (!this.backupsExpandedKeys.some((item) => item === parentKey)) {
            this.backupsExpandedKeys.push(parentKey);
          }
          //继续向上查找祖先节点
          this.getAllParentKey(parentKey, tree);
        }
      }
    },
    onExpand(expandedKeys) {
      //用户点击展开时，取消自动展开效果
      this.expandedKeys = expandedKeys;
      this.autoExpandParent = false;
    },
    onCheck(checkedKeys) {
      // console.log('onCheck', checkedKeys);
      this.checkedKeys = checkedKeys;
    },
    onSelect(selectedKeys, info) {
      console.log('onSelect', info);
      this.selectedKeys = selectedKeys;
      this.queryParams.deptId = selectedKeys[0];
      this.getUserList();
    },
    // 获取表格数据
    getUserList() {
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
    onTimeChange(val) {
      this.dateRange = val;
      // console.log(this.queryParams.dateRange);
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
          this.ids = [];
          this.ids.push(element.id);
        });
      }
      this.reset();
      this.getTreeselect();
      this.getRoles();
      const id = row.id || this.ids;
      // this.ids = [];
      // this.ids.push(id);
      console.log(id);
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
                this.$refs.formModel.resetFields();
                this.visibleModel = false;
                this.getUserList();
              }
            });
          }
        }
      });
    },
    handleCancel() {},
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
      this.$refs.selectDateRef.changeDate('resetQuery');
      this.getUserList();
    },
    handleResetPwd(row) {
      this.name = row.userName;
      this.psdForm = row;
      console.log(this.psdForm);
      this.psdForm.password = '';
      this.visible = true;
    },
    handlePsd() {
      let data = {
        id: this.psdForm.id,
        password: this.psdForm.password,
      };
      hongtuConfig.resetUserPwd(data).then((res) => {
        if (res.code == 200) {
          this.$message.success('重置密码成功！');
        }
      });
      this.visible = false;
    },
    handleCancelPsd() {
      this.visible = false;
    },
    handleExport() {
      hongtuConfig.exportUser(this.queryParams).then((res) => {
        this.downloadfileCommon(res);
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.userMonitorTemplate {
  width: 100%;
  height: 100%;
  box-shadow: $plane_shadow;

  .el-scrollbar {
    height: 100%;
  }
  .dialogBox {
    .ant-form-item {
      margin-bottom: 30px;
    }
  }
}
</style>
<style scoped>
.ant-radio-group {
  padding-top: 10px;
}
</style>
