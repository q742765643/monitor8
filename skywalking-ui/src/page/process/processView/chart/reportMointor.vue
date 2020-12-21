<template>
  <div class="dataMointor">
    <commonTitle v-bind="$attrs"></commonTitle>
    <div class="dataView_chart" :id="chartID"></div>
  </div>
</template>

<script>
  import moment from 'moment';
  import commonTitle from '../commonFull/commonTitle';
  import echarts from 'echarts';
  import { remFontSize } from '@/components/utils/fontSize.js';
  import request from '@/utils/request';
  export default {
    components: { commonTitle },
    props: ['chartID'],
    data() {
      return {
        mixChart: '',
        chartlegend: [],
        timeList: [],
        memoryList: [],
        cpuList: [],
      };
    },
    mounted() {
      this.fileLineDiagram();
      this.$nextTick(() => {});

      window.addEventListener('resize', () => {
        this.mixChart.resize();
      });
    },
    methods: {
      fileLineDiagram() {
        request({
          url: '/processQReport/getProcessView',
          method: 'get',
          params: {
            id: this.chartID,
            params: {
              beginTime: moment()
                .subtract(30, 'minutes')
                .format('YYYY-MM-DD HH:mm:ss'),
              endTime: moment().format('YYYY-MM-DD HH:mm:ss'),
              orderBy: {},
            },
          },
        }).then((data) => {
          let dataAll = data.data;
          this.chartlegend = dataAll.title;
          this.timeList = dataAll.time;
          dataAll.data.forEach((element) => {
            if (element.name == '平均cpu使用率') {
              element.data.forEach((item) => {
                this.cpuList.push(item * 100);
              });
            } else if (element.name == '平均内存使用率') {
              element.data.forEach((item) => {
                this.memoryList.push(item * 100);
              });
            }
          });
          this.drawChart(this.chartID);
        });
      },
      drawChart(id) {
        this.mixChart = echarts.init(document.getElementById(id));
        var colors = ['#5793f3', '#d14a61', '#675bba'];
        let options = {
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
            color: '#565656',
          },
          color: colors,
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
            },
          },
          grid: {
            top: '20%',
            bottom: '10%',
            right: '15%',
          },

          legend: {
            data: this.chartlegend,
          },
          xAxis: [
            {
              type: 'category',
              axisTick: {
                show: false,
              },
              splitLine: {
                show: false,
              },
              axisLine: {
                lineStyle: {
                  color: '#bababa',
                  width: 0.5,
                },
              },
              data: this.timeList,
            },
          ],
          yAxis: [
            {
              type: 'value',
              name: '(%)',
              min: 0,
              position: 'right',
              axisTick: {
                //y轴刻度线
                show: false,
              },
              splitLine: {
                show: false,
              },
              axisLine: {
                lineStyle: {
                  color: '#bababa',
                  width: 0.5,
                },
              },
            },
            {
              type: 'value',
              name: '(%)',
              min: 0,
              position: 'left',
              axisTick: {
                //y轴刻度线
                show: false,
              },
              axisLine: {
                lineStyle: {
                  color: '#bababa',
                  width: 0.5,
                },
              },
              splitLine: {
                show: false,
              },
            },
          ],
          series: [
            {
              name: '平均cpu使用率',
              type: 'line',
              barWidth: '35%',
              data: this.cpuList,
              itemStyle: {
                normal: {
                  color: '#0FC0A3 ',
                },
              },
            },
            {
              name: '平均内存使用率',
              type: 'line',
              yAxisIndex: 1,
              data: this.memoryList,
            },
          ],
        };
        this.mixChart.setOption(options);
      },
    },
  };
</script>

<style lang="scss" scoped>
  .dataMointor {
    width: 100%;
    height: 100%;
    background: white;

    .dataView_chart {
      width: 100%;
      height: calc(100% - 56px);
    }
  }
</style>
