<template>
  <div id="left">
    <div id="treeChart"></div>
    <div id="legend">
      <span class="titile">图例</span>
      <div>
        <span class="lgd1"></span>
        <span>值班区</span>
      </div>
      <div>
        <span class="lgd2"></span>
        <span>办公区4楼</span>
      </div>
      <div>
        <span class="lgd2"></span>
        <span>办公区3楼</span>
      </div>
      <div>
        <span class="lgd2"></span>
        <span>机房区</span>
      </div>
      <div>
        <span class="lgd2"></span>
        <span>其他区</span>
      </div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';
import Icon from './iconBase64';
export default {
  data() {
    return {
      tcharts: '',
      treeData: [
        {
          name: 'switch1',
          symbol: Icon.switchIcon,
          lineStyle: {
            color: '#53D7A4',
          },
          children: [
            {
              name: '交换机1',
              symbol: Icon.switchIcon,
              lineStyle: {
                color: '#53D7A4',
              },
              children: [
                {
                  name: '监视台',
                  symbol: Icon.watchIcon,
                  label: { backgroundColor: '#EDFBED', borderColor: '#01A1FF' },
                },
                {
                  name: '监视台',
                  symbol: Icon.watchIcon,
                  label: { backgroundColor: '#EDFBED', borderColor: '#01A1FF' },
                },
              ],
            },
            {
              name: '交换机2',
              symbol: Icon.switchIcon,
              lineStyle: {
                color: '#01A1FF',
              },
              children: [
                {
                  symbolSize: 0,
                  lineStyle: {
                    color: '#01A1FF',
                  },
                  children: [
                    {
                      name: '办公',
                      symbol: Icon.computerIcon,
                      label: { backgroundColor: '#EEEDFB', borderColor: '#01A1FF' },
                    },
                    {
                      name: '办公',
                      symbol: Icon.computerIcon,
                      label: { backgroundColor: '#EEEDFB', borderColor: '#01A1FF' },
                    },
                  ],
                },
              ],
            },
            {
              name: '交换机3',
              symbol: Icon.switchIcon,
              lineStyle: {
                color: '#53D7A4',
              },
              children: [
                {
                  name: '办公',
                  symbol: Icon.computerIcon,
                  label: { backgroundColor: '#FBFDEF', borderColor: '#01A1FF' },
                },
                {
                  name: '办公',
                  symbol: Icon.computerIcon,
                  label: { backgroundColor: '#FBFDEF', borderColor: '#01A1FF' },
                },
              ],
            },
            {
              name: '交换机4',
              symbol: Icon.switchIcon,
              lineStyle: {
                color: '#01A1FF',
              },
              children: [
                {
                  symbolSize: 0,
                  lineStyle: {
                    color: '#01A1FF',
                  },
                  children: [
                    {
                      name: '服务器',
                      symbol: Icon.serveiceIcon,
                      label: { backgroundColor: '#FCF3EE', borderColor: '#01A1FF' },
                    },
                    {
                      name: '服务器',
                      symbol: Icon.serveiceIcon,
                      label: { backgroundColor: '#FCF3EE', borderColor: '#01A1FF' },
                    },
                  ],
                },
              ],
            },
            {
              name: '交换机5',
              symbol: Icon.switchIcon,
              lineStyle: {
                color: '#53D7A4',
              },
              children: [
                {
                  name: '办公',
                  symbol: Icon.computerIcon,
                  label: { backgroundColor: '#FBEDFC', borderColor: '#01A1FF' },
                },
                {
                  name: '办公',
                  symbol: Icon.computerIcon,
                  label: { backgroundColor: '#FBEDFC', borderColor: '#01A1FF' },
                },
              ],
            },
          ],
        },
      ],
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.drawTree('treeChart');
    });

    window.addEventListener('resize', () => {
      this.charts.resize();
    });
  },
  methods: {
    drawTree(id) {
      this.tcharts = echarts.init(document.getElementById(id));
      let option = {
        type: 'tree',
        backgroundColor: '#fff',
        /*  tooltip: {
          trigger: 'item',
          triggerOn: 'mousemove',
          backgroundColor: 'rgba(1,70,86,1)',
          borderColor: 'rgba(0,246,255,1)',
          borderWidth: 0.5,
          textStyle: {
            fontSize: 10,
          },
        }, */
        series: [
          {
            type: 'tree',
            hoverAnimation: true, //hover样式
            edgeShape: 'polyline',
            data: this.treeData,
            top: 80,
            bottom: 80,
            left: 0,
            right: 0,
            layout: 'orthogonal',
            orient: 'TB',
            symbolSize: 60,

            animationDuration: 0,
            animationDurationUpdate: 100,
            expandAndCollapse: true, //子树折叠和展开的交互，默认打开

            initialTreeDepth: 10,
            lineStyle: { width: 2 },
            label: {
              color: '#000',
              fontSize: 12,
              rotate: 0,
              position: 'insideBottom',
              padding: [5, 10, 5, 10],
              borderWidth: 0.5,
            },
          },
        ],
      };

      this.tcharts.setOption(option);
      this.tcharts.on('click', (e) => {
        console.log(e.name);
      });
    },
  },
};
</script>

<style lang="scss" scoped>
#left {
  position: relative;
  width: 12.875rem;
  height: 11.625rem;
}
#treeChart {
  width: 12.875rem;
  height: 11.625rem;
}

#legend {
  z-index: 500;
  position: absolute;
  left: 0.25rem;
  bottom: 0.25rem;
  width: 1.875rem;
  box-shadow: 0.0125rem 0.0125rem 0.125rem 0 #ccc;

  div {
    display: flex;
    justify-content: center;
    align-items: center;

    .lgd1 {
      width: 0.625rem;
      background-color: #edfbed;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd2 {
      width: 0.625rem;
      background-color: #eeedfb;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd3 {
      width: 0.625rem;
      background-color: #fbfdef;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd4 {
      width: 0.625rem;
      background-color: #fcf3ee;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd5 {
      width: 0.625rem;
      background-color: #a183a3;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }
    span:nth-child(2) {
      margin-left: 0.0625rem;
      flex: 2;
      display: block;
      line-height: 0.4rem;
    }
    span:nth-child(1) {
      margin-left: 0.0625rem;
      flex: 1;
      display: block;
      line-height: 0.4rem;
    }
  }
  .titile {
    margin-left: 0.125rem;
    font-size: 0.2rem;
    font-weight: 600;
  }
}
</style>