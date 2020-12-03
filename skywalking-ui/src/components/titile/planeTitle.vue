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
  padding-left: 18px;
  padding-right: 30px;
  border-bottom: solid 2px $plane_border_color;
  height: 80px;
  line-height: 80px;
  justify-content: space-between;
}
#name {
  color: #333;
  font-family: Alibaba-PuHuiTi-Medium;
  font-size: 23px;
}
</style>
