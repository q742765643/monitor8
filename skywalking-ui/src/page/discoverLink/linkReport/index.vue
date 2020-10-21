<template>
  <div id="report">
    <div id="content">
      <div id="linkReport_chart">
        <div id="barlineChart"></div>
      </div>
      <div id="table">
        <div id="toolbar">
          <vxe-toolbar custom>
            <template v-slot:buttons>
              <!--   <vxe-button @click="exportEventXls">导出excel</vxe-button>
              <vxe-button @click="exportEventPdf">导出pdf</vxe-button> -->
              <a-button type="primary">
                导出excel
              </a-button>
              <a-button type="primary">
                导出pdf
              </a-button>
            </template>
          </vxe-toolbar>
        </div>
        <!--   -->
        <vxe-table
          border
          ref="xTable"
          :height="tableheight"
          :data="tableData"
          stripe
          align="center"
        >
          <!--   :checkbox-config="{ labelField: 'number' }" -->
          <!--   <vxe-table-column type="checkbox" width="80" title="序号"></vxe-table-column> -->
<!--          <vxe-table-column field="number" title="序号"></vxe-table-column>-->
          <vxe-table-column field="hostName" title="设备别名"></vxe-table-column>
          <vxe-table-column field="currentStatus" title="当前状态" :formatter="formatCurrentStatus">
          </vxe-table-column>
          <vxe-table-column field="ip" title="IP地址"></vxe-table-column>
          <vxe-table-column
            field="monitoringMethods"
            title="监视方式" :formatter="formatMonitoringMethods"
          ></vxe-table-column>
          <vxe-table-column
            field="createTime"
            title="采集时间"
            show-overflow
          >
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>

          </vxe-table-column>
          <vxe-table-column
            field="maxUptime"
            title="连续在线时长"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column
            field="avgPacketPct"
            title="平均丢包率"
          ></vxe-table-column>
          <vxe-table-column
            field="maxPacketPct"
            title="最大丢包率"
          ></vxe-table-column>
          <vxe-table-column
            field="area"
            title="区域" :formatter="formatArea"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column
            field="location"
            title="详细地址"
            show-overflow
          ></vxe-table-column>
        </vxe-table>

        <vxe-pager
                id="page_table"
                perfect
                :current-page.sync="queryParams.pageNum"
                :page-size.sync="queryParams.pageSize"
                :total="total"
                :page-sizes="[10, 20, 100]"
                @page-change="fetch"
                :layouts="[
              'PrevJump',
              'PrevPage',
              'Number',
              'NextPage',
              'NextJump',
              'Sizes',
              'FullJump',
              'Total',
            ]"
        >
        </vxe-pager>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
import { remFontSize } from '@/components/utils/fontSize.js';
import request from "@/utils/request";
export default {
  data() {
    return {
      total:0,
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      currentStatusOptions: [],
      monitoringMethodsOptions:[],
      areaOptions:[],
      Xdata: [],
      series: [],
      Ydata: [],
      charts: [],
      colors: ['#428AFF', '#6c50f3', '#00ca95'],
      chartType: ['bar', 'line', 'line'],
      name: ['在线时长', '平均丢包率', '最大丢包率'],
      tableheight: null,
      tableData:[],
    };
  },
  created() {
    this.getDicts("current_status").then(response => {
      this.currentStatusOptions = response.data;
    });
    this.getDicts("monitoring_methods").then(response => {
      this.monitoringMethodsOptions = response.data;
    });
    this.getDicts("media_area").then(response => {
      this.areaOptions = response.data;
    });

  },
  mounted() {
    this.fetch()
    this.$nextTick(() => {
    });
    window.addEventListener('resize', () => {
      this.setTableHeight();
    });
  },
  methods: {
    formatCurrentStatus({ cellValue }){
      return this.selectDictLabel(this.currentStatusOptions,cellValue);
    },
    formatMonitoringMethods({ cellValue }) {
      return this.selectDictLabel(this.monitoringMethodsOptions,cellValue);
    },
    formatArea({ cellValue }) {
      return this.selectDictLabel(this.areaOptions,cellValue);
    },
    setTableHeight() {
      let h = document.getElementById('content').clientHeight;
      let padding = getComputedStyle(document.getElementById('content'), false)[
        'paddingTop'
      ];

      let h_report = document.getElementById('linkReport_chart').clientHeight;
      let barHeight = document.getElementById('toolbar').clientHeight;
      let h_page = document.getElementById('page_table').offsetHeight;

      this.tableheight =
        h - h_report - barHeight - 2 * parseInt(padding) - h_page - 1;
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
    exportEventXls() {
      this.$refs.xTable.exportData({
        filename: '报表',
        sheetName: 'Sheet1',
        type: 'xlsx',
      });
    },
    exportEventPdf() {
      this.$refs.xTable.exportData({
        filename: '报表',
        type: 'pdf',
      });
    },
    fetch() {
      this.queryParams.beginTime="2020-10-17 00:00:00";
      this.queryParams.endTime="2020-10-20 00:00:00";
      request({
        url:'/report/list',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange)
      }).then(data => {
        this.total = data.data.totalCount;
        this.tableData = data.data.pageData;
        this.chart();
        this.drawChart('barlineChart');
        this.setTableHeight();
      });
    },
    chart(){
      let Ydata1 = [],
              Ydata2 = [],
              Ydata3 = [];
      this.Xdata=[];
      this.Ydata=[];
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
#report {
  background: #eef5fd;
  //width: 20rem;
  width: 100%;
  #content {
    box-shadow: $plane_shadow;
    height: 11.625rem;
    //width: 19.625rem;
    width: 100%;
    background: white;
    padding: 0.25rem;
    display: flex;
    flex-direction: column;
    #linkReport_chart {
      margin: 0 auto;
      width: 70%;
      height: 50%;
      //background: pink;
      display: flex;
      align-items: center;
      justify-content: center;
      #barlineChart {
        width: 100%;
        height: 100%;
      }
    }
    #table {
      width: 100%;
      height: 50%;
    }
  }
}
</style>
