<template>
  <div id="leftPlane">
    <div id="topCell">
      <planeTitle titleName="主机节点状态"> </planeTitle>
      <div class="chart_info">
        <div id="statuChart"></div>
        <div class="info">
          <span class="name">就绪节点</span>
          <span class="number">
            <p>{{ overviewNode.ready }}</p>
            <p>/{{ overviewNode.total }}</p>
          </span>
        </div>
      </div>
    </div>
    <div id="bottomCell">
      <planeTitle titleName="主机节点状态"> <div class="subtitle" slot="right">计算资源总利用率</div></planeTitle>
      <div id="usageChart">
        <div class="chart_info">
          <div id="cpuChart"></div>
          <div class="info">
            <span class="name" style="color: #ffba00">CPU core</span>
            <span class="number">
              <p style="color: #ffba00">{{ this.nodeStatus.cpuUse }}</p>
              <p>/{{ this.nodeStatus.cpuCores }}</p>
            </span>
          </div>
        </div>
        <div class="chart_info">
          <div id="romChart"></div>
          <div class="info">
            <span class="name" style="color: #20bb01">内存 GiB</span>
            <span class="number">
              <p style="color: #20bb01">{{ this.nodeStatus.memoryUse }}</p>
              <p>/{{ this.nodeStatus.memoryTotal }}</p>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  import request from '@/utils/request';
  import planeTitle from '@/components/titile/planeTitle.vue';
  import { remFontSize } from '@/components/utils/fontSize.js';
  export default {
    components: { planeTitle },
    data() {
      return {
        overviewNode: {},
        statuChart: '',
        cpuChart: '',
        nodeStatus: {},
        lnerColors: [
          ['#C225FC', '#1272EB', '#5483FF'],
          ['#FFBA00', '#FF992C', '#FF696B'],
          ['#47F092', '#35D750', '#20BB01'],
        ],
      };
    },
    created() {
      request({
        url: '/overview/getNodes',
        method: 'get',
      }).then((data) => {
        this.overviewNode = data.data;
        this.drawChart(
          'statuChart',
          this.statuChart,
          this.overviewNode.total,
          this.overviewNode.ready,
          this.lnerColors[0],
        );
      });
      request({
        url: '/overview/getNodesStatus',
        method: 'get',
      }).then((data) => {
        this.nodeStatus = data.data;
        this.drawChart('cpuChart', this.cpuChart, this.nodeStatus.cpuCores, this.nodeStatus.cpuUse, this.lnerColors[1]);
        this.drawChart(
          'romChart',
          this.romChart,
          this.nodeStatus.memoryTotal,
          this.nodeStatus.memoryUse,
          this.lnerColors[2],
        );
      });
    },
    mounted() {
      this.$nextTick(() => {});
    },
    methods: {
      drawChart(id, chart, total, value, clors) {
        chart = echarts.init(document.getElementById(id));
        let options = {
          backgroundColor: '#fff',
          angleAxis: {
            max: 100,
            show: false,
          },
          tooltip: {
            trigger: 'item',
            formatter: '{c}%',
          },
          graphic: {
            //图形中间文字
            type: 'text',
            left: 'center',
            top: 'center',
            style: {
              text:
                ((value / total) * 100) % 1 == 0
                  ? (value / total) * 100 + '%'
                  : ((value / total) * 100).toFixed(1) + '%',
              textAlign: 'center',
              fill: clors[0],
              fontSize: remFontSize(20 / 64),
            },
          },
          radiusAxis: {
            type: 'category',
            show: true,
            axisLabel: {
              show: false,
            },
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
          },
          polar: {
            radius: ['60%', '68%'],
            center: ['50%', '50%'],
          },
          series: [
            {
              type: 'bar',
              roundCap: true,
              barWidth: 40,
              showBackground: true,
              backgroundStyle: {
                color: '#f0f0f0',
              },
              itemStyle: {
                normal: {
                  opacity: 1,
                  color: '#2d82ff',
                },
              },
              data: [
                {
                  value: ((value / total) * 100).toFixed(1),
                  itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                      { offset: 0, color: clors[0] },
                      //  { offset: 0.5, color: clors[1] },
                      { offset: 1, color: clors[2] },
                    ]),
                    shadowBlur: 10, //浅蓝色阴影
                    shadowColor: 'rgba(0, 103, 255, 0.2)',
                    shadowOffsetX: -5,
                    shadowOffsetY: 5,
                  },
                },
              ],
              coordinateSystem: 'polar',
              name: 'A',
              label: {
                show: true,
              },
            },
          ],
        };

        chart.setOption(options);
      },
    },
  };
</script>

<style lang="scss" scoped>
  #leftPlane {
    width: 100%;
    height: 100%;
    #topCell {
      width: 100%;
      height: 38%;
      background: #fff;
      .chart_info {
        width: 100%;
        height: calc(100% - 56px);
        display: flex;
        #statuChart {
          width: 50%;
        }
        .info {
          .name {
            color: #5483ff;
          }
          .number {
            p:nth-child(1) {
              color: #5483ff;
            }
          }
        }
      }
    }
    #bottomCell {
      margin-top: 10px;
      width: 100%;
      height: calc(62% - 10px);
      background: white;
      #usageChart {
        width: 100%;
        height: calc(100% - 56px);
        .chart_info {
          width: 100%;
          height: 50%;
          display: flex;
          #cpuChart {
            width: 50%;
          }
          #romChart {
            width: 50%;
          }
        }
      }
    }

    .subtitle {
      font-size: 12px;
      padding-right: 4px;
    }
    .info {
      width: 50%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: flex-start;
      .name {
        font-size: 20px;
        font-weight: 200;
      }
      .number {
        letter-spacing: 2px;
        p {
          display: inline;
          font-size: 20px;
        }
        p:nth-child(1) {
          font-size: 24px;
        }
      }
    }
  }
</style>
