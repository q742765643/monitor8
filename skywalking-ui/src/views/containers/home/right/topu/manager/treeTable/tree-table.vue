<template>
  <div class="tree-table">
    <div class="tree-head">
      <table>
        <tr>
          <td class="td1 tdItem">
            <input type="checkbox" v-model="checkedall" @change="handelcheckedall" />设备别名
          </td>
          <td class="td2 tdItem">状态</td>
          <td class="td3 tdItem">设备名称</td>
          <td class="td4 tdItem">IP地址</td>
          <td class="td5 tdItem">监视方式</td>
          <td class="td6 tdItem">设备类型</td>
          <td class="td7 tdItem">发现时间</td>
          <td class="td8 tdItem">网关</td>
          <td class="td9 tdItem">
            <p>MAC</p>
            <span>地址</span>
          </td>
          <td class="td10 tdItem">区域</td>
          <td class="td11 tdItem">详细位置</td>
          <td class="td12 tdItem">运行时长</td>
          <td class="td13 tdItem">
            <p>最大</p>
            <span>丢包率</span>
          </td>
          <td class="td14 tdItem">
            <p>平均</p>
            <span>丢包率</span>
          </td>
          <td class="td15 tdItem">操作</td>
        </tr>
      </table>
    </div>
    <div id="scrollWrap" class="tree-wrap">
      <div class="tree-body">
        <table v-if="treeDataSource.length>0">
          <tbody>
            <tr>
              <td>
                <tree-item
                  v-for="(model,i) in treeDataSource"
                  :key="'root_node_'+i"
                  :root="0"
                  :num="i"
                  @actionFunc="actionFunc"
                  @handlerExpand="handlerExpand"
                  @handlerChecked="handlerChecked"
                  :nodes="treeDataSource.length"
                  :trees.sync="treeDataSource"
                  :model.sync="model"
                ></tree-item>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'treeTable',
  props: ['list'],
  data() {
    return {
      checkedall: false,
      isDesc: false,
      treeDataSource: [],
    };
  },

  watch: {
    list: {
      handler() {
        console.log('ddddd');
        this.initTreeData(false);
      },
      deep: true,
    },
  },
  computed: {},
  methods: {
    initTreeData(checkall) {
      console.log('处理前的:', JSON.parse(JSON.stringify(this.list)));
      // 这里一定要转化，要不然他们的值监听不到变化
      let tempData = JSON.parse(JSON.stringify(this.list));
      let reduceDataFunc = (data, level) => {
        data.map((m, i) => {
          if (checkall == false) {
            m.isChecked = false;
          } else if (checkall == true) {
            m.isChecked = true;
          }
          m.isExpand = false;
          m.children = m.children || [];
          m.level = level;
          m.bLeft = level === 1 ? 34 : (level - 2) * 16 + 34;
          if (m.children.length > 0) {
            reduceDataFunc(m.children, level + 1);
          }
        });
      };
      reduceDataFunc(tempData, 1);
      console.log('处理后的:', tempData);
      this.treeDataSource = tempData;
    },
    // 编辑
    actionFunc(m) {
      this.$emit('actionFunc', m);
    },
    // 展开
    handlerExpand(m) {
      this.$emit('handlerExpand', m);
    },
    // 选择
    handlerChecked(m) {
      this.$emit('handlerChecked', m);
    },
    // 全选/反选
    handelcheckedall() {
      this.initTreeData(this.checkedall);
    },
  },
  components: {
    treeItem: () => import('./tree-item.vue'),
  },
  mounted() {
    const vm = this;
    vm.$nextTick(() => {
      vm.initTreeData(false);
    });
  },
};
</script>

<style lang="scss">
.tree-table {
  width: 100%;
  min-width: 908px;
  position: relative;
  cursor: pointer;
  .tree-head {
    table {
      width: 100%;
      border-collapse: collapse;
      tr {
        background: #fafafa;
        border-top: 1px solid #ebeef5;
        border-bottom: 1px solid #ebeef5;
      }
      td {
        text-align: center;
        padding: 12px 0;
        font-size: 13px;
        &.td1 {
          padding-left: 16px;
        }
      }
    }
  }
  .td-border {
    border-bottom: 1px solid #ebeef5;
  }
  .tree-wrap {
    table {
      width: 100%;
      border-collapse: collapse;
    }
    .tree-body {
      td {
        height: 40px;
        line-height: 40px;
        color: rgba(0, 0, 0, 0.85);
      }
      .td-title {
        position: relative;

        .tree-close,
        .tree-open {
          position: absolute;
          width: 0;
          height: 0;
          border: 5px solid #999;
          cursor: pointer;
          z-index: 2;
        }
        .tree-close {
          left: -12px;
          top: 14px;
          border-color: transparent transparent transparent #aaa;
        }
        .tree-open {
          left: -17px;
          top: 17px;
          border-color: #aaa transparent transparent;
        }
      }
      input {
        margin-right: 4px;
      }
    }
  }
  .tdItem {
    width: 6.2%;
    text-align: center;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .td1 {
    width: 100px;
    padding-left: 16px;
    text-align: left !important;
    input {
      margin-right: 4px;
    }
  }
  .td2 {
    width: 6%;
  }
  .leve-1 .td1 {
    padding-left: 16px;
  }
  .leve-2 .td1 {
    padding-left: 32px;
  }
  .leve-3 .td1 {
    padding-left: 48px;
  }
  .leve-4 .td1 {
    padding-left: 64px;
  }
  .leve-5 .td1 {
    padding-left: 80px;
  }
  .leve-6 .td1 {
    padding-left: 96px;
  }
  .leve-7 .td1 {
    padding-left: 112px;
  }
  .leve-8 .td1 {
    padding-left: 128px;
  }
  .leve-9 .td1 {
    padding-left: 144px;
  }
  .ellipsis {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .editSpan {
    color: #409eff;
  }
}
</style>

