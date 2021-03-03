<template>
  <div id="distributAlarm">
    <div><planeTitle titleName="监控总览"></planeTitle></div>
    <div id="alarmChart" style="height: calc(100% - 56px)"></div>
  </div>
</template>

<script>
import echarts from 'echarts';
import planeTitle from '@/components/titile/planeTitle.vue';
import { remFontSize } from '@/components/utils/fontSize.js';
import request from '@/utils/request';
// 接口地址
import hongtuConfig from '@/utils/services';
export default {
  data() {
    return {
      alarmCharts: '',
      peiData: [],
      alarmData: [],
      pieColor: ['#F97185', '#A051EA', '#F8D27E', '#42E4D5'],
    };
  },
  name: 'distributAlarm',
  components: { planeTitle },
  async mounted() {
    setTimeout(async () => {
      await this.getMonitorView();
      await this.getAlarmDistribution();
    }, 100);
    window.addEventListener('resize', () => {
      setTimeout(() => {
        this.alarmCharts.resize();
      }, 100);
    });
  },
  methods: {
    getMonitorView() {
      request({
        url: '/main/getMonitorViewVo',
        method: 'get',
      }).then((data) => {
        this.peiData = data.data;
        this.drawPie('alarmChart');
      });
    },
    getAlarmDistribution() {
      request({
        url: '/main/getAlarmDistribution',
        method: 'get',
      }).then((data) => {
        this.alarmData = data.data;
        this.drawPie('alarmChart');
      });
    },
    drawPie(id) {
      // this.alarmCharts = echarts.init(document.querySelector(id));
      this.alarmCharts = echarts.init(document.getElementById(id));
      console.log($('#alarmChart').height());
      let option = {
        title: [
          {
            text: '监控对象分布',
            left: '25%',
            textAlign: 'center',
            textStyle: {
              fontSize: remFontSize(0.175),
            },
          },
          {
            text: '告警分布',
            left: '75%',
            textAlign: 'center',
            textStyle: {
              fontSize: remFontSize(0.175),
            },
          },
        ],
        textStyle: {
          fontFamily: 'NotoSansHans-Medium',
        },
        legend: {
          //type: 'plain',
          //orient: 'vertical',
          left: 'center',
          top: 'bottom',
          //right: remFontSize(0.375),
          //top: remFontSize(0.5),
          //bottom: remFontSize(0.25),
          textStyle: {
            fontSize: remFontSize(0.15),
          },

          data: ['主机设备', '链路设备', '进程任务', '数据任务'],
        },
        color: this.pieColor,
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
          padding: [5, 30, 5, 5],
          textStyle: {
            fontSize: remFontSize(0.15),
            fontFamily: ' NotoSansHans-Medium',
          },
        },
        series: [
          {
            name: '监控总览',
            type: 'pie',
            radius: [20, 40],
            center: ['25%', '50%'],
            roseType: 'area',
            label: {
              show: true,
              fontSize: remFontSize(0.15),

              formatter: '{c}\n\n{b}',
              color: '#000000',
              position: 'outer',
              padding: [0, -30, 0, -30],
            },

            labelLine: {
              length: remFontSize(10 / 64),
              length2: remFontSize(50 / 64),
              lineStyle: { color: '#acacac', width: 0.5 },
            },
            emphasis: {
              label: {
                show: true,
              },
            },
            data: this.peiData,
          },
          {
            name: '告警分布',
            type: 'pie',
            radius: [20, 40],
            center: ['75%', '50%'],
            roseType: 'area',
            label: {
              show: true,
              fontSize: remFontSize(0.15),
              formatter: '{c}\n\n{b}',
              color: '#000000',
              position: 'outer',
              padding: [0, -30, 0, -30],
            },

            labelLine: {
              length: remFontSize(10 / 64),
              length2: remFontSize(50 / 64),
              lineStyle: { color: '#acacac', width: 0.5 },
            },
            emphasis: {
              label: {
                show: true,
              },
            },
            data: this.alarmData,
          },
        ],
      };
      this.alarmCharts.setOption(option);
    },
  },
};
</script>

<style lang="scss" scoped>
#distributAlarm {
  width: 100%;
  height: 38%;
  box-shadow: $plane_shadow;
  margin-bottom: 10px;
  #alarmChart {
    // height: calc(100% - 56px);
    width: 100%;
  }
}
</style>
