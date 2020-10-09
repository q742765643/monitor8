<template>
  <div class="managerTemplate">
    <div class="head">
      <div class="rightBox">
        <input type="text" v-model="querform.toolName" placeholder="设备别名" />
        <select v-model="querform.toolStatus" placeholder="设备别名">
          <option value>状态</option>
        </select>
        <input type="text" v-model="querform.toolIp" placeholder="IP地址" />
        <select v-model="querform.toolType" placeholder="设备别名">
          <option value>设备类型</option>
        </select>
        <select v-model="querform.toolMethods" placeholder="设备别名">
          <option value>监视方式</option>
        </select>
        <select v-model="querform.toolArea" placeholder="设备别名">
          <option value>区域</option>
        </select>
      </div>
      <div class="leftBox">
        <button><i class="icon iconfont"></i> 搜索</button>
        <button><i class="icon iconfont iconxinzeng"></i>添加设备</button>
        <button><i class="icon iconfont icondelete"></i>取消监视</button>
        <button @click="showChart"><i class="icon iconfont iconchuli"></i>生成报表</button>
      </div>
    </div>

    <div class="body">
      <div class="leftBox">
        <tree-table
          ref="recTree"
          :list.sync="treeDataSource"
          @actionFunc="actionFunc"
          @handlerExpand="handlerExpand"
          @handlerChecked="handlerChecked"
        ></tree-table>
      </div>

      <div class="rightBox"></div>
    </div>

    <div class="dialogBox" v-if="showChartFlag">
      <div class="eldialog">
        <div class="dialogHeader">
          <div>
            <button>生成PDF</button>
            <button>生成EXL</button>
          </div>
          <div class="dialogHandleBox" @click="showChartFlag = false">
            <i class="icon iconfont iconbaseline-close-px"></i>关闭
          </div>
        </div>
        <div class="dialogBody" id="itemChartBody"></div>
      </div>
    </div>
  </div>
</template>

<script>
  import echarts from 'echarts';
  import treeTable from './treeTable/tree-table';
  export default {
    data() {
      return {
        showChartFlag: false,
        querform: {
          toolName: '',
          toolStatus: '',
          toolIp: '',
          toolType: '',
          toolMethods: '',
          toolArea: '',
        },
        treeDataSource: [
          {
            label: '一级 1',
            id: '1',
            isChecked: false,
            children: [
              {
                label: '二级 1-1',
                id: '1-1',
                isChecked: false,
                children: [
                  {
                    label: '三级 1-1-1',
                    id: '1-1-1',
                    isChecked: false,
                  },
                ],
              },
            ],
          },
          {
            label: '一级 2',
            id: '2',
            isChecked: false,
            children: [
              {
                label: '二级 2-1',
                id: '2-1',
                isChecked: false,
                children: [
                  {
                    label: '三级 2-1-1',
                    id: '2-1-1',
                    isChecked: false,
                  },
                ],
              },
              {
                label: '二级 2-2',
                id: '2-2',
                isChecked: false,
                children: [
                  {
                    label: '三级 2-2-1',
                    id: '2-2-1',
                    isChecked: false,
                  },
                ],
              },
            ],
          },
          {
            label: '一级 3',
            id: '3',
            isChecked: false,
            children: [
              {
                label: '二级 3-1',
                id: '3-1',
                isChecked: false,
                children: [
                  {
                    label: '三级 3-1-1',
                    id: '3-1-1',
                    isChecked: false,
                  },
                ],
              },
              {
                label: '二级 3-2',
                id: '3-2',
                isChecked: false,
                children: [
                  {
                    label: '三级 3-2-1',
                    id: '3-2-1',
                    isChecked: false,
                  },
                ],
              },
            ],
          },
          {
            label: '四级 1',
            id: '4',
            isChecked: false,
          },
        ],
      };
    },
    components: {
      treeTable,
    },
    mounted() {},
    methods: {
      showChart() {
        this.showChartFlag = true;
        this.$nextTick(() => {
          this.drawChart();
        });
        // 基于准备好的dom，初始化echarts实例
      },
      drawChart() {
        var myChart = echarts.init(document.getElementById('itemChartBody'));
        myChart.setOption({
          color: ['#5793f3', '#6e64bd', '#d14a61'],
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross',
              crossStyle: {
                color: '#999',
              },
            },
          },
          toolbox: {
            feature: {
              dataView: { show: true, readOnly: false },
              magicType: { show: true, type: ['line', 'bar'] },
              restore: { show: true },
              saveAsImage: { show: true },
            },
          },
          legend: {
            data: ['在线时长', '平均丢包率', '最大丢包率'],
          },
          xAxis: [
            {
              type: 'category',
              data: [
                'A设备',
                'B设备',
                'C设备',
                'D设备',
                'E设备',
                'F设备',
                'G设备',
                'H设备',
                'I设备',
                'J设备',
                'k设备',
                'L设备',
              ],
              axisPointer: {
                type: 'shadow',
              },
            },
          ],
          yAxis: [
            {
              type: 'value',
              name: '丢包率',
              min: 0,
              max: 100,
              interval: 20,
              axisLabel: {
                formatter: '{value} %',
              },
              axisLine: {
                lineStyle: {
                  color: '#d14a61',
                },
              },
            },
            {
              type: 'value',
              name: '在线时长',
              min: 0,
              max: 250,
              interval: 50,
              axisLabel: {
                formatter: '{value} h',
              },
              axisLine: {
                lineStyle: {
                  color: '#5793f3',
                },
              },
            },
          ],
          series: [
            {
              name: '在线时长',
              type: 'bar',
              data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
            },
            {
              name: '平均丢包率',
              type: 'line',
              data: [2.6, 5.9, 9.0, 26.4, 8.7, 0.7, 25.6, 22.2, 38.7, 18.8, 6.0, 2.3],
            },
            {
              name: '最大丢包率',
              type: 'line',
              yAxisIndex: 1,
              data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2],
            },
          ],
        });
      },
      actionFunc(m) {
        alert('编辑');
      },

      handlerExpand(m) {
        console.log('展开/收缩');
        m.isExpand = !m.isExpand;
      },
      handlerChecked(m) {
        console.log(m);
        console.log(m.isChecked);
        this.resetData(m.children, m.isChecked);
      },
      resetData(dataArr, checked) {
        for (var i in dataArr) {
          dataArr[i].isChecked = checked;
          this.resetData(dataArr[i].children, checked);
        }
      },
    },
  };
</script>

<style lang="scss" scoped>
  .managerTemplate {
    height: 100%;
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    background: #eef5fd;
    width: 20rem;
    input,
    select,
    button {
      border: 1px solid #dcdfe6;
      border-radius: 0.05rem;
      color: #606266;
      display: inline-block;
      font-size: inherit;
      height: 0.375rem;
      line-height: 0.375rem;
      outline: none;
      padding: 0 0.1875rem;
      transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
      margin-left: 0.075rem;
    }
    button {
      color: #409eff;
      background: #ecf5ff;
      border-color: #b3d8ff;
      cursor: pointer;
      &:hover {
        background: #409eff;
        color: #fff;
      }
    }
    .head {
      width: 100%;
      height: 1rem;
      background: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 0.125rem;
      .leftBox {
        padding-right: 0.075rem;
      }
    }
    .body {
      width: 100%;
      height: calc(100% - 1rem);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-top: 0.2rem;
      .leftBox,
      .rightBox {
        height: 100%;
        background: #fff;
        padding: 0.2rem;
      }
      .leftBox {
        width: 76%;
        margin-right: 0.2rem;
      }
      .rightBox {
        width: calc(24% - 0.2rem);
      }
    }
    .dialogBox {
      position: fixed;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      overflow: auto;
      z-index: 1000;
      background: rgba(0, 0, 0, 0.75);
      .eldialog {
        width: 7.875rem;
        height: 4.125rem;
        position: relative;
        margin: 0 auto 0.625rem;
        margin-top: 15vh;
        background: #fff;
        border-radius: 0.025rem;
        box-shadow: 0 0.0125rem 0.0375rem rgba(0, 0, 0, 0.3);
        box-sizing: border-box;
        .dialogHeader {
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0.15rem;
          span {
            font-size: 0.175rem;
          }
        }
        #itemChartBody {
          width: 100%;
          height: 3.375rem;
        }
      }
    }
  }
</style>
