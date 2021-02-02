<template>
  <div id="dataState">
    <div>
      <planeTitle titleName="数据状态">
        <div slot="right" class="lengend">
          <div class="item" v-for="(item, index) in states" :key="index">
            <span class="color" :style="{ background: item.color }"></span>
            <span class="text">{{ item.label }}</span>
          </div>
        </div>
      </planeTitle>
    </div>
    <div id="data_contnet"></div>
  </div>
</template>

<script>
import planeTitle from '@/components/titile/planeTitle.vue';
import { remFontSize } from '@/components/utils/fontSize.js';
import echarts from 'echarts';
import moment from 'moment';
import request from '@/utils/request';
import { forEach } from 'lodash';
export default {
  name: 'distributAlarm',
  components: { planeTitle },
  data() {
    return {
      charts: '',
      hoursList: [],
      dataList: [],
      daysList: [],
      states: [
        { label: '一般', value: 0, color: '#FCAB13' },
        { label: '严重', value: 1, color: '#FD651A' },
        { label: '故障', value: 2, color: '#9C82ED' },
        { label: '正常', value: 3, color: '#2BB9F7' },
        { label: '未执行', value: 4, color: '#FF0000' },
        { label: '未启动', value: 5, color: '#0CB218' },
      ],
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
        { label: '未执行', value: 4, color: '#FF0000' },
        { label: '未启动', value: 5, color: '#0CB218' },
      ];

      let option = {
        textStyle: {
          fontFamily: 'Alibaba-PuHuiTi-Regular',
        },
        tooltip: {
          position: 'left',
          formatter: (params) => {
            console.log(params);
            let html =
              '<p style="margin-bottom: 0;"><span  style="width: 10px;height: 10px;border-radius: 50%;background: #0cb218;display: inline-block;margin-right: 2px;background:' +
              states[params.value[2]].color +
              '"></span>名称:' +
              days[params.value[1]] +
              '    时间:' +
              hours[params.value[0]] +
              '    状态:' +
              states[params.value[2]].label +
              '</p>';
            return html;
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
            itemStyle: {
              borderColor: '#fff',
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
        method: 'get',
      }).then((data) => {
        this.daysList = data.data.days;
        this.hoursList = data.data.hours;
        this.dataList = data.data.data;
        this.drawheatMap('data_contnet');
      });
    },
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
  height: calc(66% + 10px);
  box-shadow: $plane_shadow;
  #data_contnet {
    height: calc(100% - 56px);
  }
  .lengend {
    display: flex;
    align-items: center;
    font-family: 'Alibaba-PuHuiTi-Regular';
    margin-top: 50px;
    .item {
      display: flex;
      align-items: center;
      height: 50px;
      .color {
        margin-left: 20px;
        width: 20px;
        height: 20px;
      }
      .text {
        margin-left: 10px;
        font-weight: 600;
        // font-size: $ant_font_size;
        font-size: 24px;
      }
    }
  }
  .chartsTips {
    margin-bottom: 0;
    span {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background: #0cb218;
      display: inline-block;
      margin-right: 2px;
    }
  }
}
</style>
