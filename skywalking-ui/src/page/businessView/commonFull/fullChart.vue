<template>
  <div id="fullChart">
    <div id="bigChart">
      <div id="component">
        <component
          :is="comList[comIndex].comName"
          :chartID="chartID"
          :titleName="changeName"
          v-bind="$attrs"
        >
        </component>
      </div>
      <slot name="chartInfo" :chartID="chartID"></slot>
    </div>
    <div id="swipeChart">
      <div id="imgList">
        <div class="imgborder" v-for="(item, index) in comList" :key="index">
          <img
            :class="[{ img: index > -1 }, { active: index == comIndex }]"
            :src="item.url"
            @click="changeBigChart(index)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import planeTitle from '@/components/titile/planeTitle.vue';
//dateView
import dataMointor from '../dataView/chart/dataMointor';
import reportMointor from '../dataView/chart/reportMointor';
import dataAlarm from '../dataView/chart/dataAlarm';
import cloundMointor from '../dataView/chart/cloundMointor';

//dateView
import cloundReport from '../softwareView/chart/cloundReport';
import mediumReport from '../softwareView/chart/mediumReport';
import SHReport from '../softwareView/chart/SHReport';
import SSReport from '../softwareView/chart/SSReport';
import SNReport from '../softwareView/chart/SNReport';
import UNReport from '../softwareView/chart/UNReport';
import USReport from '../softwareView/chart/USReport';
import QTReport from '../softwareView/chart/QTReport';

export default {
  name: 'fullChart',
  props: ['fullChartID', 'comList'],
  data() {
    return {
      chartID: '',
      comIndex: 0,
      changeName: '',
    };
  },
  components: {
    planeTitle,
    //software_View
    dataMointor,
    reportMointor,
    dataAlarm,
    cloundMointor,
    //software_View

    cloundReport,
    mediumReport,
    SHReport,
    SSReport,
    SNReport,
    UNReport,
    USReport,
    QTReport,
  },
  created() {
    this.chartID = this.fullChartID;
    this.comList.forEach((item, index) => {
      if (item.chartID == this.chartID) {
        this.comIndex = index;
        this.changeName = this.comList[index].titleName;
      }
    });
  },
  methods: {
    changeBigChart(index) {
      this.comIndex = index;
      this.chartID = this.comList[index].chartID;
      this.changeName = this.comList[index].titleName;
      this.changeChartSize(this.chartID);

      this.$emit('switchIndex', this.chartID, this.changeName);
    },
    changeChartSize(id) {
      this.$nextTick(() => {
        let el = document.getElementById(id);
        let bigChart = document.getElementById('bigChart');
        debugger;
        el.style.height = bigChart.clientHeight - 48 + 'px';
      });
    },
  },
  mounted() {
    let el = document.getElementById(this.chartID);
    debugger;
    let bigChart = document.getElementById('bigChart');
    el.style.height = bigChart.clientHeight - 48 + 'px';
  },
};
</script>

<style lang="scss" scoped>
#fullChart {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: white;
  #bigChart {
    height: calc(100% - 2rem - 0.25rem);
    box-shadow: $plane_shadow;
    display: flex;

    #component {
      width: 100%;
      box-shadow: $plane_shadow;
    }
  }
  #swipeChart {
    overflow-y: auto;
    margin-top: 0.25rem;
    height: 2rem;
    display: flex;
    justify-content: center;
    align-items: center;
    #imgList {
      display: flex;
      justify-content: center;
      align-items: center;
      .imgborder {
        border: solid 1px $borderColor;
        margin: 0 0.25rem;
        .img {
          height: 1.5rem;
          width: 2.5rem;

          border-radius: 0.05rem;
          &:hover {
            border: solid 2px #1890ff;
            transform: scale(1.1);
            animation-name: scaleDraw;
            animation-timing-function: ease-in-out;
            animation-iteration-count: 1;
            animation-duration: 0.75s;
          }
        }

        .active {
          border: solid 2px #1890ff;
          transform: scale(1.1);
          animation-name: scaleDraw;
          animation-timing-function: ease-in-out;
          animation-iteration-count: 1;
          animation-duration: 0.75s;
        }
      }
    }
  }

  @keyframes scaleDraw {
    0% {
      transform: scale(1); /*开始为原始大小*/
    }
    /*   25% {
      transform: scale(1.1); 
    }
    50% {
      transform: scale(1);
    } */
    75% {
      transform: scale(1.1);
    }
  }

  #swipeChart::-webkit-scrollbar {
    height: 0.15rem;
    background-color: #f6f7f9;
  }

  #swipeChart::-webkit-scrollbar-thumb {
    background-color: #d8dce8;
  }
  #swipeChart::-webkit-scrollbar-track {
    //box-shadow: inset 0 0 0.2rem rgba(0, 0, 0, 0.3);
    background-color: #f6f7f9;
  }
}
</style>
