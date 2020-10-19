<template>
  <div id="dataview">
    <div id="dataview_header">
      <div id="select">
        <label>数据监视任务:</label>
        <a-select default-value="lucy" style="width: 1.5rem" size="small">
          <a-select-option value="jack">
            Jack
          </a-select-option>
          <a-select-option value="lucy">
            Lucy
          </a-select-option>

          <a-select-option value="Yiminghe">
            yiminghe
          </a-select-option>
        </a-select>
      </div>
      <div id="serch">
        <label>标题:</label>
        <!--  <input id="title" type="text" /> -->
        <a-input id="title" placeholder="请输入标题名称" size="small" />
      </div>

      <div id="tool">
        <a-button type="primary" icon="search"> 搜索 </a-button>
        <a-button> 取消 </a-button>
      </div>
    </div>

    <div id="dataview_content" v-if="!showFullType">
      <div
        id="com"
        v-for="item in comList"
        v-dragging="{ item: item, list: comList, group: 'item' }"
        :key="item.titleName"
      >
        <component
          :is="item.comName"
          :titleName="item.titleName"
          :showFullType="showFullType"
          :chartID="item.chartID"
          :titleID="item.chartID"
        ></component>
      </div>
    </div>

    <div v-else id="dataview_content">
      <fullChart
        :titleName="titleName"
        :fullChartID="titleID"
        :titleID="titleID"
        :showFullType="showFullType"
        :comList="comList"
      ></fullChart>
    </div>
  </div>
</template>

<script>
const chartImg1 = require('../../../assets/imgs/chart/01.png');
const chartImg2 = require('../../../assets/imgs/chart/02.png');
const chartImg3 = require('../../../assets/imgs/chart/03.png');
const chartImg4 = require('../../../assets/imgs/chart/04.png');

import dataMointor from './chart/dataMointor';
import reportMointor from './chart/reportMointor';
import dataAlarm from './chart/dataAlarm';
import cloundMointor from './chart/cloundMointor';
import fullChart from '../commonFull/fullChart';
import eventBus from '@/components/utils/eventBus.js';

export default {
  data() {
    return {
      comList: [
        {
          comName: 'dataMointor',
          titleName: '数据监视任务总览',
          chartID: 'dataView_sankeyChart',
          url: chartImg1,
        },
        {
          comName: 'reportMointor',
          titleName: 'SS报文监视图表',
          chartID: 'dataView_mixChart',
          url: chartImg2,
        },
        {
          comName: 'dataAlarm',
          titleName: '最近数据告警',
          chartID: 'dataView_pieChart',
          url: chartImg3,
        },
        {
          comName: 'cloundMointor',
          titleName: '35云环境预报监视图表',
          chartID: 'dataView_barChart',
          url: chartImg4,
        },
      ],
      showFullType: false,
      titleID: '',
      titleName: '',
    };
  },
  created() {
    let that = this;
    eventBus.$on('fullChart', (fullChartType, name, titleID) => {
      that.showFullType = fullChartType;
      that.titleName = name;
      that.titleID = titleID;
    });
  },
  components: {
    dataMointor,
    reportMointor,
    dataAlarm,
    cloundMointor,
    fullChart,
  },
  mounted() {
    // 拖拽后触发的事件
    this.$dragging.$on('dragged', (res) => {
      console.log(res);
    });
  },
};
</script>

<style lang="scss" scoped>
#dataview {
  width: 100%;
  height: 100%;
  font-family: Alibaba-PuHuiTi-Regular;
  // background: pink;
  // padding: 0.5rem 0.375rem 0.375rem 0.2rem;
  /*  display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    overflow: auto; */

  #dataview_header {
    z-index: 1;
    width: 100%;
    height: 1.25rem;
    line-height: 1.25rem;
    font-size: 0.2rem;
    //box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    box-shadow: $plane_shadow;
    margin-bottom: 0.1rem;
    display: flex;
    text-align: center;
    align-items: center;

    #select {
      width: 33%;
      display: inline-block;
      text-align: right;
      #option {
        margin-left: 0.1rem;
        height: 0.375rem;
        width: 2rem;
        border: 1px solid #dcdfe6;
        border-radius: 0.05rem;
        &:focus {
          outline: none !important;
          border: 1px solid #0f9fec !important;
        }
      }
    }
    #serch {
      width: 33%;
      display: inline-block;
      text-align: right;
      #title {
        margin-left: 0.1rem;
        /*   height: 0.375rem; */
        width: 3.2rem;
        border: 1px solid #dcdfe6;
        border-radius: 0.05rem;
        &:focus {
          outline: none !important;
          border: 1px solid #0f9fec !important;
        }
      }
    }

    #tool {
      width: 33%;
      display: inline-block;
      text-align: left;
      padding-left: 0.5rem;
      span {
        color: #409eff;
        background: #ecf5ff;
        cursor: pointer;
        &:hover {
          background: #409eff;
          color: #fff;
        }
        padding: 0.0625rem 0.125rem;
        border: 1px solid #b3d8ff;
        border-radius: 0.05rem;
        // display: block;
        font-weight: 500;
        margin-left: 0.1875rem;
        font-size: 0.2rem !important;
      }

      .iconfont {
        font-size: 0.2rem;
        padding: 0.08rem 0.125rem;
      }
    }
  }
  #dataview_content {
    // box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    box-shadow: $plane_shadow;
    width: 100%;
    height: calc(100% - 1.35rem);
    background: white;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    overflow-y: auto;
    overflow-x: hidden;

    #com {
      box-shadow: $plane_shadow;
      width: calc((100% - 0.25rem) / 2);
      height: 5rem;
      margin-bottom: 0.4rem;

      &:nth-last-child(-n + 2) {
        margin-bottom: 0rem;
      }
    }
  }

  #dataview_content::-webkit-scrollbar {
    width: 0.15rem;
    background-color: #f6f7f9;
  }

  #dataview_content::-webkit-scrollbar-thumb {
    background-color: #d8dce8;
  }
  #dataview_content::-webkit-scrollbar-track {
    //box-shadow: inset 0 0 0.2rem rgba(0, 0, 0, 0.3);
    background-color: #f6f7f9;
  }
}
</style>
