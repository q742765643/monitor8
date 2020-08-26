<template>
  <div id="piechart"></div>
</template>

<script>
import echarts from 'echarts';
export default {
  props: ['peiData', 'pieColor'],
  data() {
    return {
      charts: '',
    };
  },
  methods: {
    drawPie(id) {
      this.charts = echarts.init(document.getElementById(id));

      let option = {
        legend: {
          type: 'plain',
          orient: 'vertical',
          right: 40,
          top: 40,
          bottom: 20,
          data: ['链路设备', '主设备', '业务软件', '业务数据'],
        },
        color: this.pieColor,
        tooltip: {
          trigger: 'item',
          formatter: '{b} : {c} ({d}%)',
          padding: [5, 30, 5, 5],
          textStyle: {
            fontSize: 18,
          },
        },
        series: [
          {
            type: 'pie',
            radius: [50, 70],
            center: ['40%', '50%'],
            roseType: 'radius',
            label: {
              show: true,
              fontSize: 13,
              formatter: '{c}\n\n{b}',
              color: '#000000',
              // margin: '80',
              position: 'outer',
              padding: [0, -30, 0, -30],
            },

            labelLine: { length: 10, length2: 50, lineStyle: { color: '#acacac', width: 0.5 } },
            emphasis: {
              label: {
                show: true,
              },
            },
            data: this.peiData,
          },
        ],
      };

      this.charts.setOption(option);
    },
  },
  //调用
  mounted() {
    this.$nextTick(function() {
      this.drawPie('piechart');
    });
    window.addEventListener('resize', () => {
      debugger;
      this.charts.resize();
    });
  },
};
</script>

<style lang="scss" scoped>
#piechart {
  height: 3rem;
  width: 9.5875rem;
}
</style>