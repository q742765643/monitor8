<template>
  <div id="rightPlane">
    <div id="title">
      <div><span>计算资源</span></div>
      <div id="sort">
        <span class="iconfont iconpaixu2"> <p>名称</p> </span><span class="iconfont iconpaixu2"> <p>CPU</p> </span
        ><span class="iconfont iconpaixu2"><p>CPU</p> </span><span class="iconfont iconpaixu2"> <p>内存</p> </span>
      </div>
    </div>
    <el-scrollbar>
      <div id="contnet">
        <div class="box" v-for="(item, index) in list" :key="index">
          <infoBox :current="item" :name="item.ip" :chartId="item.ip + index"></infoBox>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
  import request from '@/utils/request';
  import infoBox from './resInfobox.vue';
  export default {
    data() {
      return {
        list: [],
      };
    },
    created() {
      request({
        url: '/overview/get',
        method: 'get',
      }).then((data) => {
        this.list = data.data;
      });
    },
    components: { infoBox },
  };
</script>

<style lang="scss" scoped>
  #rightPlane {
    width: 100%;
    height: 100%;
    background: #f6fbfc;
    #title {
      background: #fff;
      display: flex;
      justify-content: space-between;
      border-bottom: 1px solid $plane_border_color;
      font-size: 20px;
      height: 56px;
      line-height: 56px;
      padding-left: 18px;
      #sort {
        flex: 1;
        display: flex;
        .iconfont {
          color: #3f6bff;
          font-size: 16px;
          cursor: pointer;
        }
        span {
          text-align: center;
          flex: 1;
          font-size: 16px;
          p {
            color: #999;
            display: inline;
            position: relative;
            left: -64px;
            font-size: 16px !important;
          }
        }
      }
    }
    .el-scrollbar {
      height: calc(100% - 56px);
      #contnet {
        // background: #fff;
        box-shadow: $plane_shadow;
        // margin-top: 10px;
        // height: calc(100% - 70px);
        height: 100%;
        width: 100%;
        // overflow: auto;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        .box {
          width: calc(100% / 3); //todo
          // height: 5rem;
          // box-shadow: $plane_shadow;
          margin-bottom: 10px;
          background: #fff;
        }
      }
    }
    #contnet:after {
      content: '';
      width: calc((100% - 0.75rem) / 3);
      border: 1px solid transparent;
    }
  }

  // #contnet::-webkit-scrollbar {
  //   width: 0.15rem;
  //   background-color: #f6f7f9;
  // }

  // #contnet::-webkit-scrollbar-thumb {
  //   background-color: #d8dce8;
  // }
  // #contnet::-webkit-scrollbar-track {
  //   //box-shadow: inset 0 0 0.2rem rgba(0, 0, 0, 0.3);
  //   background-color: #f6f7f9;
  // }
</style>
