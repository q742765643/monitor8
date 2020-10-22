<template>
  <div class="rightplane">
    <!-- <div class="top">
      <div class="left">
        <span class="title">计算机资源</span>
      </div>
      <div class="right">
        <span class="name">名称</span>
        <span class="cpu">cpu</span>
        <span class="usage">cpu使用率</span>
        <span class="rom">内存</span>
      </div>
    </div>-->
    <div class="top">计算机资源</div>
    <div class="info_content">
      <infobox
        :key="index"
        v-for="(item, index) in dataList"
        :stateData="item.stateData"
        :cpuData="item.cpuData"
        :romData="item.romData"
        @click.native="showinfoWindow"
      ></infobox>
    </div>
  </div>
</template>

<script>
  import infobox from './infobox';

  export default {
    //props: ['stateData', 'cpuData', 'romData'],
    components: { infobox },
    data() {
      return {
        dataList: [],
        showWindow: false,
      };
    },
    created() {
      this.initData();
    },
    mounted() {},
    methods: {
      showinfoWindow() {
        this.$parent.openMonWindow();
      },
      initData() {
        let stateData1 = {
          id: 'stateChart',
          color1: '#02d6fc',
          color2: '#367bec',
          value: 100,
          total: 100,
        };

        let cpuData1 = {
          id: 'cpuChart',
          color1: '#02d6fc',
          color2: '#367bec',
          value: 10,
          total: 100,
        };
        let romData1 = {
          id: 'romChart',
          color1: '#02d6fc',
          color2: '#367bec',
          value: 55,
          total: 100,
        };
        let data = {};
        let data1 = {};
        for (let i = 0; i < 20; i++) {
          stateData1.id = 'stateChart' + i;
          cpuData1.id = 'cpuChart' + i;
          romData1.id = 'romChart' + i;
          data.stateData = stateData1;
          data.cpuData = cpuData1;
          data.romData = romData1;
          data1 = JSON.parse(JSON.stringify(data));
          this.dataList.push(data1);
        }
      },
    },
  };
</script>

<style lang="scss" scoped>
  .rightplane {
    padding: 0.25rem 0.25rem;
    background: white;
    flex: 3;
    display: flex;
    flex-direction: column;
    width: 100%;

    .info_content::-webkit-scrollbar {
      width: 3px;
      background-color: #f5f5f5;
    }

    .info_content::-webkit-scrollbar-thumb {
      background-color: #5aa6ee;
    }
    .info_content::-webkit-scrollbar-track {
      box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.3);
      background-color: #f5f5f5;
    }
    .info_content {
      overflow: auto;
      width: 100%;
      height: 100%;
      background: #fff;
      display: flex;
      flex-wrap: wrap;
      align-content: flex-start;
      //justify-content: space-around;
      div {
        margin-top: -0.1rem;
        padding: 0.1rem;
        background: #eef5fd;
        //background: #063c7a;
        width: 25%;
        height: 3.5rem;
        border: 0.1rem solid #ffffff;
        //border: 0.1rem solid #dd1616;
      }
    }

    .top {
      margin: 0 auto;
      height: 0.5rem;
      font-size: 0.225rem;
      font-weight: 600;
    }
  }
</style>
