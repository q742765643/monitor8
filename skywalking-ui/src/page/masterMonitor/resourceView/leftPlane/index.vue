<template>
  <div id="leftPlane">
    <div id="topCell">
      <!--  <div class="title">主机节点状态</div> -->
      <planeTitle titleName="主机节点状态"> </planeTitle>
      <div class="chart_info">
        <div id="statuChart"></div>
        <div class="info">
          <span class="name">就绪节点</span>
          <span class="number">
            <p>{{ readyMater }}</p>
            <p>/{{ totalMaster }}</p>
          </span>
        </div>
      </div>
    </div>
    <div id="bottomCell">
      <!--  <div class="title"><span>主机</span><span>计算资源总利用率</span></div> -->

      <planeTitle titleName="主机节点状态">
        <div class="subtitle" slot="right">计算资源总利用率</div></planeTitle
      >
      <div id="usageChart">
        <div class="chart_info">
          <div id="cpuChart"></div>
          <div class="info">
            <span class="name" style="color:#FFBA00">CPU core</span>
            <span class="number">
              <p style="color:#FFBA00">{{ usageCpu }}</p>
              <p>/{{ totalCpu }}</p>
            </span>
          </div>
        </div>
        <div class="chart_info">
          <div id="romChart"></div>
          <div class="info">
            <span class="name" style="color:#20BB01">内存 GiB</span>
            <span class="number">
              <p style="color:#20BB01">{{ usageRom }}</p>
              <p>/{{ totalRom }}</p>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
import planeTitle from '@/components/titile/planeTitle.vue';
import { remFontSize } from '@/components/utils/fontSize.js';
export default {
  components: { planeTitle },
  data() {
    return {
      statuChart: '',
      cpuChart: '',
      totalMaster: 3,
      totalCpu: 24,
      totalRom: 93.49,
      readyMater: 3,
      usageCpu: 2.44,
      usageRom: 50.9,
      lnerColors: [
        ['#C225FC', '#1272EB', '#5483FF'],
        ['#FFBA00', '#FF992C', '#FF696B'],
        ['#47F092', '#35D750', '#20BB01'],
      ],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.drawChart(
        'statuChart',
        this.statuChart,
        this.totalMaster,
        this.readyMater,
        this.lnerColors[0]
      );
      this.drawChart(
        'cpuChart',
        this.cpuChart,
        this.totalCpu,
        this.usageCpu,
        this.lnerColors[1]
      );
      this.drawChart(
        'romChart',
        this.romChart,
        this.totalRom,
        this.usageRom,
        this.lnerColors[2]
      );
    });
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
          left: '30%',
          top: '45%',
          style: {
            text:
              ((value / total) * 100) % 1 == 0
                ? (value / total) * 100 + '%'
                : ((value / total) * 100).toFixed(1) + '%',
            textAlign: 'center',
            fill: clors[0],
            fontSize: remFontSize(30 / 64),
            // fontWeight: 'bold',
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
  color: #676767;
  width: 100%;
  height: 100%;
  #topCell {
    width: 100%;
    height: 3.75rem;
    background: white;
    // box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    box-shadow: $plane_shadow;
    .chart_info {
      width: 100%;
      height: calc(100% - 0.75rem);
      display: flex;
      #statuChart {
        width: 50%;
        height: 100%;
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
    margin-top: 0.375rem;
    width: 100%;
    height: calc(100% - 3.75rem - 0.375rem);
    background: white;
    //hadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    box-shadow: $plane_shadow;
    #usageChart {
      width: 100%;
      height: calc(100% - 0.75rem);

      .chart_info {
        width: 100%;
        height: 50%;
        display: flex;
        #cpuChart {
          height: 100%;
          width: 50%;
        }
        #romChart {
          width: 50%;
          height: 100%;
        }
        /* .info {
            .name {
              color: #ffba00;
            }
            .number {
              p:nth-child(1) {
                color: #ffba00;
              }
            }
          } */
      }
    }
  }

  /* .title {
    height: 0.75rem;
    line-height: 0.75rem;
    border-bottom: 0.025rem solid #bddfeb8f;
    font-size: 0.25rem;
    padding-left: 0.25rem;
    font-weight: 600;
    display: flex;

    span {
      display: block;
      flex: 1;
      &:last-child {
        font-size: 0.225rem !important;
        font-weight: 400;
      }
    }
  } */
  .subtitle {
    font-family: Alibaba-PuHuiTi-Regular;
    font-size: 0.225rem;
  }
  .info {
    width: 50%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    .name {
      font-weight: 200;
      font-size: 0.25rem;
    }
    .number {
      letter-spacing: 0.025rem;
      p {
        display: inline;
        font-size: 0.25rem;
      }
      p:nth-child(1) {
        font-size: 0.3rem;
      }
    }
  }
}
</style>
