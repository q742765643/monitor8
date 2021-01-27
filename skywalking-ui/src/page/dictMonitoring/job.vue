<template>
  <div class="jobTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm" ref="queryForm">
      <a-form-model-item label="任务名称" prop="taskName">
        <a-input v-model="queryParams.taskName" placeholder="请输入任务名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="执行状态" prop="triggerStatus">
        <a-select style="width: 120px" v-model="queryParams.triggerStatus" placeholder="字典状态">
          <a-select-option v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue">
            {{ dict.dictLabel }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item>
        <a-col :span="24" :style="{ textAlign: 'right' }">
          <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
        </a-col>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" @click="handleDelete"> 删除 </a-button>
        </a-col>
      </a-row>
      <!-- <a-table
      :row-selection="rowSelection"
      :row-key="(record) => record.id"
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      :loading="loading"
      @change="handleTableChange"
    >
      <span slot="triggerStatus" slot-scope="text">
        {{ statusFormat(text) }}
      </span>
      <span slot="createTime" slot-scope="text">
        {{ parseTime(text) }}
      </span>
      <span slot="operate" slot-scope="text, record">
        <a-button type="primary" icon="edit" v-if="record.triggerStatus == 0" @click="startJob(record)">
          启动
        </a-button>
        <a-button type="primary" icon="edit" v-if="record.triggerStatus == 1" @click="endJob(record)">
          停止
        </a-button>
        <a-button type="primary" icon="edit" @click="trigger(record)">
          立即执行
        </a-button>
        <a-button icon="edit" size="small" @click="handleUpdate(record)"> 编辑 </a-button>
        <a-button icon="delete" size="small" @click="handleDelete(record)"> 删除 </a-button>
      </span>
    </a-table> -->
      <vxe-table border ref="xTable" :data="data" highlight-hover-row align="center" :rules="rules">
        <vxe-table-column type="checkbox" width="80"></vxe-table-column>
        <vxe-table-column field="taskName" title="任务名称"></vxe-table-column>
        <vxe-table-column field="jobCron" title="执行策略" show-overflow> </vxe-table-column>
        <vxe-table-column field="triggerStatus" title="状态">
          <template v-slot="{ row }">
            <span v-if="row.triggerStatus == 0">停止 </span>
            <span v-if="row.triggerStatus == 1">运行 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="jobDesc" title="描述"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="operate" width="400" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" v-if="row.triggerStatus == 0" @click="startJob(row)"> 启动 </a-button>
            <a-button type="danger" icon="edit" v-if="row.triggerStatus == 1" @click="endJob(row)"> 停止 </a-button>
            <a-button type="primary" icon="edit" @click="trigger(row)"> 立即执行 </a-button>
            <a-button type="primary" icon="edit" @click="handleUpdate(row)"> 编辑 </a-button>
            <a-button type="danger" icon="delete" @click="handleDelete(row)"> 删除 </a-button>
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
    <a-modal
      class="dialogBox"
      v-model="visible"
      :title="title"
      okText="确定"
      cancelText="取消"
      width="50%"
      @ok="submitForm"
    >
      <a-form-model :label-col="{ span: 4 }" :wrapper-col="{ span: 19 }" :model="form" ref="form">
        <a-form-model-item label="任务名称" prop="taskName">
          <a-input v-model="form.taskName" placeholder="请输入任务名称"> </a-input>
        </a-form-model-item>
        <a-form-model-item label="cron策略" prop="jobCron">
          <el-popover v-model.trim="cronPopover">
            <vueCron @change="changeCron" @close="closeCronPopover" i18n="cn"></vueCron>
            <el-input
              class="jobCronEl"
              size="small"
              slot="reference"
              @click="cronPopover = true"
              v-model.trim="form.jobCron"
              placeholder="请输入cron策略"
            ></el-input>
          </el-popover>
        </a-form-model-item>
        <a-form-model-item label="调度执行器" prop="jobHandler">
          <a-select v-model="form.jobHandler" placeholder="调度执行器">
            <a-select-option v-for="dict in jobHandlerOptions" :key="dict.dictValue" :value="dict.dictValue">
              {{ dict.dictLabel }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="状态" prop="triggerStatus">
          <a-radio-group v-model="form.triggerStatus">
            <a-radio v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue">{{
              dict.dictLabel
            }}</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="备注" prop="jobDesc">
          <a-input v-model="form.jobDesc" type="textarea" placeholder="请输入内容"> </a-input>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>
<script>
import request from '@/utils/request';
// const columns = [
//   {
//     title: '任务名称',
//     dataIndex: 'taskName',
//     width: '15%',
//     scopedSlots: { customRender: 'taskName' },
//   },
//   {
//     title: '执行策略',
//     dataIndex: 'jobCron',
//   },
//   {
//     title: '状态',
//     dataIndex: 'triggerStatus',
//     scopedSlots: { customRender: 'triggerStatus' },
//   },
//   {
//     title: '描述',
//     dataIndex: 'jobDesc',
//   },
//   {
//     title: '创建时间',
//     dataIndex: 'createTime',
//     scopedSlots: { customRender: 'createTime' },
//   },
//   {
//     title: '操作',
//     dataIndex: 'operate',
//     scopedSlots: { customRender: 'operate' },
//   },
// ];

export default {
  data() {
    //校验是否为cron表达式
    var handleCronValidate = async (rule, value, callback) => {
      if (value == '') {
        callback(new Error('请输入cron策略!'));
      } else {
        let flag = true;
        await hongtuConfig
          .getNextTime({
            cronExpression: this.form.jobCron.split(' ?')[0] + ' ?',
          })
          .then((res) => {
            if (res.code == 200) {
              flag = false;
            }
          });
        if (flag) {
          callback(new Error('cron策略表达式错误!'));
        } else {
          callback();
        }
      }
    };
    return {
      data: [],
      pagination: {},
      loading: false,
      // columns,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        triggerStatus: undefined,
        taskName: undefined,
      },
      visible: false,
      form: {},
      statusOptions: [],
      jobHandlerOptions: [],
      title: '',
      ids: [],
      taskNames: [],
      paginationTotal: 0,
      cronExpression: '',
      cronPopover: false,
      rules: {
        jobCron: [{ required: true, validator: handleCronValidate, trigger: 'blur' }],
      }, //规则
    };
  },
  created() {
    this.getDicts('job_trigger_status').then((response) => {
      this.statusOptions = response.data;
    });
    this.getDicts('job_hander').then((response) => {
      this.jobHandlerOptions = response.data;
    });
  },
  mounted() {
    this.fetch();
  },
  computed: {
    rowSelection() {
      return {
        onChange: (selectedRowKeys, selectedRows) => {
          this.ids = selectedRows.map((item) => item.id);
          this.dictNames = selectedRows.map((item) => item.dictName);
        },
        getCheckboxProps: (record) => ({
          props: {
            disabled: record.id === 'Disabled User', // Column configuration not to be checked
            name: record.id,
          },
        }),
      };
    },
  },
  methods: {
    changeCron(val) {
      this.cronExpression = val;
      if (val.substring(0, 5) == '* * *') {
        this.msgError('小时,分钟,秒必填');
      } else {
        this.form.jobCron = val.split(' ?')[0] + ' ?';
        console.log(this.form.jobCron);
      }
    },
    closeCronPopover() {
      if (this.cronExpression.substring(0, 5) == '* * *') {
        return;
      }
      hongtuConfig
        .getNextTime({
          cronExpression: this.cronExpression.split(' ?')[0] + ' ?',
        })
        .then((res) => {
          let times = res.data;
          let html = '';
          times.forEach((element) => {
            html += '<p>' + element + '</p>';
          });
          this.$alert(html, '前5次执行时间', {
            dangerouslyUseHTMLString: true,
          }).then(() => {
            this.cronPopover = false;
          });
        });
    },
    statusFormat(status) {
      return this.selectDictLabel(this.statusOptions, status);
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
      this.loading = true;
      request({
        url: '/jobInfo/list',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        // const pagination = { ...this.pagination };
        // pagination.total = data.data.totalCount;
        this.loading = false;
        this.data = data.data.pageData;
        // this.pagination = pagination;
        this.paginationTotal = data.data.totalCount;
      });
    },
    handleAdd() {
      this.cronPopover = false;
      this.form.id = undefined;
      this.title = '新增调度任务';
      this.visible = true;
    },
    // handleTableChange(pagination, filters, sorter) {
    //   const pager = { ...this.pagination };
    //   this.queryParams.pageNum = pagination.current;
    //   this.pagination = pager;
    //   this.fetch();
    // },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.fetch();
    },
    handleUpdate(row) {
      this.cronPopover = false;
      this.form = {};
      const id = row.id || this.ids;
      request({
        url: '/jobInfo/' + id,
        method: 'get',
      }).then((response) => {
        this.form = response.data;
        this.form.triggerStatus = this.form.triggerStatus.toString();
        this.visible = true;
        this.title = '修改调度任务';
      });
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      const taskNames = row.taskName || this.taskNames;
      this.$confirm({
        title: '是否确认删除配置为"' + taskNames + '"的数据项?',
        content: '',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: () => {
          request({
            url: '/jobInfo/' + ids,
            method: 'delete',
          }).then((response) => {
            if (response.code === 200) {
              this.fetch();
              this.msgError('删除成功!');
            } else {
              this.msgError(response.msg);
            }
          });
        },
        onCancel() {},
      });
    },
    submitForm() {
      if (this.form.id != undefined) {
        request({
          url: '/jobInfo',
          method: 'post',
          data: this.form,
        }).then((response) => {
          if (response.code === 200) {
            this.msgSuccess('修改成功');
            this.visible = false;
            this.fetch();
          } else {
            this.msgError(response.msg);
          }
        });
      } else {
        request({
          url: '/jobInfo',
          method: 'post',
          data: this.form,
        }).then((response) => {
          if (response.code === 200) {
            this.msgSuccess('新增成功');
            this.visible = false;
            this.fetch();
          } else {
            this.msgError(response.msg);
          }
        });
      }
    },
    startJob(row) {
      const id = row.id;
      let data = { id: id, triggerStatus: 1, jobCron: row.jobCron };
      request({
        url: '/jobInfo/updateJob',
        method: 'post',
        data: data,
      }).then((response) => {
        this.$message.success('启动成功');
        this.handleQuery();
      });
    },
    endJob(row) {
      const id = row.id;
      let data = { id: id, triggerStatus: 0, jobCron: row.jobCron };
      request({
        url: '/jobInfo/updateJob',
        method: 'post',
        data: data,
      }).then((response) => {
        this.$message.success('停止成功');
        this.handleQuery();
      });
    },
    trigger(row) {
      request({
        url: '/jobInfo/trigger/' + row.id,
        method: 'get',
      }).then((response) => {
        this.$message.success('执行成功');
      });
    },
  },
};
</script>
