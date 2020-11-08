<template>
    <div class="content">

       <a-row >
            <a-col  :span="12" >
                <a-col  :span="10" style="height: 5rem">
                    <a-card  size="small" style="height: 2.25rem;margin-top: 0.2rem;margin-left: 0.15rem">
                        <a-row>当前故障设备与故障数量</a-row>
                        <a-row type="flex" justify="space-around"  align="middle" style="height: 1.3rem">
                                col-4
                        </a-row>
                    </a-card>
                    <a-card size="small"  style="height: 2.25rem;margin-top: 0.25rem;margin-left: 0.15rem">
                        <a-row>数据采集速率</a-row>
                        <a-row type="flex" justify="space-around"  align="middle" style="height: 1.3rem">
                            col-4
                        </a-row>
                    </a-card>
                </a-col>
                <a-col  :span="14" style="height: 5rem">
                    <a-card size="small" style="height: 4.75rem;margin-top: 0.2rem;margin-left: 0.15rem">
                        <div id="alarmLevel" style="height: 4.25rem"></div>
                    </a-card>
                </a-col>
                <a-col  :span="24" style="height: 4.25rem">
                    <a-card size="small"  style="height: 4.25rem;margin-top: 0.2rem;margin-left: 0.15rem" >
                        <div id="alarmTrend" style="height:  5.5rem;width: 110%;margin-top: -1rem"></div>
                    </a-card>
                </a-col>
            </a-col>
            <a-col  :span="12" style="height: 9.25rem">
                <a-card size="small"    style="height: 9.25rem;margin-top: 0.2rem;margin-left: 0.15rem;;margin-right: 0.15rem" >
                   <div style="margin-left: 1rem">
                       <a-timeline mode="left">
                           <a-timeline-item >Create a services site 2015-09-01</a-timeline-item>
                           <a-timeline-item color="green" >
                               <p style="margin-left: 0rem"> Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque
                                   laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto
                                   beatae vitae dicta sunt explicabo.</p>
                               Solve initial network problems 2015-09-01
                           </a-timeline-item>
                           <a-timeline-item>
                               <a-icon slot="dot" type="clock-circle-o" style="font-size: 16px;" />
                               <span style="margin-left: -1rem">111</span>
                               <span style="margin-left: 0.5rem"> Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque
                                   laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto
                                   beatae vitae dicta sunt explicabo.</span>

                           </a-timeline-item>
                           <a-timeline-item color="red">
                               Network problems being solved 2015-09-01
                           </a-timeline-item>
                           <a-timeline-item>Create a services site 2015-09-01</a-timeline-item>
                           <a-timeline-item>
                               <a-icon slot="dot" type="clock-circle-o" style="font-size: 16px;" />
                               Technical testing 2015-09-01
                           </a-timeline-item>
                       </a-timeline>
                   </div>

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
           let option = {
               xAxis: {
                   type: 'category',
                   boundaryGap: false,
                   data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
               },
               yAxis: {
                   type: 'value'
               },
               series: [{
                   data: [820, 932, 901, 934, 1290, 1330, 1320],
                   type: 'line',
                   areaStyle: {}
               }]
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
            background: white;
            //display: flex;
            overflow-y: auto;
            overflow-x: hidden;

        }

</style>

