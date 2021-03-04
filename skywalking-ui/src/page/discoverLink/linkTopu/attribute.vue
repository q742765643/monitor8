<template>
  <a-modal
    v-model="attributevisible"
    title="编辑"
    @ok="handleOk"
    @cancel="handleClose"
    okText="确定"
    cancelText="取消"
    width="50%"
    :maskClosable="false"
    :centered="true"
    class="dialogBox"
  >
    <a-form-model
      v-if="attributevisible"
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
              <a-select-option :key="index" v-for="(dict, index) in mediaTypeOptions" :value="parseInt(dict.dictValue)">
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
</template>
<script>
import hongtuConfig from '@/utils/services';
import request from '@/utils/request';
export default {
  props: [ 'attributeId'],
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
      mediaTypeOptions: [], //设备类型
      areaOptions: [], //区域
      currentStatusOptions: [],
      monitoringMethodsOptions: [],
      attributevisible: false,
    };
  },
  created() {
    hongtuConfig.getDicts('media_type').then((res) => {
      if (res.code == 200) {
        this.mediaTypeOptions = res.data;
      }
    });
    hongtuConfig.getDicts('media_area').then((res) => {
      if (res.code == 200) {
        this.areaOptions = res.data;
      }
    });
    hongtuConfig.getDicts('current_status').then((res) => {
      if (res.code == 200) {
        this.currentStatusOptions = res.data;
      }
    });
    hongtuConfig.getDicts('monitoring_methods').then((res) => {
      if (res.code == 200) {
        this.monitoringMethodsOptions = res.data;
      }
    });
    this.getData();
  },
  methods: {
    getData() {
      this.cronPopover = false;
      hongtuConfig.hostConfigDetail(this.attributeId).then((response) => {
        if (response.code == 200) {
          this.formDialog = response.data;
          this.attributevisible = true;
          console.log(this.formDialog);
        }
      });
    },
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          hongtuConfig.hostConfigPost(this.formDialog).then((response) => {
            if (response.code == 200) {
              this.$message.success('编辑成功');
              this.$emit('sendVisible',false)
            }
          });
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    handleClose() {
        debugger
        this.$emit('sendVisible',false)
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
  },
};
</script>
<style scoped>
</style>