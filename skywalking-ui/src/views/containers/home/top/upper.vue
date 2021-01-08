<template>
  <div class="upper">
    <div class="left">
      <div class="date">
        <div class="day">{{ date }}</div>
        <div class="week">{{ week }}</div>
      </div>
      <div class="time">
        <span>{{ time }}</span>
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
      date: '',
      week: '',
      time: '',
      timer: null,
      warnNum: 10,
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
  font-size: 0.275rem;
  font-weight: 600;
}
.upper {
  display: flex;
  height: 1rem;
  padding-left: 0.625rem;
  .left {
    float: left;
    height: 1rem;
    display: flex;
    .date {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      .day {
        font-family: electronicFont;
        font-size: 0.275rem;
      }
      .week {
        font-family: STHeiti;
        font-size: 0.225rem;
      }
    }
    .time {
      font-size: 0.375rem;
      font-family: electronicFont;
      margin-left: 0.375rem;
      //height: 100%;
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
          background: red;
          color: white;
          font-size: 0.125rem;
          padding: 0 0.0375rem;
          border-radius: 0.125rem;
          height: 0.25rem;
          line-height: 0.25rem;
          text-align: center;
          min-width: 0.25rem;
          // padding: 0 !important;
          //width: auto;
          // height: 0.2rem;
          display: inline-block;
          position: relative;
          left: -0.0625rem;
          top: -0.125rem;
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
        background-image: url('../../../../assets/icons/manager.png');
        background-size: contain;
      }
    }
  }
}
</style>
