<template>
  <div id="processReport">
    <selectDate @changeDate="changeDate"></selectDate>
    <div class="tableDateBox">
      <div id="barlineChart"></div>
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons>
            <!--   <vxe-button @click="exportEventXls">导出excel</vxe-button>
              <vxe-button @click="exportEventPdf">导出pdf</vxe-button> -->
            <a-row type="flex" class="rowToolbar" :gutter="10">
              <a-col :span="1.5">
                <a-button type="primary" @click="exportEventXls"> 导出excel </a-button>
              </a-col>
              <a-col :span="1.5">
                <a-button type="primary" @click="exportEventPdf"> 导出pdf </a-button>
              </a-col>
            </a-row>
          </template>
        </vxe-toolbar>
      </div>
      <!--   -->
      <vxe-table border ref="xTable" :height="tableheight" :data="tableData" stripe align="center">
        <vxe-table-column field="processName" title="进程名称" width="170"></vxe-table-column>
        <vxe-table-column field="ip" title="IP地址"></vxe-table-column>
        <vxe-table-column field="maxUptimePct" title="在线时长" show-overflow></vxe-table-column>
        <vxe-table-column field="downTime" title="离线时长"></vxe-table-column>
        <vxe-table-column field="downNum" title="离线次数"></vxe-table-column>
        <vxe-table-column field="exeptionTime" title="异常时长"></vxe-table-column>
        <vxe-table-column field="exeptionNum" title="异常次数"></vxe-table-column>
        <vxe-table-column field="alarmNum" title="告警次数"></vxe-table-column>
        <vxe-table-column field="maxCpuPct" title="最大cpu使用率"></vxe-table-column>
        <vxe-table-column field="avgCpuPct" title="平均cpu使用率"></vxe-table-column>
        <vxe-table-column field="maxMemoryPct" title="最大内存使用率"></vxe-table-column>
      </vxe-table>
    </div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  import { remFontSize } from '@/components/utils/fontSize.js';
  // 接口地址
  import hongtuConfig from '@/utils/services';
  import selectDate from '@/components/date/select.vue';
  import Qs from 'qs';
  import request from '@/utils/request';
  export default {
    data() {
      return {
        total: 0,
        queryParams: {},
        currentStatusOptions: [],
        monitoringMethodsOptions: [],
        areaOptions: [],
        Xdata: [],
        series: [],
        Ydata: [],
        charts: [],
        colors: ['#428AFF', '#6c50f3', '#00ca95'],
        chartType: ['bar', 'line', 'line'],
        name: ['离线时长', '最大cpu使用率', '最大内存使用率'],
        tableheight: null,
        tableData: [],
        dateRange: [],
      };
    },
    components: { selectDate },
    created() {},
    mounted() {},
    methods: {
      exportEventPdf() {
        this.queryParams.processChart = this.getFullCanvasDataURL('barlineChart');
        let params = this.addDateRange(this.queryParams, this.dateRange);
        params.params = JSON.stringify(params.params);
        request({
          url: '/processQReport/exportPdf',
          method: 'post',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          data: Qs.stringify(params),
          responseType: 'arraybuffer',
        }).then((res) => {
          this.downloadfileCommon(res);
        });
      },
      exportEventXls() {
        this.queryParams.processChart = this.getFullCanvasDataURL('barlineChart');
        let params = this.addDateRange(this.queryParams, this.dateRange);
        params.params = JSON.stringify(params.params);
        request({
          url: '/processQReport/exportExcel',
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
          .each(function(i, canvasObj) {
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

      drawChart(id) {
        this.charts = echarts.init(document.getElementById(id));
        let options = {
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
          },
          title: {
            text: '进程运行情况表',
            left: 'center',
            textStyle: {
              fontSize: remFontSize(14 / 64),
            },
          },
          legend: {
            data: this.name,
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
              data: this.Xdata,
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
              interval: 500,
              type: 'value',
              name: '在线时长',
              axisLabel: {
                formatter: '{value} h',
                fontSize: remFontSize(12 / 64),
              },
            },
            {
              type: 'value',
              name: '平均/最大丢包率',
              min: 0,
              max: 100,
              interval: 20,
              axisLabel: {
                formatter: '{value}%',
                fontSize: remFontSize(12 / 64),
              },
            },
          ],
          series: this.series,
        };

        this.charts.setOption(options);
      },
      fetch() {
        this.queryParams.processChart = '';
        request({
          url: '/processQReport/findProcessReport',
          method: 'get',
          params: this.addDateRange(this.queryParams, this.dateRange),
        }).then((res) => {
          if (res.code == 200) {
            this.tableData = res.data;
            this.chart();
            this.drawChart('barlineChart');
          }
        });
      },
      chart() {
        let Ydata1 = [],
          Ydata2 = [],
          Ydata3 = [];
        this.Xdata = [];
        this.Ydata = [];
        this.tableData.forEach((item, index) => {
          this.Xdata.push(item.processName);
          Ydata1.push(parseFloat(item.downTime));
          Ydata2.push(parseFloat(item.maxCpuPct));
          Ydata3.push(parseFloat(item.maxMemoryPct));
        });

        this.Ydata.push(Ydata1, Ydata2, Ydata3);
        this.series = this.chartType.map((item, index) => {
          let obj = {};
          obj.name = this.name[index];
          obj.type = item;
          obj.data = this.Ydata[index];
          if (item == 'bar') {
            obj.barWidth = '40%';
            obj.itemStyle = {
              color: this.colors[index],
            };
          } else {
            obj.lineStyle = {
              normal: {
                color: this.colors[index],
              },
            };
          }

          if (obj.type == 'bar') {
            obj.yAxisIndex = 0;
          } else {
            obj.yAxisIndex = 1;
          }
          return obj;
        });
      },
    },
  };
</script>

<style lang="scss" scoped>
  #processReport {
    width: 100%;
    .tableDataBox {
      background: #fff;
    }
    #barlineChart {
      width: 900px;
      height: 50%;
      margin: auto;
    }
  }
</style>
