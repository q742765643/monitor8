<template>
  <div class="managerTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="菜单名称">
        <a-input v-model="queryParams.menuName" placeholder="请输入菜单名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select v-model="queryParams.visible" style="width: 120px">
          <a-select-option value="">全部</a-select-option>
          <a-select-option :value="dict.dictValue" v-for="dict in visibleOptions" :key="dict.dictValue">
            {{ dict.dictLabel }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
      </a-form-model-item>
    </a-form-model>

    <div class="tableDateBox">
      <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
        </a-col>
        <!-- <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleLogin"> 登录 </a-button>
        </a-col> -->
      </a-row>
      <vxe-table :data="tableData" align="center" :tree-config="{}" highlight-hover-row ref="tablevxe">
        <vxe-table-column field="menuName" title="菜单名称" tree-node></vxe-table-column>
        <vxe-table-column field="icon" title="图标" show-overflow></vxe-table-column>
        <vxe-table-column field="orderNum" title="排序" show-overflow></vxe-table-column>
        <vxe-table-column field="component" title="组件路径" show-overflow></vxe-table-column>
        <vxe-table-column field="visible" title="可见" show-overflow>
          <template v-slot="{ row }">
            <span v-if="row.visible == 0">显示 </span>
            <span v-if="row.visible == 1">隐藏 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间" show-overflow>
          <template v-slot="{ row }">
            <span> {{ parseTime(row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column width="260" field="date" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="handleEdit(row)"> 编辑 </a-button>
            <a-button type="danger" icon="delete" @click="handleDelete(row)"> 删除 </a-button>
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
      width="50%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model
        :label-col="{ span: 6 }"
        :wrapperCol="{ span: 17 }"
        :model="formDialog"
        ref="formModel"
        :rules="rules"
      >
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="上级菜单" :label-col="{ span: 3 }" :wrapperCol="{ span: 20 }">
              <treeselect
                v-model.trim="formDialog.parentId"
                :options="menuOptions"
                :show-count="true"
                placeholder="选择上级菜单"
              />
              <!-- <a-tree-select
                v-model="formDialog.parentId"
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :tree-data="treeData"
                placeholder="Please select"
                tree-default-expand-all
              >
              </a-tree-select> -->
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="菜单类型" :label-col="{ span: 3 }" :wrapperCol="{ span: 20 }">
              <a-radio-group :default-value="formDialog.menuType" v-model="formDialog.menuType">
                <a-radio value="M"> 目录 </a-radio>
                <a-radio value="C"> 菜单 </a-radio>
                <a-radio value="F"> 按钮 </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>

          <a-col :span="24">
            <a-form-model-item
              label="菜单图标"
              v-if="formDialog.menuType != 'F'"
              :label-col="{ span: 3 }"
              :wrapperCol="{ span: 20 }"
            >
              <a-popover title="请选择菜单图标" trigger="click" placement="bottomLeft">
                <div slot="content" class="iconFontBox">
                  <div
                    v-for="(item, index) in iconList"
                    :key="index"
                    @click="
                      formDialog.icon = item.icon;
                      chosedName = item.name;
                    "
                  >
                    <span class="iconfont" :class="item.icon"></span>
                    {{ item.name }}
                  </div>
                </div>
                <a-input v-model="formDialog.icon" class="iconInput"> </a-input>
                <span class="iconChosedBox"
                  ><span class="iconfont" :class="formDialog.icon"></span>{{ chosedName }}</span
                >
              </a-popover>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="菜单名称" prop="menuName">
              <a-input v-model="formDialog.menuName"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="显示排序" prop="orderNum">
              <a-input-number v-model="formDialog.orderNum"> </a-input-number>
            </a-form-model-item>
          </a-col>
          <a-col :span="12" v-if="formDialog.menuType != 'F'">
            <a-form-model-item label="是否外链">
              <a-radio-group :default-value="formDialog.isFrame" v-model="formDialog.isFrame">
                <a-radio :value="0"> 是 </a-radio>
                <a-radio :value="1"> 否 </a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="12" v-if="formDialog.menuType == 'C'">
            <a-form-model-item label="组件路径">
              <a-input v-model="formDialog.component" placeholder="请输入组件路径"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="权限标识" v-if="formDialog.menuType != 'M'">
              <a-input v-model="formDialog.perms"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="路由地址" v-if="formDialog.menuType != 'F'">
              <a-input v-model="formDialog.path"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="菜单状态" v-if="formDialog.menuType != 'F'">
              <a-radio-group :default-value="formDialog.visible" v-model="formDialog.visible">
                <a-radio v-for="dict in visibleOptions" :key="dict.dictValue" :value="dict.dictValue">
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
  import echarts from 'echarts';
  // 接口地址
  import hongtuConfig from '@/utils/services';
  import Treeselect from '@riophae/vue-treeselect';
  import '@riophae/vue-treeselect/dist/vue-treeselect.css';
  import moment from 'moment';
  export default {
    components: { Treeselect },
    data() {
      return {
        chosedName: '',
        queryParams: {
          visible: '',
          menuName: '',
        },
        tableData: [], //表格
        paginationTotal: 0,
        visibleModel: false, //弹出框
        dialogTitle: '',
        // 菜单树选项
        menuOptions: [],
        formDialog: {
          // taskName: '',
          // menuType: 'M',
          // isFrame: '0',
          // visible: '1',
        },
        rules: {
          menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
          orderNum: [{ required: true, message: '请输入排序', trigger: 'blur' }],
        }, //规则
        iconList: [
          {
            icon: 'iconicon-test',
            name: '综合监控',
          },
          {
            icon: 'iconchains',
            name: '链路发现',
          },
          {
            icon: 'iconshezhi',
            name: '设置',
          },
          {
            icon: 'iconxianshiqi',
            name: '主机监视',
          },
          {
            icon: 'iconthree',
            name: '业务视图',
          },
          {
            icon: 'iconyewu',
            name: '文件监控',
          },
          {
            icon: 'iconlaba',
            name: '告警管理',
          },
          {
            icon: 'iconyonghu',
            name: '用户管理',
          },
        ],
        imgcode: '',
        visibleOptions: [],
      };
    },
    created() {
      hongtuConfig.getDicts('sys_show_hide').then((res) => {
        if (res.code == 200) {
          this.visibleOptions = res.data;
        }
      });
      this.queryTable();
    },
    // mounted() {
    //   hongtuConfig.captchaImage().then((response) => {
    //     this.imgcode = response.data.code;
    //   });
    // },
    methods: {
      /* 查询 */
      handleQuery() {
        this.queryTable();
      },
      /* 重置 */
      resetQuery() {
        this.queryParams = {
          menuName: undefined,
          visible: '',
        };
        this.queryTable();
      },

      /* table方法 */
      queryTable() {
        hongtuConfig.menuList(this.queryParams).then((response) => {
          this.tableData = response.data;
          console.log(response.data);
        });
      },
      /* 字典格式化 */
      statusFormat(list, text) {
        return hongtuConfig.formatterselectDictLabel(list, text);
      },
      handleAdd() {
        /* 新增 */
        this.reset();
        this.getTreeselect();
        console.log(this.menuOptions);
        this.dialogTitle = '新增';
        this.formDialog = {
          taskName: '',
          orderNum: '',
          menuType: 'M',
          isFrame: 0,
          visible: '1',
        };
        this.visibleModel = true;
      },
      /** 查询菜单下拉树结构 */
      getTreeselect() {
        hongtuConfig.menuTreeselect().then((res) => {
          console.log(res);
          this.menuOptions = [];
          const menu = { id: 0, label: '主类目', children: [] };
          if (this.dialogTitle == '修改菜单') {
            menu.children = res.data;
            this.resetData(menu.children, this.formDialog.menuName);
            console.log(menu.children);
          } else {
            menu.children = res.data;
          }
          this.menuOptions.push(menu);
        });
      },
      resetData(dataArr, name) {
        for (var i in dataArr) {
          if (dataArr[i].label == name) {
            dataArr[i].isDisabled = true;
          } else {
            this.resetData(dataArr[i].children, name);
          }
        }
      },
      // 表单重置
      reset() {
        this.formDialog = {
          id: undefined,
          parentId: 0,
          menuName: undefined,
          icon: undefined,
          menuType: 'M',
          orderNum: undefined,
          isFrame: 1,
          visible: '',
        };
        // this.resetForm("formModel");
        // this.formDialog.parentId = undefined
      },
      /* 编辑 */
      handleEdit(row) {
        this.reset();
        console.log(this.formDialog.parentId);
        this.getTreeselect();
        hongtuConfig.getMenu(row.id).then((response) => {
          if (response.code == 200) {
            this.formDialog = response.data;
            // this.iconList.forEach((element) => {
            //   if (element.icon == this.formDialog.icon) {
            //     this.chosedName = element.name;
            //   }
            // });
            this.visibleModel = true;
            this.dialogTitle = '修改菜单';
          }
        });
      },
      /* 确认 */
      handleOk() {
        console.log(this.formDialog);
        this.$refs.formModel.validate((valid) => {
          if (valid) {
            if (this.formDialog.id) {
              hongtuConfig.updateMenu(this.formDialog).then((response) => {
                if (response.code == 200) {
                  this.$message.success(this.dialogTitle + '成功');
                  this.visibleModel = false;
                  this.queryTable();
                }
              });
            } else {
              hongtuConfig.addMenu(this.formDialog).then((res) => {
                if (res.code == 200) {
                  this.$message.success('新增成功');
                  this.visibleModel = false;
                  this.queryTable();
                }
              });
            }
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      handleCancel() {
        this.visibleModel = false;
        this.reset();
      },
      // handleLogin() {
      //   let obj = {
      //     username: 'admin',
      //     password: '2020Sod123',
      //     code: this.imgcode,
      //     uuid: '15812e460ae548dca98143f3bac93b4f',
      //   };
      //   hongtuConfig.userLogin(this.imgcode).then((response) => {
      //     alert('111');
      //   });
      // },
      /* 删除 */
      handleDelete(row) {
        let ids = [];
        let taskNames = [];
        if (!row.id) {
          let cellsChecked = this.$refs.tablevxe.getCheckboxRecords();
          cellsChecked.forEach((element) => {
            ids.push(element.id);
            taskNames.push(element.menuName);
          });
        } else {
          ids.push(row.id);
          taskNames.push(row.menuName);
        }
        this.$confirm({
          title: '是否确认删除任务名称为"' + taskNames.join(',') + '"的数据项?',
          content: '',
          okText: '是',
          okType: 'danger',
          cancelText: '否',
          onOk: () => {
            hongtuConfig.delMenu(ids.join(',')).then((response) => {
              if (response.code == 200) {
                this.$message.success('删除成功');
                this.queryTable();
              }
            });
          },
          onCancel() {},
        });
      },
    },
  };
</script>

<style lang="scss" scoped></style>
