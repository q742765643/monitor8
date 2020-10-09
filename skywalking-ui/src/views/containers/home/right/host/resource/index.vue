<template>
  <div id="resource">
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
  #resource {
    width: 100%;
    height: 100%;
    //  background: pink;
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    display: flex;
    justify-content: space-between;
    #r_left {
      width: 5.35rem;
      height: 100%;
      background: white;
    }
    #r_right {
      height: 100%;
      width: calc(100% - 5.35rem - 0.375rem);
      background: white;
    }
  }
</style>
