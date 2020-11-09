<template>
    <div class="content">

       <a-row >
            <a-col  :span="14" >
                <a-col  :span="10" style="height: 4.45rem">
                    <a-card  size="small"  style="height: 4.25rem">
                        <a-row>当前故障设备与故障数量</a-row>
                        <a-row type="flex" justify="space-around"  align="middle" style="height: 3.7rem" >
                                col-4
                        </a-row>
                    </a-card>
                </a-col>
                <a-col  :span="14" style="height: 4.25rem">
                    <a-card size="small" style="height: 4.25rem;margin-left: 0.15rem">
                        <div id="alarmLevel" style="height: 4.25rem"></div>
                    </a-card>
                </a-col>
                <a-col  :span="24" style="height:  5rem" >
                    <a-card size="small"  >
                        <div id="alarmTrend" style="height:  5rem"></div>
                    </a-card>
                </a-col>
            </a-col>
            <a-col  :span="10" style="height: 10rem">
                <a-card size="small"    style="height: 10rem;margin-left: 0.15rem;;margin-right: 0.15rem" >
                    <el-timeline>
                        <el-timeline-item
                                v-for="(activity, index) in activities"
                                :key="index"
                                :icon="activity.icon"
                                :type="activity.type"
                                :color="activity.color"
                                :size="activity.size"
                                :timestamp="activity.timestamp">
                            {{activity.content}}
                        </el-timeline-item>
                    </el-timeline>

                </a-card>
            </a-col>
        </a-row>
    </div>
</template>
<script>
import echarts from 'echarts';
import request from "@/utils/request";
export default {
    data() {
        return {
            alarmLevelChart:'',
            activities: [{
                content: '支持使用图标',
                timestamp: '2018-04-12 20:46',
                size: 'large',
                type: 'primary',
                icon: 'el-icon-more'
            }, {
                content: '支持自定义颜色',
                timestamp: '2018-04-03 20:46',
                color: '#0bbd87'
            }, {
                content: '支持自定义尺寸',
                timestamp: '2018-04-03 20:46',
                size: 'large'
            }, {
                content: '默认样式的节点',
                timestamp: '2018-04-03 20:46'
            }]
        };
    },
    mounted(){
        this.$nextTick(function() {
            this.drawAlarmLevel();
            this.drawAlarmTrend();
        })
        let _this=this;
        window.addEventListener("resize",function () {
            _this.alarmLevelChart.resize();
        })
    },
    methods: {
        drawAlarmTrend(){
           let drawAlarmTrendChart=echarts.init(document.getElementById('alarmTrend'));
            var base = new Date(1968, 9, 3);
            var oneDay = 24 * 3600 * 1000;
            var date = [];

            var data = [Math.random() * 300];

            for (var i = 1; i < 20000; i++) {
                var now = new Date(base += oneDay);
                date.push([now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/'));
                data.push(Math.round((Math.random() - 0.5) * 20 + data[i - 1]));
            }

            let option = {
                tooltip: {
                    trigger: 'axis',
                    position: function (pt) {
                        return [pt[0], '10%'];
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '2%',
                    top:'3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: date
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%']
                },
                series: [
                    {
                        name: '模拟数据',
                        type: 'line',
                        smooth: true,
                        symbol: 'none',
                        sampling: 'average',
                        itemStyle: {
                            color: 'rgb(255, 70, 131)'
                        },
                        areaStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                offset: 0,
                                color: 'rgb(255, 158, 68)'
                            }, {
                                offset: 1,
                                color: 'rgb(255, 70, 131)'
                            }])
                        },
                        data: data
                    }
                ]
            };
           drawAlarmTrendChart.setOption(option);
           //window.onresize =  drawAlarmTrendChart.resize;
        },
        drawAlarmLevel(){
            // 基于准备好的dom，初始化echarts实例
            this.alarmLevelChart = echarts.init(document.getElementById('alarmLevel'))
            // 绘制图表
            let option = {
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b}: {c} ({d}%)'
                },
                legend: {
                    orient: 'vertical',
                    left: 10,
                    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎']
                },
                series: [
                    {
                        name: '访问来源',
                        type: 'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        label: {
                            show: false,
                            position: 'center'
                        },
                        emphasis: {
                            label: {
                                show: true,
                                fontSize: '30',
                                fontWeight: 'bold'
                            }
                        },
                        labelLine: {
                            show: false
                        },
                        data: [
                            {value: 335, name: '直接访问'},
                            {value: 310, name: '邮件营销'},
                            {value: 234, name: '联盟广告'},
                            {value: 135, name: '视频广告'},
                            {value: 1548, name: '搜索引擎'}
                        ]
                    }
                ]
            };
            this.alarmLevelChart.setOption(option);
            //window.onresize =  this.alarmLevelChart.resize;
            //window.onresize =  this.alarmLevelChart.resize;
        }
    },
}
</script>
<style lang="scss" scoped>
    .content {
            font-family: Alibaba-PuHuiTi-Regular;
            box-shadow: $plane_shadow;
            width: 100%;
            height:100%;
            background-color: #f0f2f7;
            //display: flex;
            overflow-y: hidden;
            overflow-x: hidden;

        }

    ::v-deep .el-timeline-item__timestamp.is-bottom {
        position: absolute;
        left: -117px;
        top: -3px;
        color: #333333;
    }
    ::v-deep .el-timeline {
        padding-left: 120px;
    }
</style>

