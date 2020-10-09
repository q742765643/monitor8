<template>
  <div id="dataview">
    <div id="header">
      <div id="select">
        <label>数据监视任务:</label>
        <select id="option">
          <option value="volvo">Volvo</option>
          <option value="saab">Saab</option>
          <option value="opel">Opel</option>
          <option value="audi">Audi</option>
        </select>
      </div>
      <div id="serch">
        <label>标题:</label>
        <input id="title" type="text" />
      </div>

      <div id="tool">
        <span class="iconfont iconsousuo">搜索</span>
        <span>取消</span>
      </div>
    </div>

    <div id="content">
      <div id="com" v-for="item in comList" v-dragging="{ item: item, list: comList, group: 'item' }" :key="item">
        <component :is="item"></component>
      </div>
    </div>
  </div>
</template>

<script>
  import dataMointor from './chart/dataMointor';
  import reportMointor from './chart/reportMointor';
  import dataAlarm from './chart/dataAlarm';
  import cloundMointor from './chart/cloundMointor';
  export default {
    data() {
      return {
        comList: ['dataMointor', 'reportMointor', 'dataAlarm', 'cloundMointor'],
      };
    },

    components: {
      dataMointor,
      reportMointor,
      dataAlarm,
      cloundMointor,
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
    // background: pink;
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    /*  display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    overflow: auto; */

    #header {
      z-index: 1;
      width: 100%;
      height: 1.25rem;
      line-height: 1.25rem;
      font-size: 0.2rem;
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      margin-bottom: 0.1rem;

      #select {
        width: 33%;
        display: inline-block;
        text-align: center;
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
        #title {
          margin-left: 0.1rem;
          height: 0.375rem;
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
        text-align: right;
        padding-right: 1rem;
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
    #content {
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      width: 100%;
      height: calc(100% - 1.35rem);
      background: white;
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      overflow: auto;

      #com {
        box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
        width: calc((100% - 0.35rem) / 2);
        height: 5rem;
        margin-bottom: 0.4rem;
        &:nth-last-child(-n + 2) {
          margin-bottom: 0rem;
        }
      }
    }

    #content::-webkit-scrollbar {
      width: 0.15rem;
      background-color: #f6f7f9;
    }

    #content::-webkit-scrollbar-thumb {
      background-color: #d8dce8;
    }
    #content::-webkit-scrollbar-track {
      //box-shadow: inset 0 0 0.2rem rgba(0, 0, 0, 0.3);
      background-color: #f6f7f9;
    }
  }
</style>
