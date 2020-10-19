<template>
  <div class="managerTemplate">
    <div class="head">
      <div class="leftBox">
        <a-input
          style="width:2rem"
          v-model="querform.toolName"
          placeholder="设备别名"
          size="small"
        />
        <a-select
          default-value="status"
          v-model="querform.toolStatus"
          placeholder="设备别名"
          style="width: 1.5rem"
          size="small"
        >
          <a-select-option value="status">
            状态
          </a-select-option>
        </a-select>

        <a-input
          style="width:2rem"
          v-model="querform.toolIp"
          placeholder="IP地址"
          size="small"
        />

        <a-select
          default-value="status"
          v-model="querform.toolType"
          placeholder="设备类型"
          style="width: 1.5rem"
          size="small"
        >
          <a-select-option value="status">
            设备类型
          </a-select-option>
        </a-select>

        <a-select
          default-value="status"
          v-model="querform.toolMethods"
          placeholder="设备类型"
          style="width: 1.5rem"
          size="small"
        >
          <a-select-option value="status">
            设备类型
          </a-select-option>
        </a-select>

        <a-select
          default-value="status"
          v-model="querform.toolArea"
          placeholder="设备别名"
          style="width: 1.5rem"
          size="small"
        >
          <a-select-option value="status">
            设备别名
          </a-select-option>
        </a-select>
      </div>
      <div class="rightBox">
        <a-button type="primary" icon="search"> 搜索 </a-button>

        <a-button type="primary" icon="plus-circle">
          添加设备
        </a-button>

        <a-button type="primary" icon="delete">
          取消监视
        </a-button>

        <a-button type="primary" icon="plus-circle">
          生成报表
        </a-button>
      </div>
    </div>

    <div id="linkManger_content">
      <div id="tablediv">
        <vxe-table
          :tree-config="{ key: 'id', children: 'children' }"
          :data.sync="tableData"
          align="center"
          :height="tableheight"
        >
          <vxe-table-column type="checkbox" tree-node></vxe-table-column>
          <vxe-table-column field="status" title="状态"></vxe-table-column>
          <vxe-table-column field="name" title="设备名称"></vxe-table-column>
          <vxe-table-column
            field="IP"
            title="IP地址"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column field="Type" title="设备类型"></vxe-table-column>

          <vxe-table-column
            field="time"
            title="发现时间"
            show-overflow
          ></vxe-table-column>

          <vxe-table-column
            field="DNS"
            title="网关"
            show-overflow
          ></vxe-table-column>

          <vxe-table-column
            field="MAC"
            title="MAC地址"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column
            field="area"
            title="区域"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column
            field="address"
            title="详细位置"
            show-overflow
          ></vxe-table-column>

          <vxe-table-column
            field="onlineTime"
            title="运行时长"
          ></vxe-table-column>
          <vxe-table-column
            field="maxLost"
            title="最大丢包率"
          ></vxe-table-column>
          <vxe-table-column
            field="aveLost"
            title="平均丢包率"
          ></vxe-table-column>

          <vxe-table-column width="160" field="date" title="操作">
            <template>
              <!--  <template>
                <a-button type="primary" icon="check-circle">
                  保存
                </a-button>
                <a-button type="primary" icon="close-circle">
                  取消
                </a-button>
              </template> -->
              <a-button type="primary" icon="edit" @click="editRowEvent(row)">
                编辑
              </a-button>
            </template>
          </vxe-table-column>
        </vxe-table>

        <vxe-pager
          id="page_table"
          perfect
          :current-page.sync="page.currentPage"
          :page-size.sync="page.pageSize"
          :total="tableData.length"
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
      </div>

      <div class="rightBox"></div>
    </div>
  </div>
</template>

<script>
import echarts from 'echarts';

export default {
  data() {
    return {
      page: {
        currentPage: 1,
        pageSize: 10,
      },
      showChartFlag: false,
      querform: {
        toolName: '',
        toolStatus: '',
        toolIp: '',
        toolType: '',
        toolMethods: '',
        toolArea: '',
      },
      tableheight: null,
      tableData: [
        {
          status: '正常',
          name: 'cp3',
          IP: '192.168.0.1',
          Type: '服务器',
          time: '2020-09-01',
          DNS: 'xxx.xxx.xxx.xx',
          MAC: 'xxxxxxx',
          area: '办公区',
          address: '办公区4楼405',
          onlineTime: '120小时',
          maxLost: '10%',
          aveLost: '5%',
          children: [
            {
              status: '正常',
              name: 'cp3',
              IP: '192.168.0.1',
              Type: '服务器',
              time: '2020-09-01',
              DNS: 'xxx.xxx.xxx.xx',
              MAC: 'xxxxxxx',
              area: '办公区',
              address: '办公区4楼405',
              onlineTime: '120小时',
              maxLost: '10%',
              aveLost: '5%',
              children: [
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
              ],
            },
            {
              status: '正常',
              name: 'cp3',
              IP: '192.168.0.1',
              Type: '服务器',
              time: '2020-09-01',
              DNS: 'xxx.xxx.xxx.xx',
              MAC: 'xxxxxxx',
              area: '办公区',
              address: '办公区4楼405',
              onlineTime: '120小时',
              maxLost: '10%',
              aveLost: '5%',
              children: [
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
              ],
            },
          ],
        },
        {
          status: '正常',
          name: 'cp3',
          IP: '192.168.0.1',
          Type: '服务器',
          time: '2020-09-01',
          DNS: 'xxx.xxx.xxx.xx',
          MAC: 'xxxxxxx',
          area: '办公区',
          address: '办公区4楼405',
          onlineTime: '120小时',
          maxLost: '10%',
          aveLost: '5%',
          children: [
            {
              status: '正常',
              name: 'cp3',
              IP: '192.168.0.1',
              Type: '服务器',
              time: '2020-09-01',
              DNS: 'xxx.xxx.xxx.xx',
              MAC: 'xxxxxxx',
              area: '办公区',
              address: '办公区4楼405',
              onlineTime: '120小时',
              maxLost: '10%',
              aveLost: '5%',
              children: [
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
              ],
            },
            {
              status: '正常',
              name: 'cp3',
              IP: '192.168.0.1',
              Type: '服务器',
              time: '2020-09-01',
              DNS: 'xxx.xxx.xxx.xx',
              MAC: 'xxxxxxx',
              area: '办公区',
              address: '办公区4楼405',
              onlineTime: '120小时',
              maxLost: '10%',
              aveLost: '5%',
              children: [
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
              ],
            },
          ],
        },

        {
          status: '正常',
          name: 'cp3',
          IP: '192.168.0.1',
          Type: '服务器',
          time: '2020-09-01',
          DNS: 'xxx.xxx.xxx.xx',
          MAC: 'xxxxxxx',
          area: '办公区',
          address: '办公区4楼405',
          onlineTime: '120小时',
          maxLost: '10%',
          aveLost: '5%',
          children: [
            {
              status: '正常',
              name: 'cp5',
              IP: '192.168.0.1',
              Type: '服务器',
              time: '2020-09-01',
              DNS: 'xxx.xxx.xxx.xx',
              MAC: 'xxxxxxx',
              area: '办公区',
              address: '办公区4楼405',
              onlineTime: '120小时',
              maxLost: '10%',
              aveLost: '5%',
              children: [
                {
                  status: '正常',
                  name: 'cp4',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
              ],
            },
            {
              status: '正常',
              name: 'cp3',
              IP: '192.168.0.1',
              Type: '服务器',
              time: '2020-09-01',
              DNS: 'xxx.xxx.xxx.xx',
              MAC: 'xxxxxxx',
              area: '办公区',
              address: '办公区4楼405',
              onlineTime: '120小时',
              maxLost: '10%',
              aveLost: '5%',
              children: [
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
                {
                  status: '正常',
                  name: 'cp3',
                  IP: '192.168.0.1',
                  Type: '服务器',
                  time: '2020-09-01',
                  DNS: 'xxx.xxx.xxx.xx',
                  MAC: 'xxxxxxx',
                  area: '办公区',
                  address: '办公区4楼405',
                  onlineTime: '120小时',
                  maxLost: '10%',
                  aveLost: '5%',
                },
              ],
            },
          ],
        },
      ],
    };
  },
  created() {},
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
      let h = document.getElementById('tablediv').offsetHeight;
      let padding = getComputedStyle(
        document.getElementById('linkManger_content'),
        false
      )['paddingTop'];
      let h_page = document.getElementById('page_table').offsetHeight;

      // let chartHeight = document.getElementById("chartdiv").clientHeight;
      this.tableheight = h + parseInt(padding) * 2 - h_page - 1;
    },
    showChart() {
      this.showChartFlag = true;
      this.$nextTick(() => {});
    },
    drawChart() {},

    handlerExpand(m) {
      console.log('展开/收缩');
      m.isExpand = !m.isExpand;
    },
    handlerChecked(m) {
      console.log(m);
      console.log(m.isChecked);
      this.resetData(m.children, m.isChecked);
    },
    resetData(dataArr, checked) {
      for (var i in dataArr) {
        dataArr[i].isChecked = checked;
        this.resetData(dataArr[i].children, checked);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.managerTemplate {
  // background: #eef5fd;
  //width: 20rem;
  width: 100%;
  height: 100%;
  font-family: Alibaba-PuHuiTi-Regular;

  .head {
    box-shadow: $plane_shadow;
    width: 100%;
    height: 1.25rem;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 0.1rem;

    .leftBox {
      padding-right: 0.075rem;
      height: 1.25rem;

      line-height: 1.25rem;
      width: 65%;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .rightBox {
      text-align: center;
      //height: 1.25rem;
      line-height: 1.25rem;

      width: calc(34% - 0.2rem);
    }
  }
  #linkManger_content {
    box-shadow: $plane_shadow;
    width: 100%;
    height: calc(100% - 1.35rem);
    background: white;
    padding: 0.25rem;
    overflow: hidden;
    #tablediv {
      height: calc(100% - 0.6rem);
    }
  }
  /*  .dialogBox {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    overflow: auto;
    z-index: 1000;
    background: rgba(0, 0, 0, 0.75);
    .eldialog {
      width: 7.875rem;
      height: 4.125rem;
      position: relative;
      margin: 0 auto 0.625rem;
      margin-top: 15vh;
      background: #fff;
      border-radius: 0.025rem;
      box-shadow: 0 0.0125rem 0.0375rem rgba(0, 0, 0, 0.3);
      box-sizing: border-box;
      .dialogHeader {
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.15rem;
        span {
          font-size: 0.175rem;
        }
      }
      #itemChartBody {
        width: 100%;
        height: 3.375rem;
      }
    }
  } */
}
</style>
