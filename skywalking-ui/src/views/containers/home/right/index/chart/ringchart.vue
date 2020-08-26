<template>
  <!--为echarts准备一个具备大小的容器dom-->
  <div :id="ringData.id"></div>
</template>
<script>
import echarts from 'echarts';

export default {
  props: ['ringData'],
  name: '',
  data() {
    return {
      charts: '',
    };
  },
  methods: {
    drawPie(id) {
      var placeHolderStyle = {
        normal: {
          label: {
            show: false,
          },
          labelLine: {
            show: false,
          },
          color: 'rgba(0,0,0,0)',
          borderWidth: 0,
        },
        emphasis: {
          color: 'rgba(0,0,0,0)',
          borderWidth: 0,
        },
      };
      const { random, PI, cos, sin } = Math;
      const val = random() * 100;

      // 圆心角的一半
      const angle = (PI * val) / 50 / 2;
      // 渐变起点
      const pointStart = [0.5 - 0.5 * cos(angle) * sin(angle), 0.5 + 0.5 * cos(angle) * cos(angle)];
      // 渐变终点
      const pointEnd = [0.5 - 0.5 * sin(angle), 0.5 + 0.5 * cos(angle)];

      var dataStyle = {
        normal: {
          formatter: '{c}',
          position: 'center',
          show: true,
          textStyle: {
            fontSize: '16',
            fontWeight: 'normal',
            color: '#0000FF',
          },
        },
      };

      this.charts = echarts.init(document.getElementById(id));

      let option = {
        backgroundColor: '#fff',
        title: [
          {
            text: this.ringData.name,
            left: '50%',
            top: '80%',
            textAlign: 'center',
            textStyle: {
              fontWeight: 'normal',
              fontSize: '16',
              color: '#656565',
              textAlign: 'center',
            },
          },
        ],

        //第一个图表
        series: [
          {
            type: 'pie',
            hoverAnimation: false, //鼠标经过的特效
            radius: ['55%', '65%'],
            center: ['50%', '40%'],
            startAngle: 90,
            labelLine: {
              normal: {
                show: false,
              },
            },
            label: {
              normal: {
                position: 'center',
              },
            },
            data: [
              {
                value: 360,
                itemStyle: {
                  normal: {
                    color: '#E1E8EE',
                  },
                },
              },
              {
                value: 0,
                itemStyle: placeHolderStyle,
              },
            ],
          },
          //上层环形配置
          {
            type: 'pie',
            hoverAnimation: false, //鼠标经过的特效
            radius: ['55%', '65%'],
            center: ['50%', '40%'],
            startAngle: 90,
            labelLine: {
              normal: {
                show: false,
              },
            },
            label: {
              normal: {
                position: 'center',
              },
            },

            data: [
              {
                value: this.ringData.value,
                itemStyle: {
                  normal: {
                    color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                      { offset: 0, color: this.ringData.color1 },
                      { offset: 1, color: this.ringData.color2 },
                    ]),
                    /* color: {
                      type: 'linear',
                      x: 0,
                      y: 0,
                      x2: 1,
                      y2: 1,
                      colorStops: [
                        {
                          offset: 0,
                          color: this.ringData.color1,
                        },
                        {
                          offset: 1,
                          color: this.ringData.color2,
                        },
                      ],
                      globalCoord: true,
                    }, */
                  },
                },
                label: dataStyle,
              },
              {
                value: this.ringData.total - this.ringData.value,
                itemStyle: placeHolderStyle,
              },
            ],
          },
        ],
      };

      this.charts.setOption(option);
    },
  },
  //调用
  mounted() {
    this.$nextTick(() => {
      this.drawPie(this.ringData.id);
    });

    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
};
</script>
<style scoped>
#main1,
#main2,
#main3,
#main0 {
  height: 2rem;
  width: 2.378125rem;
}
</style>