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
        <vxe-table border ref="xTable" :height="tableheight" :data="tableData" stripe align="center">
          <vxe-table-column type="seq" title="序号"></vxe-table-column>
          <vxe-table-column field="taskName" title="设备名称"></vxe-table-column>
          <vxe-table-column field="IP" title="IP地址"></vxe-table-column>
          <vxe-table-column field="currentStatus" title="设备当前状态">
            <template v-slot="{ row }">
              <span> {{ statusFormat(currentStatusOptions, row.currentStatus) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="monitoringMethods" title="监控方式">
            <template v-slot="{ row }">
              <span> {{ statusFormat(monitoringMethodsOptions, row.monitoringMethods) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="maxUptime" title="在线时长" show-overflow></vxe-table-column>
          <vxe-table-column field="avgPacketPct" title="平均丢包率"></vxe-table-column>
          <vxe-table-column field="maxPacketPct" title="最大丢包率"></vxe-table-column>
          <vxe-table-column field="area" title="区域" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(areaOptions, row.area) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="location" title="地址" show-overflow></vxe-table-column>
          <vxe-table-column field="createTime" title="发现时间" show-overflow>
            <template v-slot="{ row }">
              <span> {{ parseTime(row.createTime) }}</span>
            </template></vxe-table-column
          >
        </vxe-table>

        <vxe-pager
          id="page_table"
          perfect
          :current-page.sync="page.currentPage"
          :page-size.sync="page.pageSize"
          :total="paginationTotal"
          :page-sizes="[10, 20, 100]"
          :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
          @page-change="handlePageChange"
        >
        </vxe-pager>
      </div>
    </div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  import { remFontSize } from '@/components/utils/fontSize.js';
  // 接口地址
  import services from '@/utils/services';
  export default {
    data() {
      return {
        page: {
          currentPage: 1,
          pageSize: 10,
        },
        Xdata: [],
        series: [],
        Ydata: [],
        charts: [],
        colors: ['#428AFF', '#6c50f3', '#00ca95'],
        chartType: ['bar', 'line', 'line'],
        name: ['在线时长', '平均丢包率', '最大丢包率'],

        currentStatusOptions: [], //设备当前状态
        monitoringMethodsOptions: [], //监控方式
        areaOptions: [], //区域
        queryParams: {
          pageNum: 1,
          pageSize: 10,
        }, //查询
        // 表格
        tableData: [],
        paginationTotal: 0,
        tableheight: null,
      };
    },
    async created() {
      services.getDicts('current_status').then((res) => {
        if (res.status == 200 && res.data.code == 200) {
          this.currentStatusOptions = res.data.data;
        }
      });
      services.getDicts('monitoring_methods').then((res) => {
        if (res.status == 200 && res.data.code == 200) {
          this.monitoringMethodsOptions = res.data.data;
        }
      });
      services.getDicts('media_area').then((res) => {
        if (res.status == 200 && res.data.code == 200) {
          this.areaOptions = res.data.data;
        }
      });
      await this.queryTable();

      let Ydata1 = [],
        Ydata2 = [],
        Ydata3 = [];
      this.tableData.forEach((item, index) => {
        this.Xdata.push(item.ip);
        Ydata1.push(item.maxUptime);
        Ydata2.push(item.avgPacketPct);
        Ydata3.push(item.maxPacketPct);
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
    mounted() {
      this.$nextTick(() => {
        this.drawChart('barlineChart');
        this.setTableHeight();
      });
      window.addEventListener('resize', () => {
        this.setTableHeight();
      });
    },
    methods: {
      /* table方法 */
      async queryTable() {
        this.queryParams.beginTime = '2020-10-17 00:00:00';
        this.queryParams.endTime = '2020-10-20 00:00:00';
        await services.reportList(this.queryParams).then((response) => {
          this.tableData = response.data.data.pageData;
          this.paginationTotal = response.data.data.totalCount;
        });
      },
      /* 字典格式化 */
      statusFormat(list, text) {
        return services.formatterselectDictLabel(list, text);
      },
      /* 翻页 */
      handlePageChange({ currentPage, pageSize }) {
        this.queryParams.pageNum = currentPage;
        this.queryParams.pageSize = pageSize;
        this.queryTable();
      },
      setTableHeight() {
        let h = document.getElementById('content').clientHeight;
        let padding = getComputedStyle(document.getElementById('content'), false)['paddingTop'];

        let h_report = document.getElementById('linkReport_chart').clientHeight;
        let barHeight = document.getElementById('toolbar').clientHeight;
        let h_page = document.getElementById('page_table').offsetHeight;

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
