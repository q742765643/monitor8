<template>
  <div class="topu">
    <div id="column1">
      <topu-tree></topu-tree>
    </div>
    <div id="column2">
      <div id="deviceState">
        <div class="title">
          <!--           <span v-tooltip:bottom="{ content: 'xianshi', popperCls: ['trace-table-tooltip'] }">监控总览</span>
          -->
          <span>监控总览</span>
        </div>
        <div id="pieChart"></div>
      </div>
      <div id="devideInfo">
        <div class="title">
          <span>设备属性信息</span>
        </div>
        <div class="info">
          <div class="column">
            <div class="cell">
              <span>
                设备别名:
                <i></i>
              </span>
              <span>{{ infoData.alias }}</span>
            </div>
            <div class="cell">
              <span>
                区域:
                <i></i>
              </span>
              <span>{{ infoData.name }}</span>
            </div>
            <div class="cell">
              <span>
                IP地址:
                <i></i>
              </span>
              <span>{{ infoData.ip }}</span>
            </div>
            <div class="cell">
              <span>
                丢包率:
                <i></i>
              </span>
              <span>{{ infoData.ip }}</span>
            </div>
            <div class="cell">
              <span>
                网关:
                <i></i>
              </span>
              <span>{{ infoData.DNS }}</span>
            </div>
            <div class="cell">
              <span>
                设备类型:
                <i></i>
              </span>
              <span>{{ infoData.type }}</span>
            </div>
            <div class="cell">
              <span>
                连线状态:
                <i></i>
              </span>
              <span>{{ infoData.state }}</span>
            </div>
            <div class="cell">
              <span>
                运行时长:
                <i></i>
              </span>
              <span>{{ infoData.time }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  /* import toupuChart from './toupuchart/toupuchart'; */
  import topuTree from './toupuchart/toputree';
  export default {
    data() {
      return {
        infoData: {
          name: 'test-name',
          alias: '报文接收服务器',
          ip: '66.32.5.122',
          DNS: '255.255.255.80',
          type: 'windows服务器',
          state: '正常/异常',
          time: '20天30时45分',
        },
        charts: '',
        pieData: [
          { value: 210, name: '良好', color1: '#329A2E', color2: '#5DFC57' },
          { value: 735, name: '一般', color1: '#E4A302', color2: '#FDF901' },
          { value: 834, name: '未知', color1: '#FC000D', color2: '#E10008' },
          { value: 535, name: '严重', color1: '#0063F2', color2: '#0065F5' },
        ],
      };
    },
    components: { /* toupuChart ,*/ topuTree },
    methods: {
      drawPie(id) {
        var linearcolor = [];
        this.pieData.map((item) => {
          linearcolor.push({
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: item.color1,
              },
              {
                offset: 1,
                color: item.color2,
              },
            ],
            globalCoord: false, // 缺省为 false
          });
        });
        this.charts = echarts.init(document.getElementById(id));
        var option = {
          tooltip: {
            trigger: 'item',
            formatter: '{b} : {c} ({d}%)',
          },

          color: linearcolor,
          series: [
            {
              type: 'pie',
              radius: ['25%', '60%'],
              center: ['50%', '50%'],
              selectedMode: 'single',
              data: this.pieData,
              /* label: {
              color: '#000000',
            }, */
              label: { padding: [0, -30, 0, -30], formatter: '{b}\n\n{c}' },
              labelLine: { length: 30, length2: 50, lineStyle: { color: '#acacac', width: 0.5 } },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
              },
            },
          ],
        };

        this.charts.setOption(option);
      },
    },
    mounted() {
      this.$nextTick(() => {
        this.drawPie('pieChart');
      });
      window.addEventListener('resize', () => {
        this.charts.resize();
      });
    },
  };
</script>

<style lang="scss" scoped>
  .topu {
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    display: flex;
    background: #eef5fd;
    width: 20rem;
    justify-content: space-between;
    #column1 {
      width: 12.875rem;
      height: 11.625rem;
      background-color: #ffffff;
      box-shadow: 0.0625rem 0.0625rem 0.125rem #e2f8f8;
    }
    #column2 {
      width: 6.3375rem;
      height: 11.625rem;

      #deviceState {
        height: 4.5rem;
        background-color: #ffffff;
        box-shadow: 0.0625rem 0.0625rem 0.125rem #e2f8f8;
        #pieChart {
          height: 3.75rem;
          width: 6.3375rem;
        }
      }
      #devideInfo {
        margin-top: 0.5rem;
        height: 6.625rem;
        background-color: #ffffff;
        box-shadow: 0.0625rem 0.0625rem 0.125rem #e2f8f8;
      }
    }

    .title {
      font-family: Georgia;
      font-weight: 600;
      height: 0.75rem;
      padding-left: 0.25rem;
      font-size: 0.25rem;
      border-bottom: solid 0.025rem #eef5fd;
      span {
        line-height: 0.75rem;
      }
    }

    .info {
      padding: 0.5rem 1rem;
      .column {
        height: 4.875rem;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .cell {
          flex: 1;
          display: flex;
          span:first-child {
            font-size: 0.1875rem;
            width: 1.125rem;
            display: inline-block;
            font-weight: 600;
            text-align: justify;
            i {
              width: 100%;
              display: inline-block;
            }
          }

          span:last-child {
            margin-left: 0.125rem;
          }
        }
      }
    }
  }
</style>
