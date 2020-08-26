<template>
  <div class="nav">
    <div class="name">
      <span>气象海洋空间天气信息系统综合监控平台</span>
    </div>
    <div class="menu">
      <div class="bt_link" v-bind:class="{'avtive':showType==1}" @click="showPage(1)">
        <span class="icon iconfont iconjia"></span>
        <span>主监控</span>
      </div>
      <div class="bt_link" v-bind:class="{'avtive':showType==2}" @click="showPage(2)">
        <span class="icon iconfont iconjianzhu"></span>
        <span>链路拓扑</span>
      </div>
      <div class="bt_link" v-bind:class="{'avtive':showType==3}" @click="showPage(3)">
        <span class="icon iconfont iconyonghu"></span>
        <span>主机监视</span>
      </div>
      <div class="bt_link" v-bind:class="{'avtive':showType==4}" @click="showPage(4)">
        <span class="icon iconfont iconxianshiqi"></span>
        <span>数据监视</span>
      </div>
      <div class="bt_link" v-bind:class="{'avtive':showType==5}" @click="showPage(5)">
        <span class="icon iconfont iconruanjian"></span>
        <span>软件监视</span>
      </div>
      <div class="bt_link" v-bind:class="{'avtive':showType==6}" @click="showPage(6)">
        <span class="icon iconfont iconlaba"></span>
        <span>告警管理</span>
      </div>
      <div class="bt_link" v-bind:class="{'avtive':showType==7}" @click="showPage(7)">
        <span class="icon iconfont iconshezhi"></span>
        <span>系统设置</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showType: 1,
    };
  },
  mounted() {
    if (this.$route.fullPath == '/topu') {
      this.showType = 2;
    }
  },
  methods: {
    showPage(page) {
      if (this.showType == page) {
        return;
      }
      this.showType = page;
      if (page == 1) {
        this.$router.push('/mainer');
      } else if (page == 2) {
        this.$router.push('/topu');
      } else {
        return;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.nav {
  color: #5d76ac;
  padding: 0 0.25rem;
  background-color: #f1f2f4;

  .name {
    padding: 0 0.25rem;
    height: 1.9375rem;
    font-size: 0.275rem;
    display: flex;
    justify-content: center;
    align-items: center;
    // border-bottom: solid 0.01rem #b3b2b2;
    position: relative;
    &::after {
      content: ' ';
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 0.0125rem;
      background: #c0bebe;
      transform: scaleY(0.5);
    }
  }
  .menu {
    .bt_link {
      cursor: pointer;
      margin-top: 0.5625rem;
      width: 100%;
      height: 0.625rem;
      font-size: 0.25rem;
      line-height: 0.625rem;
      display: flex;
      // justify-content: center;
      align-items: center;

      &.avtive {
        background-color: #428aff;
        color: white;
        border-radius: 0.1rem;
        box-shadow: 0.0625rem 0.0625rem 0.125rem #c0c0c0;
      }
      .icon {
        height: 0.625rem;
        width: 0.625rem;
        font-size: 0.375rem;
        padding: 0 0.125rem 0 0.0625rem;
      }
    }
  }
}
</style>