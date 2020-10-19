<template>
  <div id="chart"></div>
</template>

<script>
import echarts from 'echarts';

import Icon from './iconBase64';
//import charts from '@/views/components/dashboard/charts';
export default {
  data() {
    return {
      charts: '',
    };
  },
  methods: {
    drawChart(id) {
      this.charts = echarts.init(document.getElementById(id));

      //X   32  Y16
      var nodes = [
        {
          x: '16',
          y: '16',
          name: 'switch1',
          img: Icon.switchIcon,
          alarm: '',
        },
        {
          x: '6',
          y: '11',
          name: 'switch2',
          img: Icon.switchIcon,
          alarm: '',
        },
        {
          x: '16',
          y: '11',
          name: 'switch3',
          img: Icon.switchIcon,
          alarm: '',
        },
        {
          x: '26',
          y: '11',
          name: 'switch4',
          img: Icon.switchIcon,
          alarm: '',
        },

        //switch2  下子节点
        {
          x: '3',
          y: '7',
          name: '接收报文',
          img: Icon.serveiceIcon,
          alarm: '',
        },

        {
          x: '6',
          y: '7',
          name: '35产品',
          img: Icon.fileIcon,
          alarm: '',
        },

        {
          x: '9',
          y: '7',
          name: 'KJ产品',
          img: Icon.fileIcon,
          alarm: '',
        },
        //swithc2 二级

        {
          x: '4',
          y: '2',
          name: 'IP地址1',
          img: Icon.computerIcon,
          alarm: '',
        },
        {
          x: '8',
          y: '2',
          name: 'IP地址2',
          img: Icon.computerIcon,
          alarm: '',
        },
        //switch3

        {
          x: '13',
          y: '7',
          name: '打印机',
          img: Icon.printIcon,
          alarm: '',
        },

        {
          x: '16',
          y: '7',
          name: '监视台',
          img: Icon.watchIcon,
          alarm: '',
        },

        {
          x: '19',
          y: '7',
          name: '业务',
          img: Icon.workIcon,
          alarm: '',
        },
        //swithc3 二级

        {
          x: '13',
          y: '2',
          name: 'IP地址3',
          img: Icon.computerIcon,
          alarm: '',
        },
        {
          x: '16',
          y: '2',
          name: 'IP地址4',
          img: Icon.computerIcon,
          alarm: '',
        },
        {
          x: '19',
          y: '2',
          name: 'IP地址5',
          img: Icon.computerIcon,
          alarm: '',
        },
        //switch4

        {
          x: '23',
          y: '7',
          name: 'hj产品',
          img: Icon.fileIcon,
          alarm: '',
        },

        {
          x: '26',
          y: '7',
          name: '打印机2',
          img: Icon.printIcon,
          alarm: '',
        },

        {
          x: '29',
          y: '7',
          name: '处理',
          img: Icon.manageIcon,
          alarm: '',
        },
        //swithc4 二级

        {
          x: '23',
          y: '2',
          name: 'IP地址6',
          img: Icon.computerIcon,
          alarm: '',
        },
        {
          x: '26',
          y: '2',
          name: 'IP地址7',
          img: Icon.computerIcon,
          alarm: '',
        },
        {
          x: '29',
          y: '2',
          name: 'IP地址8',
          img: Icon.computerIcon,
          alarm: '',
        },
      ];
      var links = [
        {
          source: 'switch1',
          target: 'switch2',
          name: '',
        },
        {
          source: 'switch1',
          target: 'switch3',
          name: '',
        },
        {
          source: 'switch1',
          target: 'switch4',
          name: '',
        },

        //switch2
        {
          source: 'switch2',
          target: '接收报文',
          name: '',
        },

        {
          source: 'switch2',
          target: '35产品',
          name: '',
        },

        {
          source: 'switch2',
          target: 'KJ产品',
          name: '',
        },
        //swithc2 二级
        {
          source: 'switch2',
          target: 'IP地址1',
          name: '',
        },

        {
          source: 'switch2',
          target: 'IP地址2',
          name: '',
        },

        //switch3
        {
          source: 'switch3',
          target: '打印机',
          name: '',
        },

        {
          source: 'switch3',
          target: '监视台',
          name: '',
        },

        {
          source: 'switch3',
          target: '业务',
          name: '',
        },

        //switch  二级

        {
          source: 'switch3',
          target: 'IP地址3',
          name: '',
        },

        {
          source: 'switch3',
          target: 'IP地址4',
          name: '',
        },

        {
          source: 'switch3',
          target: 'IP地址5',
          name: '',
        },
        //switch4
        {
          source: 'switch4',
          target: 'hj产品',
          name: '',
        },

        {
          source: 'switch4',
          target: '打印机2',
          name: '',
        },

        {
          source: 'switch4',
          target: '处理',
          name: '',
        },
        //switch 4 二级

        {
          source: 'switch4',
          target: 'IP地址6',
          name: '',
        },

        {
          source: 'switch4',
          target: 'IP地址7',
          name: '',
        },

        {
          source: 'switch4',
          target: 'IP地址8',
          name: '',
        },
      ];
      var charts = {
        nodes: [],
        links: [],
        linesData: [],
      };

      var dataMap = new Map();
      for (var j = 0; j < nodes.length; j++) {
        var x = parseInt(nodes[j].x);
        var y = parseInt(nodes[j].y);
        let symbolSize = nodes[j].img == Icon.switchIcon ? 40 : 25;
        var node = {
          name: nodes[j].name,
          value: [x, y],
          symbolSize: symbolSize,
          alarm: nodes[j].alarm,
          //symbol: 'image://static/toupu/' + nodes[j].img,
          symbol: nodes[j].img,

          /*    itemStyle: {
            normal: {
              color: '#12b5d0',
            },
          }, */
        };

        // dataMap.set(nodes[j].name, [x, y]);
        charts.nodes.push(node);
      }

      for (var i = 0; i < links.length; i++) {
        let color = links[i].target.indexOf('IP地址') > -1 ? '#428AFF' : '#52D7A4';
        var link = {
          source: links[i].source,
          target: links[i].target,
          label: {
            normal: {
              show: true,
              formatter: links[i].name,
            },
          },
          lineStyle: {
            normal: {
              width: 2,
              color: color,
              opacity: 1,
            },
          },
        };
        charts.links.push(link);
        // 组装动态移动的效果数据
        /*  var lines = [
          {
            coord: dataMap.get(links[i].source),
          },
          {
            coord: dataMap.get(links[i].target),
          },
        ];
        charts.linesData.push(lines); */
      }
      let option = {
        xAxis: {
          show: false,
          type: 'value',
        },
        yAxis: {
          show: false,
          type: 'value',
        },
        series: [
          {
            type: 'graph',
            layout: 'none',
            coordinateSystem: 'cartesian2d',
            edgeSymbol: ['', 'arrow'],
            symbolSize: 50,
            label: {
              normal: {
                show: true,
                position: 'bottom',
                color: '#353535',
              },
            },
            /*  lineStyle: {
              normal: {
                width: 2,
                shadowColor: 'none',
              },
            }, */
            edgeSymbolSize: 8,
            data: charts.nodes,
            links: charts.links,
            itemStyle: {
              normal: {
                label: {
                  show: true,
                  formatter: function(item) {
                    return item.data.name;
                  },
                },
              },
            },
          },
          {
            name: 'A',
            type: 'lines',
            coordinateSystem: 'cartesian2d',
            data: charts.linesData,
          },
        ],
      };

      this.charts.setOption(option);
    },
  },
  mounted() {
    this.$nextTick(() => {
      this.drawChart('chart');
    });

    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
};
</script>

<style lang="scss" scoped>
#chart {
  width: 12.875rem;
  height: 11.625rem;
}
</style>
