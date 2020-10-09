<template>
  <div id="rightPlane">
    <div id="title">
      <div id="name"><span>计算资源</span></div>
      <div id="sort">
        <span class="iconfont iconpaixu2"> <p>名称</p> </span><span class="iconfont iconpaixu2"> <p>CPU</p> </span
        ><span class="iconfont iconpaixu2"><p>CPU</p> </span><span class="iconfont iconpaixu2"> <p>内存</p> </span>
      </div>
    </div>
    <div id="contnet">
      <div class="box" v-for="(item, index) in list" :key="index">
        <infoBox :name="item.name" :chartId="item.name + index"></infoBox>
      </div>
    </div>
  </div>
</template>

<script>
import infoBox from './resInfobox.vue';
export default {
  data() {
    return {
      list: [
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
        { name: 'DCP33' },
      ],
    };
  },
  components: { infoBox },
};
</script>

<style lang="scss" scoped>
#rightPlane {
  width: 100%;
  height: 100%;
  #title {
    height: 1.25rem;
    line-height: 1.25rem;
    width: 100%;
    box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    display: flex;
    #name {
      letter-spacing: 0.025rem;
      flex: 1;
      font-size: 0.275rem;
      font-weight: 500;
      padding-left: 0.2rem;
    }
    #sort {
      flex: 1;
      display: flex;
      .iconfont {
        color: #3f6bff;
        font-size: 0.2rem;
        cursor: pointer;
      }
      span {
        text-align: center;
        flex: 1;
        font-size: 0.2rem;

        p {
          color: #999;
          display: inline;
          position: relative;
          left: -0.8rem;
          font-size: 0.2rem !important;
        }
      }
    }
  }
  #contnet {
    box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
    margin-top: 0.1rem;
    height: calc(100% - 1.35rem);
    width: 100%;
    overflow: auto;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    .box {
      width: calc((100% - 0.75rem) / 3);
      height: 5rem;
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      margin-bottom: 0.375rem;
    }
  }

  #contnet:after {
    content: '';
    width: calc((100% - 0.75rem) / 3);
    border: 1px solid transparent;
  }
}

#contnet::-webkit-scrollbar {
  width: 0.15rem;
  background-color: #f6f7f9;
}

#contnet::-webkit-scrollbar-thumb {
  background-color: #d8dce8;
}
#contnet::-webkit-scrollbar-track {
  //box-shadow: inset 0 0 0.2rem rgba(0, 0, 0, 0.3);
  background-color: #f6f7f9;
}
</style>
