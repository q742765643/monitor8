<template>
  <a-layout id="components-layout">
    <div class="leftAside"><navbar /></div>
    <a-layout id="rightPalne">
      <div id="top"><upper></upper></div>
      <div id="center">
        <el-scrollbar class="centerScroll">
          <container></container>
        </el-scrollbar>
      </div>
    </a-layout>
  </a-layout>
</template>

<script>
  import navbar from '@/components/layout/navbar';
  import upper from '@/components/layout/upper';
  import container from '@/components/layout/container';
  export default {
    data() {
      return {
        collapsed: false,
      };
    },
    components: { navbar, upper, container },
  };
</script>

<style lang="scss" scoped>
  #components-layout {
    width: 100%;
    height: 100vh;
    .ant-layout-sider {
      width: 100% !important;
      height: 100%;
      -webkit-box-flex: 0 !important;
      -ms-flex: none !important;
      flex: none !important;
      max-width: none !important;
      min-width: 100% !important;
      background: #f0f2f3;
    }
    .leftAside {
      width: 340px;
      height: 100%;
      background: #f0f2f3;
    }
    #rightPalne {
      width: calc(100% - 340px);
      height: 100%;
      background: #f6fbfc;
      position: relative;
      #top {
        width: calc(100% - 340px);
        height: 80px;
        background: #fff;
        position: fixed;
        top: 0;
        z-index: 999;
      }
      #center {
        width: 100%;
        height: calc(100% - 110px);
        margin-top: 110px;
        padding-bottom: 20px;
        .centerScroll {
          height: 100%;
          margin: 0 20px 0 10px;
        }
      }
    }
    /*   ::-webkit-scrollbar {
    width: 3px;
    background-color: #f5f5f5;
  }
  ::-webkit-scrollbar-thumb {
    // background-color: #5aa6ee;
  }
  ::-webkit-scrollbar-track {
    box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.3);
    background-color: #f5f5f5;
  } */
  }
</style>
