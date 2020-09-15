<template>
  <div id="config">
    <div class="contnet">
      <div class="name">
        <span>网段名称</span>
        <div>
          <input type="text" />
        </div>
      </div>
      <div class="startIP">
        <span>起始IP</span>
        <div class="ip">
          <input type="text" />— <input type="text" />— <input type="text" />—
          <input type="text" />
        </div>
      </div>
      <div class="endIP">
        <span>结束IP</span>
        <div class="ip">
          <input type="text" />— <input type="text" />— <input type="text" />—
          <input type="text" />
        </div>
      </div>
      <div class="manager">
        <span class="iconfont iconxinzeng">新增</span>
        <span class="iconfont iconxiugai">修改</span>
        <span class="iconfont icondelete">删除</span>
      </div>
      <div class="tableEdit">
        <div class="title">
          <span class="number">序号</span>
          <span class="netname">网段名称</span>
          <span class="sIP">起始IP</span>
          <span class="eIP">结束IP</span>
          <span class="cTime">创建时间</span>
        </div>

        <div class="info" v-for="(item, index) in tableInfo" :key="index">
          <span class="number">
            <span>{{ index }}</span>
            <input type="checkbox" />
          </span>
          <span class="netname">{{ item.netname }}</span>
          <span class="sIP">{{ item.sIP }}</span>
          <span class="eIP">{{ item.eIP }}</span>
          <span class="cTime">{{ item.cTime }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        tableInfo: [
          { netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
        ],
      };
    },
  };
</script>

<style lang="scss" scoped>
  #config {
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    // display: flex;
    // flex-direction: column;
    background: #eef5fd;
    width: 20rem;

    font-weight: 600;
    .contnet {
      height: 11.625rem;
      width: 19.625rem;
      background: white;
      padding-left: 0.375rem;
      input[type='text'] {
        height: 0.45rem;
        font-size: 16px;
        width: 2.5rem;
        border: 1px solid #c9c1c1 !important;
        border-radius: 0.0375rem;
        &:focus {
          outline: none !important;
          border: 2px solid #0f9fec !important;
        }
      }
      .name {
        padding-top: 0.25rem;
        input {
          width: 4rem;
        }
        span {
          line-height: 0.5rem;
          font-size: 0.24rem;
        }
      }
      .startIP {
        margin-top: 0.25rem;
        span {
          line-height: 0.5rem;
          font-size: 0.24rem;
        }
        .ip {
          display: flex;
          //justify-content: center;
          align-items: center;
          line-height: 0.5rem;
        }
      }
      .endIP {
        margin-top: 0.25rem;
        span {
          line-height: 0.5rem;
          font-size: 0.24rem;
        }
        .ip {
          line-height: 0.5rem;
          display: flex;
          //justify-content: center;
          align-items: center;
        }
      }
      .manager {
        padding-right: 0.5rem;
        margin-top: 0.4rem;
        display: flex;
        justify-content: flex-end;
        span {
          display: block;
          font-weight: 500;
          margin-left: 0.1875rem;
          font-size: 0.25rem;
          cursor: pointer;
        }
      }
      .tableEdit {
        margin-top: 0.5rem;
        width: 100%;
        div:nth-child(2n) {
          background: #eef5fd;
        }
        .title {
          line-height: 0.7rem;
          width: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          span {
            text-align: center;
            display: inline-block;
            font-size: 0.25rem;
          }
          .number {
            flex: 1;
          }
          .netname {
            flex: 3;
          }
          .sIP {
            flex: 3;
          }
          .eIP {
            flex: 3;
          }
          .cTime {
            flex: 4;
          }
        }
        .info {
          &:hover {
            background: #eeeeee;
          }
          @extend .title;
          border-bottom: 1px solid #eeeeee;

          .number {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            input[type='checkbox'] {
              display: inline-block;
              height: 0.45rem;
              font-size: 16px;
              width: 0.5rem;
            }
          }
          font-size: 0.2rem;
          font-weight: 300;
        }
      }
    }
  }
</style>
