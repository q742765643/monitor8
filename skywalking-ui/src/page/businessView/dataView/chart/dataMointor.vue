<template>
  <div id="dataMointor">
    <commonTitle v-bind="$attrs"></commonTitle>
    <div class="dataView_chart" :id="chartID"></div>
  </div>
</template>

<script>
import commonTitle from '../../commonFull/commonTitle';
import echarts from 'echarts';
import { remFontSize } from '@/components/utils/fontSize.js';
export default {
  components: { commonTitle },
  props: ['chartID'],

  data() {
    return {
      sankeyChart: '',
    };
  },
  mounted() {
    this.$nextTick(() => {
      this.drawChart(this.chartID);
    });
    window.addEventListener('resize', () => {
      this.sankeyChart.resize();
    });
  },
  methods: {
    drawChart(id) {
      this.sankeyChart = echarts.init(document.getElementById(id));
      let options = {
        textStyle: {
          fontFamily: 'Alibaba-PuHuiTi-Regular',
        },
        series: {
          type: 'sankey',
          layout: 'none',
          nodeGap: 30,
          layoutIterations: 50,
          /*  nodeWidth: 30,
            nodeHeight: 30, */
          // focusNodeAdjacency: 'allEdges',
          nodeAlign: 'left',
          levels: [
            {
              depth: 0,
              itemStyle: {
                color: '#017FEE',
                borderColor: '#017FEE',
              },
              lineStyle: {
                color: 'source',
                opacity: 0.3,
                curveness: 0.5,
              },
            },
            {
              depth: 1,
              itemStyle: {
                color: '#FF972E',
                borderColor: '#FF972E',
              },
              lineStyle: {
                color: 'source',
                opacity: 0.3,
                curveness: 0.5,
              },
            },
            {
              depth: 2,
              itemStyle: {
                color: '#3ADF66',
                borderColor: '#3ADF66',
              },
            },
          ],
          data: [
            {
              name: 'a',
            },
            {
              name: 'b1',
            },
            {
              name: 'b2',
            },
            {
              name: 'b3',
            },

            {
              name: 'c1',
            },
            {
              name: 'c2',
            },
            {
              name: 'c3',
            },
            {
              name: 'c4',
            },
            {
              name: 'c5',
            },
            {
              name: 'c6',
            },
          ],
          links: [
            {
              source: 'a',
              target: 'b1',
              value: 5,
            },
            {
              source: 'a',
              target: 'b2',
              value: 5,
            },
            {
              source: 'a',
              target: 'b3',
              value: 5,
            },
            {
              source: 'b1',
              target: 'c1',
              value: 2.5,
            },
            {
              source: 'b1',
              target: 'c2',
              value: 2.5,
            },
            {
              source: 'b2',
              target: 'c3',
              value: 2.5,
            },
            {
              source: 'b2',
              target: 'c4',
              value: 2.5,
            },
            {
              source: 'b3',
              target: 'c5',
              value: 2.5,
            },
            {
              source: 'b3',
              target: 'c6',
              value: 2.5,
            },
          ],
        },
      };
      this.sankeyChart.setOption(options);
    },
  },
};
</script>

<style lang="scss" scoped>
#dataMointor {
  width: 100%;
  height: 5rem;
  background: white;

  .dataView_chart {
    width: 100%;
    height: calc(5rem - 0.75rem);
  }
}
</style>
