<template>
  <div id="softwareview">
    <div id="sofewareView_content" v-if="!showFullType">
      <div id="left_tree">
        <planeTitle titleName="数据监视任务列表" />
        <a-input-search
          style="margin: 5px 0px;padding:0  10px"
          placeholder="输入报文名称"
          v-model="searchStr"
          @change="onChange"
          size="small"
        />
        <!--  @search="onSearch" -->
        <a-tree
          :defaultExpandAll="true"
          :treeData="treeData"
          :selectedKeys="selectedKeys"
          :expandedKeys.sync="expandedKeys"
          @expand="onExpand"
          :autoExpandParent="autoExpandParent"
          @select="onSelect"
          :replaceFields="replaceFields"
        >
          <template slot="title" slot-scope="{ name }">
            <span
              v-html="name.replace(new RegExp(searchValue, 'g'), '<span style=color:#f50>' + searchValue + '</span>')"
            ></span>
          </template>
        </a-tree>
      </div>
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

    <div v-else id="sofewareView_content">
      <fullChart
        :titleName="titleName"
        :fullChartID="titleID"
        :titleID="titleID"
        :showFullType="showFullType"
        :comList="comList"
        @switchIndex="getSwiperChartID"
      >
        <template slot="chartInfo">
          <div class="slot_chart">
            <planeTitle titleName="基本信息" />
            <div class="info">
              <div class="column">
                <div class="cell">
                  <span>
                    设备名称:
                    <i></i>
                  </span>
                  <span>{{ infoData.name }}</span>
                </div>

                <div class="cell">
                  <span>
                    运行状态:
                    <i></i>
                  </span>
                  <span>{{ infoData.status }}</span>
                </div>
                <div class="cell">
                  <span>
                    服务器IP:
                    <i></i>
                  </span>
                  <span>{{ infoData.ip }}</span>
                </div>

                <div class="cell">
                  <span>
                    进程PID:
                    <i></i>
                  </span>
                  <span>{{ infoData.pid }}</span>
                </div>
                <div class="cell">
                  <span>
                    开始时间:
                    <i></i>
                  </span>
                  <span>{{ infoData.startTime }}</span>
                </div>
                <div class="cell">
                  <span>
                    启动用户:
                    <i></i>
                  </span>
                  <span>{{ infoData.startAdmin }}</span>
                </div>
                <div class="cell">
                  <span>
                    运行目录:
                    <i></i>
                  </span>
                  <span>{{ infoData.dir }}</span>
                </div>
                <div class="cell">
                  <span>
                    命令行:
                    <i></i>
                  </span>
                  <span>{{ infoData.dos }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </fullChart>
    </div>
  </div>
</template>

<script>
  import cloundReport from './chart/cloundReport';
  import mediumReport from './chart/mediumReport';
  import SHReport from './chart/SHReport';
  import SSReport from './chart/SSReport';
  import SNReport from './chart/SNReport';
  import UNReport from './chart/UNReport';
  import USReport from './chart/USReport';
  import QTReport from './chart/QTReport';

  import planeTitle from '@/components/titile/planeTitle.vue';
  import fullChart from '../commonFull/fullChart';
  import eventBus from '@/components/utils/eventBus.js';

  const chartImg1 = require('../../../assets/imgs/chart/01.png');
  const chartImg2 = require('../../../assets/imgs/chart/02.png');
  const chartImg3 = require('../../../assets/imgs/chart/03.png');
  const chartImg4 = require('../../../assets/imgs/chart/04.png');
  export default {
    data() {
      return {
        treeData: [
          {
            name: '绘图报监视',
            key: '0',
            scopedSlots: { title: 'title' },
            children: [
              {
                name: 'SN报文监视图图表',
                key: '0-0',
                scopedSlots: { title: 'title' },
              },
              {
                name: 'SS报文监视图图表',
                key: '01',
                scopedSlots: { title: 'title' },
              },
              {
                name: '35中期预报监视图图表',
                key: '02',
                scopedSlots: { title: 'title' },
              },
              {
                name: '35云环境监视图图表',
                key: '03',
                scopedSlots: { title: 'title' },
              },
              {
                name: 'SH报文监视图图表',
                key: '04',
                scopedSlots: { title: 'title' },
              },
              {
                name: 'UN报文监视图图表',
                key: '05',
                scopedSlots: { title: 'title' },
              },
            ],
          },
        ],
        replaceFields: { name: '图表' },
        expandedKeys: ['0'],
        backupsExpandedKeys: [],
        autoExpandParent: false,
        checkedKeys: [],
        selectedKeys: [],
        searchValue: '',
        searchStr: '',
        selectNode: '',
        showFullType: false,

        showFullType: false,
        titleID: '',
        titleName: '',

        comList: [
          {
            comName: 'cloundReport',
            titleName: '35云环境监视图图表',
            chartID: 'softview_cloundChart',
            url: chartImg1,
          },
          {
            comName: 'mediumReport',
            titleName: '35中期预报监视图图表',
            chartID: 'softview_mediumChart',
            url: chartImg2,
          },
          {
            comName: 'SHReport',
            titleName: 'SH云环境监视图图表',
            chartID: 'softview_SHReport',
            url: chartImg3,
          },
          {
            comName: 'SSReport',
            titleName: 'SS报文监视图图表',
            chartID: 'softview_SSChart',
            url: chartImg4,
          },
          {
            comName: 'SNReport',
            titleName: 'SN报文监视图图表',
            chartID: 'softview_SNChart',
            url: chartImg1,
          },
          {
            comName: 'UNReport',
            titleName: 'UN报文监视图图表',
            chartID: 'softview_UNChart',
            url: chartImg2,
          },
          {
            comName: 'USReport',
            titleName: 'US报文监视图图表',
            chartID: 'softview_USChart',
            url: chartImg3,
          },
          {
            comName: 'QTReport',
            titleName: 'QT报文监视图图表',
            chartID: 'softview_QTChart',
            url: chartImg4,
          },
        ],
        infoData: {
          name: '',

          status: '正常',
          ip: '66.32.5.122',
          pid: '12467',
          startTime: '2020-9-2',
          startAdmin: 'admin',
          dir: 'd:/xxxx',
          dos: 'xxxxxxxxxxx',
        },
      };
    },
    created() {
      let that = this;
      eventBus.$on('fullChart', (fullChartType, name, titleID) => {
        that.showFullType = fullChartType;
        that.titleName = name;
        that.titleID = titleID;

        this.infoData.name = name;
      });
    },
    components: {
      fullChart,
      planeTitle,
      cloundReport,
      mediumReport,
      SHReport,
      SSReport,
      SNReport,
      UNReport,
      USReport,
      QTReport,
    },
    mounted() {
      // 拖拽后触发的事件
      this.$dragging.$on('dragged', (res) => {
        console.log(res);
      });
    },
    methods: {
      //底部切换 info信息切换
      getSwiperChartID(id, name) {
        this.infoData.name = name;
        // console.log(id);
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

      getkeyList(value, tree, keyList) {
        for (let i = 0; i < tree.length; i++) {
          let node = tree[i];
          if (node.name.indexOf(value) > -1) {
            keyList.push(node.key);
          }
          if (node.children) {
            this.getkeyList(value, node.children, keyList);
          }
        }
        return keyList;
      },

      getParentKey(key, tree) {
        let parentKey, temp;
        for (let i = 0; i < tree.length; i++) {
          const node = tree[i];
          if (node.children) {
            if (node.children.some((item) => item.key === key)) {
              parentKey = node.key;
            } else if ((temp = this.getParentKey(key, node.children))) {
              parentKey = temp;
            }
          }
        }
        return parentKey;
      },

      getAllParentKey(key, tree) {
        var parentKey;
        if (key) {
          parentKey = this.getParentKey(key, tree);
          if (parentKey) {
            if (!this.backupsExpandedKeys.some((item) => item === parentKey)) {
              this.backupsExpandedKeys.push(parentKey);
            }
            this.getAllParentKey(parentKey, tree);
          }
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
  #softwareview {
    width: 100%;
    height: 100%;
    font-family: Alibaba-PuHuiTi-Regular;

    #sofewareView_header {
      z-index: 1;
      width: 100%;
      height: 1.25rem;
      line-height: 1.25rem;
      font-size: 0.2rem;

      box-shadow: $plane_shadow;
      margin-bottom: 0.1rem;

      text-align: center;

      #serch {
        display: inline-block;

        #title {
          margin-left: 0.1rem;
          width: 3.2rem;
          border: 1px solid #dcdfe6;
          border-radius: 0.05rem;
          &:focus {
            outline: none !important;
            border: 1px solid #0f9fec !important;
          }
        }
        #search_btn {
          margin-left: 0.5rem;
        }
      }

      #tool {
        display: inline-block;
        text-align: right;
        padding-right: 1rem;

        .iconfont {
          font-size: 0.2rem;
          padding: 0.08rem 0.125rem;
        }
      }
    }
    #sofewareView_content {
      box-shadow: $plane_shadow;
      width: 100%;
      height: 100%;
      background: white;
      display: flex;
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
        width: 80%;
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
        overflow-y: auto;
        overflow-x: hidden;
        #com {
          width: calc((100% - 0.25rem) / 2);
          height: 5rem;
          box-shadow: $plane_shadow;
          margin-bottom: 0.4rem;

          &:nth-last-child(-n + 2) {
            margin-bottom: 0rem;
          }
        }
      }

      .slot_chart {
        margin-left: 0.125rem;
        box-shadow: $plane_shadow;
        width: 25%;
        height: 100%;
        .info {
          padding: 0.5rem 0.125rem;
          .column {
            height: 100%;
            display: flex;
            flex-direction: column;
            //justify-items: center;
            justify-content: space-between;
            .cell {
              flex: 1;
              display: flex;
              span:first-child {
                font-size: $ant_font_size;
                width: 1.125rem;
                display: inline-block;
                font-weight: 600;
                text-align: justify;
                i {
                  width: 100%;
                  display: inline-block;
                }
              }

              span:last-child {
                margin-left: 0.125rem;
              }
            }
          }
        }
      }
    }

    #left_tree,
    #right_comp::-webkit-scrollbar {
      width: 0.15rem;
      background-color: #f6f7f9;
    }

    #left_tree,
    #right_comp::-webkit-scrollbar-thumb {
      background-color: #d8dce8;
    }
    #left_tree,
    #right_comp::-webkit-scrollbar-track {
      background-color: #f6f7f9;
    }
  }
</style>
