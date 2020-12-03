<template>
  <div id="main_moniter">
    <div id="row1">
      <!--<viewMonitor></viewMonitor>-->
      <distributAlarm class="backColorF"></distributAlarm>
      <deviceState class="backColorF"></deviceState>
      <untreatAlarm class="backColorF"></untreatAlarm>
    </div>
    <div id="row2">
      <dataState class="backColorF"></dataState>
      <softwareState class="backColorF"></softwareState>
    </div>
  </div>
</template>

<script>
  import viewMonitor from './plane/viewMonitor';
  import distributAlarm from './plane/distributAlarm';
  import untreatAlarm from './plane/untreatAlarm';

  import deviceState from './plane/deviceState';
  import dataState from './plane/dataState';
  import softwareState from './plane/softwareState';

  export default {
    components: {
      viewMonitor,
      distributAlarm,
      untreatAlarm,
      deviceState,
      dataState,
      softwareState,
    },
  };
</script>

<style lang="scss" scoped>
  #main_moniter {
    width: 100%;
    height: 100%;
    display: flex;
    #row1 {
      width: 760px;
      /* margin-right: 0.125rem;
    height: calc(12.5rem - 0.5rem - 0.375rem);
    width: calc((100% - 0.25rem) / 2); */
    }
    #row2 {
      width: calc(100% - 770px);
      margin-left: 10px;
      /* margin-left: 0.125rem;
    height: calc(12.5rem - 0.5rem - 0.375rem);
    width: calc((100% - 0.25rem) / 2); */
    }
    /*  .cel1 {
    height: 2.6875rem;
    box-shadow: $plane_shadow;
    width: 100%;
  }
  .cel2 {
    height: 3.75rem;
    margin-top: 0.5rem;
    box-shadow: $plane_shadow;
    width: 100%;
  }
  .cel3 {
    height: 4.375rem;
    margin-top: 0.375rem;
    box-shadow: $plane_shadow;
    width: 100%;
  } */
  }
</style>
