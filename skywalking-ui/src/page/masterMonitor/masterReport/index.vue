<template>
  <div id="reportMaterTemplate">
    <div class="hasHandleExportBox">
      <selectDate @changeDate="changeDate"></selectDate>
      <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" @click="exportEventXls"> 导出excel </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="primary" @click="exportEventPdf"> 导出pdf </a-button>
        </a-col>
      </a-row>
    </div>

    <div class="tableDateBox">
      <p id="title">服务器监视情况</p>
      <div id="dataPlane">
        <div id="report_chartdiv">
          <div id="alarmChart"></div>
          <div id="useageChart"></div>
        </div>
        <div id="toolbar">
          <vxe-toolbar custom>
            <template v-slot:buttons><p style="text-align: right; margin-bottom: 0">列选择</p> </template>
          </vxe-toolbar>
        </div>
        <vxe-table border ref="xTable" :height="tableheight" :data="tableData">
          <vxe-table-column field="hostName" title="设备名称"></vxe-table-column>
          <vxe-table-column field="ip" title="IP地址" show-overflow></vxe-table-column>
          <vxe-table-column field="updateTime" title="最新时间" show-overflow>
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.updateTime) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="maxUptime" title="连续在线时长(时)" show-overflow></vxe-table-column>
          <vxe-table-column field="alarmCount" title="告警次数"></vxe-table-column>
          <vxe-table-column field="downCount" title="宕机次数"></vxe-table-column>
          <vxe-table-column field="downTime" title="宕机总时长(时)"></vxe-table-column>
          <vxe-table-column field="maxCpuPct" title="CPU最高使用率(%)"></vxe-table-column>
          <vxe-table-column field="maxMemoryPct" title="内存最高使用率(%)"></vxe-table-column>
          <vxe-table-column field="maxFilesystemPct" title="磁盘最高占用率(%)"></vxe-table-column>
          <vxe-table-column field="maxProcessSize" title="最多进程数"></vxe-table-column>
        </vxe-table>
      </div>
    </div>
  </div>
</template>

<script>
import aYearPicker from '@/components/datePickYear/datePickYear.vue';
import selectDate from '@/components/date/select.vue';
import { remFontSize } from '@/components/utils/fontSize.js';
import echarts from 'echarts';
import moment from 'moment';
import request from '@/utils/request';
import Qs from 'qs';
export default {
  data() {
    return {
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10000,
        deviceType: 0,
      },
      dayFormat: 'YYYY-MM-DD',
      monthFormat: 'YYYY-MM',
      yearFormat: 'YYYY',
      size: 'small',
      currentType: 'day',
      // date: moment(new Date()).format('YYYY-MM-DD'),
      date_title: moment(new Date()).format('YYYY-MM-DD'),
      dateRange: '',
      alarmChart: '',
      useageChart: '',
      tableheight: null,
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
      tableData: [],
    };
  },
  components: { aYearPicker, selectDate },
  created() {},
  mounted() {
    //this.fetch();
    this.$nextTick(() => {});

    window.addEventListener('resize', () => {
      setTimeout(() => {
        this.alarmChart.resize();
        this.useageChart.resize();
      }, 500);
    });
  },
  methods: {
    exportEventPdf() {
      this.queryParams.alarmChart = this.getFullCanvasDataURL('alarmChart');
      this.queryParams.useageChart = this.getFullCanvasDataURL('useageChart');
      let params = this.addDateRange(this.queryParams, this.dateRange);
      params.params = JSON.stringify(params.params);
      request({
        url: '/report/exportPdf',
        method: 'post',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        data: Qs.stringify(params),
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    exportEventXls() {
      this.queryParams.alarmChart = this.getFullCanvasDataURL('alarmChart');
      this.queryParams.useageChart = this.getFullCanvasDataURL('useageChart');
      let params = this.addDateRange(this.queryParams, this.dateRange);
      console.log(params);
      params.params = JSON.stringify(params.params);
      request({
        url: '/report/export',
        method: 'post',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        data: Qs.stringify(params),
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    getFullCanvasDataURL(divId) {
      //将第一个画布作为基准。
      var baseCanvas = $('#' + divId)
        .find('canvas')
        .first()[0];
      if (!baseCanvas) {
        return false;
      }
      var width = baseCanvas.width;
      var height = baseCanvas.height;
      var ctx = baseCanvas.getContext('2d');
      //遍历，将后续的画布添加到在第一个上
      $('#' + divId)
        .find('canvas')
        .each(function (i, canvasObj) {
          if (i > 0) {
            var canvasTmp = $(canvasObj)[0];
            ctx.drawImage(canvasTmp, 0, 0, width, height);
          }
        });
      //获取base64位的url
      return baseCanvas.toDataURL();
    },
    changeDate(data) {
      this.dateRange = data;
      this.fetch();
    },
    moment,
    onChangeType(currentType) {
      if (currentType == 'day') {
        this.date_title = moment().format(this.dayFormat);
      } else if (currentType == 'week') {
        let startTime = moment().startOf('week').format('MM/DD'); //周初
        let endTime = moment().endOf('week').format('MM/DD'); //周末

        this.date_title = `
          ${moment().format('YYYY')} 年第${moment().weeks()}周(${startTime}-${endTime})`;
      } else if (currentType == 'month') {
        this.date_title = moment().format(this.monthFormat);
      } else if (currentType == 'year') {
        this.date_title = moment().format(this.yearFormat) + '年';
      }
    },
    timeChange(date, dateString) {
      if (this.currentType == 'week') {
        let startTime = moment(date).startOf('week').format('MM/DD'); //周初
        let endTime = moment(date).endOf('week').format('MM/DD'); //周末

        this.date_title = `
          ${moment(date).format('YYYY')} 年第${moment(date).weeks()}周(${startTime}-${endTime})`;
      } else {
        this.date_title = dateString;
      }
    },
    initData() {
      this.downCountData = [];
      this.downDurationData = [];
      this.xAxisData = [];
      this.alarmCountData = [];
      this.cpuUsageData = [];
      this.ramUsageData = [];
      this.romUsageData = [];

      this.tableData.forEach((item) => {
        this.downCountData.push(item.downCount);
        this.downDurationData.push(item.downTime);
        this.xAxisData.push(item.ip);
        this.alarmCountData.push(item.alarmCount);
        this.cpuUsageData.push(item.maxCpuPct);
        this.romUsageData.push(item.maxFilesystemPct);
        this.ramUsageData.push(item.maxMemoryPct);
      });
    },
    initSeries() {
      this.alarmSeries = [];
      this.useageSeries = [];
      this.alarmSeries.push({
        type: 'bar',
        data: this.downDurationData,
        name: this.alarmLegend[0],
        barWidth: '30%',
      });
      this.alarmSeries.push({
        type: 'line',
        data: this.alarmCountData,
        name: this.alarmLegend[1],
        yAxisIndex: 1,
      });
      this.alarmSeries.push({
        type: 'line',
        data: this.downCountData,
        name: this.alarmLegend[2],
        yAxisIndex: 2,
      });
      this.useageSeries.push({
        type: 'line',
        data: this.cpuUsageData,
        name: this.usageLegend[0],
      });
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
      this.alarmYaxis = [
        {
          type: 'value',
          name: '(小时)',
          min: 0,
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
            fontSize: remFontSize(12 / 64),
            formatter: '{value} ',
          },
          /*   axisLabel: {
            interval: 0,
            show: true,
            fontSize: remFontSize(12 / 64),
            color: '#353535',
          }, */
        },
        {
          type: 'value',
          name: '(次)',
          min: 0,
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
            fontSize: remFontSize(12 / 64),
          },
        },
        {
          type: 'value',
          name: '(次)',
          min: 0,
          max: 15,
          position: 'right',
          offset: remFontSize(40 / 64),
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
            fontSize: remFontSize(12 / 64),
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
            fontSize: remFontSize(12 / 64),
          },
        },
      ];
    },
    setTableHeight() {
      let h = document.getElementById('dataPlane').clientHeight;
      let padding = getComputedStyle(document.getElementById('tablediv'), false)['paddingLeft'];
      let chartHeight = document.getElementById('report_chartdiv').offsetHeight;
      let h_page = 0;
      //let h_page = document.getElementById('page_table').offsetHeight;
      this.tableheight = h - chartHeight - parseInt(padding) * 2 - h_page - 1;
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
    fetch() {
      this.queryParams.alarmChart = '';
      this.queryParams.useageChart = '';
      request({
        url: '/report/list',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.total = data.data.totalCount;
        this.tableData = data.data.pageData;
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
  },
};
</script>

<style lang="scss" scoped>
#reportMaterTemplate {
  width: 100%;
  height: 100%;
  #title {
    border-bottom: solid 2px $plane_border_color;
    font-size: 20px;
    height: 56px;
    line-height: 56px;
    text-align: center;
  }
  #dataPlane {
    width: 100%;
    #report_chartdiv {
      flex: 1;
      width: 100%;
      height: 50%;
      display: flex;
      margin-bottom: 14px;
      #alarmChart {
        width: 50%;
        height: 100%;
      }
      #useageChart {
        height: 100%;
        width: 50%;
      }
    }
  }
}
</style>
