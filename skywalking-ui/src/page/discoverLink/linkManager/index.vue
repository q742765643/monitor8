<template>
  <div class="managerTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm">
      <a-form-model-item label="ip地址">
        <a-input v-model="queryParams.ip" placeholder="请输入ip地址"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="运行状态">
        <a-select v-model="queryParams.triggerStatus" style="width: 120px">
          <a-select-option value="">全部</a-select-option>
          <a-select-option v-for="(dict, index) in triggerStatusOptions" :value="dict.dictValue" :key="index">
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
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" @click="handleDelete"> 删除 </a-button>
        </a-col>
      </a-row>
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons> <p style="text-align: right; margin-bottom: 0">列选择</p> </template>
        </vxe-toolbar>
      </div>
      <vxe-table :data="tableData" align="center" highlight-hover-row ref="tablevxe">
        <vxe-table-column type="checkbox" width="160"></vxe-table-column>
        <vxe-table-column field="ip" title="ip地址" show-overflow width="160"></vxe-table-column>
        <vxe-table-column field="taskName" title="设备别名" width="160"></vxe-table-column>
        <vxe-table-column field="jobCron" title="cron策略" show-overflow width="160"></vxe-table-column>
        <vxe-table-column field="currentStatus" title="设备状态" show-overflow width="160">
          <template v-slot="{ row }">
            <span> {{ statusFormat(currentStatusOptions, row.currentStatus) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="hostName" title="设备名称" show-overflow width="160"></vxe-table-column>
        <vxe-table-column field="monitoringMethods" title="监控方式" show-overflow width="160">
          <template v-slot="{ row }">
            <span> {{ statusFormat(monitoringMethodsOptions, row.monitoringMethods) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="mediaType" title="设备类型" show-overflow width="160">
          <template v-slot="{ row }">
            <span> {{ statusFormat(mediaTypeOptions, row.mediaType) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="gateway" title="网关" show-overflow width="160"></vxe-table-column>
        <vxe-table-column field="mac" title="mac地址" show-overflow width="160"></vxe-table-column>
        <vxe-table-column field="area" title="区域" show-overflow width="160">
          <template v-slot="{ row }">
            <span> {{ statusFormat(areaOptions, row.area) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="location" title="地址" show-overflow width="160"></vxe-table-column>
        <vxe-table-column field="triggerStatus" title="运行状态" show-overflow width="120">
          <template v-slot="{ row }">
            <span> {{ statusFormat(triggerStatusOptions, row.triggerStatus) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="createTime" title="发现时间" width="160" show-overflow>
          <template v-slot="{ row }">
            <span> {{ parseTime(row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column width="400" field="date" title="操作" fixed="right">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="updateAsHost(row)"> 设为主机 </a-button>
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
      width="50%"
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
          <a-col :span="12">
            <a-form-model-item label="设备别名" prop="taskName">
              <a-input v-model="formDialog.taskName" placeholder="请输入设备别名"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="设备名称" prop="hostName">
              <a-input v-model="formDialog.hostName" placeholder="请输入设备名称"> </a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="12">
            <a-form-model-item label="ip地址" prop="ip">
              <a-input v-model="formDialog.ip" placeholder="请输入ip地址"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="网关" prop="gateway">
              <a-input v-model="formDialog.gateway" placeholder="请输入网关"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="子网掩码" prop="mask">
              <a-input v-model="formDialog.mask" placeholder="请输入子网掩码"> </a-input>
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

          <a-col :span="12">
            <a-form-model-item label="mac地址" prop="mac">
              <a-input v-model="formDialog.mac" placeholder="请输入mac地址"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="负责人" prop="createBy">
              <a-input v-model="formDialog.createBy" placeholder="负责人"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="监视方式" prop="monitoringMethods">
              <a-select v-model="formDialog.monitoringMethods" placeholder="字典状态">
                <a-select-option
                  :key="index"
                  v-for="(dict, index) in monitoringMethodsOptions"
                  :value="parseInt(dict.dictValue)"
                >
                  {{ dict.dictLabel }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="设备类型" prop="mediaType">
              <a-select v-model="formDialog.mediaType">
                <a-select-option
                  :key="index"
                  v-for="(dict, index) in mediaTypeOptions"
                  :value="parseInt(dict.dictValue)"
                >
                  {{ dict.dictLabel }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="区域" prop="area">
              <a-select v-model="formDialog.area">
                <a-select-option v-for="(dict, index) in areaOptions" :value="parseInt(dict.dictValue)" :key="index">
                  {{ dict.dictLabel }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="设备状态" prop="currentStatus">
              <a-select v-model="formDialog.currentStatus">
                <a-select-option
                  v-for="(dict, index) in currentStatusOptions"
                  :value="parseInt(dict.dictValue)"
                  :key="index"
                >
                  {{ dict.dictLabel }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }" label="详细地址" prop="location">
              <a-input type="textarea" v-model="formDialog.location" placeholder="详细地址"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }" label="备注" prop="remark">
              <a-input type="textarea" v-model="formDialog.remark" placeholder="备注"> </a-input>
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
            cronExpression: this.formDialog.jobCron.split(' ?')[0] + ' ?',
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
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceType: 1,
        ip: '',
        triggerStatus: '',
      },
      triggerStatusOptions: [], //运行状态
      currentStatusOptions: [], //设备状态
      mediaTypeOptions: [], //设备类型
      monitoringMethodsOptions: [], //监视方式
      areaOptions: [], //区域
      tableData: [], //表格
      paginationTotal: 0,
      visibleModel: false, //弹出框
      dialogTitle: '',
      formDialog: {
        taskName: '',
        hostName: '',
        ip: '',
        gateway: '',
        mask: '',
        jobCron: '',
        mac: '',
        createBy: '',
        monitoringMethods: '',
        mediaType: '',
        area: '',
        location: '',
        currentStatus: '',
      },
      rules: {
        taskName: [{ required: true, message: '请输入设备别名', trigger: 'blur' }],
        jobCron: [{ required: true, validator: handleCronValidate, trigger: 'blur' }],
      }, //规则
      cronExpression: '',
      cronPopover: false,
    };
  },
  created() {
    hongtuConfig.getDicts('job_trigger_status').then((res) => {
      if (res.code == 200) {
        this.triggerStatusOptions = res.data;
      }
    });
    hongtuConfig.getDicts('current_status').then((res) => {
      if (res.code == 200) {
        this.currentStatusOptions = res.data;
      }
    });
    hongtuConfig.getDicts('media_type').then((res) => {
      if (res.code == 200) {
        this.mediaTypeOptions = res.data;
      }
    });
    hongtuConfig.getDicts('monitoring_methods').then((res) => {
      if (res.code == 200) {
        this.monitoringMethodsOptions = res.data;
      }
    });
    hongtuConfig.getDicts('media_area').then((res) => {
      if (res.code == 200) {
        this.areaOptions = res.data;
      }
    });
    this.queryTable();
  },
  mounted() {},
  methods: {
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
        ip: '',
        triggerStatus: '',
        deviceType: 1,
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
      hongtuConfig.hostConfigLIst(this.queryParams).then((response) => {
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
        taskName: '',
        hostName: '',
        ip: '',
        gateway: '',
        mask: '',
        jobCron: '',
        mac: '',
        createBy: '',
        monitoringMethods: '',
        mediaType: '',
        area: '',
        location: '',
        currentStatus: '',
      };
      this.visibleModel = true;
    },
    /* 编辑 */
    handleEdit(row) {
      this.cronPopover = false;
      hongtuConfig.hostConfigDetail(row.id).then((response) => {
        if (response.code == 200) {
          this.formDialog = response.data;
          this.visibleModel = true;
          this.dialogTitle = '编辑';
        }
      });
    },
    /* 确认 */
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          hongtuConfig.hostConfigPost(this.formDialog).then((response) => {
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
      let ips = [];
      if (!row.id) {
        let cellsChecked = this.$refs.tablevxe.getCheckboxRecords();
        cellsChecked.forEach((element) => {
          ids.push(element.id);
          ips.push(element.ip);
        });
      } else {
        ids.push(row.id);
        ips.push(row.ip);
      }
      if (ips.length == 0) {
        this.$message.error('请选择一条数据');
        return;
      }
      this.$confirm({
        title: '是否确认删除ip为"' + ips.join(',') + '"的数据项?',
        content: '',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: () => {
          hongtuConfig.hostConfigDelete(ids.join(',')).then((response) => {
            if (response.code == 200) {
              this.$message.success('删除成功');
              this.resetQuery();
            }
          });
        },
        onCancel() {},
      });
    },

    updateAsHost(row) {
      const id = row.id;
      let data = { id: id, deviceType: 0, isHost: 1 };
      request({
        url: '/hostConfig/updateHost',
        method: 'post',
        data: data,
      }).then((response) => {
        this.$message.success('设置成功');
        this.queryTable();
      });
    },
    startJob(row) {
      const id = row.id;
      let data = { id: id, triggerStatus: 1, jobCron: row.jobCron };
      request({
        url: '/hostConfig/updateHost',
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
        url: '/hostConfig/updateHost',
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

<style lang="scss" scoped></style>
