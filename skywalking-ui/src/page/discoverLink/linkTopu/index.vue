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
                设备别名:
                <i></i>
              </span>
              <span>{{ infoData.alias }}</span>
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
                网关:
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
                丢包率:
                <i></i>
              </span>
              <span>{{ infoData.packetLoss }}</span>
            </div>
            <div class="cell">
              <span>
                连线状态:
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
              <span>{{ infoData.maxUptime }}</span>
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
                区域:
                <i></i>
              </span>
              <span>{{ infoData.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
/* import toupuChart from './toupuchart/toupuchart'; */
import topuTree from './toupuchart/toputree1';
import planeTitle from '@/components/titile/planeTitle.vue';
import { remFontSize } from '@/components/utils/fontSize.js';
import request from '@/utils/request';

export default {
  data() {
    return {
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
    };
  },
  components: { /* toupuChart ,*/ topuTree, planeTitle },
  methods: {
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
            radius: ['25%', '60%'],
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
              length: remFontSize(30 / 64),
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
  mounted() {
    this.findStateStatistics();
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
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      .cell {
        flex: 1;
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
          font-size: $ant_font_size;
          margin-left: 10px;
        }
      }
    }
  }
}
</style>
