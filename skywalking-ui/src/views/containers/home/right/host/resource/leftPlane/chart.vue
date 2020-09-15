<template>
  <div :id="ringData.id"></div>
</template>

<script>
import echarts from 'echarts';
export default {
  props: ['ringData'],
  data() {
    return {
      charts: '',
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.drawpipe(this.ringData.id);
    });
  },
  methods: {
    drawpipe(id) {
      this.charts = echarts.init(document.getElementById(id));
      var fontColor = '#fff';
      var datas = {
        value: this.ringData.value,
        total: this.ringData.total,
        company: '%',
        ringColor: [
          {
            offset: 0,
            color: this.ringData.color1,
          },
          {
            offset: 1,
            color: this.ringData.color2,
          },
        ],
      };
      let option = {
        //backgroundColor: '#000',
        title: {
          text: datas.value + datas.company,
          x: 'center',
          y: 'center',
          textStyle: {
            fontWeight: 'normal',
            color: '#000',
            fontSize: '14',
          },
        },
        color: ['#D6D6D4'],

        series: [
          {
            type: 'pie',
            clockWise: true,
            radius: ['70%', '90%'],
            itemStyle: {
              normal: {
                label: {
                  show: false,
                },
                labelLine: {
                  show: false,
                },
              },
            },
            hoverAnimation: false,
            data: [
              {
                value: datas.value,
                name: '',
                itemStyle: {
                  normal: {
                    color: {
                      colorStops: datas.ringColor,
                    },
                    label: {
                      show: false,
                    },
                    labelLine: {
                      show: false,
                    },
                  },
                },
              },
              {
                name: '',
                value: datas.total - datas.value,
              },
            ],
          },
        ],
      };

      this.charts.setOption(option);
    },
  },
};
</script>

<style lang="scss" scoped>
div {
  height: 1.25rem;
  width: 1.25rem;
}
</style>