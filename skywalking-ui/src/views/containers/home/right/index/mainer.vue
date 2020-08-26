<template>
  <div class="content">
    <div class="section1">
      <div class="part">
        <div class="title">
          <span>监控总览</span>
        </div>
        <div class="chart1">
          <div class="ring" v-for="(item,index) in ringList" :key="index">
            <ring-chart :ringData="item"></ring-chart>
          </div>
        </div>
      </div>
      <div class="part">
        <div class="title">
          <span>设备状态</span>
          <div class="chart2">
            <device-state></device-state>
          </div>
        </div>
      </div>
    </div>
    <div class="partition1"></div>
    <div class="section2">
      <div class="part">
        <div class="title">
          <span>告警分布</span>
        </div>
        <div class="chart3">
          <pie-chart :peiData="piedata" :pieColor="piecolor"></pie-chart>
        </div>
      </div>
      <div class="part">
        <div class="title">
          <span>数据状态</span>
        </div>
        <div class="chart4">
          <data-state></data-state>
        </div>
      </div>
    </div>
    <div class="partition2"></div>
    <div class="section3">
      <div class="part">
        <div class="title">
          <span>未处理告警</span>
        </div>
        <div class="chart5">
          <alarm-Table></alarm-Table>
        </div>
      </div>
      <div class="part">
        <div class="title">
          <span>软件状态</span>
          <div class="lengend">
            <div class="item" v-for=" (item,index) in types" :key="index">
              <span class="color" :style="{'background':item.color}"></span>
              <span class="text">{{item.name}}</span>
            </div>
          </div>
        </div>
        <div class="chart6">
          <ware-State></ware-State>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ringChart from './chart/ringchart.vue';
import pieChart from './chart/pieshape.vue';
import alarmTable from './chart/alarmtable';
import deviceState from './chart/devicestate';
import dataState from './chart/datastate';
import wareState from './chart/warestate';
export default {
  data() {
    return {
      ringList: [
        { name: '链路设备', value: 25, total: 30, color1: '#C818F2', color2: '#0E6EBA', id: 'main0' },
        { name: '主机设备', value: 6, total: 10, color1: '#FFB20E', color2: '#FF7559', id: 'main1' },
        { name: '数据任务', value: 15, total: 40, color1: '#30D03C', color2: '#25BF0F', id: 'main2' },
        { name: '进程任务', value: 7, total: 11, color1: '#FA905A', color2: '#6F82FB', id: 'main3' },
      ],
      piedata: [
        { name: '链路设备', value: 10 },
        { name: '主设备', value: 8 },
        { name: '业务软件', value: 25 },
        { name: '业务数据', value: 15 },
      ],
      piecolor: ['#F97185', '#A051EA', '#F8D27E', '#42E4D5'],

      types: [
        //软件状态   图例制作数据
        { name: '未监控', color: '#0CB218', color1: '#01F21D' },
        { name: '不在线', color: '#0063C8', color1: '#00C9FD' },
        { name: '可能异常', color: '#5A15AB', color1: '#9620F3' },
        { name: '在线', color: '#159D9F', color1: '#0AEBF1' },
      ],
    };
  },
  components: { ringChart, pieChart, alarmTable, deviceState, dataState, wareState },
  computed: {
    /*  style(i) {
      return function() {
        return 'linear-gradient(to right,' + this.types[i].color + ',' + this.types[i].color1 + ')';
      };
    }, */
  },
};
</script>

<style lang="scss" scoped>
.content {
  padding: 0.5rem 0.375rem 0.375rem 0.2rem;
  // display: flex;
  // flex-direction: column;
  background: #eef5fd;
  width: 20rem;
  .section1 {
    margin-bottom: 0.5rem;
    background-color: #eef5fd;
    height: 2.75rem;
    display: flex;
    justify-content: space-between;
  }

  .section2 {
    margin-bottom: 0.25rem;
    background-color: #eef5fd;
    height: 3.75rem;
    display: flex;
    justify-content: space-between;
  }

  .section3 {
    background-color: #eef5fd;
    height: 4.375rem;
    display: flex;
    justify-content: space-between;
  }

  .part {
    width: 9.5875rem;
    background-color: white;
    box-shadow: 0.0625rem 0.0625rem 0.125rem #e2f8f8;
    .title {
      font-family: Georgia;
      font-weight: 600;
      height: 0.75rem;
      padding-left: 0.25rem;
      font-size: 0.25rem;
      border-bottom: solid 0.025rem #eef5fd;
      span:first-child {
        display: inline-block;
        line-height: 0.75rem;
      }
    }
    .chart1 {
      background-color: #eef5fd;
      height: 2rem;
      display: flex;
      justify-content: space-between;
      .ring {
        /*   height: 2rem;
        width: 2.378125rem;  */
        &:nth-child(-n + 3) {
          margin-right: 0.025rem;
        }
      }
    }

    .chart2 {
      height: 2.75rem;
      width: 9.5875rem;
    }

    .chart3 {
      height: 3rem;
      width: 9.5875rem;
    }

    .chart4 {
      height: 3rem;
      width: 9.5875rem;
    }

    .chart5 {
      height: 3.625rem;
      width: 9.5875rem;
    }

    .chart6 {
      height: 3.625rem;
      width: 9.5875rem;
    }
  }

  .lengend {
    margin-right: 0.25rem;
    float: right;
    display: flex;
    align-items: center;
    height: 0.75rem;
    .item {
      display: flex;
      align-items: center;
      height: 0.75rem;
      .color {
        margin-left: 0.2rem;
        width: 0.175rem;
        height: 0.175rem;
      }
      .text {
        margin-left: 0.0625rem;
        font-weight: 600;
        font-size: 0.175rem;
      }
    }
  }
}
</style>