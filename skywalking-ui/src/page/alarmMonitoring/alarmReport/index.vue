<template>
  <div class="alarmReportChartsTemplate">
    <a-row :gutter="10">
      <a-col :span="14">
        <a-row :gutter="10">
          <a-col :span="10" class="colCardBox">
            <a-card size="small" title="当前故障设备与故障数量">
              <a-row type="flex" justify="center" align="middle">
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
              </a-row>
            </a-card>
          </a-col>
          <a-col :span="14" class="colCardBox">
            <a-card size="small" title="当前故障级别">
              <div id="alarmLevel" class="colCardbody"></div>
            </a-card>
          </a-col>
          <a-col :span="24" class="colCardBox topClass">
            <a-card size="small" title="当前故障趋势">
              <div id="alarmTrend" class="colCardbody"></div>
            </a-card>
          </a-col>
        </a-row>
      </a-col>
      <a-col :span="10" class="rightLineBox">
        <a-card size="small" title="当前故障列表">
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
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script>
  import echarts from 'echarts';
  import request from '@/utils/request';
  export default {
    data() {
      return {
        alarmLevelChart: '',
        alarmTrendDate: [],
        alarmTrendData: [],
        alarmLevelData: [],
        alarmList: [],
        deviceNum: 0,
        faultNum: 0,
      };
    },
    mounted() {
      this.selectAlarmNum();
      this.selectAlarmTrend();
      this.selectAlarmLevel();
      this.$nextTick(function() {});
      let _this = this;
      window.addEventListener('resize', function() {
        _this.alarmLevelChart.resize();
      });
    },
    created() {
      this.selectAlarmList();
    },
    methods: {
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
          this.alarmList.forEach(function(item, index) {
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
          url: '/alarmEsLog/selectAlarmLevel',
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
            position: function(pt) {
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
          color: ['#A051EA', '#F8D27E', '#F97185'],
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)',
          },
          legend: {
            orient: 'vertical',
            left: 10,
            data: ['一般', '危险', '故障'],
          },
          series: [
            {
              name: '告警级别',
              type: 'pie',
              radius: ['50%', '70%'],
              center: ['60%', '40%'],
              avoidLabelOverlap: false,
              label: {
                show: false,
                position: 'center',
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '30',
                  fontWeight: 'bold',
                },
              },
              labelLine: {
                show: false,
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
    height: calc(100vh - 130px);
    .colCardBox {
      height: calc(50% - 10px);
      .colCardbody {
        height: calc(100% - 56px);
      }
    }
    .topClass {
      margin-top: 20px;
    }
    .rightLineBox {
      height: 100%;
    }
  }

  ::v-deep .ant-card-small > .ant-card-head > .ant-card-head-wrapper > .ant-card-head-title {
    padding: 0;
  }
  ::v-deep .ant-card-small > .ant-card-head {
    border-bottom: solid 2px $plane_border_color;
    font-size: 20px;
    height: 56px;
    line-height: 56px;
    padding-left: 18px;
  }
  ::v-deep .colCardBox .ant-card-small > .ant-card-body,
  .colCardBox .ant-card-bordered {
    height: 100%;
  }
  ::v-deep .rightLineBox .ant-card-small > .ant-card-body {
    height: calc(100% - 56px);
  }
  /* ::v-deep .el-timeline-item__timestamp.is-bottom {
  position: absolute;
  left: -117px;
  top: -3px;
  color: #333333;
}
::v-deep .el-timeline {
  padding-left: 120px;
}
.info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  margin-left: 0.3rem;
  width: 100%;
  .name {
    font-weight: 200;
  }
  .number {
    p {
      display: inline;
      font-weight: 200;
    }
  }
} */
</style>
