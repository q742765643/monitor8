<template>
  <a-layout id="components-layout">
    <div id="leftPlane"><navbar /></div>

    <a-layout id="rightPalne">
      <div id="top"><upper></upper></div>
      <div id="center"><container></container></div>
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
    height: 100%;
    #leftPlane {
      width: 380px;
      padding: 30px 30px;
    }

    #rightPalne {
      width: calc(100% - 380px);
      background: #f6fbfc;
    }
    #center {
      height: calc(100% - 89px);
      // background: #fff;
      // padding-right: 30px;
    }
    ::-webkit-scrollbar {
      width: 3px;
      background-color: #f5f5f5;
    }
    ::-webkit-scrollbar-thumb {
      // background-color: #5aa6ee;
    }
    ::-webkit-scrollbar-track {
      box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.3);
      background-color: #f5f5f5;
    }
  }
</style>
