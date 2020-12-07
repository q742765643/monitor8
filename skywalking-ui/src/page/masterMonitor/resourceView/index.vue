<template>
  <div id="resourceTemplate">
    <div id="r_left">
      <leftPlane></leftPlane>
    </div>
    <div id="r_right">
      <rightPlane></rightPlane>
    </div>
  </div>
</template>

<script>
  import leftPlane from './leftPlane/index';
  import rightPlane from './rightPlane/index';
  export default {
    components: {
      leftPlane,
      rightPlane,
    },
  };
</script>

<style lang="scss" scoped>
  #resourceTemplate {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-between;
    #r_left {
      width: 428px;
      height: 100%;
    }
    #r_right {
      height: 100%;
      width: calc(100% - 438px);
    }
  }
</style>
