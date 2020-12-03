<template>
  <div class="topu">
    <div id="column1">
      <topu-tree></topu-tree>
    </div>
    <div id="column2">
      <div id="deviceState">
        <planeTitle titleName="监控总览"></planeTitle>
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
              <span>{{ infoData.DNS }}</span>
            </div>

            <div class="cell">
              <span>
                设备类型:
                <i></i>
              </span>
              <span>{{ infoData.type }}</span>
            </div>
            <div class="cell">
              <span>
                丢包率:
                <i></i>
              </span>
              <span>{{ infoData.lostPack }}</span>
            </div>
            <div class="cell">
              <span>
                连线状态:
                <i></i>
              </span>
              <span>{{ infoData.state }}</span>
            </div>
            <div class="cell">
              <span>
                运行时长:
                <i></i>
              </span>
              <span>{{ infoData.time }}</span>
            </div>
            <div class="cell">
              <span>
                详细地址:
                <i></i>
              </span>
              <span>{{ infoData.addr }}</span>
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
  export default {
    data() {
      return {
        infoData: {
          name: 'test-name',
          alias: '报文接收服务器',
          addr: '办公区三层306',
          ip: '66.32.5.122',
          lostPack: '3%',
          DNS: '255.255.255.80',
          type: 'windows服务器',
          state: '正常/异常',
          time: '20天30时45分',
        },
        charts: '',
        pieData: [
          { value: 210, name: '良好', color1: '#329A2E', color2: '#5DFC57' },
          { value: 735, name: '一般', color1: '#E4A302', color2: '#FDF901' },
          { value: 834, name: '未知', color1: '#FC000D', color2: '#E10008' },
          { value: 535, name: '严重', color1: '#0063F2', color2: '#0065F5' },
        ],
      };
    },
    components: { /* toupuChart ,*/ topuTree, planeTitle },
    methods: {
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
            fontFamily: 'Alibaba-PuHuiTi-Medium',
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
      this.$nextTick(() => {
        this.drawPie('pieChart');
      });
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
      padding: 40px 80px;

      .column {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .cell {
          flex: 1;
          display: flex;
          span:first-child {
            font-size: $ant_font_size;
            width: 90px;
            display: inline-block;
            font-weight: 600;
            text-align: justify;
            i {
              width: 100%;
              display: inline-block;
            }
          }

          span:last-child {
            margin-left: 10px;
          }
        }
      }
    }
  }
</style>
