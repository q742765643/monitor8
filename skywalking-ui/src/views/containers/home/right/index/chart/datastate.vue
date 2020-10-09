<template>
  <div id="heatMap"></div>
</template>

<script>
import echarts from 'echarts';
import moment from 'moment';
export default {
  data() {
    return {
      charts: '',
    };
  },
  methods: {
    drawheatMap(id) {
      //获取横轴刻度
      var nowh = moment().hour();
      var h = '';
      var xArray = [];
      xArray.push(nowh);

      for (let i = 1; i < 24; i++) {
        h = moment()
          .subtract(i, 'hour')
          .format('H');

        if (h == '0') {
          //  h = h + '\n' + moment().format('MM/DD');
          h = moment().format('M/D');
        }
        if (i == 23) {
          h = moment()
            .subtract(1, 'day')
            .format('M/D');
        }
        xArray.push(h);
      }

      this.charts = echarts.init(document.getElementById(id));

      var hours = xArray.reverse();
      var days = ['we', 'df', 'gh', 'qw', 'df', 'fg', 'er', 'sd'];

      let states = [
        /*    { label: '正常', value: 0, color: '#2BB9F7' }, */
        { label: '一般', value: 1, color: '#FCAB13' },
        { label: '严重', value: 2, color: '#FD651A' },
        { label: '断线', value: 3, color: '#9C82ED' },
      ];
      var data = [];
      let x, y, value;
      for (let i = 0; i < 30; i++) {
        y = Math.floor(Math.random() * (0 - 7)) + 7;
        x = Math.floor(Math.random() * (0 - 23)) + 23;
        value = Math.floor(Math.random() * (1 - 4)) + 4;
        data.push([x, y, value]);
      }

      let option = {
        tooltip: {
          position: 'top',
          formatter: (params) => {
            return states[params.value[2] - 1].label;
          },
        },
        animation: false,
        grid: {
          bottom: '10%',
          top: '5%',
          left: '5%',
          right: '5%',
        },
        xAxis: {
          axisTick: {
            //y轴刻度线
            show: false,
          },
          splitLine: {
            //网格线
            lineStyle: {
              color: '#FFFFFF',
              width: 1,
              type: 'solid',
            },

            show: true,
          },

          data: hours,
          axisLabel: {
            interval: 0,
            show: true,
            fontSize: 10,
            color: '#353535',
          },
        },
        yAxis: {
          axisLabel: {
            show: true,
            fontSize: 10,
            color: '#353535',
          },
          axisTick: {
            //y轴刻度线
            show: false,
          },
          splitLine: {
            //网格线
            lineStyle: {
              color: '#FFFFFF',
              width: 1,
              type: 'solid',
            },
            show: true,
          },
          data: days,
          splitArea: {
            show: true,
            areaStyle: {
              color: '#2BB9F7',
              opacity: 1,
            },
          },
        },
        visualMap: {
          show: false,
          min: 0,
          max: 10,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '15%',
          type: 'piecewise',
          pieces: states,
        },
        series: [
          {
            type: 'heatmap',
            data: data,
            label: {
              show: true,
              /* formatter: (params) => {
                return states[params.value[2] - 1].label;
              }, */
            },
            emphasis: {
              show: false,
              itemStyle: {
                color: '#cacaca',
                shadowBlur: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
            },
          },
        ],
      };

      this.charts.setOption(option);
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.drawheatMap('heatMap');
    });
    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
};
</script>

<style lang="scss" scoped>
#heatMap {
  height: 3rem;
  width: 9.5875rem;
}
</style>