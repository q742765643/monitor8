<template>
  <div id="deviceStates">
    <!--   <div id="lemgend"></div> -->
    <div id="pieChart"></div>
    <div id="heatChart">
      <div
        class="box"
        v-for="(item,index) in showData"
        :style="{backgroundColor:item.color}"
        :key="index"
      >
        <div class="tooltip">
          <div class="text">
            <div class="column">
              <div class="cell">
                <span>
                  设备名称:
                  <i></i>
                </span>
                <span>{{item.name}}</span>
              </div>
              <div class="cell">
                <span>
                  设备IP:
                  <i></i>
                </span>
                <span>{{item.ip}}</span>
              </div>
              <div class="cell">
                <span>
                  设备状态:
                  <i></i>
                </span>
                <span>{{item.type}}</span>
              </div>
            </div>
          </div>
          <div class="arrow"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
export default {
  data() {
    return {
      heatData: [
        { id: '1', name: 'server1', ip: '192.168.0.1', type: '正常' },
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
        { id: '20', name: 'server10', ip: '192.168.0.1', type: '一般' },
      ],
      showData: [],
      pieChart: '',
      heatChart: '',
      color: ['#2BB9F7', '#FCAB13', '#FD651A', '#9C82ED'],
      type: ['正常', '一般', '严重', '断线'],
      data: [
        {
          name: '正常',
          value: 0,
          color: '#2BB9F7',
          check: true,
        },
        {
          name: '一般',
          value: 0,
          color: '#FCAB13',
          check: true,
        },
        {
          name: '严重',
          value: 0,
          color: '#FD651A',
          check: true,
        },
        {
          name: '断线',
          value: 0,
          color: '#9C82ED',
          check: true,
        },
      ],
    };
  },
  mounted() {
    this.heatData.forEach((item) => {
      this.data.forEach((data) => {
        if (item.type == data.name) {
          item.color = data.color;
        }
      });
    });

    this.heatData.forEach((item) => {
      this.data.forEach((data) => {
        if (item.type == data.name) {
          data.value++;
        }
      });
    });

    this.showData = JSON.parse(JSON.stringify(this.heatData));
    this.$nextTick(function() {
      this.drawchartRing('pieChart');
      //this.drawchartGrid('heatChart');
    });

    window.addEventListener('resize', () => {
      this.drawchartRing('pieChart');
      // this.drawchartGrid('heatChart');
    });
  },
  methods: {
    drawchartRing(id) {
      this.pieChart = echarts.init(document.getElementById(id));

      let data = this.data;

      let arrName = getArrayValue(data, 'name');
      let arrValue = getArrayValue(data, 'value');
      let sumValue = 10;
      let objData = array2obj(data, 'name');
      let optionData = getData(data);

      function getArrayValue(array, key) {
        var key = key || 'value';
        var res = [];
        if (array) {
          array.forEach(function(t) {
            res.push(t[key]);
          });
        }
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
              fontSize: 25,
              position: 'inside',
            },

            labelLine: {
              show: false,
            },
            data: [
              {
                value: data[i].value,
                name: data[i].name,
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
        legend: [
          {
            type: 'plain',
            orient: 'horizontal',
            width: 60,
            x: 20,
            y: 20,
            show: true,
            data: this.type /* ['正常', '一般', '严重', '断线'], */,
            textStyle: {
              color: '#353535',
              fontWeight: 600,
              fontSize: 10,
            },
            formatter: (name) => {
              let value;
              this.data.forEach((item) => {
                if (item.name == name) {
                  value = item.value;
                  debugger;
                }
              });
              return name + ':' + value;
            },
          },
        ],
        /* tooltip: {
          trigger: 'item',
          formatter: '{b} : {c}',
        }, */
        //color: this.color,
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
                fontSize: 25,
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
        if (item.name == type) {
          return (item.check = !item.check);
        }
      });

      this.showData = [];
      this.heatData.forEach((item) => {
        this.data.forEach((item1) => {
          if (item1.check && item1.name == item.type) {
            this.showData.push(item);
          }
        });
      });
    },
    drawchartGrid(id) {
      /* 
      this.heatChart = echarts.init(document.getElementById(id));

      var hours = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14];
      var days = ['we', 'df', 'gh', 'qw'];

      var data = [];
      for (let i = 0; i < 4; i++) {
        for (let j = 0; j < 14; j++) {
          let K = parseInt(Math.random() * 10) + 1;

          data.push([i, j, K]);
        }
      }

      data = data.map(function(item) {
        return [item[1], item[0], item[2] || '-'];
      });

      let option = {
        tooltip: {
          position: 'top',
        },
        animation: false,
        grid: {
          height: '80%',
          top: '10%',
        },
        xAxis: {
          show: false,
          axisLine: {
            lineStyle: {
              width: 0.5,
              color: ['#353535'],
            },
          },

          axisTick: {
            //y轴刻度线
            show: false,
          },
          splitLine: {
            // show: false,
            //网格线
            lineStyle: {
              color: ['#353535'],
              width: 0.5,
              type: 'solid',
            },

            show: true,
          },
          type: 'category',
          data: hours,
          splitArea: {
            show: false,
          },
        },
        yAxis: {
          show: false,
          axisLine: {
            lineStyle: {
              width: 0.5,
              color: ['#353535'],
            },
          },

          axisTick: {
            //y轴刻度线
            show: false,
          },
          splitLine: {
            //show: false,
            //网格线
            lineStyle: {
              color: ['#353535'],
              width: 0.5,
              type: 'solid',
            },
            show: true,
          },
          type: 'category',
          data: days,
          splitArea: {
            show: false,
          },
        },
        visualMap: {
          show: false,
          min: 0,
          max: 20,
          calculable: true,
          orient: 'horizontal',
          left: 'center',
          bottom: '15%',
          pieces: [
            {
              gt: 0,
              color: '#66CC66',
            },
            {
              value: 1,
              color: '#FF0000',
            },
            {
              value: 2,
              color: '#00FF00',
            },
          ],
        },
        series: [
          {
            type: 'heatmap',
            data: data,
            label: {
              show: false,
            },
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
              borderWidth: 1,
              borderColor: '#fff',
            },
          },
        ],
      };

      this.heatChart.setOption(option); */
      /* 改用dom操作来做 */
    },
  },
};
</script>

<style lang="scss" scoped>
#deviceStates {
  display: flex;
  width: 9.5875rem;
  height: 2rem;
  align-items: center;
  #pieChart {
    flex: 1;
    height: 2rem;
  }
  #heatChart {
    flex: 1;
    padding: 0 0.25rem;
    display: flex;
    //justify-content: center;
    align-content: flex-start;
    align-items: center;
    flex-wrap: wrap;
    .box {
      // display: inline-block;
      height: 0.3rem;
      width: 0.3rem;
      margin: 0.025rem;
      border-radius: 20%;

      &:hover .tooltip {
        display: block;
      }
    }
  }
}

.tooltip {
  display: none;
  // min-width: 1.5rem;
  padding: 0.125rem;
  border-radius: 0.0375rem;
  position: absolute;
  color: white;
  box-shadow: 0.0125rem 0.0125rem 0.125rem 0 #ccc;
  background: rgba($color: #353535, $alpha: 0.75);
  transform: translate(-35%, -100%);

  .text {
    font-family: sans-serif;
    font-size: 0.175rem;
    font-weight: 300;
    .column {
      // height: 4.875rem;
      display: flex;
      flex-direction: column;
      // justify-content: space-between;
      .cell {
        //flex: 1;
        display: flex;
        span:first-child {
          font-size: 0.1875rem;
          width: 1rem;
          display: inline-block;
          font-weight: 600;
          text-align: justify;
          i {
            /*  width: 50%; */
            display: inline-block;
          }
        }

        span:last-child {
          margin-left: 0.125rem;
        }
      }
    }
  }

  .arrow {
    position: absolute;
    margin: 0.125rem 0 0 0.625rem;
    width: 0;
    height: 0;
    border-left: 0.125rem solid transparent;
    border-right: 0.125rem solid transparent;
    border-top: 0.125rem solid rgba($color: #353535, $alpha: 0.75);
  }
}
</style>