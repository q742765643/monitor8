<template>
  <div id="mointorWindow">
    <div class="title">
      <span>主机监测信息</span>
      <span class="icon iconfont iconbaseline-close-px" v-on:click="closeWindow"></span>
    </div>
    <div class="content">
      <div class="cell1">
        <div id="Cpu">
          <div class="chartTitile">
            <span>CPU使用率情况</span>
          </div>
          <div class="chart" id="cpuChart"></div>
        </div>
        <div id="Rom">
          <div class="chartTitile">
            <span>内存使用率</span>
          </div>
          <div class="chart" id="romChart"></div>
        </div>
        <div id="Net">
          <div class="chartTitile">
            <span>网络流量</span>
          </div>
          <div class="chart" id="netChart"></div>
        </div>
      </div>
      <div class="cell2">
        <div id="Ram">
          <div class="chartTitile">
            <span>磁盘可用量</span>
          </div>
          <div class="chart" id="ramChart"></div>
        </div>
        <div id="Pak">
          <div class="chartTitile">
            <span>网络丢包率</span>
          </div>
          <div class="chart" id="pakChart"></div>
        </div>
        <div id="Thd">
          <div class="chartTitile">
            <span>进程状态</span>
          </div>
          <div class="chart" id="tableChart">
            <div class="tabtitle">
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
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import moment from 'moment';
  import echarts from 'echarts';
  export default {
    data() {
      return {
        tableData: [
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
          { name: 'java', time: moment().format('YYYY-MM-DD HH:mm:ss'), state: '正常' },
        ],
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
    mounted() {
      //let now = moment().format('HH:mm:ss');

      this.initXdata();
      this.drawChart0('cpuChart');
      this.drawChart1('romChart');
      this.drawChart2('netChart');
      this.drawChart3('ramChart');
      this.drawChart4('pakChart');
    },
    methods: {
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
        let YAxisData = [];
        this.XAxisData.forEach((index) => {
          YAxisData.push((Math.random() * 100).toFixed(2));
        });
        let option = {
          tooltip: {
            trigger: 'axis',
          },
          xAxis: {
            boundaryGap: false,
            type: 'category',
            data: this.XAxisData,
            axisTick: { show: false },
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
              formatter: function(value) {
                return value + '%';
              },
            },
          },
          series: [
            {
              data: YAxisData,
              type: 'line',
            },
          ],
        };
        this.chart0.setOption(option);
      },
      drawChart1(id) {
        this.chart1 = echarts.init(document.getElementById(id));
        let YAxisData = [];
        this.XAxisData.forEach((index) => {
          YAxisData.push((Math.random() * 100).toFixed(2));
        });
        let option = {
          tooltip: {
            trigger: 'axis',
          },
          xAxis: {
            boundaryGap: false,
            type: 'category',
            data: this.XAxisData,
            axisTick: { show: false },
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
                        color: 'rgba(124, 128, 244, 0)',
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
      },
      drawChart2(id) {
        this.chart1 = echarts.init(document.getElementById(id));
        let YAxisData1 = [];
        this.XAxisData.forEach((index) => {
          YAxisData1.push(((Math.random() * 10000000) / 2).toFixed(2));
        });

        let YAxisData2 = [];
        this.XAxisData.forEach((index) => {
          YAxisData2.push(((Math.random() * 1000000) / 2).toFixed(2));
        });

        let option = {
          tooltip: {
            trigger: 'axis',
          },
          xAxis: {
            boundaryGap: false,
            type: 'category',
            data: this.XAxisData,
            axisTick: { show: false },
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
                        color: 'rgba(10, 161, 144, 0)',
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
              data: YAxisData2,
              type: 'line',
              color: '#158CEB',
            },
          ],
        };
        this.chart1.setOption(option);
      },
      drawChart3(id) {
        this.chart3 = echarts.init(document.getElementById(id));
        let c = [
          '/dev/mapper/docker',
          '/dev/mapper/docker',
          '/dev/mapper/docker',
          '/dev/mapper/docker',
          '/dev/mapper/docker',
          '/dev/mapper/docker',
          '/dev/mapper/docker',
        ];
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
          tooltip: {
            show: true,

            formatter: function(data) {
              return c[data.dataIndex] + '<br>' + '已用空间:' + data.data + 'GB';
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
                  return c[data.dataIndex];
                },
              },
              data: [7.74, 8.74, 9.74, 9.74, 9.74, 9.74, 9.74],
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
              data: [9.74, 9.74, 9.74, 9.74, 9.74, 9.74, 9.74],
            },
          ],
        };
        this.chart3.setOption(option);
      },
      drawChart4(id) {
        this.chart4 = echarts.init(document.getElementById(id));
        let YAxisData = [];
        this.XAxisData.forEach((index) => {
          YAxisData.push((Math.random() * 100).toFixed(2));
        });
        let option = {
          tooltip: {
            trigger: 'axis',
          },
          xAxis: {
            boundaryGap: false,
            type: 'category',
            data: this.XAxisData,
            axisTick: { show: false },
            splitLine: {
              lineStyle: {
                color: 'rgba(65,65,65,0.35)',
                width: 0.5,
              },
            },
          },
          grid: { left: '10%', top: '10%', right: '5%', bottom: '15%' },
          color: '#15E125',

          yAxis: {
            type: 'value',
            // boundaryGap: [0, '60%'],
            axisTick: { show: false },
            axisLabel: {
              formatter: function(value) {
                return value + '%';
              },
            },
          },
          series: [
            {
              data: YAxisData,
              type: 'line',
            },
          ],
        };
        this.chart4.setOption(option);
      },
      drawChart5() {},
    },
  };
</script>

<style lang="scss" scoped>
  #mointorWindow {
    height: 11.625rem;
    width: 19.54rem;
    background: #eff2f8;
    box-shadow: 0 0 0.0625rem #ffffff;
    z-index: 502;
    position: absolute;
    /*  top: 0;
  left: 0; */
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
      .cell1 {
        height: 50%;
        padding: 0.2rem 0.2rem 0.1rem 0.2rem;
        // background: pink;
        width: 19.54rem;
        display: flex;
        justify-content: center;
        align-items: center;
        div {
          flex: 1;
          background: white;
          height: 100%;
          width: 100%;

          .chartTitile {
            height: 0.75rem;
            width: 100%;
            border-bottom: solid 1px #eee;
            span {
              padding-left: 0.25rem;
              line-height: 0.75rem;
              display: inline-block;
              font-size: 0.25rem;
            }
          }
          .chart {
            height: 4.3875rem;
            //background: pink;
            margin: 0 !important;
          }
        }

        div:nth-child(1) {
          margin-right: 0.1rem;
        }
        div:nth-child(2) {
          margin: 0 0.1rem;
        }
        div:nth-child(3) {
          margin-left: 0.1rem;
        }
      }
      .cell2 {
        height: 50%;
        flex: 1;
        padding: 0.1rem 0.2rem 0.2rem 0.2rem;
        // background: pink;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        div {
          flex: 1;
          background: white;
          height: 100%;
          width: 100%;

          .chartTitile {
            height: 0.75rem;
            width: 100%;
            border-bottom: solid 1px #eee;
            span {
              padding-left: 0.25rem;
              line-height: 0.75rem;
              display: inline-block;
              font-size: 0.25rem;
            }
          }
          .chart {
            height: 4.3875rem;
            // background: pink;
            margin: 0 !important;
          }
        }

        div:nth-child(1) {
          margin-right: 0.1rem;
        }
        div:nth-child(2) {
          margin: 0 0.1rem;
        }
        div:nth-child(3) {
          margin-left: 0.1rem;
        }

        #tableChart {
          padding: 0 0.2rem;
          height: 3.6375rem;
          width: 100%;

          .tabtitle {
            height: 0.4rem;
            line-height: 0.4rem;
            display: flex;
            margin: 0 !important;
            span {
              flex: 1;
              font-size: 16px;
              text-align: center;
            }
            span:nth-child(2) {
              flex: 2;
            }
          }
          .cell {
            margin: 0 !important;
            div:nth-child(2n-1) {
              background: #eef5fd;
            }
            .data {
              &:hover {
                background: rgba(201, 199, 199, 0.2);
              }
              height: 0.4rem;
              line-height: 0.4rem;
              display: flex;
              margin: 0 !important;
              span {
                flex: 1;
                font-size: 13px;
                text-align: center;
              }
              span:nth-child(2) {
                flex: 2;
              }
            }
          }
        }
      }
    }
  }
</style>
