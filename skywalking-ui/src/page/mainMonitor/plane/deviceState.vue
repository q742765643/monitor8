<template>
  <div id="deviceState">
    <div><planeTitle titleName="设备状态"></planeTitle></div>
    <div id="device_contnet">
      <div id="device_pieChart"></div>
      <div id="heatChart">
        <div class="box" v-for="(item, index) in showData" :style="{ backgroundColor: item.color }" :key="index">
          <div class="tooltip">
            <div class="text">
              <div class="column">
                <div class="cell">
                  <span>
                    设备名称:
                    <i></i>
                  </span>
                  <span>{{ item.name }}</span>
                </div>
                <div class="cell">
                  <span>
                    设备IP:
                    <i></i>
                  </span>
                  <span>{{ item.ip }}</span>
                </div>
                <div class="cell">
                  <span>
                    设备状态:
                    <i></i>
                  </span>
                  <span>{{ item.currentStatus }}</span>
                </div>
              </div>
            </div>
            <div class="arrow"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import planeTitle from '@/components/titile/planeTitle.vue';
import echarts from 'echarts';
import { remFontSize } from '@/components/utils/fontSize.js';
// 接口地址
import hongtuConfig from '@/utils/services';
export default {
  name: 'deviceState',
  components: { planeTitle },
  data() {
    return {
      heatData: [
        /*  { id: '1', name: 'server1', ip: '192.168.0.1', type: '正常' },
        { id: '2', name: 'server2', ip: '192.168.0.1', type: '严重' },
        { id: '3', name: 'server3', ip: '192.168.0.1', type: '一般' },
        { id: '4', name: 'server4', ip: '192.168.0.1', type: '断线' },
        { id: '5', name: 'server5', ip: '192.168.0.1', type: '正常' },
        { id: '6', name: 'server6', ip: '192.168.0.1', type: '正常' },
        { id: '7', name: 'server7', ip: '192.168.0.1', type: '严重' },
        { id: '8', name: 'server8', ip: '192.168.0.1', type: '严重' },
        { id: '9', name: 'server9', ip: '192.168.0.1', type: '一般' },
        { id: '10', name: 'server10', ip: '192.168.0.1', type: '一般' },
        { id: '11', name: 'server1', ip: '192.168.0.1', type: '一般' },
        { id: '12', name: 'server2', ip: '192.168.0.1', type: '正常' },
        { id: '13', name: 'server3', ip: '192.168.0.1', type: '正常' },
        { id: '14', name: 'server4', ip: '192.168.0.1', type: '正常' },
        { id: '15', name: 'server5', ip: '192.168.0.1', type: '一般' },
        { id: '16', name: 'server6', ip: '192.168.0.1', type: '严重' },
        { id: '17', name: 'server7', ip: '192.168.0.1', type: '断线' },
        { id: '18', name: 'server8', ip: '192.168.0.1', type: '正常' },
        { id: '19', name: 'server9', ip: '192.168.0.1', type: '正常' },
        { id: '20', name: 'server10', ip: '192.168.0.1', type: '一般' }, */
      ],
      showData: [],
      pieChart: '',
      heatChart: '',
      /*   color: ['#2BB9F7', '#FCAB13', '#FD651A', '#9C82ED'],*/
      //type: ['一般', '危险', '故障', '正常'],// 0 一般 1 危险 2故障 3正常
      //0 服务器 1网络设备 2进程 3文件
      type: [],
      color: [],
      data: [
        {
          status: '一般',
          value: 0,
          color: '#fcab13',
          // color: '#fcff13',
          check: true,
        },
        {
          status: '危险',
          value: 0,
          color: '#DAA520',
          check: true,
        },
        {
          status: '故障',
          value: 0,
          color: 'red',
          check: true,
        },
        {
          status: '正常',
          value: 0,
          color: '#0cb218',
          check: true,
        },
      ],
    };
  },
  async created() {
    this.type = this.data.map((item) => item.status);
    this.color = this.data.map((item) => item.color);

    await hongtuConfig.getDeviceStatus().then((res) => {
      if (res.code == 200) {
        this.heatData = res.data;
        this.initData();
      }
    });
    //this.heatData = [];
    //this.initData();
  },
  mounted() {
    window.addEventListener('resize', () => {
      //this.drawchartRing("device_pieChart");
      // this.drawchartGrid('heatChart');
      setTimeout(() => {
        this.pieChart.resize();
      }, 500);
    });
  },
  methods: {
    initData() {
      this.heatData.forEach((item) => {
        //console.log(item)
        //item.currentStatus = this.data[Math.floor(Math.random() * (0 - 4)) + 4].status;
        this.data.forEach((item2, index) => {
          if (item.currentStatus == item2.status) {
            item.color = item2.color;
            item2.value++;
          }
        });
      });

      this.showData = JSON.parse(JSON.stringify(this.heatData));
      for (let index = 0; index < this.showData.length; index++) {
        console.log('render', this.showData[index].currentStatus + ':' + this.showData[index].color);
      }

      this.$nextTick(function () {
        this.drawchartRing('device_pieChart');
      });
    },
    drawchartRing(id) {
      this.pieChart = echarts.init(document.getElementById(id));
      let data = this.data;
      let arrName = getArrayValue(data, 'status');
      let arrValue = getArrayValue(data, 'value');
      let sumValue = this.heatData.length;
      let objData = array2obj(data, 'status');
      let optionData = getData(data);
      function getArrayValue(array, key) {
        var key = key || 'value';
        var res = [];
        if (array) {
          array.forEach(function (t) {
            res.push(t[key]);
          });
        }
        res;
        return res;
      }

      function array2obj(array, key) {
        var resObj = {};
        for (var i = 0; i < array.length; i++) {
          resObj[array[i][key]] = array[i];
        }
        return resObj;
      }

      function getData(data) {
        var res = {
          series: [],
          yAxis: [],
        };
        for (let i = 0; i < data.length; i++) {
          res.series.push({
            type: 'pie',
            clockWise: true,
            z: 2,
            hoverAnimation: false,
            color: data[i].color,
            radius: [90 - i * 16 + '%', 77 - i * 16 + '%'],
            center: ['70%', '50%'],
            label: {
              show: false,
              formatter: '{d}%',
              color: 'RGB(246,175,101)',
              fontSize: remFontSize(25 / 64),
              position: 'inside',
            },

            labelLine: {
              show: false,
            },
            data: [
              {
                value: data[i].value,
                name: data[i].status,
              },
              {
                value: sumValue - data[i].value,
                name: '',
                itemStyle: {
                  color: 'rgba(0,0,0,0)',
                  borderWidth: 0,
                },
                hoverAnimation: false,
              },
            ],
          });
        }
        return res;
      }

      let option = {
        textStyle: {
          fontFamily: 'Alibaba-PuHuiTi-Regular',
        },
        //添加圆圈里面总数
        title: {
          text: this.showData.length,
          left: '65%',
          top: '40%',
        },
        legend: [
          {
            type: 'plain',
            orient: 'horizontal',
            width: remFontSize(60 / 64),
            x: remFontSize(20 / 64),
            y: 4,
            show: true,
            data: this.type /* ['正常', '一般', '严重', '断线'], */,
            textStyle: {
              color: '#353535',
              fontWeight: 600,
              fontSize: remFontSize(11 / 64),
            },
            formatter: (name) => {
              let value;
              this.data.forEach((item) => {
                if (item.status == name) {
                  value = item.value;
                }
              });
              return name + ':' + value;
            },
          },
        ],
        grid: {
          top: '16%',
          bottom: '54%',
          left: '50%',
          containLabel: false,
        },
        yAxis: [
          {
            type: 'category',
            inverse: true,
            z: 3,
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              interval: 0,
              inside: false,
              textStyle: {
                color: 'RGB(78,184,252)',
                fontSize: remFontSize(25 / 64),
              },
              show: true,
            },
            data: optionData.yAxis,
          },
        ],
        xAxis: [
          {
            show: false,
          },
        ],
        series: optionData.series,
      };
      this.pieChart.setOption(option);
      this.pieChart.on('legendselectchanged', (param) => {
        this.showType(param.name);
      });
    },
    showType(type) {
      this.data.map((item) => {
        if (item.status == type) {
          return (item.check = !item.check);
        }
      });

      this.showData = [];
      this.heatData.forEach((item) => {
        this.data.forEach((item1) => {
          if (item1.check && item1.status == item.currentStatus) {
            this.showData.push(item);
          }
        });
      });
    },
  },
};
</script>

<style lang="scss" scoped>
#deviceState {
  height: 28%;
  width: 100%;
  box-shadow: $plane_shadow;

  #device_contnet {
    width: 100%;
    height: calc(100% - 56px);
    display: flex;
    align-items: center;
    padding-top: 20px;
    #device_pieChart {
      height: 100%;
      flex: 1;
    }
    #heatChart {
      height: 100%;
      flex: 1;
      padding: 0 20px;
      display: flex;
      align-content: flex-start;
      align-items: center;
      flex-wrap: wrap;
      .box {
        height: 24px;
        width: 24px;
        margin: 2px;
        border-radius: 20%;
        &:hover .tooltip {
          display: block;
        }
      }
    }
  }
}

.tooltip {
  display: none;
  padding: 10px;
  border-radius: 4px;
  position: absolute;
  color: white;
  background: rgba($color: #353535, $alpha: 0.75);
  transform: translate(-29%, -112%);

  .text {
    font-size: $ant_font_size;
    font-weight: 300;
    .column {
      display: flex;
      flex-direction: column;
      .cell {
        display: flex;
        span:first-child {
          font-size: $ant_font_size;
          width: 80px;
          display: inline-block;
          font-weight: 600;
          text-align: justify;
          i {
            display: inline-block;
          }
        }

        span:last-child {
          margin-left: 10px;
        }
      }
    }
  }

  .arrow {
    position: absolute;
    margin: 10px 0 0 50px;
    width: 0;
    height: 0;
    border-left: 10px solid transparent;
    border-right: 10px solid transparent;
    border-top: 12px solid rgba($color: #353535, $alpha: 0.75);
  }
}
</style>
