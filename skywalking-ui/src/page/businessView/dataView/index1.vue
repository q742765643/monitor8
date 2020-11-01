<template>
  <div id="dataview">
    <div id="dataview_content" v-if="!showFullType">
      <div id="right_comp">
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
  import request from "@/utils/request";

  import dataMointor from './chart/dataMointor';
  import reportMointor from './chart/reportMointor';
  import dataAlarm from './chart/dataAlarm';
  import cloundMointor from './chart/cloundMointor';
  import fullChart from '../commonFull/fullChart';
  import eventBus from '@/components/utils/eventBus.js';
  import planeTitle from '@/components/titile/planeTitle.vue';
  export default {
    data() {
      return {
        searchStr: '',
        selectedKeys: [],
        expandedKeys: ['0'],
        autoExpandParent: false,
        replaceFields: { name: '图表' },
        searchValue: '',
        comList: [],
        showFullType: false,
        titleID: '',
        titleName: '',
      };
    },
    created() {
      let that = this;
      this.findHeader();
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
      planeTitle,
    },
    mounted() {
      // 拖拽后触发的事件
      this.$dragging.$on('dragged', (res) => {
        console.log(res);
      });
    },
    methods: {
      findHeader() {
        request({
          url: '/fileQReport/findHeader',
          method: 'get',
        }).then(data => {
          let list = data.data;
          this.comList=[];
          list.forEach((item, index) => {
            let com={
                    comName: 'reportMointor',
                    titleName: item.title,
                    chartID: item.taskId,
                    url: chartImg2,
                  }
            if(index==2){
              com={
                comName: 'reportMointor',
                titleName: item.title,
                chartID: item.taskId,
                url: chartImg1,
              }
            }
            this.comList.push(com);
          });
        });
      },
      //树查询
      onChange() {
        var vm = this;
        vm.searchValue = vm.searchStr;
        if (vm.searchValue === '') {
          vm.expandedKeys = ['0'];
        } else {
          vm.expandedKeys = [];
          vm.backupsExpandedKeys = [];
          let candidateKeysList = vm.getkeyList(vm.searchValue, vm.treeData, []);

          candidateKeysList.map((item) => {
            var key = vm.getParentKey(item, vm.treeData);
            if (key && !vm.backupsExpandedKeys.some((item) => item === key)) vm.backupsExpandedKeys.push(key);
          });
          let length = this.backupsExpandedKeys.length;
          for (let i = 0; i < length; i++) {
            vm.getAllParentKey(vm.backupsExpandedKeys[i], vm.treeData);
          }
          vm.expandedKeys = vm.backupsExpandedKeys.slice();
        }
      },
      onExpand(expandedKeys) {
        this.expandedKeys = expandedKeys;
        this.autoExpandParent = false;
      },
      onCheck(checkedKeys) {
        console.log('onCheck', checkedKeys);
        this.checkedKeys = checkedKeys;
      },
      getKeyvalue(selectedKeys, treeData) {
        for (let index in treeData) {
          if (treeData[index].key == selectedKeys) {
            this.selectNode = treeData[index];
            break;
          } else {
            if (treeData[index].hasOwnProperty('children')) {
              this.getKeyvalue(selectedKeys, treeData[index].children);
            }
          }
        }
      },
      onSelect(selectedKeys, info) {
        console.log('onSelect', info);
        this.selectedKeys = selectedKeys;
        this.getKeyvalue(selectedKeys[0], this.treeData);

        this.searchStr = this.selectNode.name;
      },
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
      box-shadow: $plane_shadow;
      width: 100%;
      height: 100%;
      background: white;
      display: flex;
      overflow-y: auto;
      overflow-x: hidden;
      #left_tree {
        box-shadow: $plane_shadow;
        background: white;
        overflow: auto;
        // flex: 1;
        width: 20%;
      }
      #right_comp {
        box-shadow: $plane_shadow;
        overflow: auto;
        margin-left: 0.25rem;
        // flex: 4;
        width: 100%;
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
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
