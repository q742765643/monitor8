<template>
  <div class="alarmReportChartsTemplate">
    <div class="leftBox">
      <div class="colCardBox">
        <planeTitle titleName="未处理告警分布" />
        <!-- <a-row type="flex" justify="center" align="middle">
          <a-col :span="12" align="center">
            <span style="font-size: 30px">{{ deviceNum }}</span
            ><span>个</span>
            <p>故障设备</p>
          </a-col>
          <a-col :span="12" align="center">
            <span style="font-size: 30px">{{ faultNum }}</span
            ><span>个</span>
            <p>故障数量</p>
          </a-col>
        </a-row> -->
        <div id="alarmLevel" class="colCardbody" @dblclick="gopage"></div>
      </div>
      <!--  <div class="colCardBox" >
        <planeTitle titleName="当前故障级别" />
        <div id="alarmLevel" class="colCardbody"></div>
      </div> -->
      <div class="colCardBox topClass">
        <planeTitle titleName="当前故障趋势" />
        <div id="alarmTrend" class="colCardbody"></div>
      </div>
    </div>
    <div class="rightBox">
      <planeTitle titleName="当前故障列表" />
      <el-scrollbar>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in alarmList"
            :key="index"
            :icon="item.icon"
            :type="item.type"
            :color="item.color"
            :size="item.size"
            :timestamp="item.start_time"
          >
            <p :style="{ color: item.color }">{{ item.message }}</p>
            <p :style="{ color: item.color }">{{ item.level }}({{ item.status }})</p>
          </el-timeline-item>
        </el-timeline>
      </el-scrollbar>
    </div>
  </div>
</template>
<script>
import { remFontSize } from '@/components/utils/fontSize.js';
import echarts from 'echarts';
import request from '@/utils/request';
import planeTitle from '@/components/titile/planeTitle.vue';
export default {
  components: { planeTitle },
  data() {
    return {
      alarmLevelChart: '',
      alarmTrendDate: [],
      alarmTrendData: [],
      alarmLevelData: [],
      alarmList: [],
      deviceNum: 0,
      faultNum: 0,
      pieColor: ['#F97185', '#A051EA', '#F8D27E', '#42E4D5'],
    };
  },
  mounted() {
    this.selectAlarmNum();
    this.selectAlarmTrend();
    this.selectAlarmLevel();
    this.$nextTick(function () {});
    let _this = this;
    window.addEventListener('resize', function () {
      _this.alarmLevelChart.resize();
    });
  },
  created() {
    this.selectAlarmList();
  },
  methods: {
    gopage() {
      this.$router.push({
        name: 'unAlarm',
      });
    },
    selectAlarmNum() {
      request({
        url: '/alarmEsLog/selectAlarmNum',
        method: 'get',
      }).then((data) => {
        this.deviceNum = data.data.deviceNum;
        this.faultNum = data.data.faultNum;
      });
    },
    selectAlarmList() {
      let that = this;
      request({
        url: '/alarmEsLog/selectAlarmList',
        method: 'get',
      }).then((data) => {
        this.alarmList = data.data;
        this.alarmList.forEach(function (item, index) {
          //item 就是当日按循环到的对象
          //index是循环的索引，从0开始
          if (item.status == 0) {
            item.status = '未处理';
          } else {
            item.status = '已处理';
          }

          item.type = 'primary';
          if (item.level == 0) {
            item.color = '#A051EA';
            item.level = '一般';
          }
          if (item.level == 1) {
            item.color = '#F8D27E';
            item.level = '危险';
          }
          if (item.level == 2) {
            item.color = '#F97185';
            item.level = '故障';
          }
          item.start_time = that.parseTime(item.start_time, '{y}-{m}-{d} {h}:{i}');
        });
      });
    },
    selectAlarmLevel() {
      request({
        url: '/main/getMonitorViewVo',
        method: 'get',
      }).then((data) => {
        this.alarmLevelData = data.data;

        this.drawAlarmLevel();
      });
    },
    selectAlarmTrend() {
      request({
        url: '/alarmEsLog/selectAlarmTrend',
        method: 'get',
      }).then((data) => {
        this.alarmTrendDate = data.data.date;
        this.alarmTrendData = data.data.data;
        this.drawAlarmTrend();
      });
    },
    drawAlarmTrend() {
      let drawAlarmTrendChart = echarts.init(document.getElementById('alarmTrend'));
      var date = this.alarmTrendDate;

      var data = this.alarmTrendData;

      let option = {
        tooltip: {
          trigger: 'axis',
          position: function (pt) {
            return [pt[0], '10%'];
          },
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '2%',
          top: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: date,
          axisLabel: {
            interval: 0, //代表显示所有x轴标签显示
          },
        },
        yAxis: {
          type: 'value',
          //boundaryGap: [0, 100]
        },
        series: [
          {
            name: '告警次数',
            type: 'line',
            smooth: true,
            symbol: 'none',
            sampling: 'average',
            itemStyle: {
              color: 'rgb(74,92,165)',
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgb(92,83,199)',
                },
                {
                  offset: 1,
                  color: 'rgb(92,83,199)',
                },
              ]),
            },
            data: data,
          },
        ],
      };
      drawAlarmTrendChart.setOption(option);
      //window.onresize =  drawAlarmTrendChart.resize;
    },
    drawAlarmLevel() {
      // 基于准备好的dom，初始化echarts实例
      this.alarmLevelChart = echarts.init(document.getElementById('alarmLevel'));
      // 绘制图表
      let option = {
        legend: {
          left: 'center',
          top: 'bottom',
          textStyle: {
            fontSize: remFontSize(0.15),
          },

          data: ['主机设备', '链路设备', '进程任务', '数据任务'],
        },
        color: this.pieColor,

        series: [
          {
            name: '告警分布',
            type: 'pie',
            radius: [30, 60],
            center: ['50%', '50%'],
            roseType: 'area',
            label: {
              show: true,
              fontSize: remFontSize(0.15),
              formatter: '{c}\n\n{b}',
              color: '#000000',
              position: 'outer',
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
            data: this.alarmLevelData,
          },
        ],
      };
      this.alarmLevelChart.setOption(option);
      window.onresize = this.alarmLevelChart.resize;
      //window.onresize =  this.alarmLevelChart.resize;
    },
  },
};
</script>
<style lang="scss" scoped>
.alarmReportChartsTemplate {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  .leftBox,
  .rightBox {
    width: calc(50% - 5px);
    height: 100%;
  }
  .leftBox {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    .colCardBox {
      width: 100%;
      height: calc(50% - 10px);
      background: #fff;
      .colCardbody {
        height: calc(100% - 56px);
      }
      &.topClass {
        margin-top: 20px;
      }
    }
  }

  .rightBox {
    background: #fff;
    .el-scrollbar {
      width: calc(100% - 20px);
      margin: 10px;
      height: calc(100% - 76px);
    }
  }
}
</style>
