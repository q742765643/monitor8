<template>
  <div id="title">
    <div id="name">
      <planeTitle :titleName="$attrs.titleName" :currentStatus="$attrs.currentStatus" :ip="$attrs.ip">
        <div slot="right">
          <div id="tool">
            <!--  <span class="iconfont iconyidong"></span>
            <span class="iconfont iconshuaxin1"></span>
            <span class="iconfont iconbianji"></span> -->

            <span
              v-if="!$attrs.showFullType"
              class="iconfont iconfangda"
              @click="fullChart(true, $attrs.titleName, $attrs.titleID, $attrs.currentStatus, $attrs.ip )"
            ></span>

            <span
              v-if="$attrs.showFullType"
              class="iconfont iconsuoxiao"
              @click="fullChart(false, $attrs.titleName, $attrs.titleID, $attrs.currentStatus, $attrs.ip)"
            ></span>
          </div>
        </div>
      </planeTitle>
    </div>
  </div>
</template>

<script>
//commonTitle.vue --> cloundMointor.vue -->index.vue     eventBus
//indexvue -->cloundMointor.vue --->commonTitle.vue      $attrs
//indexvue -->fullChart--->cloundMointor.vue --->commonTitle.vue   $attrs
import planeTitle from '@/components/titile/planeTitle.vue';
import eventBus from '@/components/utils/eventBus.js';
export default {
  components: { planeTitle },
  // props: ['titleName', 'titleID', 'showFullType'],
  data() {
    return {};
  },

  methods: {
    fullChart(type, titleName, titleID = '', currentStatus, ip) {
      console.log(currentStatus);
      eventBus.$emit('fullChart', type, titleName, titleID, currentStatus, ip);
    },
  },
};
</script>

<style lang="scss" scoped>
#tool {
  .iconfont {
    margin: 0 10px;
    font-size: 16px;
    color: #06a5ff;
    cursor: pointer;
  }
  .full {
    font-size: 18px;
  }
}
</style>
