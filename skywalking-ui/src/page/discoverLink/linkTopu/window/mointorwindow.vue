<template>
  <div id="mointorWindow">
    <div class="title">
      <span>主机监测信息</span>
      <!--    <planeTitle titleName="主机监测信息"></planeTitle> -->
      <span class="icon iconfont iconbaseline-close-px" v-on:click="closeWindow"></span>
    </div>
    <div class="content">
      <div class="cell1">
        <div id="Cpu" class="plane">
          <!--   <div class="chartTitile"> -->
          <!--    <span>CPU使用率情况</span> -->
          <planeTitle titleName="CPU使用率情况"></planeTitle>
          <!-- </div> -->
          <div class="chart" id="cpuChart"></div>
        </div>
        <div id="Rom" class="plane">
          <!-- <div class="chartTitile">
            <span>内存使用率</span>
          </div> -->
          <planeTitle titleName="内存使用率"></planeTitle>
          <div class="chart" id="romChart"></div>
        </div>
        <div id="Net" class="plane">
          <!-- <div class="chartTitile">
            <span>网络流量</span>
          </div> -->
          <planeTitle titleName="网络流量"></planeTitle>
          <div class="chart" id="netChart"></div>
        </div>
      </div>
      <div class="cell2">
        <div id="Ram" class="plane">
          <!--  <div class="chartTitile">
            <span>磁盘可用量</span>
          </div> -->

          <planeTitle titleName="磁盘可用量"></planeTitle>
          <div class="chart plane" id="ramChart"></div>
        </div>
        <div id="Pak" class="plane">
          <!-- <div class="chartTitile">
            <span>网络丢包率</span>
          </div> -->
          <planeTitle titleName="网络丢包率"></planeTitle>
          <div class="chart" id="pakChart"></div>
        </div>
        <div id="Thd" class="plane">
          <!-- <div class="chartTitile">
            <span>进程状态</span>
          </div> -->
          <planeTitle titleName="进程状态"></planeTitle>
          <div class="chart" id="tableChart">
            <!-- <div class="tabtitle">
              <span>进程名称</span>
              <span>进程时间</span>
              <span>进程状态</span>
            </div>
            <div class="cell">
              <div class="data" v-for="(item, index) in tableData" :key="index">
                <span>{{ item.name }}</span>
                <span>{{ item.time }}</span>
                <span>{{ item.state }}</span>
              </div>
            </div> -->

            <vxe-table :height="table_height" width="90%" :data="tableData" stripe>
              <vxe-table-column field="processName" title="进程名称" show-overflow></vxe-table-column>
              <vxe-table-column field="updateTime" title="进程时间" show-overflow>
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.updateTime) }}</span>
                </template>
              </vxe-table-column>
              <vxe-table-column
                field="currentStatus"
                title="进程状态"
                :formatter="formatAlarmLevel"
                show-overflow
              ></vxe-table-column>
            </vxe-table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import moment from 'moment';
  import echarts from 'echarts';
  import planeTitle from '@/components/titile/planeTitle.vue';
  import { remFontSize } from '@/components/utils/fontSize.js';
  import request from '@/utils/request';
  export default {
    props: ['ip'],
    data() {
      return {
        table_height: null,
        tableData: [],
        alarmLevelOptions: [],
        timer: null,
        XAxisData: [],
        chart0: '',
        chart1: '',
        chart2: '',
        chart3: '',
        chart4: '',
        chart5: '',
      };
    },
    created() {
      this.getDicts('current_status').then((response) => {
        this.alarmLevelOptions = response.data;
      });
    },
    mounted() {
      //let now = moment().format('HH:mm:ss');
      this.fetch();
      this.initXdata();
      this.drawChart0('cpuChart');
      this.drawChart1('romChart');
      this.drawChart2('netChart');
      this.drawChart3('ramChart');
      this.drawChart4('pakChart');
      this.setTableHeight();

      window.addEventListener('resize', () => {
        this.setTableHeight();
      });
    },
    components: { planeTitle },
    methods: {
      formatAlarmLevel({ cellValue }) {
        return this.selectDictLabel(this.alarmLevelOptions, cellValue);
      },
      setTableHeight() {
        let h = document.getElementById('tableChart').clientHeight;
        this.table_height = h - 3;
      },
      closeWindow() {
        this.$parent.closeMonWindow();
      },
      initXdata() {
        let t = '';
        for (let i = 0; i < 10; i++) {
          t = moment()
            .subtract(i, 'seconds')
            .format('HH:mm:ss');
          this.XAxisData.push(t);
        }
        this.XAxisData.reverse();
      },
      drawChart0(id) {
        this.chart0 = echarts.init(document.getElementById(id));
        let params = {
          ip: this.ip,
          startTime: this.parseTime(new Date().getTime() - 10 * 1000 * 60),
          endTime: this.parseTime(new Date().getTime()),
        };
        let XAxisData = [];
        let YAxisData = [];
        request({
          url: '/system/getCpu',
          method: 'get',
          params: params,
        }).then((data) => {
          let result = data.data;
          result.forEach((item) => {
            XAxisData.push(item.timestamp);
            YAxisData.push(item.usage);
          });
          let option = {
            textStyle: {
              fontFamily: 'Alibaba-PuHuiTi-Regular',
            },
            tooltip: {
              trigger: 'axis',
            },
            xAxis: {
              boundaryGap: false,
              type: 'category',
              data: XAxisData,
              axisTick: { show: false },

              axisLabel: {
                fontSize: remFontSize(12 / 64),
              },
            },
            grid: { left: '10%', top: '10%', right: '5%', bottom: '15%' },
            color: '#15E125',

            yAxis: {
              type: 'value',
              max: 100,
              axisTick: { show: false },
              splitLine: {
                lineStyle: {
                  color: 'rgba(65,65,65,0.35)',
                  width: 0.5,
                },
              },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
                formatter: function(value) {
                  return value + '%';
                },
              },
            },
            series: [
              {
                lineStyle: { width: 1 },
                data: YAxisData,
                type: 'line',
                //symbol: 'none',
                symbolSize: 3,
                // smooth: true,
              },
            ],
          };
          this.chart0.setOption(option);
        });
      },
      drawChart1(id) {
        this.chart1 = echarts.init(document.getElementById(id));
        let params = {
          ip: this.ip,
          startTime: this.parseTime(new Date().getTime() - 10 * 1000 * 60),
          endTime: this.parseTime(new Date().getTime()),
        };
        let XAxisData = [];
        let YAxisData = [];
        request({
          url: '/system/getMemory',
          method: 'get',
          params: params,
        }).then((data) => {
          let result = data.data;
          result.forEach((item) => {
            XAxisData.push(item.timestamp);
            YAxisData.push(item.usage);
          });
          let option = {
            textStyle: {
              fontFamily: 'Alibaba-PuHuiTi-Regular',
            },
            tooltip: {
              trigger: 'axis',
            },
            xAxis: {
              boundaryGap: false,
              type: 'category',
              data: XAxisData,
              axisTick: { show: false },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
              },
            },
            grid: { left: '10%', top: '10%', right: '5%', bottom: '15%' },
            color: '#15E125',

            yAxis: {
              type: 'value',
              // boundaryGap: [0, '60%'],
              axisTick: { show: false },

              splitLine: {
                lineStyle: {
                  color: 'rgba(65,65,65,0.35)',
                  width: 0.5,
                },
              },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
                formatter: function(value) {
                  return value + '%';
                },
              },
            },
            series: [
              {
                data: YAxisData,
                type: 'line',
                color: '#7c80f4',
                lineStyle: { width: 1 },
                areaStyle: {
                  //区域填充样式
                  normal: {
                    color: new echarts.graphic.LinearGradient(
                      0,
                      0,
                      0,
                      1,
                      [
                        {
                          offset: 0,
                          color: 'rgba(124, 128, 244,.5)',
                        },
                        {
                          offset: 1,
                          color: 'rgba(124, 128, 244, 0.1)',
                        },
                      ],
                      false,
                    ),
                    shadowColor: 'rgba(53,142,215, 0.9)',
                    shadowBlur: 20,
                  },
                },
              },
            ],
          };
          this.chart1.setOption(option);
        });
      },
      drawChart2(id) {
        this.chart2 = echarts.init(document.getElementById(id));
        let params = {
          ip: this.ip,
          startTime: this.parseTime(new Date().getTime() - 10 * 1000 * 60),
          endTime: this.parseTime(new Date().getTime()),
        };
        let XAxisData = [];
        let YAxisData1 = [];
        let YAxisData2 = [];
        request({
          url: '/system/getNetwork',
          method: 'get',
          params: params,
        }).then((data) => {
          let result = data.data;
          result.forEach((item) => {
            XAxisData.push(item.timestamp);
            YAxisData1.push(item.inSpeed);
            YAxisData2.push(item.outSpeed);
          });
          let option = {
            textStyle: {
              fontFamily: 'Alibaba-PuHuiTi-Regular',
            },
            tooltip: {
              trigger: 'axis',
            },
            xAxis: {
              boundaryGap: false,
              type: 'category',
              data: XAxisData,
              axisTick: { show: false },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
              },
            },
            grid: { left: '20%', top: '10%', right: '5%', bottom: '15%' },
            color: '#15E125',

            yAxis: {
              boundaryGap: [0, '60%'],
              type: 'value',
              splitLine: {
                lineStyle: {
                  color: 'rgba(65,65,65,0.35)',
                  width: 0.5,
                },
              },
              axisTick: { show: false },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
                formatter: function(value) {
                  return value + 'kb';
                },
              },
            },
            series: [
              {
                data: YAxisData1,
                type: 'line',
                color: '#0AA190',
                lineStyle: { width: 1 },
                areaStyle: {
                  //区域填充样式
                  normal: {
                    color: new echarts.graphic.LinearGradient(
                      0,
                      0,
                      0,
                      1,
                      [
                        {
                          offset: 0,
                          color: 'rgba(10, 161, 144,.5)',
                        },
                        {
                          offset: 1,
                          color: 'rgba(10, 161, 144, 0.1)',
                        },
                      ],
                      false,
                    ),
                    shadowColor: 'rgba(10, 161, 144, 0.9)',
                    shadowBlur: 20,
                  },
                },
              },
              {
                lineStyle: { width: 1 },
                data: YAxisData2,
                type: 'line',
                color: '#158CEB',
              },
            ],
          };
          this.chart2.setOption(option);
        });
      },
      drawChart3(id) {
        this.chart3 = echarts.init(document.getElementById(id));
        let params = {
          ip: this.ip,
          startTime: this.parseTime(new Date().getTime() - 10 * 1000 * 60),
          endTime: this.parseTime(new Date().getTime()),
        };
        let XAxisData = [];
        let YAxisData = [];
        request({
          url: '/system/getFileSystem',
          method: 'get',
          params: params,
        }).then((data) => {
          let result = data.data;
          result.forEach((item) => {
            XAxisData.push(item.diskName);
            YAxisData.push(item.useByte);
          });
          let color = [];
          let color1 = new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' },
          ]);

          let color2 = new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: '#ED0DEF' },
            { offset: 0.5, color: '#EE73EF' },
            { offset: 1, color: '#F3AFF3' },
          ]);

          color.push(color1, color2);

          let option = {
            textStyle: {
              fontFamily: 'Alibaba-PuHuiTi-Regular',
            },
            tooltip: {
              show: true,

              formatter: function(data) {
                return XAxisData[data.dataIndex] + '<br>' + '已用空间:' + data.data + 'GB';
              },
            },
            xAxis: {
              type: 'value',
              boundaryGap: [0, 0.01],
              splitLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              axisLine: {
                show: false,
              },
              axisLabel: {
                show: false,
              },
            },
            grid: { left: '20%', top: '10%', right: '5%', bottom: '15%' },

            yAxis: {
              type: 'category',
              data: ['750', '750', '750', '750', '750', '750', '750'],
              splitLine: {
                show: false,
              },
              axisTick: {
                show: false,
              },
              axisLine: {
                show: false,
              },
              axisLabel: {
                show: false,
                textStyle: {
                  color: '#FF0000',
                },
              },
            },
            series: [
              {
                barWidth: 20,
                name: '前',
                type: 'bar',
                barGap: 0,
                zlevel: 1,
                itemStyle: {
                  color: function(para) {
                    if (para.dataIndex < 2) {
                      return color[1];
                    } else {
                      return color[0];
                    }
                  },
                },
                label: {
                  show: true,
                  position: 'insideleft',
                  offset: [10, 5],
                  formatter: function(data) {
                    return XAxisData[data.dataIndex];
                  },
                },
                data: YAxisData,
              },
              {
                barWidth: 20,
                name: '背景',
                type: 'bar',
                color: 'RGBA(25,25,25,0.3)',
                barGap: '-100%',
                label: {
                  show: true,
                  position: 'left',
                  color: '#000000',
                  formatter: '{c}' + 'GB',
                },
                data: YAxisData,
              },
            ],
          };
          this.chart3.setOption(option);
        });
      },
      drawChart4(id) {
        this.chart4 = echarts.init(document.getElementById(id));
        let params = {
          ip: this.ip,
          startTime: this.parseTime(new Date().getTime() - 30 * 1000 * 60),
          endTime: this.parseTime(new Date().getTime()),
        };
        let XAxisData = [];
        let YAxisData = [];
        request({
          url: '/system/getPacketLoss',
          method: 'get',
          params: params,
        }).then((data) => {
          let result = data.data;
          result.forEach((item) => {
            XAxisData.push(item.timestamp);
            YAxisData.push(item.usage);
          });
          let option = {
            textStyle: {
              fontFamily: 'Alibaba-PuHuiTi-Regular',
            },
            tooltip: {
              trigger: 'axis',
            },
            xAxis: {
              boundaryGap: false,
              type: 'category',
              data: XAxisData,
              axisTick: { show: false },
              splitLine: {
                lineStyle: {
                  color: 'rgba(65,65,65,0.35)',
                  width: 0.5,
                },
              },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
              },
            },
            grid: { left: '10%', top: '10%', right: '5%', bottom: '15%' },
            color: '#15E125',

            yAxis: {
              type: 'value',
              // boundaryGap: [0, '60%'],
              axisTick: { show: false },
              axisLabel: {
                fontSize: remFontSize(12 / 64),
                formatter: function(value) {
                  return value + '%';
                },
              },
            },
            series: [
              {
                lineStyle: { width: 1 },
                data: YAxisData,
                type: 'line',
              },
            ],
          };
          this.chart4.setOption(option);
        });
      },
      drawChart5() {},
      fetch() {
        let params = {
          ip: this.ip,
        };
        request({
          url: '/system/getProcess',
          method: 'get',
          params: params,
        }).then((data) => {
          this.tableData = data.data;
        });
      },
    },
  };
</script>

<style lang="scss" scoped>
  #mointorWindow {
    font-family: 'Alibaba-PuHuiTi-Medium';
    height: 11.625rem;
    width: 19.04rem;
    //background: #eff2f8;
    background: #fff;
    box-shadow: 0 0 0.0625rem #ffffff;
    z-index: 502;
    position: absolute;
    top: 0;
    left: 0;
    .title {
      font-family: Georgia;
      font-weight: 600;
      height: 0.75rem;
      padding-left: 0.25rem;
      font-size: 0.25rem;
      border-bottom: solid 0.025rem #eef5fd;
      background: #fff;
      .icon {
        position: relative;
        float: right;
        right: 0.25rem;
        cursor: pointer;
      }
      span {
        display: inline-block;
        line-height: 0.75rem;
      }
    }
    .content {
      height: 10.875rem;
      display: flex;
      flex-direction: column;
      background: #fff;
      .cell1 {
        height: 50%;
        padding: 0.15rem 0.2rem 0.2rem 0.15rem;
        // background: pink;
        //width: 19.04rem;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        .plane {
          flex: 1;
          //background: white;
          height: 100%;
          width: 100%;
          box-shadow: $plane_shadow;

          .chart {
            height: 4.3875rem;
            //background: pink;
            margin: 0 !important;
          }
        }

        /*   div:nth-child(1) {
        margin-right: 0.1rem;
      } */
        div:nth-child(2) {
          margin: 0 0.2rem;
        }
        /* div:nth-child(3) {
        margin-left: 0.1rem;
      } */
      }
      .cell2 {
        height: 50%;
        flex: 1;
        // width: 19.04rem;
        padding: 0.15rem 0.2rem 0.2rem 0.15rem;
        // background: pink;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        .plane {
          box-shadow: $plane_shadow;
          flex: 1;
          background: white;
          height: 100%;
          width: 100%;

          /*   .chartTitile {
          height: 0.75rem;
          width: 100%;
          border-bottom: solid 1px #eee;
          span {
            padding-left: 0.25rem;
            line-height: 0.75rem;
            display: inline-block;
            font-size: 0.25rem;
          }
        } */
          .chart {
            height: 4.3875rem;
            // background: pink;
            margin: 0 !important;
          }
        }

        div:nth-child(2) {
          margin: 0 0.2rem;
        }
        /*  div:nth-child(1) {
        margin-right: 0.1rem;
      }
      div:nth-child(2) {
        margin: 0 0.1rem;
      }
      div:nth-child(3) {
        margin-left: 0.1rem;
      } */

        #tableChart {
          height: 4.3875rem;
          width: 100%;
        }
      }
    }
  }
</style>
