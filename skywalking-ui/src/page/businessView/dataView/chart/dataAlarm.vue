<template>
  <div id="dataMointor">
    <commonTitle v-bind="$attrs"></commonTitle>
    <div class="dataView_chart" :id="chartID"></div>
  </div>
</template>

<script>
import commonTitle from '../../commonFull/commonTitle';
import echarts from 'echarts';
import { remFontSize } from '@/components/utils/fontSize.js';
export default {
  components: { commonTitle },
  props: ['chartID'],

  data() {
    return {
      dataViev_pieChart: '',
      peiData: [
        { value: 5, name: '任务A' },
        { value: 4, name: '任务B' },
        { value: 2, name: '任务C' },
      ],
      legendData: ['任务A', '任务B', '任务C'],
      pieColors: ['#F69F40', '#2E65FD', '#1FCAA8'],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.drawChart(this.chartID);
    });
    window.addEventListener('resize', () => {
      this.dataViev_pieChart.resize();
    });
  },
  methods: {
    drawChart(id) {
      this.dataViev_pieChart = echarts.init(document.getElementById(id));

      let options = {
        textStyle: {
          fontFamily: 'Alibaba-PuHuiTi-Regular',
        },
        legend: {
          type: 'plain',
          bottom: remFontSize(20 / 64),
          data: this.legendData,
        },
        color: this.pieColors,
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)',
          // padding: [5, 30, 5, 5],
          textStyle: {
            fontSize: remFontSize(18 / 64),
          },
        },
        tooltip: {
          trigger: 'axis',
        },
        series: [
          {
            type: 'pie',
            radius: ['50%', '70%'],
            center: ['50%', '50%'],
            //roseType: 'radius',
            label: {
              show: true,
              fontSize: remFontSize(13 / 64),
              formatter: '{b}\n\n{c}',
              // color: '#000000',
              position: 'outer',
              padding: [0, -remFontSize(30 / 64), 0, -remFontSize(30 / 64)],
            },

            labelLine: {
              length: remFontSize(30 / 64),
              length2: remFontSize(100 / 64),
              lineStyle: { width: 0.5 },
            },
            emphasis: {
              label: {
                show: true,
              },
            },
            data: this.peiData,
          },
        ],
      };

      this.dataViev_pieChart.setOption(options);
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
