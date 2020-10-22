<template>
  <div class="infobox" v-on:click="showinfoWindow">
    <div class="cell1">
      <div class="master">
        <span>主</span>
      </div>
      <div class="name">dpc33</div>
    </div>
    <div class="cell2">
      <pieChart :ringData="stateData"></pieChart>

      <div class="number">
        <p>CPU {{ stateData.value }}</p>
        <p>/{{ stateData.value }}</p>
      </div>
    </div>
    <div class="cell3">
      <pieChart :ringData="cpuData"></pieChart>
      <div class="number">
        <p>内存 {{ cpuData.value }}</p>
        <p>/{{ cpuData.total }}</p>
      </div>
    </div>
    <div class="cell4">
      <pieChart :ringData="romData"></pieChart>
      <div class="number">
        <p>硬盘 {{ romData.value }}</p>
        <p>/100{{ romData.total }}</p>
      </div>
    </div>
    <div class="cell5">
      <div class="baakimg">
        <span class="ip">192.168.0.1</span>
      </div>
      <div class="info">
        <span class="thred">进程20个</span>
        <span class="net">网络在线</span>
      </div>
    </div>
  </div>
</template>

<script>
  import pieChart from './chart';
  export default {
    props: ['stateData', 'cpuData', 'romData'],
    components: { pieChart },
    mounted() {
      this.stateData;
    },
    methods: {
      showinfoWindow() {
        //this.$parent.showinfoWindow();
      },
    },
  };
</script>

<style lang="scss" scoped>
  .infobox {
    height: 100%;
    width: 100%;

    display: flex;
    flex-direction: column;

    .number {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-left: 0.3rem;
      p {
        display: inline;
        font-weight: 200;
      }
      p:nth-child(1) {
        font-weight: 600;
        font-size: 0.2rem;
      }
    }
    .cell1 {
      flex: 1;
      border: 0.05rem solid #eef5fd;
      // background: #146cdf;
      width: 100%;
      display: flex;
      font-weight: 600;
      align-items: center;
      background: rgba(216, 216, 216, 0.4);
      .master {
        width: 0.4rem;
        height: 0.4rem;
        line-height: 0.4rem;
        text-align: center;
        background: chartreuse;
        border-radius: 50%;
      }
      .name {
        font-size: 0.2rem;
        padding-left: 0.2rem;
        height: 0.55rem;
        line-height: 0.55rem;
      }
    }
    .cell2 {
      background: rgba(216, 216, 216, 0.4);
      flex: 1;
      display: flex;
      width: 100%;
      align-items: center;
      border: 0.05rem solid #eef5fd;
    }
    .cell3 {
      background: rgba(216, 216, 216, 0.4);
      flex: 1;
      display: flex;
      width: 100%;
      align-items: center;
      border: 0.05rem solid #eef5fd;
    }
    .cell4 {
      background: rgba(216, 216, 216, 0.4);
      flex: 1;
      display: flex;
      width: 100%;
      align-items: center;
      border: 0.05rem solid #eef5fd;
    }
    .cell5 {
      border: 0.05rem solid #eef5fd;
      flex: 2;
      display: flex;
      background: rgba(216, 216, 216, 0.4);
      width: 100%;

      .baakimg {
        //width: 50%;
        width: 50%;
        height: 1rem;
        //background-image: url('../../../');
        background: url('../../../../../../../static/toupu/master.png') center center no-repeat;
        background-size: 1rem 1rem;

        .ip {
          color: rgb(2, 1, 2);
          position: relative;
          display: block;
          text-align: center;
          margin: 0 auto;
          top: 0.6rem;
          font-weight: 600;
        }
      }

      .info {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: flex-start;
        margin-left: 0.3rem;
        font-size: 0.2rem;
        .thred {
          font-weight: 200;
        }
        .net {
          font-weight: 200;
        }
      }
    }
  }
</style>
