<template>
  <div id="dataState">
    <div><planeTitle titleName="数据状态"></planeTitle></div>
    <div id="data_contnet"></div>
  </div>
</template>

<script>
import planeTitle from '@/components/titile/planeTitle.vue';
import { remFontSize } from '@/components/utils/fontSize.js';
import echarts from 'echarts';
import moment from 'moment';
import request from "@/utils/request";
export default {
  name: 'distributAlarm',
  components: { planeTitle },
  data() {
    return {
      charts: '',
      hoursList:[],
      dataList:[],
      daysList:[],
    };
  },
  methods: {
    drawheatMap(id) {

      this.charts = echarts.init(document.getElementById(id));

      var hours = this.hoursList;
      var days = this.daysList;

      let states = [
        { label: '一般', value: 0, color: '#FCAB13' },
        { label: '严重', value: 1, color: '#FD651A' },
        { label: '故障', value: 2, color: '#9C82ED' },
        { label: '正常', value: 3, color: '#2BB9F7' },
        { label: '未执行', value: 4, color: '#9C82ED' },
      ];


      let option = {
        textStyle: {
          fontFamily: 'Alibaba-PuHuiTi-Regular',
        },
        tooltip: {
          position: 'top',
          formatter: (params) => {
            return states[params.value[2]].label;
            },
        },
        animation: false,
        grid: {
          bottom: '10%',
          top: '5%',
          //left: '10%',
          right: '0%',
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
            fontSize: remFontSize(12 / 64),
            color: '#353535',
          },
        },
        yAxis: {
          axisLabel: {
            show: true,
            fontSize: remFontSize(12 / 64),
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
            data: this.dataList,
            label: {
              //show: true,
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
    getFileStatus() {
      request({
        url: '/main/getFileStatus',
        method: 'get'
      }).then(data => {
        this.daysList = data.data.days;
        this.hoursList = data.data.hours;
        this.dataList = data.data.data
        this.drawheatMap('data_contnet');
      });
    }
  },
  mounted() {
    this.getFileStatus();
    this.$nextTick(() => {
      //this.drawheatMap('data_contnet');
    });
    setTimeout(() => {
      window.addEventListener('resize', () => {
        this.charts.resize();
      });
    }, 500);
  },


};
</script>

<style lang="scss" scoped>
#dataState {
  height: 6.45rem;
  width: 100%;
  //margin-top: 0.5rem;
  box-shadow: $plane_shadow;
  #data_contnet {
    height: 85%;
  }
}
</style>
