<template>
  <div id="reportMater">
    <div id="header">
      <div id="selectType">
        <label>报表类型:</label>
        <select id="option" v-model="currentType" @change="onChangeType(currentType)">
          <option value="date">日报</option>
          <option value="week">周报</option>
          <option value="month">月报</option>
          <option value="year">年报</option>
        </select>
      </div>
      <div id="timePicker">
        <label>报表周期:</label>
        <date-picker
          @change="timeChange"
          v-if="currentType == 'week'"
          v-model="dateRange"
          :type="currentType"
          range
          size="small"
        />
        <date-picker @change="timeChange" v-else v-model="date" :type="currentType" size="small" />
      </div>
    </div>
    <div id="content">
      <div id="title">({{ date_title }})服务器监视情况</div>
      <div id="dataPlane">
        <div id="chartdiv">
          <div id="alarmChart"></div>
          <div id="useageChart"></div>
        </div>
        <div id="tablediv">
          <vxe-table border ref="xTable" :height="tableheight" :data="tableData">
            <vxe-table-column field="number" title="序号"></vxe-table-column>
            <vxe-table-column field="devAlias" title="设备别名"></vxe-table-column>
            <vxe-table-column field="IP" title="IP地址"></vxe-table-column>
            <vxe-table-column field="updateTime" title="最新时间"></vxe-table-column>
            <vxe-table-column field="onlineDuration" title="连续在线时长(时)"></vxe-table-column>
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
  import DatePicker from '@hyjiacan/vue-datepicker';
  import '@hyjiacan/vue-datepicker/dist/datepicker.css';
  import echarts from 'echarts';
  import moment from 'moment';
  export default {
    data() {
      return {
        currentType: 'date',
        date: moment(new Date()).format('YYYY-MM-DD'),
        date_title: moment(new Date()).format('YYYY-MM-DD'),
        dateRange: [
          moment()
            .weekday(0)
            .format('YYYY-MM-DD'),
          moment()
            .weekday(6)
            .format('YYYY-MM-DD'),
        ],
        alarmChart: '',
        useageChart: '',
        tableheight: 200,
        downCountData: [],
        downDurationData: [],
        xAxisData: [],
        alarmCountData: [],
        cpuUsageData: [],
        ramUsageData: [],
        romUsageData: [],
        alarmSeries: [],
        useageSeries: [],
        alarmYaxis: [],
        usageYaxis: [],
        alarmLegend: ['宕机总时长', '告警次数', '宕机次数'],
        usageLegend: ['CPU最高使用率', '内存最高使用率', '磁盘最高使用率'],
        colors: ['#5793F3', '#FF0000', '#0000FF'],
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
    components: { DatePicker },
    created() {},
    mounted() {
      this.$nextTick(() => {
        this.setTableHeight();
        this.initData(); //数据
        this.initSeries(); //series
        this.initYaxis(); //Y轴
        this.drawChart(
          'alarmChart',
          this.alarmChart,
          '主机告警与宕机情况统计',
          this.alarmLegend,
          this.alarmYaxis,
          this.alarmSeries,
        );
        this.drawChart(
          'useageChart',
          this.useageChart,
          '主机CPU、内存、磁盘使用率情况统计',
          this.usageLegend,
          this.usageYaxis,
          this.useageSeries,
        );
      });
    },
    methods: {
      onChangeType(currentType) {
        if (currentType == 'date') {
          this.date_title = moment(new Date()).format('YYYY-MM-DD');
        } else if (currentType == 'week') {
          this.date_title = moment().format('YYYY') + '年第' + moment().weeks() + '周';
        } else if (currentType == 'month') {
          this.date_title = moment(this.date).format('YYYY-MM') + '月';
        } else if (currentType == 'year') {
          this.date_title = moment(this.date).format('YYYY') + '年';
        }
      },
      timeChange({ type, value, src }, oldValue) {
        if (value == oldValue) {
          return;
        } else {
          if (type == 'date') {
            this.date_title = moment(value).format('YYYY-MM-DD');
          } else if (type == 'week') {
            this.date_title = moment().format('YYYY') + '年,第' + moment(value[0]).weeks() + '周';
          } else if (type == 'month') {
            this.date_title = moment(value).format('YYYY-MM') + '月';
          } else if (type == 'year') {
            this.date_title = moment(value).format('YYYY') + '年';
          }
        }
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
      initSeries() {
        this.alarmSeries.push({ type: 'bar', data: this.downDurationData, name: this.alarmLegend[0], barWidth: '30%' });
        this.alarmSeries.push({ type: 'line', data: this.alarmCountData, name: this.alarmLegend[1], yAxisIndex: 1 });
        this.alarmSeries.push({ type: 'line', data: this.downCountData, name: this.alarmLegend[2], yAxisIndex: 2 });

        this.useageSeries.push({ type: 'line', data: this.cpuUsageData, name: this.usageLegend[0] });
        this.useageSeries.push({ type: 'line', data: this.ramUsageData, name: this.usageLegend[1] });
        this.useageSeries.push({ type: 'line', data: this.romUsageData, name: this.usageLegend[2] });
      },
      initYaxis() {
        this.alarmYaxis = [
          {
            type: 'value',
            name: '(小时)',
            min: 0,
            max: 10,
            position: 'left',
            axisTick: {
              //y轴刻度线
              show: false,
            },
            axisLine: {
              lineStyle: {
                color: this.colors[0],
                width: 0.5,
              },
            },
            splitLine: {
              show: false,
            },
            axisLabel: {
              formatter: '{value} ',
            },
          },
          {
            type: 'value',
            name: '(次)',
            min: 0,
            max: 10,
            position: 'right',
            axisTick: {
              //y轴刻度线
              show: false,
            },
            splitLine: {
              show: false,
            },
            axisLine: {
              lineStyle: {
                color: this.colors[1],
                width: 0.5,
              },
            },

            axisLabel: {
              formatter: '{value} ',
            },
          },

          {
            type: 'value',
            name: '(次)',
            min: 0,
            max: 15,
            position: 'right',
            offset: 40,
            axisLine: {
              lineStyle: {
                color: this.colors[2],
                width: 0.5,
              },
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
            },
          },
        ];

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
            },
          },
        ];
      },
      setTableHeight() {
        let h = document.getElementById('dataPlane').clientHeight;
        //let padding = getComputedStyle(document.getElementById('reportMater'), false)['paddingTop'];
        let chartHeight = document.getElementById('chartdiv').clientHeight;
        this.tableheight = h - chartHeight - 1;
      },
      drawChart(id, chart, title, legend, Yaxis, series) {
        chart = echarts.init(document.getElementById(id));

        let options = {
          color: this.colors,

          title: {
            text: title,
            left: 'center',
            textStyle: { fontSize: 14, fontStyle: 'normal', fontWeight: 'normal' },
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
    },
  };
</script>

<style lang="scss" scoped>
  #reportMater {
    width: 100%;
    height: 100%;
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    #header {
      z-index: 1;
      width: 100%;
      height: 1.25rem;
      // line-height: 1.25rem;
      font-size: 0.2rem;
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      margin-bottom: 0.1rem;
      #selectType {
        display: inline-block;
        width: 50%;
        line-height: 1.25rem;
        text-align: center;
        #option {
          margin-left: 0.1rem;
          height: 0.375rem;
          width: 3.2rem;
          border: 1px solid #dcdfe6;
          border-radius: 0.05rem;
          &:focus {
            outline: none !important;
            border: 1px solid #0f9fec !important;
          }
        }
      }
      #timePicker {
        display: inline-block;
        width: 50%;
        // line-height: 1.25rem;
      }
    }
    #content {
      // overflow: hidden;
      overflow-x: hidden;
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      width: 100%;
      height: calc(100% - 1.35rem);
      background: white;
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      overflow: auto;
      #title {
        margin: 0 auto;
        text-align: center;
        width: 90%;
        font-size: 0.3rem;
        height: 0.75rem;
        line-height: 0.75rem;
        border-bottom: 1px solid #bddfeb8f;
      }
      #dataPlane {
        height: calc(100% - 0.75rem);
        width: 100%;
        display: flex;
        flex-direction: column;
        #chartdiv {
          flex: 3;
          width: 100%;
          display: flex;
          #alarmChart {
            height: calc(100% - 0.125rem);
            width: 59%;
          }
          #useageChart {
            height: calc(100% - 0.125rem);
            width: 50%;
          }
        }
        #tablediv {
          flex: 2;
          width: 100%;
          padding: 0 0.25rem;
          //background: pink;
        }
      }
    }
  }
</style>
