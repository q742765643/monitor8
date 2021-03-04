<template>
  <div id="mointorWindow">
    <div class="palne_titile">
      <span>{{ this.titleName }}-{{ this.ip }}</span>
      <span class="icon iconfont iconbaseline-close-px" v-on:click="closeWindow"></span>
    </div>
    <div class="contentBox">
      <div class="cell1">
        <div id="Cpu" class="plane">
          <planeTitle titleName="CPU使用率情况"></planeTitle>
          <div class="chart" id="cpuChart">暂无数据</div>
        </div>
        <div id="Rom" class="plane">
          <planeTitle titleName="内存使用率"></planeTitle>
          <div class="chart" id="romChart">暂无数据</div>
        </div>
        <div id="Net" class="plane">
          <planeTitle titleName="网络流量"></planeTitle>
          <div class="chart" id="netChart">暂无数据</div>
        </div>
      </div>
      <div class="cell2">
        <div id="Ram" class="plane">
          <planeTitle titleName="磁盘可用量"></planeTitle>
          <div class="chart plane" id="ramChart">暂无数据</div>
        </div>
        <div id="Pak" class="plane">
          <planeTitle titleName="网络丢包率"></planeTitle>
          <div class="chart" id="pakChart">暂无数据</div>
        </div>
        <div id="Thd" class="plane">
          <planeTitle titleName="进程状态"></planeTitle>
          <div class="chart" id="tableChart">
            <vxe-table :height="table_height" width="90%" :data="tableData" stripe style="width: 100%">
              <vxe-table-column field="processName" title="进程名称" show-overflow></vxe-table-column>
              <vxe-table-column field="updateTime" title="进程时间" show-overflow>
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.updateTime) }}</span>
                </template>
              </vxe-table-column>
              <vxe-table-column field="currentStatus" title="进程状态" show-overflow>
                <template slot-scope="scope">
                  <span
                    v-if="selectDictLabel(alarmLevelOptions, scope.row.currentStatus) == '危险'"
                    style="color: red"
                    >{{ selectDictLabel(alarmLevelOptions, scope.row.currentStatus) }}</span
                  >
                  <span v-else>{{ selectDictLabel(alarmLevelOptions, scope.row.currentStatus) }}</span>
                </template>
              </vxe-table-column>
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
  // beforeRouteLeave(to,from,next) {
  //     console.log('7978')
  //     this.$router.push('/discoverLink/linkTopu')
  //     next()
  //   },
  // props: ['ip'],
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
      ip: '',
      parentPageName: '',
    };
  },
  created() {
    this.getDicts('current_status').then((response) => {
      this.alarmLevelOptions = response.data;
    });

    if (this.$route.params.ip) {
      this.ip = this.$route.params.ip;
      this.titleName = this.$route.params.titleName == undefined ? '设备运行状态' : this.$route.params.titleName;
      this.parentPageName = this.$route.params.parentPageName;
      localStorage.setItem('paramsIp', this.ip);
      localStorage.setItem('paramsTitleName', this.titleName);
      localStorage.setItem('paramsParentPageName', this.parentPageName);
    } else {
      this.ip = localStorage.getItem('paramsIp');
      this.titleName = localStorage.getItem('paramsTitleName');
      this.parentPageName = localStorage.getItem('paramsParentPageName');
    }

    // console.log(this.$route.params)
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
      this.$router.push({
        name: this.parentPageName,
      });
    },
    initXdata() {
      let t = '';
      for (let i = 0; i < 10; i++) {
        t = moment().subtract(i, 'seconds').format('HH:mm:ss');
        this.XAxisData.push(t);
      }
      this.XAxisData.reverse();
    },
    drawChart0(id) {
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
        if (result.length == 0) {
          return;
        }
        this.chart0 = echarts.init(document.getElementById(id));
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
          grid: { left: '15%', top: '10%', right: '5%', bottom: '15%' },
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
              formatter: function (value) {
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
        if (result.length == 0) {
          return;
        }
        this.chart1 = echarts.init(document.getElementById(id));
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
          grid: { left: '15%', top: '10%', right: '5%', bottom: '15%' },
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
              formatter: function (value) {
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
      let params = {
        ip: this.ip,
        startTime: this.parseTime(new Date().getTime() - 30 * 1000 * 60),
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
        if (result.length == 0) {
          return;
        }
        this.chart2 = echarts.init(document.getElementById(id));
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
              formatter: function (value) {
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
        if (result.length == 0) {
          return;
        }
        this.chart3 = echarts.init(document.getElementById(id));
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

            formatter: function (data) {
              return (
                XAxisData[data.dataIndex] +
                '<br>' +
                '总量:' +
                (Number(data.data) + Number(result[data.dataIndex]['free'])).toFixed(2) +
                'GB' +
                '<br>' +
                '可用:' +
                result[data.dataIndex]['free'] +
                'GB' +
                '<br>' +
                '已用空间:' +
                data.data +
                'GB'
              );
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
            //data: ['750', '750', '750', '750', '750', '750', '750'],
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
              barWidth: 10,
              name: '前',
              type: 'bar',
              barGap: 0,
              zlevel: 1,
              itemStyle: {
                color: function (para) {
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
                offset: [10, 0],
                formatter: function (data) {
                  return XAxisData[data.dataIndex];
                },
                textStyle: {
                  fontSize: '12px',
                },
              },
              data: YAxisData,
            },
            {
              barWidth: 10,
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
        if (result.length == 0) {
          return;
        }
        this.chart4 = echarts.init(document.getElementById(id));
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
            // formatter: function (params) {
            //   return params[0].value * 100 + '%';
            // },
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
          grid: { left: '15%', top: '10%', right: '5%', bottom: '15%' },
          color: '#15E125',

          yAxis: {
            type: 'value',
            // boundaryGap: [0, '60%'],
            axisTick: { show: false },
            axisLabel: {
              fontSize: remFontSize(12 / 64),
              formatter: function (value) {
                return value * 100 + '%';
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
  height: 100vh;
  width: 100vw;
  background: #fff;
  z-index: 1000;
  position: fixed;
  top: 0;
  left: 0;
  .palne_titile {
    display: flex;
    justify-content: space-between;
    border-bottom: solid 2px $plane_border_color;
    font-size: 20px;
    height: 56px;
    line-height: 56px;
    padding-left: 18px;
    .icon {
      position: relative;
      float: right;
      right: 20px;
      cursor: pointer;
    }
    span {
      display: inline-block;
    }
  }
  .contentBox {
    display: flex;
    flex-direction: column;
    background: #f6fbfc;
    height: calc(100vh - 60px);
    .cell1 {
      height: 50%;
      padding: 10px;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      .plane {
        flex: 1;
        height: 100%;
        width: 100%;
        background: white;
        .chart {
          display: flex;
          align-items: center;
          justify-content: center;
          height: calc(100% - 56px);
          margin: 0 !important;
        }
      }
      div:nth-child(2) {
        margin: 0 10px;
      }
    }
    .cell2 {
      height: 50%;
      flex: 1;
      padding: 10px;
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      .plane {
        flex: 1;
        background: white;
        height: 100%;
        width: 100%;
        .chart {
          display: flex;
          align-items: center;
          justify-content: center;
          height: calc(100% - 56px);
          margin: 0 !important;
        }
      }

      div:nth-child(2) {
        margin: 0 10px;
      }
      #tableChart {
        height: calc(100% - 86px);
        width: 100%;
      }
    }
  }
}
</style>
