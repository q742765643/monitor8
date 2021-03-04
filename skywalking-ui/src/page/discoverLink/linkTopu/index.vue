<template>
  <div class="topu">
    <div id="column1">
      <topu-tree @findInfoData="findInfoData"></topu-tree>
    </div>
    <div id="column2">
      <div id="deviceState">
        <planeTitle titleName="链路设备状态统计"></planeTitle>
        <div id="pieChart"></div>
      </div>
      <div id="devideInfo">
        <planeTitle titleName="设备属性信息"></planeTitle>
        <div class="info">
          <div class="column">
            <div class="cell">
              <span>
                设备名称:
                <i></i>
              </span>
              <span>{{ infoData.hostName }}</span>
            </div>

            <div class="cell">
              <span>
                IP地址:
                <i></i>
              </span>
              <span>{{ infoData.ip }}</span>
            </div>
            <div class="cell">
              <span>
                网关地址:
                <i></i>
              </span>
              <span>{{ infoData.gateway }}</span>
            </div>

            <div class="cell">
              <span>
                设备类型:
                <i></i>
              </span>
              <span v-if="infoData.mediaType == '11'">未知</span>
              <span v-if="infoData.mediaType == '0'">windows</span>
              <span v-if="infoData.mediaType == '1'">linux</span>
              <span v-if="infoData.mediaType == '2'">二层交换机</span>
              <span v-if="infoData.mediaType == '3'">三层交换机</span>
              <span v-if="infoData.mediaType == '4'">路由</span>
            </div>
            <div class="cell">
              <span>
                设备状态:
                <i></i>
              </span>
              <span v-if="infoData.currentStatus == '11'">未知</span>
              <span v-if="infoData.currentStatus == '0'">一般</span>
              <span v-if="infoData.currentStatus == '1'">危险</span>
              <span v-if="infoData.currentStatus == '2'">故障</span>
              <span v-if="infoData.currentStatus == '3'">正常</span>
            </div>
            <div class="cell">
              <span>
                运行时长:
                <i></i>
              </span>
              <span>{{ infoData.maxUptime }}h</span>
            </div>
            <div class="cell">
              <span>
                丢包率:
                <i></i>
              </span>
              <span>{{ infoData.packetLoss }}</span>
            </div>
            <div class="cell">
              <span>
                区域分类:
                <i></i>
              </span>
              <span>{{ statusFormat(areaOptions, infoData.area) }}</span>
            </div>
            <div class="cell">
              <span>
                详细地址:
                <i></i>
              </span>
              <span>{{ infoData.location }}</span>
            </div>
            <div class="cell">
              <span>
                备注:
                <i></i>
              </span>
              <span>
                {{ infoData.remark }}
              </span>
            </div>
            <div class="cell">
              <span>
                操作:
                <i></i>
              </span>
              <span>
                <a-button type="primary" style="margin-right: 10px" @click="setAttribute(infoData.id, infoData.ip)"> 设置属性 </a-button>
                <a-button type="primary" @click="handleEdit(infoData.id, infoData.ip)"> 设置链路 </a-button>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      okText="确定"
      cancelText="取消"
      width="45%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox fileMonitoringdialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 5 }"
        :wrapperCol="{ span: 18 }"
        :model="formDialog"
        ref="formModel"
      >
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="源主机" prop="source">
              <a-input v-model="formDialog.ip"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="目标主机" prop="target">
              <a-select mode="multiple" v-model="formDialog.target" placeholder="目标主机">
                <a-select-option v-for="host in targetLists" :key="host.id">
                  {{ host.ip }}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
    <!-- 设置属性 -->
    <Attribute :attributeId="attributeId"  @sendVisible="sendVisible($event)"  v-if="attributevisible"></Attribute>
  </div>
</template>

<script>
import echarts from 'echarts';
/* import toupuChart from './toupuchart/toupuchart'; */
import topuTree from './toupuchart/toputree1';
import planeTitle from '@/components/titile/planeTitle.vue';
import Attribute from '@/page/discoverLink/linkTopu/attribute.vue'
import { remFontSize } from '@/components/utils/fontSize.js';
import request from '@/utils/request';
import hongtuConfig from '@/utils/services';
export default {
  data() {
    return {
      areaOptions: [], //区域
      infoData: {
        name: '',
        alias: '',
        location: '',
        ip: '',
        packetLoss: '',
        gateway: '',
        mediaType: '',
        currentStatus: '',
        maxUptime: '',
      },
      charts: '',
      pieData: [],
      hostLists: [],
      targetLists: [],
      visibleModel: false, //弹出框
      dialogTitle: '',
      formDialog: {
        target: [],
      },
      attributeId: "",
      attributevisible: false,
    };
  },
  components: { /* toupuChart ,*/ topuTree, planeTitle, Attribute },
  methods: {
    /* 字典格式化 */
    statusFormat(list, text) {
      return hongtuConfig.formatterselectDictLabel(list, text);
    },
    findInfoData(info) {
      this.infoData = info;
    },
    findStateStatistics() {
      request({
        url: '/networkTopy/findStateStatistics',
        method: 'get',
      }).then((response) => {
        this.pieData = response.data;
        this.pieData.forEach((element) => {
          if (element.name == '危险') {
            element.color1 = '#FF00FF';
            element.color2 = '#FF00FF';
          } else if (element.name == '正常') {
            element.color1 = '#329A2E';
            element.color2 = '#5DFC57';
          }
        });
        console.log(this.pieData);
        this.drawPie('pieChart');
      });
    },
    findAllHost() {
      request({
        url: '/hostConfig/selectAll',
        method: 'get',
      }).then((response) => {
        this.hostLists = response.data;
      });
    },
    setAttribute(id,ip) {
      // 设置属性
      this.attributeId = id;
      this.attributevisible = true;
    },
    sendVisible() {
      debugger
      this.attributevisible = false;
    },
    // attributrHandleOk() {
    //   this.$refs.formAttributeModel.validate((valid) => {
    //     if (valid) {
    //       hongtuConfig.hostConfigPost(this.formAttributeDialog).then((response) => {
    //         if (response.code == 200) {
    //           this.$message.success(this.dialogTitle + '成功');
    //           this.setAttributeModel = false;
    //           // this.queryTable();
    //         }
    //       });
    //     } else {
    //       console.log('error submit!!');
    //       return false;
    //     }
    //   });
    // },
    handleEdit(id, ip) {
      /* 新增 */
      this.dialogTitle = '设置链路';
      this.formDialog.ip = ip;
      this.formDialog.source = id;
      this.targetLists = [];
      this.hostLists.forEach((item) => {
        if (id != item.id) {
          this.targetLists.push(item);
        }
      });
      request({
        url: '/networkTopy/selectBySource',
        method: 'get',
        params: { source: id },
      }).then((response) => {
        this.formDialog.target = response.data;
        this.visibleModel = true;
      });
    },
    handleOk() {
      console.log(this.formDialog);
      request({
        url: '/networkTopy/saveSource',
        method: 'post',
        params: this.formDialog,
      }).then((data) => {
        this.visibleModel = false;
      });
    },
    drawPie(id) {
      var linearcolor = [];
      this.pieData.map((item) => {
        linearcolor.push({
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0,
              color: item.color1,
            },
            {
              offset: 1,
              color: item.color2,
            },
          ],
          globalCoord: false, // 缺省为 false
        });
      });
      this.charts = echarts.init(document.getElementById(id));
      var option = {
        textStyle: {
          fontFamily: 'NotoSansHans-Medium',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)',
        },
        grid: { top: '10%' },
        color: linearcolor,
        series: [
          {
            type: 'pie',
            radius: ['20%', '40%'],
            center: ['50%', '50%'],
            selectedMode: 'single',
            data: this.pieData,
            /* label: {
              color: '#000000',
            }, */
            label: {
              padding: [0, -remFontSize(30 / 64), 0, -remFontSize(30 / 64)],
              formatter: '{b}\n\n{c}',
            },
            labelLine: {
              length: remFontSize(10 / 64),
              length2: remFontSize(50 / 64),
              lineStyle: { color: '#acacac', width: 0.5 },
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      };

      this.charts.setOption(option);
    },
  },
  created() {
    hongtuConfig.getDicts('media_area').then((res) => {
      if (res.code == 200) {
        this.areaOptions = res.data;
      }
    });
  },
  mounted() {
    this.findStateStatistics();
    this.findAllHost();
    this.$nextTick(() => {});
    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
};
</script>

<style lang="scss" scoped>
.topu {
  display: flex;
  width: 100%;
  height: calc(100vh - 130px);
  display: flex;
  justify-content: space-between;
  #column1 {
    width: 70%;
    height: 100%;
    background-color: #ffffff;
    box-shadow: $plane_shadow;
  }
  #column2 {
    width: 29%;
    height: 100%;
    #deviceState {
      background-color: #ffffff;
      box-shadow: $plane_shadow;
      #pieChart {
        width: 100%;
        height: 300px;
      }
    }
    #devideInfo {
      height: calc(100% - 376px);
      margin-top: 20px;
      background-color: #ffffff;
      box-shadow: $plane_shadow;
    }
  }

  .info {
    padding: 40px 0;
    width: 90%;
    margin: auto;
    .column {
      .cell {
        display: flex;
        span:first-child {
          font-size: $ant_font_size;
          width: 100px;
          display: inline-block;
          text-align: right;
          letter-spacing: 4px;
          i {
            width: 100%;
            display: inline-block;
          }
        }

        span:last-child {
          width: calc(100% - 100px);
          font-size: $ant_font_size;
          margin-left: 10px;
        }
      }
    }
  }
}
</style>
