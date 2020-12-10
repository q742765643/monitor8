<template>
  <div class="dataMointor">
    <commonTitle v-bind="$attrs"></commonTitle>
    <div class="dataView_chart" :id="chartID"></div>
  </div>
</template>

<script>
  import commonTitle from '../../commonFull/commonTitle';
  import echarts from 'echarts';
  import { remFontSize } from '@/components/utils/fontSize.js';
  import request from '@/utils/request';
  export default {
    components: { commonTitle },
    props: ['chartID'],
    data() {
      return {
        mixChart: '',
        timeList: [],
        lateList: [],
        shouldList: [],
        actualList: [],
        tempList: [],
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
          url: '/fileQReport/fileLineDiagram',
          method: 'get',
          params: { taskId: this.chartID },
        }).then((data) => {
          let list = data.data;
          this.timeList = [];
          this.lateList = [];
          this.shouldList = [];
          this.actualList = [];
          this.tempList = [];
          list.forEach((item, index) => {
            this.timeList.push(this.parseTime(item.start_time_l, '{d}/{h}:{i}'));
            this.lateList.push(item.late_num);
            this.shouldList.push(item.file_num);
            this.tempList.push(item.file_num);
            this.actualList.push(Number(item.late_num) + Number(item.real_file_num));
          });
          this.drawChart(this.chartID);
        });
      },
      drawChart(id) {
        this.mixChart = echarts.init(document.getElementById(id));
        var colors = ['#5793f3', '#d14a61', '#675bba'];
        let shouldA = this.tempList.sort((a, b) => a - b);
        let shouldL = Number(shouldA[shouldA.length - 1]) + 20;
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
            data: ['文件晚到', '文件应到', '文件实到'],
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
              name: '(个)',
              min: 0,
              max: shouldL,
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

              axisLabel: {
                formatter: '{value} ',
              },
            },
            {
              type: 'value',
              name: '(个)',
              min: 0,
              max: shouldL,
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
              axisLabel: {
                formatter: '{value} ',
              },
            },
            {
              type: 'value',
              name: '(个)',
              min: 0,
              max: shouldL,
              position: 'right',
              offset: 50,
              axisLine: {
                lineStyle: {
                  color: '#bababa',
                  width: 0.5,
                },
              },
              splitLine: {
                lineStyle: {
                  color: '#bababa',
                  width: 0.5,
                  type: 'solid',
                },
                show: true,
              },
              axisLabel: {
                formatter: '{value} ',
              },
            },
          ],
          series: [
            {
              name: '文件晚到',
              type: 'bar',
              barWidth: '35%',
              data: this.lateList,

              itemStyle: {
                barBorderRadius: 5,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#BF439B' },
                  { offset: 0.5, color: '#1272EB' },
                  { offset: 1, color: '#0FC0A3' },
                ]),
              },
            },
            {
              name: '文件应到',
              type: 'line',
              yAxisIndex: 1,
              data: this.shouldList,
            },
            {
              name: '文件实到',
              type: 'bar',
              yAxisIndex: 2,
              data: this.actualList,
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
