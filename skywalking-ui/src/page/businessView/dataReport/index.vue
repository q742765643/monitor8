<template>
  <!-- 软件报表 -->
  <div id="dataReport">
    <div class="header">
      <a-range-picker
        @change="onTimeChange"
        :show-time="{
          hideDisabledOptions: true,
          defaultValue: [moment('00:00:00', 'HH:mm:ss'), moment('11:59:59', 'HH:mm:ss')],
        }"
        format="YYYY-MM-DD HH:mm:ss"
      />
      <div class="right">
        <a-button type="primary" icon="file-pdf">
          导出pdf
        </a-button>
        <a-button type="primary" icon="file-excel">
          导出excel
        </a-button>
      </div>
    </div>
    <div class="content">
      <div class="title">软件报表title</div>
      <div class="dataBox">
        <div class="chartBox">
          <div id="mychart"></div>
        </div>
        <div class="tableBox">
          <vxe-table border ref="xTable" :data="tableData" width="100%">
            <vxe-table-column field="number" title="序号" width="55"></vxe-table-column>
            <vxe-table-column field="devAlias" title="设备别名"></vxe-table-column>
            <vxe-table-column field="IP" title="IP地址" show-overflow></vxe-table-column>
            <vxe-table-column field="updateTime" title="最新时间" show-overflow></vxe-table-column>
            <vxe-table-column field="onlineDuration" title="连续在线时长(时)" show-overflow></vxe-table-column>
            <vxe-table-column field="alarmCount" title="告警次数"></vxe-table-column>
            <vxe-table-column field="downCount" title="宕机次数"></vxe-table-column>
            <vxe-table-column field="downDuration" title="宕机总时长(时)"></vxe-table-column>
            <vxe-table-column field="cpuUsage" title="CPU最高使用率(%)"></vxe-table-column>
            <vxe-table-column field="ramUsage" title="内存最高使用率(%)"></vxe-table-column>
            <vxe-table-column field="romUsage" title="磁盘最高占用率(%)"></vxe-table-column>
            <vxe-table-column field="maxThreadCount" title="最多进程数"></vxe-table-column>
          </vxe-table>
        </div>
        <div class="chartBox">
          <div id="mychart2"></div>
        </div>
        <div class="tableBox">
          <vxe-table border ref="xTable" :data="tableData" width="100%">
            <vxe-table-column field="number" title="序号" width="55"></vxe-table-column>
            <vxe-table-column field="devAlias" title="设备别名"></vxe-table-column>
            <vxe-table-column field="IP" title="IP地址" show-overflow></vxe-table-column>
            <vxe-table-column field="updateTime" title="最新时间" show-overflow></vxe-table-column>
            <vxe-table-column field="onlineDuration" title="连续在线时长(时)" show-overflow></vxe-table-column>
            <vxe-table-column field="alarmCount" title="告警次数"></vxe-table-column>
            <vxe-table-column field="downCount" title="宕机次数"></vxe-table-column>
            <vxe-table-column field="downDuration" title="宕机总时长(时)"></vxe-table-column>
            <vxe-table-column field="cpuUsage" title="CPU最高使用率(%)"></vxe-table-column>
            <vxe-table-column field="ramUsage" title="内存最高使用率(%)"></vxe-table-column>
            <vxe-table-column field="romUsage" title="磁盘最高占用率(%)"></vxe-table-column>
            <vxe-table-column field="maxThreadCount" title="最多进程数"></vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import { remFontSize } from '@/components/utils/fontSize.js';
  import echarts from 'echarts';
  import moment from 'moment';
  export default {
    data() {
      return {
        useageChart: '',
        usageLegend: ['CPU最高使用率', '内存最高使用率', '磁盘最高使用率'],
        downCountData: [],
        downDurationData: [],
        xAxisData: [],
        alarmCountData: [],
        cpuUsageData: [],
        ramUsageData: [],
        romUsageData: [],
        colors: ['#5793F3', '#FF0000', '#0000FF'],
        useageSeries: [],
        tableData: [
          {
            number: '1',
            devAlias: '报文接收器',
            IP: 'xxx.xxx.xxx.xx',
            updateTime: '2020/8/20 10:00',
            onlineDuration: '2000',
            alarmCount: '5',
            downCount: '3',
            downDuration: '2.4',
            cpuUsage: '90',
            ramUsage: '85',
            romUsage: '(d:/)96',
            maxThreadCount: '27',
          },
          {
            number: '2',
            devAlias: 'MySql数据库',
            IP: 'xxx.xxx.xxx.xx',
            updateTime: '2020/8/20 10:03',
            onlineDuration: '2000',
            alarmCount: '6',
            downCount: '2',
            downDuration: '2.3',
            cpuUsage: '90',
            ramUsage: '85',
            romUsage: '(/boot)92',
            maxThreadCount: '22',
          },
          {
            number: '3',
            devAlias: '监控台A',
            IP: 'xxx.xxx.xxx.xx',
            updateTime: '2020/8/20 10:20',
            onlineDuration: '2000',
            alarmCount: '7',
            downCount: '3',
            downDuration: '2.3',
            cpuUsage: '90',
            ramUsage: '85',
            romUsage: '(/boot)92',
            maxThreadCount: '23',
          },
          {
            number: '4',
            devAlias: '监控台B',
            IP: 'xxx.xxx.xxx.xx',
            updateTime: '2020/8/20 10:10',
            onlineDuration: '2000',
            alarmCount: '8',
            downCount: '4',
            downDuration: '2.5',
            cpuUsage: '90',
            ramUsage: '85',
            romUsage: '(/boot)92',
            maxThreadCount: '10',
          },
          {
            number: '5',
            devAlias: '监控台C',
            IP: 'xxx.xxx.xxx.xx',
            updateTime: '2020/8/20 10:00',
            onlineDuration: '2000',
            alarmCount: '9',
            downCount: '5',
            downDuration: '2',
            downDuration: '2.5',
            cpuUsage: '90',
            ramUsage: '85',
            romUsage: '(/boot)92',
            maxThreadCount: '10',
          },
          {
            number: '6',
            devAlias: '监控台C',
            IP: 'xxx.xxx.xxx.xx',
            updateTime: '2020/8/20 10:00',
            onlineDuration: '2000',
            alarmCount: '9',
            downCount: '5',
            downDuration: '2',
            downDuration: '2.5',
            cpuUsage: '90',
            ramUsage: '85',
            romUsage: '(/boot)92',
            maxThreadCount: '10',
          },
        ],
      };
    },
    mounted() {
      this.initData();
      this.initSeries(); //series
      this.initYaxis(); //Y轴
      this.drawChart(
        'mychart',
        this.useageChart,
        '主机CPU、内存、磁盘使用率情况统计',
        this.usageLegend,
        this.usageYaxis,
        this.useageSeries,
      );
      this.drawChart(
        'mychart2',
        this.useageChart,
        '主机CPU、内存、磁盘使用率情况统计',
        this.usageLegend,
        this.usageYaxis,
        this.useageSeries,
      );
    },
    methods: {
      onTimeChange(value, dateString) {
        console.log(dateString);
      },
      moment,
      range(start, end) {
        const result = [];
        for (let i = start; i < end; i++) {
          result.push(i);
        }
        return result;
      },

      drawChart(id, chart, title, legend, Yaxis, series) {
        chart = echarts.init(document.getElementById(id));
        let options = {
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
          },
          color: this.colors,
          title: {
            text: title,
            left: 'center',
            textStyle: {
              fontSize: remFontSize(14 / 64),
              fontStyle: 'normal',
              fontWeight: 'normal',
            },
          },
          tooltip: {
            trigger: 'axis',
            /*  axisPointer: {
              type: 'cross',
            }, */
          },
          grid: {
            top: '20%',
            bottom: '10%',
            right: '15%',
            left: '10%',
          },
          legend: {
            data: legend,
            top: '10%',
            textStyle: {
              fontSize: remFontSize(12 / 64),
            },
          },
          xAxis: {
            type: 'category',
            data: this.xAxisData,
            splitLine: {
              show: false,
            },
            axisTick: {
              //y轴刻度线
              show: false,
            },
            axisLine: {
              lineStyle: {
                color: '#565656',
                width: 0.5,
              },
            },
          },
          yAxis: Yaxis,
          series: series,
        };
        chart.setOption(options);
      },
      initSeries() {
        this.useageSeries.push({
          type: 'line',
          data: this.ramUsageData,
          name: this.usageLegend[1],
        });
        this.useageSeries.push({
          type: 'line',
          data: this.romUsageData,
          name: this.usageLegend[2],
        });
      },

      initYaxis() {
        this.usageYaxis = [
          {
            type: 'value',
            name: '(%)',
            min: 0,
            max: 100,
            axisLine: {
              lineStyle: {
                color: '#FF0000',
                width: 0.5,
              },
            },
            axisTick: {
              //y轴刻度线
              show: false,
            },
            splitLine: {
              lineStyle: {
                color: '#bababa',
                width: 0.5,
                type: 'solid',
              },
              show: true,
            },
            axisLabel: {
              formatter: '{value} ',
              fontSize: remFontSize(12 / 64),
            },
          },
        ];
      },
      initData() {
        this.tableData.forEach((item) => {
          this.downCountData.push(item.downCount);
          this.downDurationData.push(item.downDuration);
          this.xAxisData.push(item.devAlias);
          this.alarmCountData.push(item.alarmCount);
          this.cpuUsageData.push(item.cpuUsage);
          this.ramUsageData.push(item.ramUsage);
          let romarr = item.romUsage.split(')');
          this.romUsageData.push(parseInt(romarr[1]));
        });
      },
    },
  };
</script>

<style lang="scss" scoped>
  #dataReport {
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      z-index: 1;
      width: 100%;
      height: 1.25rem;
      font-size: 0.2rem;
      -webkit-box-shadow: 0.0375rem 0.0375rem 0.3rem 0.1rem #5d77ae1c;
      box-shadow: 0.0375rem 0.0375rem 0.3rem 0.1rem #5d77ae1c;
      margin-bottom: 0.1rem;
      padding: 0 0.4rem;
      .ant-btn-group {
        .ant-btn {
          margin-right: 0;
        }
      }
    }
    .content {
      -webkit-box-shadow: 0.0375rem 0.0375rem 0.3rem 0.1rem #5d77ae1c;
      box-shadow: 0.0375rem 0.0375rem 0.3rem 0.1rem #5d77ae1c;
      width: 100%;
      height: calc(100% - 1.35rem);
      background: white;
      display: -webkit-box;
      display: -ms-flexbox;
      display: flex;
      -ms-flex-wrap: wrap;
      flex-wrap: wrap;
      -webkit-box-pack: justify;
      -ms-flex-pack: justify;
      justify-content: space-between;
      overflow: auto;
      overflow-x: hidden;
      .title {
        margin: 0 auto;
        text-align: center;
        width: 90%;
        font-size: 0.3rem;
        height: 0.75rem;
        line-height: 0.75rem;
        border-bottom: 1px solid #bddfeb8f;
      }
      .dataBox {
        height: calc(100% - 0.75rem);
        width: 100%;
        .chartBox {
          width: 100%;
          margin: 0.3rem 0;
          #mychart,
          #mychart2 {
            width: 100%;
            height: 4rem;
          }
        }
        .tableBox {
          width: 100%;
          height: 5rem;
          padding: 0.25rem;
          margin-bottom: 0.3rem;
        }
      }
    }
  }
  .ant-calendar-header .ant-calendar-prev-month-btn {
    top: 16px;
  }
</style>
