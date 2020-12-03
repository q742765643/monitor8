<template>
  <div id="palne_titile">
    <span id="name">{{ titleName }}</span>
    <div id="right"><slot name="right"></slot></div>
  </div>
</template>

<script>
  export default {
    props: ['titleName'],
  };
</script>

<style lang="scss" scoped>
  #palne_titile {
    display: flex;
    justify-content: space-between;
    border-bottom: solid 2px $plane_border_color;
    font-size: 20px;
    height: 56px;
    line-height: 56px;
    padding-left: 18px;
    #right {
      .lengend {
        margin: 0;
        .item {
          height: auto;
          .color {
            width: 14px;
            height: 14px;
            margin-left: 12px;
          }
          .text {
            font-size: 12px;
          }
        }
      }
    }
  }
</style>
