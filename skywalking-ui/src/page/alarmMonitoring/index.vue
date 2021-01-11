<template>
  <div class="alarmMonitoringTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="告警任务名称">
        <a-input v-model="queryParams.taskName" placeholder="请输入告警任务名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="时间">
        <a-range-picker
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
        <vxe-table-column type="checkbox" width="80"></vxe-table-column>
        <vxe-table-column field="taskName" title="告警任务名称">
          <template v-slot="{ row }">
            <span> {{ statusFormat(typeOptions, row.monitorType) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="triggerStatus" title="状态" show-overflow>
          <template v-slot="{ row }">
            <span v-if="row.triggerStatus == 0">未启动 </span>
            <span v-if="row.triggerStatus == 1">启动 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间" show-overflow>
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column width="400" field="date" title="操作">
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
      class="dialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 6 }"
        :wrapperCol="{ span: 18 }"
        :model="formDialog"
        ref="formModel"
        :rules="rules"
      >
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="告警任务名称" prop="taskName">
              <a-input v-model="formDialog.taskName" placeholder="请输入告警任务名称"> </a-input>
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
          <a-col :span="12">
            <a-form-model-item label="监测类型" prop="monitorType">
              <a-select v-model="formDialog.monitorType" @change="changeType">
                <a-select-option v-for="(item, index) in typeOptions" :key="index" :value="item.dictValue">
                  {{ item.dictLabel }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="corn表达式1" prop="jobCron">
              <el-popover v-model.trim="cronPopover">
                <vueCron @change="changeCron" @close="closeCronPopover" i18n="cn"></vueCron>
                <el-input
                  size="small"
                  slot="reference"
                  @click="cronPopover = true"
                  v-model.trim="formDialog.jobCron"
                  placeholder="请输入定时策略"
                ></el-input>
              </el-popover>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-for="(itemp, indexp) in formDialog.generals" :key="'0-' + indexp">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <span v-show="indexp == 0" slot="label" class="lineLabel">
                <i class="formIcon">*</i><i class="rulesIcon rulesIcon1"></i> 一般阈值</span
              >
              <span v-show="indexp > 0" slot="label" class="lineLabel2">
                <a-select v-model="itemp.operate" style="width: 130px; float: left; margin-left: 24px">
                  <a-select-option value="and"> 并且 </a-select-option>
                  <a-select-option value="or"> 或者 </a-select-option>
                </a-select>
              </span>
              <a-row class="lineContent" :gutter="[16, 16]">
                <a-col :span="7">
                  <a-select v-model="itemp.monitorValue" disabled>
                    <a-select-option v-for="(item, index) in kpiOptions" :key="index" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="6">
                  <a-select v-model="itemp.paramname">
                    <a-select-option v-for="(item, index) in operatorOptions" :key="index" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="7">
                  <a-input v-model="itemp.paramvalue" placeholder="请输入调度地址"> </a-input>
                </a-col>
                <a-col :span="4" class="unitBox">
                  <a-button
                    type="danger"
                    icon="delete"
                    @click="generalsHandleDelete(indexp)"
                    v-show="indexp > 0"
                    class="deleteBtn"
                  >
                    删除
                  </a-button>
                  <a-button type="primary" icon="plus" @click="generalsHandleAdd"> 新增 </a-button>
                </a-col>
              </a-row>
            </a-form-model-item>
          </a-col>
          <a-col :span="24" v-for="(itemp, indexp) in formDialog.dangers" :key="'1-' + indexp">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <span v-show="indexp == 0" slot="label" class="lineLabel">
                <i class="formIcon">*</i><i class="rulesIcon rulesIcon2"></i> 危险阈值</span
              >
              <span v-show="indexp > 0" slot="label" class="lineLabel2">
                <a-select v-model="itemp.operate" style="width: 130px; float: left; margin-left: 24px">
                  <a-select-option value="and"> 并且 </a-select-option>
                  <a-select-option value="or"> 或者 </a-select-option>
                </a-select>
              </span>
              <a-row class="lineContent" :gutter="[16, 16]">
                <a-col :span="7">
                  <a-select v-model="itemp.monitorValue" disabled>
                    <a-select-option v-for="(item, index) in kpiOptions" :key="index" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="6">
                  <a-select v-model="itemp.paramname">
                    <a-select-option v-for="(item, index) in operatorOptions" :key="index" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="7">
                  <a-input v-model="itemp.paramvalue" placeholder="请输入调度地址"> </a-input>
                </a-col>
                <a-col :span="4" class="unitBox">
                  <a-button
                    type="danger"
                    icon="delete"
                    @click="dangerHandleDelete(indexp)"
                    v-show="indexp > 0"
                    class="deleteBtn"
                  >
                    删除
                  </a-button>
                  <a-button type="primary" icon="plus" @click="dangerHandleAdd"> 新增 </a-button>
                </a-col>
              </a-row>
            </a-form-model-item>
          </a-col>

          <a-col :span="24" v-for="(itemp, indexp) in formDialog.severitys" :key="'2-' + indexp">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <span v-show="indexp == 0" slot="label" class="lineLabel">
                <i class="formIcon">*</i><i class="rulesIcon rulesIcon3"></i> 故障阈值</span
              >
              <span v-show="indexp > 0" slot="label" class="lineLabel2">
                <a-select v-model="itemp.operate" style="width: 130px; float: left; margin-left: 24px">
                  <a-select-option value="and"> 并且 </a-select-option>
                  <a-select-option value="or"> 或者 </a-select-option>
                </a-select>
              </span>
              <a-row class="lineContent" :gutter="[16, 16]">
                <a-col :span="7">
                  <a-select v-model="itemp.monitorValue" disabled>
                    <a-select-option v-for="(item, index) in kpiOptions" :key="index" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="6">
                  <a-select v-model="itemp.paramname">
                    <a-select-option v-for="(item, index) in operatorOptions" :key="index" :value="item.dictValue">
                      {{ item.dictLabel }}
                    </a-select-option>
                  </a-select>
                </a-col>
                <a-col :span="7">
                  <a-input v-model="itemp.paramvalue" placeholder="请输入调度地址"> </a-input>
                </a-col>
                <a-col :span="4" class="unitBox">
                  <a-button
                    type="danger"
                    icon="delete"
                    @click="severitysHandleDelete(indexp)"
                    v-show="indexp > 0"
                    class="deleteBtn"
                  >
                    删除
                  </a-button>
                  <a-button type="primary" icon="plus" @click="severitysHandleAdd"> 新增 </a-button>
                </a-col>
              </a-row>
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
import moment from 'moment';
import request from '@/utils/request';
export default {
  data() {
    //校验是否为cron表达式
    var handleCronValidate = async (rule, value, callback) => {
      if (value == '') {
        callback(new Error('请输入执行策略!'));
      } else {
        let flag = true;
        await getNextTime({
          cronExpression: this.formDialog.jobCron.split(' ?')[0] + ' ?',
        }).then((res) => {
          flag = false;
        });
        if (flag) {
          callback(new Error('执行策略表达式错误!'));
        } else {
          callback();
        }
      }
    };
    return {
      cronPopover: false,
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
      operatorOptions: [], //告警运算符号
      kpiOptions: [], //告警指标
      typeOptions: [], //告警类型,监测类型
      dialogTitle: '',
      monitorVal: '',
      formDialog: {
        taskName: '',
        dangers: [{ operate: 'and', paramname: '', paramvalue: '' }],
      },
      rules: {
        taskName: [{ required: true, message: '请输入设备别名', trigger: 'blur' }],
        //jobCron: [{ required: true, validator: handleCronValidate, trigger: 'blur' }],
      }, //规则
    };
  },
  created() {
    hongtuConfig.getDicts('alarm_operator').then((res) => {
      if (res.code == 200) {
        this.operatorOptions = res.data;
      }
    });
    hongtuConfig.getDicts('alarm_kpi').then((res) => {
      if (res.code == 200) {
        this.kpiOptions = res.data;
        this.kpiOptions.forEach((item, index) => {
          item.dictValue = parseInt(item.dictValue);
        });
      }
    });
    hongtuConfig.getDicts('alarm_type').then((res) => {
      if (res.code == 200) {
        this.typeOptions = res.data;
        this.typeOptions.forEach((item, index) => {
          item.dictValue = parseInt(item.dictValue);
        });
      }
    });
    this.queryTable();
  },
  mounted() {
    this.$nextTick(() => {
      this.setTableHeight();
    });
    window.addEventListener('resize', () => {
      this.setTableHeight();
    });
  },
  methods: {
    changeCron(val) {
      this.cronExpression = val;
      if (val.substring(0, 5) == '* * *') {
        this.msgError('小时,分钟,秒必填');
      } else {
        this.formDialog.jobCron = val.split(' ?')[0] + ' ?';
      }
    },
    closeCronPopover() {
      if (this.cronExpression.substring(0, 5) == '* * *') {
        return;
      } else {
        /*   getNextTime({
          cronExpression: this.cronExpression.split(' ?')[0] + ' ?',
        }).then((res) => {
          let times = res.data;
          let html = '';
          times.forEach((element) => {
            html += '<p>' + element + '</p>';
          });
          this.$alert(html, '前5次执行时间', {
            dangerouslyUseHTMLString: true,
          }).then(() => {
            this.CronPopover = false;
          });
        }); */
      }
    },
    changeType(val) {
      this.monitorVal = val;
      this.formDialog.dangers.forEach((element) => {
        element.monitorValue = val;
      });
      this.formDialog.severitys.forEach((element) => {
        element.monitorValue = val;
      });
      this.formDialog.generals.forEach((element) => {
        element.monitorValue = val;
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
    onTimeChange(value, dateString) {
      this.queryParams.beginTime = dateString[0];
      this.queryParams.endTime = dateString[1];
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
        beginTime: '',
        endTime: '',
      };
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
      hongtuConfig.alarmCofigList(this.queryParams).then((response) => {
        this.tableData = response.data.pageData;
        this.paginationTotal = response.data.totalCount;
      });
    },
    /* 字典格式化 */
    statusFormat(list, text) {
      return hongtuConfig.formatterselectDictLabel(list, text);
    },
    /* 一般阈值条件 */
    generalsHandleAdd() {
      this.formDialog.generals.push({
        operate: 'and',
        paramname: '',
        paramvalue: '',
        monitorValue: this.monitorVal,
      });
    },
    generalsHandleDelete(index) {
      this.formDialog.generals.splice(index, 1);
    },
    /* 危险阈值条件 */
    dangerHandleAdd() {
      this.formDialog.dangers.push({
        operate: 'and',
        paramname: '',
        paramvalue: '',
        monitorValue: this.monitorVal,
      });
    },
    dangerHandleDelete(index) {
      this.formDialog.dangers.splice(index, 1);
    },
    /* 故障阈值条件 */
    severitysHandleAdd() {
      this.formDialog.severitys.push({
        operate: 'and',
        paramname: '',
        paramvalue: '',
        monitorValue: this.monitorVal,
      });
    },
    severitysHandleDelete(index) {
      this.formDialog.severitys.splice(index, 1);
    },
    handleAdd() {
      /* 新增 */
      this.dialogTitle = '新增';
      this.formDialog = {
        taskName: '',
        dangers: [{ operate: 'and', paramname: '', paramvalue: '' }],
        severitys: [{ operate: 'and', paramname: '', paramvalue: '' }],
        generals: [{ operate: 'and', paramname: '', paramvalue: '' }],
      };
      this.visibleModel = true;
    },
    /* 编辑 */
    handleEdit(row) {
      hongtuConfig.alarmCofigDetail(row.id).then((response) => {
        if (response.code == 200) {
          this.formDialog = response.data;
          this.changeType(this.formDialog.monitorType);
          this.visibleModel = true;
          this.dialogTitle = '编辑';
        }
      });
    },
    /* 确认 */
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          hongtuConfig.alarmCofigPost(this.formDialog).then((response) => {
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
        title: '是否确认删除告警任务名称为"' + taskNames.join(',') + '"的数据项?',
        content: '',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: () => {
          hongtuConfig.alarmCofigDelete(ids.join(',')).then((response) => {
            if (response.code == 200) {
              this.$message.success('删除成功');
              this.resetQuery();
            }
          });
        },
        onCancel() {},
      });
    },
    setTableHeight() {
      let h = document.getElementById('tablediv').offsetHeight;
      let padding = getComputedStyle(document.getElementById('linkManger_content'), false)['paddingTop'];
      let h_page = document.getElementById('page_table').offsetHeight;

      // let chartHeight = document.getElementById("chartdiv").clientHeight;
      this.tableheight = h + parseInt(padding) * 2 - h_page - 1;
    },
    startJob(row) {
      const id = row.id;
      let data = { id: id, triggerStatus: 1, jobCron: row.jobCron };
      console.log(data);
      request({
        url: '/alarmCofig/updateAlarm',
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
        url: '/alarmCofig/updateAlarm',
        method: 'post',
        data: data,
      }).then((response) => {
        this.$message.success('停止成功');
        this.handleQuery();
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.fileMonitorTemplate {
  width: 100%;
  height: 100%;
  font-family: Alibaba-PuHuiTi-Regular;

  .head {
    box-shadow: $plane_shadow;
    width: 100%;
    height: 1.25rem;
    background: #fff;
    display: flex;
    align-items: center;
    margin-bottom: 0.1rem;
  }

  #linkManger_content {
    box-shadow: $plane_shadow;
    width: 100%;
    // height: calc(100% - 1.5rem);
    background: white;
    padding: 0.25rem;
    overflow: hidden;
  }
}

.lineLabel {
  .formIcon {
    color: red;
  }
  .rulesIcon {
    width: 0.15rem;
    height: 0.15rem;
    display: inline-block;
    margin: 0 0.15rem;
  }
  .rulesIcon1 {
    background: rgb(65, 184, 181);
  }
  .rulesIcon2 {
    background: rgb(237, 184, 29);
  }
  .rulesIcon3 {
    background: rgb(235, 93, 93);
  }
}

.lineContent {
  .unitBox {
    text-align: right;
  }
  .deleteBtn {
    margin-right: 4px;
  }
}

.ant-form-item-label > label::after {
  opacity: 0;
}
</style>
