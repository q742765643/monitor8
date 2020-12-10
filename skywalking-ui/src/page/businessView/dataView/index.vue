<template>
  <div id="dataviewTemplate">
    <div class="dataview_contentall" v-if="!showFullType">
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

    <div v-else class="dataview_content">
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
  import Vue from 'vue';
  import request from '@/utils/request';

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
    created() {},
    components: {
      dataMointor,
      reportMointor,
      dataAlarm,
      cloundMointor,
      fullChart,
      planeTitle,
    },
    mounted() {
      setTimeout(() => {
        let that = this;
        this.findHeader();
        eventBus.$on('fullChart', (fullChartType, name, titleID) => {
          that.showFullType = fullChartType;
          that.titleName = name;
          that.titleID = titleID;
        });
      }, 500);
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
        }).then((data) => {
          let list = data.data;
          this.comList = [];
          list.forEach((item, index) => {
            let com = {
              comName: 'reportMointor',
              titleName: item.title,
              chartID: item.taskId,
              url: chartImg2,
            };
            if (index == 2) {
              com = {
                comName: 'reportMointor',
                titleName: item.title,
                chartID: item.taskId,
                url: chartImg1,
              };
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
  #dataviewTemplate {
    width: 100%;
    height: 100%;
    .dataview_contentall {
      width: 100%;
      height: 100%;
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
    }
    #com {
      width: calc(50% - 5px);
      height: 50%;
      margin-bottom: 10px;
    }
  }
</style>
