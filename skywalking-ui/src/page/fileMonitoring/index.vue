<template>
  <div class="fileMonitorTemplate">
    <div class="timerSelect">
      <a-form-model layout="inline" :model="queryParams" class="queryForm">
        <a-form-model-item label="任务名称">
          <a-input v-model="queryParams.taskName" placeholder="请输入任务名称"> </a-input>
        </a-form-model-item>
        <a-form-model-item label="时间">
          <selectDate
            class="selectDate"
            @changeDate="changeDate"
            :HandleDateRange="dateRange"
            ref="selectDateRef"
          ></selectDate>
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
          <a-button type="danger" icon="delete" @click="handleDelete"> 删除 </a-button>
        </a-col>
      </a-row>
      <vxe-table :data="tableData" align="center" highlight-hover-row ref="tablevxe">
        <vxe-table-column type="checkbox"></vxe-table-column>
        <vxe-table-column field="taskName" title="任务名称"></vxe-table-column>
        <vxe-table-column field="triggerStatus" title="状态" show-overflow>
          <template v-slot="{ row }">
            <span v-if="row.triggerStatus == 0">未启动 </span>
            <span v-if="row.triggerStatus == 1">启动 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="fileNum" title="应到数量" show-overflow></vxe-table-column>
        <vxe-table-column field="fileSize" title="应到大小" show-overflow></vxe-table-column>
        <vxe-table-column field="isUt" title="时区" show-overflow>
          <template v-slot="{ row }">
            <span v-if="row.isUt == 0">北京时 </span>
            <span v-if="row.isUt == 1">世界时 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="jobCron" title="cron策略" show-overflow></vxe-table-column>
        <vxe-table-column width="320" field="date" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" v-if="row.triggerStatus == 0" @click="startJob(row)"> 启动 </a-button>
            <a-button type="primary" icon="edit" v-if="row.triggerStatus == 1" @click="endJob(row)"> 停止 </a-button>
            <a-button type="primary" icon="edit" @click="handleEdit(row)"> 编辑 </a-button>
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
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      okText="确定"
      cancelText="取消"
      width="80%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox fileMonitoringdialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 5 }"
        :wrapperCol="{ span: 19 }"
        :model="formDialog"
        ref="formModel"
        :rules="rules"
      >
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="任务名称" prop="taskName">
              <a-input v-model="formDialog.taskName" placeholder="请输入任务名称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="状态" prop="triggerStatus">
              <a-select v-model="formDialog.triggerStatus" placeholder="字典状态">
                <a-select-option :value="0"> 未启动 </a-select-option>
                <a-select-option :value="1"> 启动 </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="扫描方式" prop="scanType">
              <a-radio-group v-model="formDialog.scanType">
                <a-radio v-for="dict in scanTypeOptions" :value="dict.dictValue" :key="dict.dictValue">{{
                  dict.dictLabel
                }}</a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="远程访问凭证" v-if="formDialog.scanType == '1'" prop="acountId">
              <a-select v-model="formDialog.acountId" placeholder="远程访问凭证">
                <a-select-option v-for="dict in accountOptions" :value="dict.id" :key="dict.id">
                  {{ dict.name }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="资料文件目录规则" prop="folderRegular">
              <a-input v-model="formDialog.folderRegular" placeholder="资料文件目录规则"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="资料文件名规则 " prop="filenameRegular">
              <a-input v-model="formDialog.filenameRegular" placeholder="资料文件名规则 "> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item
              :label-col="{ span: 3 }"
              :wrapperCol="{ span: 21 }"
              label="文件路径样例"
              prop="fileSample"
            >
              <a-input-search v-model="formDialog.fileSample" placeholder="请输入文件路径样例" @search="regularCheck">
                <a-button slot="enterButton"> 校验 </a-button>
              </a-input-search>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="应到数量" prop="fileNum">
              <a-input-number v-model="formDialog.fileNum" placeholder="请输入应到数量"> </a-input-number>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="应到大小" prop="fileSize">
              <a-input-number v-model="formDialog.fileSize" placeholder="请输入应到大小"> </a-input-number>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="时区" prop="isUt">
              <a-select v-model="formDialog.isUt">
                <a-select-option :value="0"> 北京时 </a-select-option>
                <a-select-option :value="1"> 世界时 </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="cron策略" prop="jobCron">
              <el-popover v-model.trim="cronPopover">
                <vueCron @change="changeCron" @close="closeCronPopover" i18n="cn"></vueCron>
                <el-input
                  class="jobCronEl"
                  size="small"
                  slot="reference"
                  @click="cronPopover = true"
                  v-model.trim="formDialog.jobCron"
                  placeholder="请输入cron策略"
                ></el-input>
              </el-popover>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }" label="任务描述" prop="jobDesc">
              <a-input type="textarea" v-model="formDialog.jobDesc" placeholder="任务描述"> </a-input>
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
import request from '@/utils/request';
import moment from 'moment';
import selectDate from '@/components/date/select.vue';
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
            cronExpression: this.msgFormDialog.jobCron.split(' ?')[0] + ' ?',
          })
          .then((res) => {
            if (res.data.code == 200) {
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
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskName: '',
        beginTime: '',
        endTime: '',
        timeRange: [],
      },
      tableData: [], //表格
      paginationTotal: 0,
      visibleModel: false, //弹出框
      dialogTitle: '',
      formDialog: {
        taskName: '',
        jobCron: '',
      },
      scanTypeOptions: [],
      accountOptions: [],
      rules: {
        taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
        triggerStatus: [{ required: true, message: '请选择任务状态', trigger: 'change' }],
        acountId: [{ required: true, message: '请选择远程访问凭证', trigger: 'change' }],
        filenameRegular: [{ required: true, message: '请输入资料文件名规则', trigger: 'blur' }],
        folderRegular: [{ required: true, message: '请输入资料文件目录规则', trigger: 'blur' }],
        fileNum: [{ required: true, message: '请输入应到数量', trigger: 'blur' }],
        fileSize: [{ required: true, message: '请输入应到大小', trigger: 'blur' }],
        jobCron: [{ required: true, validator: handleCronValidate, trigger: 'blur' }],
      }, //规则
      cronExpression: '',
      cronPopover: false,
      dateRange: [],
    };
  },
  components: { selectDate },
  created() {
    this.getDicts('scan_type').then((response) => {
      this.scanTypeOptions = response.data;
    });
    this.selectAcount();
    this.queryTable();
  },
  mounted() {},
  methods: {
    changeDate(data) {
      this.dateRange = data;
    },
    changeCron(val) {
      this.cronExpression = val;
      if (val.substring(0, 5) == '* * *') {
        this.msgError('小时,分钟,秒必填');
      } else {
        this.formDialog.jobCron = val.split(' ?')[0] + ' ?';
        console.log(this.formDialog.jobCron);
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
    moment,
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },

    selectAcount() {
      request({
        url: '/directoryAccount/selectAll',
        method: 'get',
      }).then((response) => {
        this.accountOptions = response.data;
      });
    },
    regularCheck() {
      request({
        url: '/fileMonitor/regularCheck',
        method: 'post',
        data: this.formDialog,
      }).then((response) => {
        if (response.code === 200) {
          this.msgSuccess('校验成功');
        } else {
          this.msgError(response.msg);
        }
      });
    },
    /* 查询 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryTable();
    },
    /* 重置 */
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        taskName: '',
        beginTime: '',
        endTime: '',
      };
      this.$refs.selectDateRef.changeDate();
      this.queryTable();
    },
    /* 翻页 */
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.queryTable();
    },
    /* table方法 */
    queryTable() {
      hongtuConfig.fileMonitorList(this.addDateRange(this.queryParams, this.dateRange)).then((response) => {
        this.tableData = response.data.pageData;
        this.paginationTotal = response.data.totalCount;
      });
    },
    /* 字典格式化 */
    statusFormat(list, text) {
      return hongtuConfig.formatterselectDictLabel(list, text);
    },
    handleAdd() {
      this.cronPopover = false;
      /* 新增 */
      this.dialogTitle = '新增';
      this.formDialog = {
        scanType: '1',
        jobCron: '',
      };
      this.visibleModel = true;
    },
    /* 编辑 */
    handleEdit(row) {
      this.cronPopover = false;
      hongtuConfig.fileMonitorDetail(row.id).then((response) => {
        if (response.code == 200) {
          this.formDialog = response.data;
          this.formDialog.scanType = this.formDialog.scanType.toString();
          this.visibleModel = true;
          this.dialogTitle = '编辑';
        }
      });
    },
    /* 确认 */
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          hongtuConfig.fileMonitorPost(this.formDialog).then((response) => {
            if (response.code == 200) {
              this.$message.success(this.dialogTitle + '成功');
              this.visibleModel = false;
              this.queryTable();
            }
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    /* 删除 */
    handleDelete(row) {
      let ids = [];
      let taskNames = [];
      if (!row.id) {
        let cellsChecked = this.$refs.tablevxe.getCheckboxRecords();
        cellsChecked.forEach((element) => {
          ids.push(element.id);
          taskNames.push(element.taskName);
        });
      } else {
        ids.push(row.id);
        taskNames.push(row.taskName);
      }
      if (taskNames.length == 0) {
        this.$message.error('请选择一条数据');
        return;
      }
      this.$confirm({
        title: '是否确认删除任务名称为"' + taskNames.join(',') + '"的数据项?',
        content: '',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: () => {
          hongtuConfig.fileMonitorDelete(ids.join(',')).then((response) => {
            if (response.code == 200) {
              this.$message.success('删除成功');
              this.resetQuery();
            }
          });
        },
        onCancel() {},
      });
    },

    startJob(row) {
      const id = row.id;
      let data = { id: id, triggerStatus: 1, jobCron: row.jobCron };
      request({
        url: '/fileMonitor/updateFileMonitor',
        method: 'post',
        data: data,
      }).then((response) => {
        this.$message.success('启动成功');
        this.queryTable();
      });
    },
    endJob(row) {
      const id = row.id;
      let data = { id: id, triggerStatus: 0, jobCron: row.jobCron };
      request({
        url: '/fileMonitor/updateFileMonitor',
        method: 'post',
        data: data,
      }).then((response) => {
        this.$message.success('停止成功');
        this.queryTable();
      });
    },
  },
};
</script>

<style lang="scss">
.fileMonitoringdialogBox {
  .selectDate {
  }
  .ant-col-3 {
    width: 10.5%;
  }
  .ant-col-21 {
    width: 89.5%;
  }
  .ant-input-number {
    width: 100%;
  }
  #changeContab .language {
    display: none;
  }
}
</style>
