<template>
  <div id="softwareMointor">
    <commonTitle v-bind="$attrs"></commonTitle>
    <div class="softwareView_chart" :id="chartID"></div>
  </div>
</template>

<script>
import commonTitle from '../../commonFull/commonTitle';
export default {
  props: ['chartID'],
  data() {
    return {};
  },
  name: 'QTReport',
  components: { commonTitle },
};
</script>

<style lang="scss" scoped>
#softwareMointor {
  width: 100%;
  height: 5rem;

  .softwareView_chart {
    width: 100%;
    height: calc(5rem - 0.75rem);
  }
}
</style>
