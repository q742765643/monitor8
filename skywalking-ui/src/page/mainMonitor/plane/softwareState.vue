<template>
  <div id="softwareState">
    <div>
      <planeTitle titleName="软件状态">
        <div slot="right" class="lengend">
          <div class="item" v-for="(item, index) in types" :key="index">
            <span class="color" :style="{ background: item.color }"></span>
            <span class="text">{{ item.name }}</span>
          </div>
        </div>
      </planeTitle>
    </div>
    <div id="software_contnet"></div>
  </div>
</template>

<script>
  import planeTitle from '@/components/titile/planeTitle.vue';
  import { remFontSize } from '@/components/utils/fontSize.js';
  import echarts from 'echarts';
  import request from '@/utils/request';
  import moment from 'moment';
  export default {
    name: 'softwareState',
    components: { planeTitle },

    data() {
      return {
        charts: '',
        timeData: [],
        dataList: [],
        longhoursList: [],
        types: [
          //{ name: '未监控', color: '#0CB218', color1: '#01F21D' },
          { name: '不在线', color: '#0063C8', color1: '#00C9FD' },
          { name: '可能异常', color: '#5A15AB', color1: '#9620F3' },
          { name: '在线', color: '#159D9F', color1: '#0AEBF1' },
        ],
        categories: [],
      };
    },
    methods: {
      drawBar(id) {
        let that = this;
        this.charts = echarts.init(document.getElementById(id)); //加载图形的div

        var data = [];
        var mockData = [];

        /* for (let k = 0; k < that.categories.length; k++) {
        data = this.getData(k);
        mockData.push(...data);
      }*/

        for (let j = 0; j < that.dataList.length; j++) {
          var num = this.dataList[j];
          var typeNum = num[3];
          mockData.push({
            name: that.types[typeNum],
            value: [num[0], num[1], num[2]],
            itemStyle: {
              normal: {
                //color: that.types[num].color,
                color: {
                  type: 'linear',
                  x: 1,
                  y: 1,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    {
                      offset: 0,
                      color: that.types[typeNum].color, // 0% 处的颜色
                    },
                    {
                      offset: 1,
                      color: that.types[typeNum].color1, // 100% 处的颜色
                    },
                  ],
                  globalCoord: false, // 缺省为 false
                },
              },
            },
          });
        }

        //设定图形效果
        function renderItem(params, api) {
          var categoryIndex = api.value(0);
          var start = api.coord([api.value(1), categoryIndex]);
          var end = api.coord([api.value(2), categoryIndex]);
          var height = api.size([0, 1])[1] * 0.5;

          var rectShape = echarts.graphic.clipRectByRect(
            {
              x: start[0],
              y: start[1] - height / 2,
              width: end[0] - start[0],
              height: height,
            },
            {
              x: params.coordSys.x,
              y: params.coordSys.y,
              width: params.coordSys.width,
              height: params.coordSys.height,
            },
          );

          return (
            rectShape && {
              type: 'rect',
              shape: rectShape,
              style: api.style(),
            }
          );
        }

        var option = {
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
          },
          //鼠标提示
          tooltip: {
            formatter: function(params) {
              return (
                params.name.name +
                '</br>' +
                that.longhoursList[params.value[1]] +
                '~' +
                that.longhoursList[params.value[2]]
              );
            },
          },

          grid: {
            left: '20%',
            right: '5%',
            top: '5%',
            width: '75%',
            height: '80%',
          },
          xAxis: {
            data: that.timeData,
            axisTick: {
              show: false,
            },
            axisLabel: {
              interval: 0,
              show: true,
              fontSize: remFontSize(12 / 64),
              color: '#353535',
            },
            splitLine: {
              show: false,
            },
          },
          yAxis: {
            data: that.categories,
            axisTick: {
              show: false,
            },
            splitLine: {
              show: false,
            },
            axisLabel: {
              interval: 0,
              show: true,
              fontSize: remFontSize(12 / 64),
              color: '#353535',
            },
          },

          series: [
            {
              name: '未监控',
              type: 'custom',
              renderItem: renderItem,
              encode: {
                x: [1, 2],
                y: 0,
              },
              data: mockData,
            },
            {
              name: '不在线',
              type: 'custom',
              renderItem: renderItem,

              encode: {
                x: [1, 2],
                y: 0,
              },
              data: mockData,
            },
            {
              name: '可能异常',
              type: 'custom',
              renderItem: renderItem,

              encode: {
                x: [1, 2],
                y: 0,
              },
              data: mockData,
            },
            {
              name: '在线',
              type: 'custom',
              renderItem: renderItem,

              encode: {
                x: [1, 2],
                y: 0,
              },
              data: mockData,
            },
          ],
        };
        this.charts.setOption(option);
      },
      getData(k) {
        let that = this;
        let data = [];
        let typeNum = 2;
        data.push({
          name: that.types[typeNum],
          value: [k, 0, 24],
          itemStyle: {
            normal: {
              //color: that.types[num].color,
              color: {
                type: 'linear',
                x: 1,
                y: 1,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: that.types[typeNum].color, // 0% 处的颜色
                  },
                  {
                    offset: 1,
                    color: that.types[typeNum].color1, // 100% 处的颜色
                  },
                ],
                globalCoord: false, // 缺省为 false
              },
            },
          },
        });

        return data;
      },
      getProcess() {
        request({
          url: '/main/getProcess',
          method: 'get',
        }).then((data) => {
          this.categories = data.data.days;
          this.timeData = data.data.hours;
          this.longhoursList = data.data.longhoursList;
          this.dataList = data.data.nums;
          this.drawBar('software_contnet');
        });
      },
    },
    mounted() {
      this.getProcess();
      this.$nextTick(function() {});

      setTimeout(() => {
        window.addEventListener('resize', () => {
          this.charts.resize();
        });
      }, 500);
    },
  };
</script>

<style lang="scss" scoped>
  #softwareState {
    width: 100%;
    height: calc(34% - 30px);
    margin-top: 20px;
    box-shadow: $plane_shadow;
    #software_contnet {
      width: 100%;
      height: calc(100% - 66px);
      margin-top: 10px;
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
  }
</style>
