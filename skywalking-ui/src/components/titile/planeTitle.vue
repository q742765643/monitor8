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
  padding-left: 0.25rem;
  padding-right: 0.25rem;
  border-bottom: solid 2px $plane_border_color;
  height: 0.75rem;
  line-height: 0.75rem;
  justify-content: space-between;
}
#name {
  color: #323232;
  font-family: Alibaba-PuHuiTi-Medium;
  font-size: 0.275rem;
}
</style>
