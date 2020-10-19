<template>
    <div id="report">
        <div id="content">
            <div id="chart">
                <div id="barlineChart"></div>
            </div>
            <!--<div id="table">
              <div id="toolbar">
                &lt;!&ndash; <vxe-toolbar custom>
                  <template v-slot:buttons>
                    <vxe-button @click="exportEventXls">导出excel</vxe-button>
                    <vxe-button @click="exportEventPdf">导出pdf</vxe-button>
                  </template>
                </vxe-toolbar> &ndash;&gt;
              </div>
              &lt;!&ndash;   &ndash;&gt;
              <vxe-table border ref="xTable" :height="tableheight" :data="tableData">
                &lt;!&ndash;   :checkbox-config="{ labelField: 'number' }" &ndash;&gt;
                &lt;!&ndash;   <vxe-table-column type="checkbox" width="80" title="序号"></vxe-table-column> &ndash;&gt;
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
            </div>-->
            <a-row type="flex">
                <a-table  :row-selection="rowSelection"
                          :row-key="record => record.id"
                          :columns="columns"
                          :data-source="data"
                          :pagination="pagination"
                          :loading="loading"
                          size="small"
                          @change="handleTableChange"
                          :rowClassName="fnRowClass"
                          :scroll="{ x: 'calc(1200px + 50%)'}"

                >
                <span slot="currentStatus" slot-scope="text">
                 {{statusFormat(currentStatusOptions,text)}}
                </span>
                <span slot="monitoringMethods" slot-scope="text">
                 {{statusFormat(monitoringMethodsOptions,text)}}
                </span>
                <span slot="area" slot-scope="text">
                  {{statusFormat(areaOptions,text)}}
                </span>
                <span slot="createTime" slot-scope="text">
                 {{ parseTime(text) }}
                </span>
                </a-table>
            </a-row>
        </div>
    </div>
</template>

<script>
    import echarts from 'echarts';
    import request from "@/utils/request";
    const columns = [
        {
            title: '设备名称',
            dataIndex: 'taskName',
            width: '10%',
        },
        {
            title: 'ip地址',
            dataIndex: 'ip',
            width: '10%',
        },
        {
            title: '设备当前状态',
            dataIndex: 'currentStatus',
            width: '10%',
            scopedSlots: { customRender: 'currentStatus' },
        },
        {
            title: '监控方式',
            dataIndex: 'monitoringMethods',
            width: '10%',
            scopedSlots: { customRender: 'monitoringMethods' },
        },
        {
            title: '在线时长',
            dataIndex: 'maxUptime',
            width: '10%'
        },
        {
            title: '平均丢包率',
            dataIndex: 'avgPacketPct',
            width: '10%'
        },
        {
            title: '最大丢包率',
            dataIndex: 'maxPacketPct',
            width: '10%'
        },
        {
            title: '区域',
            dataIndex: 'area',
            width: '10%',
            scopedSlots: { customRender: 'area' },
        },
        {
            title: '地址',
            dataIndex: 'location',
            width: '15%',
        },
        {
            title: '发现时间',
            dataIndex: 'createTime',
            width: '200px',
            scopedSlots: { customRender: 'createTime' },
        }
    ];
    export default {
        data() {
            return {
                data: [],
                pagination: {},
                loading: false,
                columns,
                // 日期范围
                dateRange: [],
                // 查询参数
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
            };
        },
        computed: {
            rowSelection() {
                return {
                    onChange: (selectedRowKeys, selectedRows) => {
                        this.ids = selectedRows.map(item => item.id);
                        this.ips = selectedRows.map(item => item.ip);
                    },
                    getCheckboxProps: record => ({
                        props: {
                            disabled: record.id === 'Disabled User', // Column configuration not to be checked
                            name: record.id,
                        },
                    }),
                };
            },
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
            this.fetch();
        },
        methods: {
            statusFormat(options,status) {
                return this.selectDictLabel(options,status);
            },
            toChart(){
                let Ydata1 = [],
                    Ydata2 = [],
                    Ydata3 = [];
                console.log(this.data)
                this.data.forEach((item, index) => {
                    console.log(item)
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
                this.$nextTick(() => {
                    this.drawChart('barlineChart');
                });
            },
            fetch() {
                this.loading = true;
                this.queryParams.beginTime="2020-10-17 00:00:00";
                this.queryParams.endTime="2020-10-20 00:00:00";
                request({
                    url:'/report/list',
                    method: 'get',
                    params: this.addDateRange(this.queryParams, this.dateRange)
                }).then(data => {
                    const pagination = { ...this.pagination };
                    pagination.total = data.data.totalCount;
                    this.loading = false;
                    this.data = data.data.pageData;
                    this.toChart();
                    this.pagination = pagination;
                });
            },
            fnRowClass(record,index){
                return  "csbsTypes"
            },
            handleTableChange(pagination, filters, sorter) {
                const pager = { ...this.pagination };
                this.queryParams.pageNum = pagination.current;
                this.pagination = pager;
                this.fetch();
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
                            max: 100,
                            interval: 10,
                            axisLabel: {
                                formatter: '{value}%',
                            },
                        },
                    ],
                    series: this.series,
                };

                this.charts.setOption(options);
            },
            /*  exportEventXls() {
              this.$refs.xTable.exportData({
                filename: '报表',
                sheetName: 'Sheet1',
                type: 'xlsx',
              });
            }, */
            /*  exportEventPdf() {
              this.$refs.xTable.exportData({
                filename: '报表',
                type: 'pdf',
              });
            }, */
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
    .csbsTypes{
        font-size: 14px;
    }
    .ant-table-thead > tr > th {
        font-size: 15px;
    }
</style>
