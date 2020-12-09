<template>
  <div id="reportTemplate">
    <selectDate @changeDate="changeDate"></selectDate>
    <div class="tableDateBox">
      <div id="barlineChart"></div>
      <div id="toolbar">
        <vxe-toolbar custom>
          <template v-slot:buttons>
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

      <vxe-table border ref="xTable" :height="tableheight" :data="tableData" stripe align="center">
        <vxe-table-column field="hostName" title="设备别名"></vxe-table-column>
        <vxe-table-column field="currentStatus" title="当前状态" :formatter="formatCurrentStatus"> </vxe-table-column>
        <vxe-table-column field="ip" title="IP地址"></vxe-table-column>
        <vxe-table-column
          field="monitoringMethods"
          title="监视方式"
          :formatter="formatMonitoringMethods"
        ></vxe-table-column>
        <vxe-table-column field="createTime" title="采集时间" show-overflow>
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="maxUptime" title="连续在线时长" show-overflow></vxe-table-column>
        <vxe-table-column field="avgPacketPct" title="平均丢包率"></vxe-table-column>
        <vxe-table-column field="maxPacketPct" title="最大丢包率"></vxe-table-column>
        <vxe-table-column field="area" title="区域" :formatter="formatArea" show-overflow></vxe-table-column>
        <vxe-table-column field="location" title="详细地址" show-overflow></vxe-table-column>
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
        queryParams: {
          pageNum: 1,
          pageSize: 10000,
          deviceType: 1,
        },
        currentStatusOptions: [],
        monitoringMethodsOptions: [],
        areaOptions: [],
        Xdata: [],
        series: [],
        Ydata: [],
        charts: [],
        colors: ['#428AFF', '#6c50f3', '#00ca95'],
        chartType: ['bar', 'line', 'line'],
        name: ['在线时长', '平均丢包率', '最大丢包率'],
        tableheight: null,
        tableData: [],
        dateRange: [],
      };
    },
    components: { selectDate },
    created() {
      this.getDicts('current_status').then((response) => {
        this.currentStatusOptions = response.data;
      });
      this.getDicts('monitoring_methods').then((response) => {
        this.monitoringMethodsOptions = response.data;
      });
      this.getDicts('media_area').then((response) => {
        this.areaOptions = response.data;
      });
    },
    mounted() {
      //this.fetch();
      this.$nextTick(() => {});
      window.addEventListener('resize', () => {
        this.setTableHeight();
      });
    },
    methods: {
      exportEventPdf() {
        this.queryParams.alarmChart = this.getFullCanvasDataURL('barlineChart');
        let params = this.addDateRange(this.queryParams, this.dateRange);
        params.params = JSON.stringify(params.params);
        request({
          url: '/report/exportPdfLink',
          method: 'post',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          data: Qs.stringify(params),
          responseType: 'arraybuffer',
        }).then((res) => {
          this.downloadfileCommon(res);
        });
      },
      exportEventXls() {
        this.queryParams.alarmChart = this.getFullCanvasDataURL('barlineChart');
        let params = this.addDateRange(this.queryParams, this.dateRange);
        params.params = JSON.stringify(params.params);
        request({
          url: '/report/exportExcelLink',
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
      formatCurrentStatus({ cellValue }) {
        return this.selectDictLabel(this.currentStatusOptions, cellValue);
      },
      formatMonitoringMethods({ cellValue }) {
        return this.selectDictLabel(this.monitoringMethodsOptions, cellValue);
      },
      formatArea({ cellValue }) {
        return this.selectDictLabel(this.areaOptions, cellValue);
      },
      setTableHeight() {
        let h = document.getElementById('content').clientHeight;
        let padding = getComputedStyle(document.getElementById('content'), false)['paddingTop'];

        let h_report = document.getElementById('linkReport_chart').clientHeight;
        let barHeight = document.getElementById('toolbar').clientHeight;
        //let h_page = document.getElementById('page_table').offsetHeight;
        let h_page = 0;
        this.tableheight = h - h_report - barHeight - 2 * parseInt(padding) - h_page - 1;
      },
      drawChart(id) {
        this.charts = echarts.init(document.getElementById(id));
        let options = {
          textStyle: {
            fontFamily: 'Alibaba-PuHuiTi-Regular',
          },
          title: {
            text: '全网链路运行情况表',
            left: 'center',
            top: '10px',
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
        this.queryParams.alarmChart = '';
        hongtuConfig.reportList(this.addDateRange(this.queryParams, this.dateRange)).then((res) => {
          if (res.code == 200) {
            this.total = res.data.totalCount;
            this.tableData = res.data.pageData;
            this.chart();
            this.drawChart('barlineChart');
            this.setTableHeight();
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
          this.Xdata.push(item.ip);
          Ydata1.push(parseFloat(item.maxUptime));
          Ydata2.push(parseFloat(item.avgPacketPct));
          Ydata3.push(parseFloat(item.maxPacketPct));
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
  #reportTemplate {
    width: 100%;
    .tableDataBox {
      background: #fff;
    }
    #barlineChart {
      width: 900px;
      height: 400px;
      margin: auto;
    }
  }
</style>
