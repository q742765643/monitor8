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
    <audio muted style="display: none" id="ring1" src="../../assets/sounds/click.mp3"></audio>
  </div>
</template>

<script>
  import request from '@/utils/request';
  import { createWebSocket } from '@/components/utils/WebSocket.js';
  export default {
    data() {
      return {
        warnNum: 0,
      };
    },
    created() {
      request({
        url: '/main/getAlarm',
        method: 'get',
      }).then((data) => {
        this.warnNum = data.data.length;
      });
      var domain=window.location.host;
      createWebSocket('ws://'+domain+'/ws/webSocket/12345', '');
    },
    mounted() {},
    destroyed() {},
    methods: {},
  };
</script>

<style lang="scss" scoped>
  .upper {
    width: 100%;
    height: 100%;
    z-index: 100;

    display: flex;
    position: relative;
    padding-left: 34px;
    box-shadow: $plane_shadow;
    align-items: center;
    justify-content: space-between;
    .left {
      .web_title {
        font-size: 30px;
        font-family: NotoSansHans-Medium;
        font-weight: 500;
        color: #4a5a7f;
      }
    }
    .right {
      display: flex;
      align-items: center;
      justify-content: center;
      .icon {
        cursor: pointer;
        font-size: 22px;
        font-weight: 600;
      }
      .tool {
        span {
          padding: 0 12px;
          position: relative;
          .warnNum {
            padding: 0 !important;
            font-weight: 400;
            background: red;
            color: white;
            font-size: 12px;
            border-radius: 10px;
            height: 22px;
            width: 22px;
            line-height: 23px;
            text-align: center;
            min-width: 10px;
            display: inline-block;
            position: absolute;
            right: 0;
            top: -12px;
          }
        }
      }

      .user {
        display: flex;
        margin-right: 24px;
        align-items: center;
        font-size: 16px;
        color: #666;
        .manger {
          height: 40px;
          width: 40px;
          border-radius: 50%;
          margin-right: 12px;
          background-image: url('../../assets/imgs/manager.png');
          background-size: contain;
        }
      }
    }
  }
</style>
