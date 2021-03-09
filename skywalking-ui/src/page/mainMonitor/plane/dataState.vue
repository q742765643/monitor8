<template>
  <div id="dataState">
    <div>
      <planeTitle titleName="数据状态">
        <div slot="right" class="lengend">
          <div class="item" v-for="(item, index) in distList" :key="index">
            <span class="color" :style="{ background: item.color }"></span>
            <span class="text">{{ item.name }}</span>
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
      distList: [
        { color: '#efefef', name: '无采集' },
        { color: '#ff8040', name: '一般' },
        { color: 'red', name: '严重' },
        { color: '#0cb218', name: '正常' },
      ],
      charts: '',
      hoursList: [],
      dataList: [],
      daysList: [],
      tipsList: [],
    };
  },
  methods: {
    drawheatMap(id) {
      this.charts = echarts.init(document.getElementById(id));

      var hours = this.hoursList;
      var days = this.daysList;

      let option = {
        tooltip: {
          position: 'top',
        },
        animation: false,
        grid: {
          bottom: '10%',
          top: '5%',
          right: '0%',
          left: '12%',
        },
        tooltip: {
          position: 'left',
          formatter: (params) => {
            let thatValue = params.value.join(',');
            let thatIndex = '';
            this.dataList.forEach((element, ei) => {
              if (element.join(',') == thatValue) {
                thatIndex = ei;
              }
            });
            console.log(this.tipsList[thatIndex]);
            //thatIndex = 0;
            //应到，实到 ，晚到 ，准时率，时次
            let html =
              '<p style="margin-bottom: 0;">资料名称:' +
              days[params.value[1]] +
              '    时次:' +
              hours[params.value[0]] +
              '</br>' +
              '    到报率:' +
              params.value[2] +
              '%' +
              '    准时率:' +
              this.tipsList[thatIndex][3] +
              '</br>' +
              '应到:' +
              this.tipsList[thatIndex][0] +
              '实到:' +
              (Number(this.tipsList[thatIndex][1]) + Number(this.tipsList[thatIndex][2])) +
              '    准时到:' +
              this.tipsList[thatIndex][1] +
              '    迟到:' +
              this.tipsList[thatIndex][2] +
              '</br>' +
              '    资料时间:' +
              this.tipsList[thatIndex][4];
            ('</p>');
            return html;
          },
        },
        xAxis: {
          name: '时次',
          nameLocation: 'start',
          nameTextStyle: {
            padding: [25, -12, 0, 0],
            color: '#999',
          },
          type: 'category',
          data: hours,
          splitArea: {
            show: true,
          },
          axisTick: {
            lineStyle: {
              color: '#c4c4c4',
            },
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
          axisLabel: {
            interval: 0,
            show: true,
            fontSize: remFontSize(12 / 64),
            color: '#353535',
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: 'rgba(238,238,238,0.75)',
              opacity: 1,
            },
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
              color: 'rgba(238,238,238,0.75)',
              opacity: 1,
            },
          },
        },
        visualMap: {
          show: false,
          orient: 'horizontal',
          type: 'piecewise',
          pieces: [
            { min: 0, max: 99, color: '#ff8040' },
            { gte: 100, color: '#0cb218' },
            { value: 0, color: 'red' },
          ],
        },
        series: [
          {
            name: '',
            type: 'heatmap',
            data: this.dataList,
            label: {
              show: true,
            },
            itemStyle: {
              borderColor: '#fff',
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
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
        this.tipsList = data.data.tip;
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
