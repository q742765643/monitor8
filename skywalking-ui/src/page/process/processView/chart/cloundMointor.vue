<template>
  <div id="dataMointor">
    <commonTitle v-bind="$attrs"></commonTitle>
    <div class="dataView_chart" :id="chartID"></div>
  </div>
</template>

<script>
  import commonTitle from '../commonFull/commonTitle';
  import { remFontSize } from '@/components/utils/fontSize.js';
  import echarts from 'echarts';
  export default {
    components: { commonTitle },
    props: ['chartID'],

    data() {
      return {
        barChart: '',
        lnerColors: [
          ['#BF439B', '#1272EB', '#0FC0A3'],
          ['#FFBA00', '#FF992C', '#FF696B'],
          ['#47F092', '#35D750', '#20BB01'],
        ],
      };
    },
    mounted() {
      this.$nextTick(() => {
        this.drawChart(this.chartID);
      });
      window.addEventListener('resize', () => {
        this.barChart.resize();
      });
    },
    methods: {
      drawChart(id) {
        this.barChart = echarts.init(document.getElementById(id));

        let options = {
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
          },
          color: ['#5793f3', '#d14a61', '#675bba'],
          textStyle: { color: '#565656' },
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
            data: ['文件合计个数', '文件合计大小', '文件平均大小'],
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

              data: ['0:00', '1:00', '2:00', '3:00', '4:00', '5:00', '6:00', '7:00', '8:00', '9:00', '10:00', '11:00'],
            },
          ],
          yAxis: [
            {
              type: 'value',
              name: '(KB)',
              min: 0,
              max: 250,
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
              max: 250,
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
              name: '(KB)',
              min: 0,
              max: 25,
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
              name: '文件合计个数',
              type: 'bar',
              barWidth: '25%',
              data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],

              itemStyle: {
                barBorderRadius: 5,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: this.lnerColors[0][0] },
                  { offset: 0.5, color: this.lnerColors[0][1] },
                  { offset: 1, color: this.lnerColors[0][2] },
                ]),
              },
            },
            {
              name: '文件合计大小',
              type: 'bar',
              barWidth: '25%',
              yAxisIndex: 1,
              data: [2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3],
              itemStyle: {
                barBorderRadius: 5,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: this.lnerColors[1][0] },
                  { offset: 0.5, color: this.lnerColors[1][1] },
                  { offset: 1, color: this.lnerColors[1][2] },
                ]),
              },
            },
            {
              name: '文件平均大小',
              type: 'bar',
              barWidth: '25%',
              yAxisIndex: 2,
              data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2],
              itemStyle: {
                barBorderRadius: 5,
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: this.lnerColors[2][0] },
                  { offset: 0.5, color: this.lnerColors[2][1] },
                  { offset: 1, color: this.lnerColors[2][2] },
                ]),
              },
            },
          ],
        };

        this.barChart.setOption(options);
      },
    },
  };
</script>

<style lang="scss" scoped>
  #dataMointor {
    width: 100%;
    height: 5rem;
    background: white;

    .dataView_chart {
      width: 100%;
      height: calc(5rem - 0.75rem);
    }
  }
</style>
