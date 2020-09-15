<template>
  <div class="home">
    <div class="nav">
      <naver></naver>
    </div>
    <div class="right">
      <div class="header">
        <upper></upper>
      </div>
      <div class="content">
        <!-- <mainer></mainer> -->
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import naver from './left/naver';
import upper from './top/upper';
import mainer from './right/index/mainer';
export default {
  components: { naver, upper, mainer },
};
</script>

<style lang="scss" scoped>
.home {
  box-sizing: border-box;
  display: flex;
  .nav {
    flex: 1;
    height: 13.5rem;
  }
  .right {
    flex: 5;
    display: flex;
    flex-direction: column;

    .header {
      height: 1rem;
      background-color: #ffffff;
    }
    .content {
      height: 12.5rem;
    }
  }
}
</style>