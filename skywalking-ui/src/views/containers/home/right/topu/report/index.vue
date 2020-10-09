<template>
  <div id="report">
    <div id="content">
      <div id="chart">
        <div id="barlineChart"></div>
      </div>
      <div id="table">
        <div id="toolbar">
          <vxe-toolbar custom>
            <template v-slot:buttons>
              <vxe-button @click="exportEventXls">导出excel</vxe-button>
              <vxe-button @click="exportEventPdf">导出pdf</vxe-button>
            </template>
          </vxe-toolbar>
        </div>
        <!--   -->
        <vxe-table border ref="xTable" :height="tableheight" :data="tableData">
          <!--   :checkbox-config="{ labelField: 'number' }" -->
          <!--   <vxe-table-column type="checkbox" width="80" title="序号"></vxe-table-column> -->
          <vxe-table-column field="number" title="序号"></vxe-table-column>
          <vxe-table-column field="name" title="设备别名"></vxe-table-column>
          <vxe-table-column field="state" title="当前状态"></vxe-table-column>
          <vxe-table-column field="IP" title="IP地址"></vxe-table-column>
          <vxe-table-column field="monitMode" title="监视方式"></vxe-table-column>
          <vxe-table-column field="time" title="采集时间"></vxe-table-column>
          <vxe-table-column field="onlineTime" title="连续在线时长"></vxe-table-column>
          <vxe-table-column field="aveLost" title="平均丢包率"></vxe-table-column>
          <vxe-table-column field="maxLost" title="最大丢包率"></vxe-table-column>
          <vxe-table-column field="area" title="区域"></vxe-table-column>
          <vxe-table-column field="address" title="详细地址"></vxe-table-column>
        </vxe-table>
      </div>
    </div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  export default {
    data() {
      return {
        Xdata: [],
        series: [],
        Ydata: [],
        charts: [],
        colors: ['#428AFF', '#6c50f3', '#00ca95'],
        chartType: ['bar', 'line', 'line'],
        name: ['在线时长', '平均丢包率', '最大丢包率'],
        tableheight: 300,
        tableData: [
          {
            number: '1',
            name: 'Oracle数据库',
            state: '正常',
            IP: 'xxx.xxx.xxx.xx',
            monitMode: '代理',
            time: '2020/8/20 10:00',
            onlineTime: '2000小时',
            aveLost: '1.1',
            maxLost: '2.3',
            area: '机房',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
          },
          {
            number: '2',
            name: '报文接收器',
            state: '正常',
            IP: 'xxx.xxx.xxx.xx',
            monitMode: '代理',
            time: '2020/8/20 10:00',
            onlineTime: '2001小时',
            aveLost: '1.2',
            maxLost: '2.4',
            area: '机房',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
          },
          {
            number: '3',
            name: 'MySql数据库',
            state: '正常',
            IP: 'xxx.xxx.xxx.xx',
            monitMode: '代理',
            time: '2020/8/20 10:03',
            onlineTime: '2002小时',
            aveLost: '1.3',
            maxLost: '2.3',
            area: '机房',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
          },
          {
            number: '4',
            name: '监控台A',
            state: '正常',
            IP: 'xxx.xxx.xxx.xx',
            monitMode: 'snmp',
            time: '2020/8/20 10:20',
            onlineTime: '2300',
            aveLost: '1.4',
            maxLost: '2.3',
            area: '值班室',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
          },
          {
            number: '5',
            name: '监控台B',
            state: '正常',
            IP: 'xxx.xxx.xxx.xx',
            monitMode: 'snmp',
            time: '2020/8/20 10:10',
            onlineTime: '1300',
            aveLost: '1.5',
            maxLost: '2.5',
            area: '值班室',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
          },
          {
            number: '6',
            name: '监控台B',
            state: '正常',
            IP: 'xxx.xxx.xxx.xx',
            monitMode: 'snmp',
            time: '2020/8/20 10:10',
            onlineTime: '1500',
            aveLost: '1.5',
            maxLost: '2.5',
            area: '值班室',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
          },
        ],
      };
    },
    created() {
      let Ydata1 = [],
        Ydata2 = [],
        Ydata3 = [];
      this.tableData.forEach((item, index) => {
        this.Xdata.push(item.name);
        Ydata1.push(parseFloat(item.onlineTime));
        Ydata2.push(parseFloat(item.aveLost));
        Ydata3.push(parseFloat(item.maxLost));
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
    },
    methods: {
      setTableHeight() {
        let h = document.getElementById('content').clientHeight;
        let padding = getComputedStyle(document.getElementById('content'), false)['paddingTop'];
        let barHeight = document.getElementById('toolbar').clientHeight;
        this.tableheight = (h - 2 * parseInt(padding)) / 2 - 52;
      },
      drawChart(id) {
        this.charts = echarts.init(document.getElementById(id));
        let options = {
          title: {
            text: '全网链路运行情况表',
            left: 'center',
          },
          legend: {
            data: this.name,
            top: '10%',
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
            },
          ],
          yAxis: [
            {
              min: 0,
              max: 3000,
              interval: 500,
              type: 'value',
              name: '在线时长',
              axisLabel: {
                formatter: '{value} h',
              },
            },
            {
              type: 'value',
              name: '平均丢包率',
              min: 0,
              max: 3,
              interval: 0.5,
              axisLabel: {
                formatter: '{value}%',
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
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;

    background: #eef5fd;
    width: 20rem;
    #content {
      height: 11.625rem;
      width: 19.625rem;
      background: white;
      padding: 0.375rem;
      display: flex;
      flex-direction: column;
      #chart {
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
