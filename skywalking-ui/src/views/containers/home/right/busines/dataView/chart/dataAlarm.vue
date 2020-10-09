<template>
  <div id="dataMointor">
    <commonTitle :titleName="titleName"></commonTitle>
    <div id="pieChart"></div>
  </div>
</template>

<script>
import commonTitle from './commonTitle';
import echarts from 'echarts';
export default {
  components: { commonTitle },
  data() {
    return {
      titleName: '最近数据告警',
      pieChart: '',
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
      this.drawChart('pieChart');
    });
  },
  methods: {
    drawChart(id) {
      this.pieChart = echarts.init(document.getElementById(id));

      let options = {
        legend: {
          type: 'plain',
          bottom: 20,
          data: this.legendData,
        },
        color: this.pieColors,
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)',
          // padding: [5, 30, 5, 5],
          textStyle: {
            fontSize: 18,
          },
        },
        tooltip: {
          trigger: 'axis',
        },
        series: [
          {
            type: 'pie',
            radius: [50, 70],
            center: ['50%', '50%'],
            //roseType: 'radius',
            label: {
              show: true,
              fontSize: 13,
              formatter: '{b}\n\n{c}',
              // color: '#000000',
              position: 'outer',
              padding: [0, -30, 0, -30],
            },

            labelLine: { length: 30, length2: 100, lineStyle: { width: 0.5 } },
            emphasis: {
              label: {
                show: true,
              },
            },
            data: this.peiData,
          },
        ],
      };

      this.pieChart.setOption(options);
    },
  },
};
</script>

<style lang="scss" scoped>
#dataMointor {
  width: 100%;
  height: 100%;
  background: white;

  #pieChart {
    width: 100%;
    height: calc(100% - 0.75rem);
  }
}
</style>
