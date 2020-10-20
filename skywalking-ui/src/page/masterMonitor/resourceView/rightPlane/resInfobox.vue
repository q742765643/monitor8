<template>
  <div id="box">
    <div id="title">
      <span id="states" style=" background: #00ff00;"></span>
      <span id="name">{{ name }}</span>
    </div>

    <div id="info_content">
      <div class="progressChart" :id="chartId"></div>

      <div id="thred">
        <div id="img"></div>
        <div id="thred_info">
          <span>
            <p>进程{{current.processSize}}个</p>
            <p v-if="this.current.online==0">网络不在线</p>
            <p v-if="this.current.online==1">网络在线</p>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
import { remFontSize } from '@/components/utils/fontSize.js';
export default {
  props: {
    current: {
      type: Object,
      default: () => ({}),
    },
    chartId: {
      type: String,
      default: () => "",
    },
    name: {
      type: String,
      default: () => "",
    },
  },
  data() {
    return {
      progressChart: '',
      colors: ['#FF9434', '#27C71F', '#3E6CFF'],
    };
  },

  mounted() {
    this.$nextTick(() => this.drawChart(this.chartId));
  },
  methods: {
    drawChart(id) {
      this.progressChart = echarts.init(document.getElementById(id));

      var myData = ['CPU', '内存', '硬盘'];
      var lineData = [this.current.cpuCores, this.current.memoryTotal, this.current.filesystemSize];
      var thisYearData = [this.current.cpuUse, this.current.memoryUse, this.current.filesystemUse];
      let options = {
        baseOption: {
          timeline: {
            show: false,
            top: 0,
            data: [],
          },

          grid: [
            {
              show: false,
              left: '0%',
              top: '0%',
              bottom: '0%',
              containLabel: true,
              width: '82%',
            },
            {
              // 进度条左侧文字位置调整 -- 开始
              show: false,
              left: '10%',
              top: '5%',
              bottom: '0%', // 进度条左侧文字之间的间距
              width: '0%',
            }, // 进度条左侧文字位置调整 -- 结束
            {
              // 进度条位置调整 -- 开始
              show: false,
              left: '18%',
              top: '5%',
              bottom: '0%', // 进度条之间的间距
              containLabel: true,
              width: '82%',
            }, // 进度条位置调整 -- 结束
          ],
          xAxis: [
            {
              type: 'value',
              inverse: true,
              axisLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              position: 'top',
              axisLabel: {
                show: false,
              },
              splitLine: {
                show: false,
              },
            },
            {
              gridIndex: 1,
              show: false,
            },
            {
              gridIndex: 2,
              axisLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              position: 'top',
              axisLabel: {
                show: false,
              },
              splitLine: {
                show: false,
              },
            },
          ],
          yAxis: [
            {
              type: 'category',
              inverse: true,
              position: 'right',
              axisLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              axisLabel: {
                show: false,
              },
              data: myData,
            },
            {
              gridIndex: 1,
              type: 'category',
              inverse: true,
              position: 'left',
              axisLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              axisLabel: {
                show: true,
                textStyle: {
                  color: '#676767',
                  fontSize: remFontSize(12 / 64),
                },
              },
              data: myData.map(function(value) {
                return {
                  value: value,
                  textStyle: {
                    align: 'center', // 进度条左侧文字对齐方式
                  },
                };
              }),
            },
            {
              gridIndex: 2,
              type: 'category',
              inverse: true,
              position: 'left',
              axisLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              axisLabel: {
                show: false,
              },
              data: myData,
            },
          ],
          series: [],
        },
        options: [],
      };

      options.baseOption.timeline.data.push(1);
      options.options.push({
        tooltip: {
          show: false,
        },
        series: [
          {
            type: 'pictorialBar',
            xAxisIndex: 2,
            yAxisIndex: 2,
            symbol: 'rect',
            itemStyle: {
              normal: {
                color: '#EBEBEB', // 进度条阴影颜色
              },
            },
            barWidth: 5,
            symbolRepeat: true, // 是否显示进度条阴影
            symbolSize: [4, 12], // 进度条阴影格子大小
            data: lineData,
            barGap: '-100%',
            barCategoryGap: 0,
            label: {
              normal: {
                show: true, // 是否显示进度条上方的百分比
                formatter: (series) => {
                  let astyle = 'a0';
                  if (series.dataIndex == 0) {
                    astyle = 'a0';
                  } else if (series.dataIndex == 1) {
                    astyle = 'a1';
                  } else if (series.dataIndex == 2) {
                    astyle = 'a2';
                  }

                  return (
                    '{' +
                    astyle +
                    '|' +
                    thisYearData[series.dataIndex] +
                    '}' +
                    '{b|' +
                    '/' +
                    series.data +
                    '}'
                  );
                },
                rich: {
                  a0: {
                    color: this.colors[0],
                    fontSize: remFontSize(14 / 64),
                  },
                  a1: {
                    color: this.colors[1],
                    fontSize: remFontSize(14 / 64),
                  },
                  a2: {
                    color: this.colors[2],
                    fontSize: remFontSize(14 / 64),
                  },
                  b: {
                    color: '#676767',
                    fontSize: remFontSize(12 / 64),
                  },
                },
                position: 'insideTopRight',
                /* textStyle: {
                    color: '#767676', // 进度条上方百分比字体颜色
                    fontSize: 14,
                  }, */
                offset: [0, -remFontSize(25 / 64)],
              },
            },
          },
          {
            type: 'pictorialBar',
            xAxisIndex: 2,
            yAxisIndex: 2,
            symbol: 'rect',
            barWidth: remFontSize(5 / 64),
            itemStyle: {
              normal: {
                barBorderRadius: 5,
                color: (params) => {
                  return this.colors[params.dataIndex];
                }, // 进度条颜色 #36d7b6
              },
            },
            symbolRepeat: true,
            symbolSize: [remFontSize(4 / 64), remFontSize(12 / 64)], // 进度条格子大小
            data: thisYearData,
          },
        ],
      });

      this.progressChart.setOption(options);
    },
  },
};
</script>

<style lang="scss" scoped>
#box {
  width: 100%;
  height: 100%;
  #title {
    height: 0.75rem;
    line-height: 0.75rem;
    border-bottom: 0.025rem solid #bddfeb8f;
    padding-left: 0.2rem;
    #states {
      display: inline-block;
      height: 0.15rem;
      width: 0.15rem;
      border-radius: 50%;
    }
    #name {
      padding-left: 0.2rem;
      font-size: 0.25rem;
    }
  }

  #info_content {
    padding: 0.25rem;
    width: 100%;
    height: calc(100% - 0.75rem);
    display: flex;
    flex-direction: column;
    .progressChart {
      flex: 1;
    }
    #thred {
      flex: 1;
      display: flex;
      #img {
        flex: 3;
        background-image: url('../../../../assets/imgs/thred.png');
        background-repeat: no-repeat;
        background-size: cover;
        background-color: #f2fafd;
        border-radius: 0.125rem;
      }
      #thred_info {
        flex: 2;
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 0.25rem;
      }
    }
  }
}
</style>
