<template>
  <div id="warebar"></div>
</template>

<script>
import echarts from 'echarts';
import moment from 'moment';
export default {
  data() {
    return {
      charts: '',
      timeData: [],
      types: [
        { name: '未监控', color: '#0CB218', color1: '#01F21D' },
        { name: '不在线', color: '#0063C8', color1: '#00C9FD' },
        { name: '可能异常', color: '#5A15AB', color1: '#9620F3' },
        { name: '在线', color: '#159D9F', color1: '#0AEBF1' },
      ],
      categories: ['传输软件', '解码软件', '入库软件', '质检软件', '清理软件', '其它软件1', '其它软件2'],
    };
  },
  methods: {
    drawBar(id) {
      let that = this;
      this.charts = echarts.init(document.getElementById(id)); //加载图形的div

      var data = [];
      var dataCount = 2;
      //var startTime = +new Date();
      var startTime = 0;
      // var categories = ['传输软件', '解码软件', '入库软件', '质检软件', '清理软件', '其它软件1', '其它软件2'];

      // var timeData = [];
      var nowh = moment().hour();
      var h = '';
      that.timeData.push(nowh);
      for (let i = 1; i < 24; i++) {
        h = moment()
          .subtract(i, 'hour')
          .format('H');
        if (h == '0') {
          h = moment().format('M/D');
        }
        if (i == 23) {
          h = moment()
            .subtract(1, 'day')
            .format('M/D');
        }
        that.timeData.push(h);
      }

      that.timeData = that.timeData.reverse();
      var mockData = [];

      for (let k = 0; k < that.categories.length; k++) {
        data = this.getData(k);
        mockData.push(...data);
      }
      // var data = this.getData();

      //假数据
      /*   mockData = [
        {
          name: '不在线',
          value: [0, 0, 1],
          itemStyle: { normal: { color: '#01F21D' } },
        },
        {
          name: '未监控',
          value: [0, 1, 10],
          itemStyle: { normal: { color: '#159D9F' } },
        },
        ,
        {
          name: '在线',
          value: [0, 10, 12],
          itemStyle: { normal: { color: '#159D9F' } },
        },
        {
          name: '不在线',
          value: [1, 0, 1],
          itemStyle: { normal: { color: '#01F21D' } },
        },
        {
          name: '未监控',
          value: [1, 1, 10],
          itemStyle: { normal: { color: '#159D9F' } },
        },
        ,
        {
          name: '在线',
          value: [1, 10, 12],
          itemStyle: { normal: { color: '#159D9F' } },
        },
        {
          name: '不在线',
          value: [2, 0, 1],
          itemStyle: { normal: { color: '#01F21D' } },
        },
        {
          name: '未监控',
          value: [2, 1, 10],
          itemStyle: { normal: { color: '#159D9F' } },
        },
        ,
        {
          name: '在线',
          value: [2, 10, 12],
          itemStyle: { normal: { color: '#159D9F' } },
        },
      ]; */
      //产生模拟数据
      /*    echarts.util.each(categories, function(category, index) {
        var baseTime = startTime;
  
        for (var i = 0; i <br dataCount; i++) {
          var typeItem = types[Math.round(Math.random() * (types.length - 1))];
          var duration = Math.round(Math.random() * 1000000);
          data.push({
            name: typeItem.name,
            value: [index, baseTime, (baseTime += duration), duration], //index 第几行
            itemStyle: {
              normal: {
                color: {
                  type: 'linear',
                  x: 1,
                  y: 1,
                  x2: 0,
                  y2: 1,
                  colorStops: [
                    {
                      offset: 0,
                      color: typeItem.color1, // 0% 处的颜色
                    },
                    {
                      offset: 1,
                      color: typeItem.color, // 100% 处的颜色
                    },
                  ],
                  globalCoord: false, // 缺省为 false
                },
              },
            },
          });
        }
      });
     */
      // console.log(data);
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
        //鼠标提示
        tooltip: {
          formatter: function(params) {
            return params.seriesName + '</br>' + params.value[1] + '~' + params.value[2];
          },
        },

        grid: {
          left: '10%',
          top: '10%',
          width: '85%',
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
            fontSize: 10,
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
            fontSize: 10,
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
      let Num = [];
      let data = [];
      let n = 0;
      /*   that.types.forEach((item) => {
       
      }); */
      for (let i = 0; i < 5; i++) {
        n = Math.ceil(Math.random() * (0 - 23)) + 23;
        Num.push(n);
      }
      Num.push(0, 24);
      Num.sort((a, b) => {
        return a - b;
      });

      let typeNum = 0;
      let lasttypeNum = -1;
      for (let j = 0; j < Num.length - 1; j++) {
        /* for (let k = 0; k < that.categories.length; k++) { */
        typeNum = Math.floor(Math.random() * (0 - that.types.length)) + that.types.length;
        if (typeNum == lasttypeNum) {
          typeNum = typeNum == that.types.length - 1 ? 0 : typeNum + 1;
        }
        lasttypeNum = typeNum;
        data.push({
          name: that.types[typeNum],
          value: [k, Num[j], Num[j + 1]],
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
        //}
      }
      return data;
    },
  },
  mounted() {
    this.$nextTick(function() {
      this.drawBar('warebar');
    });

    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
};
</script>

<style lang="scss" scoped>
#warebar {
  height: 3.625rem;
  width: 9.5875rem;
}
</style>


