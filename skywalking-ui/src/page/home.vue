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
      width: 4.25rem;
    }

    #rightPalne {
      width: calc(100% - 4.25rem);
    }
    #center {
      height: calc(100% - 1rem);
    }
  }
</style>
