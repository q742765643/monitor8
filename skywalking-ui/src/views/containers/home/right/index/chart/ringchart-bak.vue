<template>
  <!--为echarts准备一个具备大小的容器dom-->
<!--   <div :id="ringData.id"></div>
 -->
 <div id="ring">
   <div v-for="(item,index) in ringList" :key="index">
        <div :id="item.id"></div>
    </div>
</div>
</template>
<script>
import echarts from 'echarts';

export default {
  props: ['ringData'],
  name: '',
  data() {
    return {
      charts: '',
      ringList: [
        { name: '链路设备', value: 25, total: 30, color1: '#C818F2', color2: '#0E6EBA',id:"main0" },
        { name: '主机设备', value: 6, total: 10, color1: '#FFB20E', color2: '#FF7559',id:"main1"},
        { name: '数据任务', value: 15, total: 40, color1: '#30D03C', color2: '#25BF0F' ,id:"main2"},
        { name: '进程任务', value: 7, total: 11, color1: '#FA905A', color2: '#6F82FB' ,id:"main3"},
      ]
    };
  },
  methods: {
    drawPie(elid) {
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
            fontSize: '20',
            fontWeight: 'normal',
            color: '#0000FF',
          },
        },
      };

      this.charts = echarts.init(document.getElementById(elid));

      let id= parseInt(elid.charAt(elid.length-1)); 

      let option = {
        backgroundColor: '#fff',
        title: [
          {
            text: '正面评论',
            left: '50%',
            top: '80%',
            textAlign: 'center',
            textStyle: {
              fontWeight: 'normal',
              fontSize: '14',
              color: '#000000',
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
                value: this.ringList[id].value,
                itemStyle: {
                  normal: {
                    color: {
                      type: 'linear',
                      x: pointStart[0],
                      y: pointStart[1],
                      x2: pointEnd[0],
                      y2: pointEnd[1],
                      colorStops: [
                        // !! 在此添加渐变过程色 !!
                        {
                          offset: 0,
                          color: this.ringList[id].color1,
                        },
                        {
                          offset: 1,
                          color: this.ringList[id].color2,
                        },
                      ],
                    },
                  },
                },
                label: dataStyle,
              },
              {
                value: this.ringList[id].total - this.ringList[id].value,
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

      this.$nextTick(()=>{
        for (let index = 0; index < 4; index++) {
          this.drawPie(this.ringList[index].id);
        }
     
    }); 



    
  },
};
</script>
<style  lang="scss"  scoped>
#ring{
 display: flex;
#main0,#main1,#main2,#main3,#main4 {
  height: 124px;
  width: 151px;
}
}

</style>