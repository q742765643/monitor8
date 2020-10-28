<template>
  <div class="upper">
    <div class="left">
      <span class="web_title"> 气象海洋空间天气信息系统综合监控平台 </span>
    </div>
    <div class="right">
      <div class="user">
        <div class="manger"></div>
        <div>管理员</div>
      </div>
      <div class="tool">
        <span class="icon iconfont iconshuaxin"></span>
        <span class="icon iconfont iconlingdang">
          <span v-if="warnNum" class="warnNum">{{ warnNum }}</span>
        </span>
        <span class="icon iconfont iconskin"></span>
        <span class="icon iconfont iconxinxi"></span>
        <span class="icon iconfont iconguanbi1"></span>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        warnNum: 26,
      };
    },
  };
</script>

<style lang="scss" scoped>
  .icon {
    cursor: pointer;
    font-size: 0.3rem;
    font-weight: 600;
  }
  .upper {
    width: 100%;
    z-index: 100;
    background: #fff;
    display: flex;
    height: 1rem;
    padding-left: 0.625rem;
    box-shadow: $plane_shadow;
    .left {
      float: left;
      .web_title {
        display: block;
        font-size: 0.3rem;
        color: $nav_textColor;
        font-family: Alibaba-PuHuiTi-Medium;
        height: 1rem;
        line-height: 1rem;
      }
    }
    .right {
      height: 1rem;
      position: absolute;
      right: 0.375rem;
      display: flex;
      align-items: center;
      justify-content: center;
      .tool {
        span {
          padding: 0 0.125rem;

          .warnNum {
            padding: 0 0.075rem !important;
            font-weight: 400;
            background: red;
            color: white;
            font-size: 0.15rem;
            padding: 0 0.0375rem;
            border-radius: 0.125rem;
            height: 0.25rem;
            line-height: 0.25rem;
            text-align: center;
            min-width: 0.25rem;
            display: inline-block;
            position: absolute;
            right: 1.75rem;
          }
        }
      }

      .user {
        display: flex;
        margin-right: 0.375rem;
        align-items: center;
        font-size: 0.2rem;

        .manger {
          margin-right: 0.125rem;
          height: 0.375rem;
          width: 0.375rem;
          border-radius: 0.1875rem;
          /*   background-color: #6f572c; */
          padding-right: 0.125rem;
          background-image: url('../../assets/imgs/manager.png');
          background-size: contain;
        }
      }
    }
  }
</style>
