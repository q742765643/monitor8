<template>
  <div id="res">
    <resleft :stateData="stateData" :cpuData="cpuData" :romData="romData"></resleft>

    <resright></resright>
    <monWindow v-if="showType"></monWindow>
  </div>
</template>

<script>
  import monWindow from './rightPlane/mointorwindow';
  import resleft from './leftPlane/resleft';
  import resright from './rightPlane/resright';
  export default {
    data() {
      return {
        showType: false,
        stateData: {
          id: 'stateChart22',
          color1: '#02d6fc',
          color2: '#367bec',
          value: 3,
          total: 3,
        },
        cpuData: {
          id: 'cpuChart22',
          color1: '#02d6fc',
          color2: '#367bec',
          value: 10,
          total: 100,
        },
        romData: {
          id: 'romChart22',
          color1: '#02d6fc',
          color2: '#367bec',
          value: 55,
          total: 100,
        },
      };
    },
    components: { resleft, resright, monWindow },
    methods: {
      closeMonWindow() {
        this.showType = false;
      },
      openMonWindow() {
        this.showType = true;
      },
    },
  };
</script>

<style lang="scss" scoped>
  #res {
    width: 20rem;
    height: 100%;
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    background: #eef5fd;
    display: flex;
    .rightplane {
      background: white;
      flex: 3;
      display: flex;
    }
  }
</style>
