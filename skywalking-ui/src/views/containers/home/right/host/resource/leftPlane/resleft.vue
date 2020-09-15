<template>
  <div class="leftplane">
    <div class="state">
      <span class="name0">主机节点状态</span>
      <div class="leftChart">
        <pieChart :ringData="stateData"></pieChart>
        <div class="info">
          <span class="name">就绪节点</span>
          <span class="number">
            <p>{{stateData.value}}</p>
            <p>/{{stateData.total}}</p>
          </span>
        </div>
      </div>
    </div>
    <div class="usage">
      <span class="name1">主机</span>
      <span class="name2">计算机资源总利用率</span>
      <div class="leftChart">
        <pieChart :ringData="cpuData"></pieChart>
        <div class="info">
          <span class="name">就绪节点</span>
          <span class="number">
            <p>{{cpuData.value}}</p>
            <p>/{{cpuData.total}}</p>
          </span>
        </div>
      </div>
      <div class="leftChart">
        <pieChart :ringData="romData"></pieChart>
        <div class="info">
          <span class="name">就绪节点</span>
          <span class="number">
            <p>{{romData.value}}</p>
            <p>/1{{romData.total}}</p>
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import pieChart from './chart';
export default {
  props: ['stateData', 'cpuData', 'romData'],
  components: { pieChart },
};
</script>

<style lang="scss" scoped>
.leftplane {
  flex: 1;
  background: white;
  padding: 0.25rem 0.25rem;
  height: 100%;
  .leftChart {
    margin-top: 0.125rem;
    width: 100%;
    height: 1.25rem;
    background: #f0f0eb;
    display: flex;
    .info {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: flex-start;
      margin-left: 0.3rem;
      .name {
        font-weight: 200;
      }
      .number {
        p {
          display: inline;
          font-weight: 200;
        }
        p:nth-child(1) {
          font-weight: 600;
          font-size: 0.3rem;
        }
      }
    }
  }
  .state {
    .name0 {
      padding-left: 0.3rem;
      font-size: 0.225rem;
      font-weight: 600;
    }
  }

  .usage {
    margin-top: 0.125rem;
    .name1 {
      padding-left: 0.3rem;
      font-size: 0.225rem;
      font-weight: 500;
      font-weight: 600;
    }
    .name2 {
      font-size: 0.225rem;
      font-weight: 500;
    }
  }
}
</style>