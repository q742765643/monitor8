<template>
  <div>
    <div  class="head">
      <a-form-model layout="inline"  :model="queryParams"  class="queryForm" ref="queryForm" >
        <a-form-model-item label="目录名称"  prop="name">
          <a-input v-model="queryParams.name" placeholder="请输入目录名称">
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="ip" prop="ip">
          <a-input v-model="queryParams.ip" placeholder="请输入ip">
          </a-input>
        </a-form-model-item>
        <a-form-model-item >
          <a-col :span="24" :style="{ textAlign: 'right' }">
            <a-button type="primary" html-type="submit" @click="handleQuery">
              搜索
            </a-button>
            <a-button :style="{ marginLeft: '8px' }" @click="resetQuery">
              重置
            </a-button>
          </a-col>
        </a-form-model-item>
      </a-form-model>
    </div>
    <a-row type="flex"  style="margin-left: 2% " >
      <a-col :span="1.5">
        <a-button type="primary"
                  icon="plus"
                  @click="handleAdd">
          新增
        </a-button>
        <a-button type="danger"
                  icon="delete"
                  @click="handleDelete">
          删除
        </a-button>
      </a-col>
    </a-row>
    <vxe-table border ref="xTable" :data="tableData" stripe align="center" @checkbox-change="rowSelection">
      <vxe-table-column type="checkbox"></vxe-table-column>
      <vxe-table-column field="name" title="名称"></vxe-table-column>
      <vxe-table-column field="ip" title="ip"> </vxe-table-column>
      <vxe-table-column field="user" title="用户名"></vxe-table-column>
      <vxe-table-column field="pass" title="密码"></vxe-table-column>
      <vxe-table-column field="createTime" title="创建时间" show-overflow>
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </vxe-table-column>
      <vxe-table-column width="160" field="date" title="操作">
        <template v-slot="{ row }">
          <a-button type="primary" icon="edit" @click="handleUpdate(row)">
            编辑
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
            :total="total"
            :page-sizes="[10, 20, 100]"
            @page-change="fetch"
            :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
    ></vxe-pager>
      <a-modal v-model="visible" :title="title" okText="确定" cancelText="取消"
             width="50%" @ok="submitForm">
      <a-form-model  :label-col="{ span: 5 }" :wrapper-col="{ span: 12 }" :model="form" ref="form" >
        <a-form-model-item  label="名称"  prop="name">
          <a-input v-model="form.name" placeholder="请输入名称">
          </a-input>
        </a-form-model-item>
        <a-form-model-item  label="远程ip"  prop="ip">
          <a-input v-model="form.ip" placeholder="请输入ip">
          </a-input>
        </a-form-model-item>
        <a-form-model-item  label="用户名"  prop="user">
          <a-input v-model="form.user" placeholder="请输入用户名">
          </a-input>
        </a-form-model-item>
        <a-form-model-item  label="密码"  prop="pass">
          <a-input v-model="form.pass" placeholder="请输入密码">
          </a-input>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>

</template>

<script>
  import request from "@/utils/request";
  export default {
    data() {
      return {
        tableData: [],
        total: 0,
        // 日期范围
        dateRange: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          name: undefined,
          ip: undefined
        },
        form: {},
        title: "",
        ids: [],
        names: [],
        visible : false,
      };
    },
    created(){
    },
    mounted() {
      this.fetch();
    },
    computed: {

    },
    methods: {
      statusFormat(status) {
        return this.selectDictLabel(this.statusOptions,status);
      },
      rowSelection({selection}) {
        this.ids = selection.map(item => item.id);
        this.names = selection.map(item => item.name);
      },
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.fetch();
      },
      resetQuery() {
        this.dateRange = [];
        this.$refs.queryForm.resetFields();
        this.handleQuery();
      },
      fetch() {
        request({
          url:'/directoryAccount/list',
          method: 'get',
          params: this.addDateRange(this.queryParams, this.dateRange)
        }).then(data => {
          this.tableData = data.data.pageData;
          this.total = data.data.totalCount;
        });
      },
      handleAdd(){
        this.form.id=undefined;
        this.visible = true;
        this.title="添加共享目录";
      },
      handleUpdate(row) {
        this.form={};
        const id = row.id || this.ids;
        request({
          url: '/directoryAccount/' + id,
          method: 'get'
        }).then(response => {
          this.form = response.data;
          this.visible = true;
          this.title = "修改共享目录";
        });
      },
      handleDelete(row) {
        const ids = row.id || this.ids;
        const names = row.name || this.names;
        this.$confirm({
          title:  '是否确认删除字典标签为"' + names + '"的数据项?',
          content: '',
          okText: '是',
          okType: 'danger',
          cancelText: '否',
          onOk:()=> {
            request({
              url: '/directoryAccount/' + ids,
              method: 'delete'
            }).then((response) => {
              if (response.code === 200) {
                this.fetch();
                this.msgError("删除成功!");
              } else {
                this.msgError(response.msg);
              }
            })
          },
          onCancel() {

          },
        });
      },
      submitForm(){
        if (this.form.id != undefined) {
          request({
            url: '/directoryAccount',
            method: 'post',
            data: this.form
          }).then(response => {
            if (response.code === 200) {
              this.msgSuccess("修改成功");
              this.visible=false;
              this.fetch();
            } else {
              this.msgError(response.msg);
            }
          });
        }else {
          request({
            url: '/directoryAccount',
            method: 'post',
            data: this.form
          }).then(response => {
            if (response.code === 200) {
              this.msgSuccess("新增成功");
              this.visible = false;
              this.fetch();
            } else {
              this.msgError(response.msg);
            }
          });
        }
      }
    },
  };
</script>
<style scoped>
.ant-row-flex .ant-btn {
  margin: 15px 15px 15px 0;
}
</style>
