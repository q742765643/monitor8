<template>
  <div id="distributAlarm">
    <div><planeTitle titleName="告警分布"></planeTitle></div>
    <div id="alarmChart"></div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  import planeTitle from '@/components/titile/planeTitle.vue';
  import { remFontSize } from '@/components/utils/fontSize.js';
  // 接口地址
  import hongtuConfig from '@/utils/services';
  export default {
    data() {
      return {
        alarmCharts: '',
        peiData: [
          { name: '链路设备', value: 10 },
          { name: '主设备', value: 8 },
          { name: '业务软件', value: 25 },
          { name: '业务数据', value: 15 },
        ],
        pieColor: ['#F97185', '#A051EA', '#F8D27E', '#42E4D5'],
      };
    },
    name: 'distributAlarm',
    components: { planeTitle },
    async mounted() {
      await hongtuConfig.getAlarmDistribution().then((res) => {
        if (res.status == 200 && res.data.code == 200) {
          let dataarray = res.data.data;
          dataarray.forEach((element, index) => {
            if (element.classify == this.peiData[index].name) {
              this.peiData[index].value = element.num;
            }
          });
        }
      });
      this.$nextTick(function() {
        this.drawPie('alarmChart');
      });

      window.addEventListener('resize', () => {
        setTimeout(() => {
          this.alarmCharts.resize();
        }, 500);
      });
    },
    methods: {
      drawPie(id) {
        this.alarmCharts = echarts.init(document.getElementById(id));
        let option = {
          textStyle: {
            fontFamily: ' Alibaba-PuHuiTi-Medium',
          },
          legend: {
            type: 'plain',
            orient: 'vertical',
            right: remFontSize(0.375),
            top: remFontSize(0.5),
            bottom: remFontSize(0.25),
            textStyle: {
              fontSize: remFontSize(0.175),
            },

            data: ['链路设备', '主设备', '业务软件', '业务数据'],
          },
          color: this.pieColor,
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)',
            padding: [5, 30, 5, 5],
            textStyle: {
              fontSize: remFontSize(0.175),
              fontFamily: ' Alibaba-PuHuiTi-Medium',
            },
          },
          series: [
            {
              name: '统计',
              type: 'pie',
              radius: [30, 60],
              center: ['40%', '50%'],
              roseType: 'radius',
              label: {
                show: true,
                fontSize: remFontSize(0.175),

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
          ],
        };
        this.alarmCharts.setOption(option);
      },
    },
  };
</script>

<style lang="scss" scoped>
  #distributAlarm {
    height: 3.75rem;
    width: 100%;
    margin-top: 0.5rem;
    box-shadow: $plane_shadow;
    #alarmChart {
      height: 3rem;
      width: 100%;
    }
  }
</style>
