<template>
  <div class="fileReportRowTemplate">
    <div class="hasHandleExportBox">
      <selectDate @changeDate="onTimeChange" :handleRange="7"></selectDate>
      <a-button type="primary" @click="exportEventXls" style="margin-right: 10px"> 导出excel </a-button>
    </div>
    <div class="tableDateBox">
      <div id="barlineChart"></div>
      <vxe-table border ref="xTable" :span-method="mergeRowMethod" :data="tableData" resizable stripe align="center">
        <vxe-table-column field="taskName" title="名称"></vxe-table-column>
        <vxe-table-column field="timestamp" title="时间" show-overflow>
          <template slot-scope="scope">
            <span v-if="scope.row.timestamp == '合计'">{{ scope.row.timestamp }}</span>
            <span v-if="scope.row.timestamp !== '合计'">{{ parseTime(scope.row.timestamp, '{y}-{m}-{d}') }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="sumRealFileNum" title="准时到(个)"></vxe-table-column>
        <vxe-table-column field="sumLateNum" title="晚到(个)"></vxe-table-column>
        <vxe-table-column field="sumFileNum" title="应到(个)"></vxe-table-column>
        <vxe-table-column field="sumRealFileSize" title="大小(KB)"></vxe-table-column>
        <vxe-table-column field="toQuoteRate" title="到报率(%)"></vxe-table-column>
        <vxe-table-column field="timelinessRate" title="及时率(%)"></vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
import selectDate from '@/components/date/select.vue';
var XEUtils = require('xe-utils');
import echarts from 'echarts';
import { remFontSize } from '@/components/utils/fontSize.js';
import request from '@/utils/request';
import moment from 'moment';
export default {
  data() {
    return {
      tableData: [],
      headers: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        // startTime: '',
        // endTime: '',
      },
      form: {},
      visible: false,
      // chartParams: {
      //   deviceType: '',
      //   endTime: '',
      //   ip: '',
      //   startTime: '',
      // },
      chartData: [],
      chartTime: [],
      chartTitle: [],
      series: [],
      Ydata: [],
      charts: [],
      colors: ['#428AFF', '#6c50f3', '#00ca95'],
      chartType: ['line'],
      // name: ['离线时长', '最大cpu使用率', '最大内存使用率'],
    };
  },
  components: { selectDate },
  created() {
    this.fetch();
    this.fileReportLineChart();
  },
  mounted() {
    //this.findHeader();
    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
  computed: {},
  methods: {
    moment,
    range(start, end) {
      const result = [];
      for (let i = start; i < end; i++) {
        result.push(i);
      }
      return result;
    },
    onTimeChange(value) {
      this.queryParams.startTime = value[0];
      this.queryParams.endTime = value[1];
      this.fetch();
    },
    findHeader() {
      request({
        url: '/fileQReport/findHeader',
        method: 'get',
      }).then((data) => {
        this.headers = data.data;
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
    drawChart(id) {
      this.charts = echarts.init(document.getElementById(id));
      let options = {
        textStyle: {
          fontFamily: 'Alibaba-PuHuiTi-Regular',
        },
        title: {
          text: '文件报表折线图-到报率',
          left: 'center',
          textStyle: {
            fontSize: remFontSize(14 / 64),
          },
        },
        legend: {
          data: this.chartTitle,
          top: '10%',
          textStyle: {
            fontSize: remFontSize(13 / 64),
          },
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999',
            },
          },
          formatter: function (params) {
            return params[0].value + '%';
          },
        },

        grid: {
          top: '20%',
          bottom: '6%',
        },
        toolbox: {
          feature: {
            dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ['line', 'bar'] },
            restore: { show: true },
            saveAsImage: { show: true },
          },
        },

        xAxis: [
          {
            type: 'category',
            data: this.chartTime,
            axisPointer: {
              type: 'shadow',
            },
            axisLabel: {
              fontSize: remFontSize(12 / 64),
            },
          },
        ],
        yAxis: [
          {
            min: 0,
            max: 100,
            type: 'value',
            name: '到报率',
            axisLabel: {
              formatter: '{value} %',
              fontSize: remFontSize(12 / 64),
            },
          },
        ],
        series: this.series,
      };

      this.charts.setOption(options);
    },

    chart() {
      this.series = [];
      this.chartData.forEach((element, index) => {
        let obj = {};
        obj.name = element.name;
        obj.type = 'line';
        obj.data = [10, 5];
        obj.data = element.data;
        // obj.lineStyle = {
        //   normal: {
        //     color: '#6c50f3',
        //   },
        // };
        this.series.push(obj);
      });
      console.log(this.series);
    },

    statusFormat(status) {
      return this.selectDictLabel(this.statusOptions, status);
    },
    handleQuery() {
      //this.queryParams.pageNum = 1;
      this.fetch();
    },
    resetQuery() {
      this.dateRange = [];
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    exportEventXls() {
      request({
        url: '/fileQReport/exportFileReportRow',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
        responseType: 'arraybuffer',
      }).then((res) => {
        this.downloadfileCommon(res);
      });
    },
    fetch() {
      request({
        url: '/fileQReport/findFileReportRow',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.tableData = data.data.tableData;
      });
    },

    fileReportLineChart() {
      request({
        url: '/fileQReport/findFileReportLineChart',
        method: 'get',
        params: this.chartParams,
      }).then((data) => {
        this.chartData = data.data.data;
        this.chartTime = data.data.time;
        this.chartTitle = data.data.title;
        console.log(this.chartTitle);
        this.chart();
        this.drawChart('barlineChart');
      });
    },

    // 通用行合并函数（将相同多列数据合并为一行）
    mergeRowMethod({ row, _rowIndex, column, visibleData }) {
      const fields = ['taskName'];
      const cellValue = XEUtils.get(row, column.property);
      if (cellValue && fields.includes(column.property)) {
        const prevRow = visibleData[_rowIndex - 1];
        let nextRow = visibleData[_rowIndex + 1];
        if (prevRow && XEUtils.get(prevRow, column.property) === cellValue) {
          return { rowspan: 0, colspan: 0 };
        } else {
          let countRowspan = 1;
          while (nextRow && XEUtils.get(nextRow, column.property) === cellValue) {
            nextRow = visibleData[++countRowspan + _rowIndex];
          }
          if (countRowspan > 1) {
            return { rowspan: countRowspan, colspan: 1 };
          }
        }
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.fileReportRowTemplate {
  .dcBtn {
    margin-left: 10px;
  }
  #barlineChart {
    width: 900px;
    height: 50%;
    margin: auto;
  }
}
</style>
