<template>
  <div id="config">
    <div id="linkConfig_contnet">
      <div id="topconfig">
        <div class="name">
          <span>网段名称</span>
          <div>
            <a-input placeholder="" />
          </div>
        </div>
        <div class="startIP">
          <span>起始IP</span>
          <div class="ip">
            <a-input placeholder="" />
            <span class="dunder">—</span> <a-input placeholder="" /><span
              class="dunder"
              >—</span
            >
            <a-input placeholder="" /><span class="dunder">—</span>
            <a-input placeholder="" />
          </div>
        </div>
        <div class="endIP">
          <span>结束IP</span>
          <div class="ip">
            <a-input placeholder="" /><span class="dunder">—</span>
            <a-input placeholder="" /><span class="dunder">—</span>
            <a-input placeholder="" /><span class="dunder">—</span>
            <a-input placeholder="" />
          </div>
        </div>
        <div class="manager">
          <a-button type="primary" icon="plus">
            新增
          </a-button>
          <a-button type="primary" icon="minus">
            修改
          </a-button>
          <a-button type="primary" icon="delete">
            删除
          </a-button>
        </div>
      </div>
      <div class="tableEdit">
        <vxe-table
          border
          ref="xTable2"
          :data="tableInfo"
          align="center"
          :checkbox-config="{ labelField: 'id' }"
          :max-height="tableHeight"
        >
          <!--   <vxe-table-column field="id" title="序号"></vxe-table-column> -->
          <vxe-table-column type="checkbox" title="序号"></vxe-table-column>
          <vxe-table-column field="netname" title="网段名称"></vxe-table-column>
          <vxe-table-column field="sIP" title="起始IP"></vxe-table-column>
          <vxe-table-column field="eIP" title="结束IP"></vxe-table-column>
          <vxe-table-column
            field="cTime"
            title="创建时间"
            show-overflow
          ></vxe-table-column>
        </vxe-table>

        <vxe-pager
          id="page_table"
          perfect
          :current-page.sync="page.currentPage"
          :page-size.sync="page.pageSize"
          :total="tableInfo.length"
          :page-sizes="[10, 20, 100]"
          :layouts="[
            'PrevJump',
            'PrevPage',
            'Number',
            'NextPage',
            'NextJump',
            'Sizes',
            'FullJump',
            'Total',
          ]"
        >
        </vxe-pager>
        <!--   :checkbox-config="{ labelField: 'netname' }" -->
        <!-- <div class="title">
          :checkbox-config="{labelField: 'name'}"
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
        </div> -->
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      page: {
        currentPage: 1,
        pageSize: 10,
      },
      tableHeight: null,
      tableInfo: [
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '1',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '2',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '3',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '4',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '5',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
        {
          id: '6',
          netname: '机房网段',
          sIP: '10.65.24.1',
          eIP: '10.65.24.159',
          cTime: '2019-08-24',
        },
      ],
    };
  },

  mounted() {
    this.$nextTick(() => {
      this.setTableHeight();
    });
    window.addEventListener('resize', () => {
      this.setTableHeight();
    });
  },

  methods: {
    setTableHeight() {
      let h = document.getElementById('linkConfig_contnet').clientHeight;
      let h_config = document.getElementById('topconfig').clientHeight;
      let padding = getComputedStyle(
        document.getElementById('linkConfig_contnet'),
        false
      )['paddingTop'];

      let h_page = document.getElementById('page_table').clientHeight;

      debugger;
      this.tableHeight = h - h_config - parseInt(padding) * 2 - h_page - 1;
    },
  },
};
</script>

<style lang="scss" scoped>
#config {
  //padding: 0.5rem 0.375rem 0.375rem 0.2rem;
  // display: flex;
  // flex-direction: column;
  background: #eef5fd;
  width: 100%;

  //font-weight: 600;

  #linkConfig_contnet {
    box-shadow: $plane_shadow;
    height: 11.625rem;
    width: 100%;
    //width: 19.625rem;
    background: white;
    padding: 0.25rem;
    font-size: 0.2rem;
    /*  input[type='text'] {
      height: 0.35rem;
      font-size: 16px;
      width: 2rem;
      // border: 1px solid #c9c1c1 !important;
      border: 1px solid #dcdfe6;
      border-radius: 0.05rem;
      &:focus {
        outline: none !important;
        border: 1px solid #0f9fec !important;
      }
    } */
    .name {
      font-family: Alibaba-PuHuiTi-Medium;
      input {
        width: 2.5rem;
        font-family: Alibaba-PuHuiTi-Regular;
      }
      span {
        line-height: 0.5rem;
      }
    }
    .startIP {
      font-family: Alibaba-PuHuiTi-Medium;
      margin-top: 0.25rem;
      span {
        line-height: 0.5rem;
      }
      .ip {
        display: flex;
        //justify-content: center;
        align-items: center;
        line-height: 0.5rem;
        input {
          width: 2.5rem;
          font-family: Alibaba-PuHuiTi-Regular;
        }
      }
    }
    .endIP {
      font-family: Alibaba-PuHuiTi-Medium;
      margin-top: 0.25rem;
      span {
        line-height: 0.5rem;
      }
      .ip {
        line-height: 0.5rem;
        display: flex;
        //justify-content: center;
        align-items: center;
        input {
          width: 2.5rem;
        }
      }
    }
    .manager {
      padding-right: 0.5rem;
      margin-top: 0.4rem;
      display: flex;
      justify-content: flex-end;

      .ant-btn {
        margin-right: 0.125rem;
      }
    }
    .tableEdit {
      padding-right: 0.3rem;
      margin-top: 0.1rem;
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

.dunder {
  display: inline-block;
  padding: 0.1rem;
  font-family: Alibaba-PuHuiTi-Light;
}
</style>
