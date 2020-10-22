<template>
  <a-layout-content id="main_content">
    <router-view></router-view>
  </a-layout-content>
</template>

<script>
  import echarts from 'echarts';
  export default {
    name: 'container',
    data() {
      return {
        charts: '',
      };
    },
    mounted() {
      /*  this.$nextTick(() => {
      setTimeout(() => {
        this.drawChart();
      }, 300);
    }); */
    },
    methods: {
      drawChart() {
        this.charts = echarts.init(document.getElementById('chartsdiv1'));
        let option = {
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          },
          yAxis: {
            type: 'value',
          },
          series: [
            {
              data: [820, 932, 901, 934, 1290, 1330, 1320],
              type: 'line',
            },
          ],
        };

        this.charts.setOption(option);

        window.addEventListener('resize', () => {
          setTimeout(() => {
            this.charts.resize();
          }, 300);
        });
        /* window.onresize = (value) => {
        this.charts.resize();
      }; */
      },
    },
  };
</script>

<style lang="scss" scoped>
  #main_content {
    background: #fff;
    height: 100%;
    width: 100%;
    padding: 0.5rem 0.375rem 0.375rem 0.25rem;
  }
</style>
