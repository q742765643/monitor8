<template>
  <div id="palne_titile">
    <span id="name"
      >
      <span id="ipClass">{{ip}}</span>
      {{ titleName }}
      <span v-if="currentStatus">
        <span v-if="currentStatus == 0" style="color: #fcff13">
          {{ statusFormat(currentStatusOptions, currentStatus) }}</span
        >
        <span v-if="currentStatus == 1" style="color: #fcab13">
          {{ statusFormat(currentStatusOptions, currentStatus) }}</span
        >
        <span v-if="currentStatus == 2" style="color: red">
          {{ statusFormat(currentStatusOptions, currentStatus) }}</span
        >
        <span v-if="currentStatus == 3" style="color: #0cb218">
          {{ statusFormat(currentStatusOptions, currentStatus) }}</span
        >
        <span v-if="currentStatus == 11" style="color: #efefef">
          {{ statusFormat(currentStatusOptions, currentStatus) }}</span
        >
      </span>
    </span>
    <div id="right"><slot name="right"></slot></div>
  </div>
</template>



<script>
import hongtuConfig from '@/utils/services';
export default {
  props: ['titleName', 'currentStatus', 'ip'],
  data() {
    return {
      currentStatusOptions: [],
    };
  },
  created() {
    this.getDicts('current_status').then((response) => {
      this.currentStatusOptions = response.data;
    });
  },
  methods: {
    /* 字典格式化 */
    statusFormat(list, text) {
      return hongtuConfig.formatterselectDictLabel(list, text);
    },
  },
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
  #ipClass {
    margin-right: 10px;
  }
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
