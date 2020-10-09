<template>
  <div id="title">
    <div id="name">
      <span>{{ titleName }}</span>
    </div>
    <div id="tool">
      <span class="iconfont iconyidong"></span>
      <span class="iconfont iconshuaxin1"></span>
      <span class="iconfont iconbianji"></span>
    </div>
  </div>
</template>

<script>
export default {
  props: ['titleName'],
};
</script>

<style lang="scss" scoped>
#title {
  display: flex;
  justify-content: space-between;
  height: 0.75rem;
  line-height: 0.75rem;
  border-bottom: 0.025rem solid #bddfeb8f;
  #name {
    font-size: 0.25rem;
    padding-left: 0.25rem;
    font-weight: 600;
  }
  #tool {
    padding-right: 0.2rem;
    .iconfont {
      margin: 0 0.1rem;
      font-size: 0.3rem;
      color: #06a5ff;
      cursor: pointer;
    }
  }
}
</style>
