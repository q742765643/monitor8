<template>
  <div class="upper">
    <div class="left">
      <!-- <span class="web_title"> 气象海洋空间天气信息系统综合监控平台 </span> -->
      <div class="timeBox">
        <div class="date">
          <div class="day">{{ date }}</div>
          <div class="week">{{ week }}</div>
        </div>
        <div class="time">
          <span>{{ time }}</span>
        </div>
      </div>
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
import moment from 'moment';
export default {
  data() {
    return {
      warnNum: 26,
      date: '',
      week: '',
      time: '',
      timer: null,
    };
  },
  created() {
    this.getTime();
  },
  mounted() {
    let data;

    this.timer = setInterval(() => this.getTime(), 1000);
  },
  destroyed() {
    clearInterval(this.timer); // 清除定时器
    this.timer = null;
  },
  methods: {
    getTime: function () {
      moment.locale('zh-cn');
      this.date = moment().format('YYYY-MM-DD');
      this.week = moment().format('dddd');
      this.time = moment().format('LTS');
    },
  },
};
</script>

<style lang="scss" scoped>
.icon {
  cursor: pointer;
  font-size: 21px;
  font-weight: 600;
}
.upper {
  width: 100%;
  z-index: 100;
  background: #fff;
  display: flex;
  position: relative;
  height: 89px;
  padding-left: 50px;
  margin-bottom: 30px;
  box-shadow: $plane_shadow;
  // .left {
  //   float: left;
  //   .web_title {
  //     display: block;
  //     font-size: 40px;
  //     color: $nav_textColor;
  //     font-family: Alibaba-PuHuiTi-Medium;
  //     height: 150px;
  //     line-height: 150px;
  //   }
  // }
  .left {
    float: left;
    height: 89px;
    .timeBox {
      height: 100px;
      display: flex;
      position: relative;
      justify-content: center;
      &::after {
        content: '';
        height: 1px;
        background: $borderColor;
        // width: calc(100% - 0.75rem);
        position: absolute;
        bottom: 0;
        left: 10px;
      }
      .date {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        margin-left: 0px;
        .day {
          font-family: LCD-BOLD;
          font-size: 16px;
        }
        .week {
          font-family: LCD-BOLD;
          font-size: 16px;
        }
      }
      .time {
        font-size: 24px;
        font-family: LCD-BOLD;
        margin-left: 40px;
        //height: 100%;
        line-height: 100px;
      }
    }
  }
  .right {
    height: 89px;
    // width: 300px;
    position: absolute;
    right: 25px;
    display: flex;
    align-items: center;
    justify-content: center;
    .tool {
      span {
        padding: 0 20px;
        .warnNum {
          padding: 0 !important;
          font-weight: 400;
          background: red;
          color: white;
          font-size: 12px;
          // padding: 5px;
          border-radius: 10px;
          height: 15px;
          width: 15px;
          line-height: 16px;
          text-align: center;
          min-width: 10px;
          display: inline-block;
          position: absolute;
          right: 195px;
        }
      }
    }

    .user {
      display: flex;
      margin-right: 25px;
      align-items: center;
      font-size: 16px;

      .manger {
        margin-right: 10px;
        height: 39px;
        width: 39px;
        border-radius: 50%;
        /*   background-color: #6f572c; */
        padding-right: 15px;
        background-image: url('../../assets/imgs/manager.png');
        background-size: contain;
      }
    }
  }
}
</style>
