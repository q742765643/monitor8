<template>
  <div id="left">
    <div id="mountNode"></div>
    <div id="legend">
      <span class="titile">图例</span>
      <div>
        <span class="lgd1"></span>
        <span>值班区</span>
      </div>
      <div>
        <span class="lgd2"></span>
        <span>办公区4楼</span>
      </div>
      <div>
        <span class="lgd3"></span>
        <span>办公区3楼</span>
      </div>
      <div>
        <span class="lgd4"></span>
        <span>机房区</span>
      </div>
      <div>
        <span class="lgd5"></span>
        <span>其他区</span>
      </div>
    </div>
    <!--   <editWindow :showEditWindow="showEditWindow"></editWindow> -->
    <mointorWindow v-if="showMointorWindow"></mointorWindow>

    <!--  编辑框 -->
    <template>
      <vxe-modal
        :mask="false"
        v-model="showEditWindow"
        size="mini"
        title="添加设备"
      >
        <vxe-form
          ref="xForm"
          title-width="80"
          title-align="right"
          :title-colon="true"
        >
          <vxe-form-item title="设备别名" field="othername" span="24">
            <template v-slot="scope">
              <vxe-input
                placeholder="请输入设备别名"
                clearable
                @input="$refs.xForm.updateStatus(scope)"
              ></vxe-input>
            </template>
          </vxe-form-item>

          <vxe-form-item title="设备名称" field="name" span="24">
            <!--   <template v-slot="scope">
          <vxe-input placeholder="请输入设备名称" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->
            hostname
          </vxe-form-item>

          <vxe-form-item title="设备IP" field="IP" span="24">
            <!--  <template v-slot="scope">
          <vxe-input placeholder="请输入设备IP" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->
            192.168.0.1
          </vxe-form-item>

          <vxe-form-item title="网关" field="gateway" span="24">
            <!--  <template v-slot="scope">
          <vxe-input placeholder="请输入网关" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->
            114.108.12.155
          </vxe-form-item>

          <vxe-form-item title="监视方式" field="monitorType" span="24">
            <vxe-select v-model="monitor" placeholder="默认尺寸">
              <vxe-option
                v-for="(item, index) in monitorType"
                :key="index"
                :value="item"
                :label="item"
              ></vxe-option>
            </vxe-select>
          </vxe-form-item>

          <vxe-form-item title="设备类型" field="deviceType" span="24">
            <!--  <template v-slot="scope">
          <vxe-input placeholder="请输入设备类型" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->

            <vxe-select v-model="type" placeholder="默认尺寸">
              <vxe-option
                v-for="(item, index) in deviceType"
                :key="index"
                :value="item"
                :label="item"
              ></vxe-option>
            </vxe-select>
          </vxe-form-item>

          <vxe-form-item title="设备负责人" field="devCharge" span="24">
            <template v-slot="scope">
              <vxe-input
                placeholder="请输入设备负责人"
                clearable
                @input="$refs.xForm.updateStatus(scope)"
              ></vxe-input>
            </template>
          </vxe-form-item>

          <vxe-form-item title="区域" field="position" span="24">
            <!-- <template v-slot="scope">
          <vxe-input placeholder="请输入设备负责人" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->
            <vxe-select v-model="position" placeholder="默认尺寸">
              <vxe-option
                v-for="(item, index) in positionType"
                :key="index"
                :value="item"
                :label="item"
              ></vxe-option>
            </vxe-select>
          </vxe-form-item>
          <vxe-form-item title="详细地址" field="address" span="24">
            <template v-slot="scope">
              <vxe-input
                placeholder="请输入详细地址"
                clearable
                @input="$refs.xForm.updateStatus(scope)"
              ></vxe-input>
            </template>
          </vxe-form-item>

          <vxe-form-item align="center" span="24">
            <template v-slot>
              <vxe-button type="submit" status="primary">保存</vxe-button>
            </template>
          </vxe-form-item>
        </vxe-form>
      </vxe-modal>
    </template>
  </div>
</template>

<script>
/*   import editWindow from '../window/editwindow'; */
import mointorWindow from "../window/mointorwindow";
import request from "@/utils/request";
import echarts from "echarts";
import Icon from "../toupuchart/iconBase64";
import G6 from "@antv/g6";
import switchIcon from "@/assets/toupu/switchIcon.png";
import watchIcon from "@/assets/toupu/watchIcon.png";
import computerIcon from "@/assets/toupu/computerIcon.png";
import serveiceIcon from "@/assets/toupu/serveiceIcon.png";

export default {
  data() {
    return {
      //编辑框
      monitor: "snmp协议接口",
      type: "windows服务器",
      position: "机房区",
      deviceType: [
        "windows服务器",
        "linux服务器",
        "打印机",
        "交换机",
        "监视器",
        "其它",
      ],
      monitorType: ["snmp协议接口", "代理接口", "ping"],
      positionType: ["机房区", "值班区", "办公区四楼", "办公区五楼", "其它"],

      //
      timeer: null,
      showEditWindow: false,
      showMointorWindow: false,
      data: {},
    };
  },
  components: { mointorWindow },
  created() {
    request({
      url:'/networkTopy/getTopy',
      method: 'get'
    }).then(data => {
      let nodes=[];
      this.data = data.data;
      this.data.nodes.forEach((item) => {
        item.img=computerIcon
        nodes.push(item)
      });
      this.data.nodes=nodes;
      this.drawRectTree();
    });
  },
  mounted() {
    this.$nextTick(() => {

    });
  },
  methods: {
    drawRectTree() {
      let sortByCombo = true;
      let width = document.getElementById("mountNode").clientWidth;

      var colors = ['rgb(64, 174, 247)', 'rgb(108, 207, 169)', 'rgb(157, 223, 125)', 'rgb(240, 198, 74)', 'rgb(221, 158, 97)', 'rgb(141, 163, 112)', 'rgb(115, 136, 220)', 'rgb(133, 88, 219)', 'rgb(203, 135, 226)', 'rgb(227, 137, 163)'];

      var nodes = this.data.nodes;
      var clusterMap = new Map();
      var clusterId = 0;
      nodes.forEach(function(node) {
        // cluster
        if (node.cluster && clusterMap.get(node.cluster) === undefined) {
          clusterMap.set(node.cluster, clusterId);
          clusterId++;
        }
        var cid = clusterMap.get(node.cluster);
        if (node.style) {
          node.style.fill = colors[cid % colors.length];
        } else {
          node.style = {
            fill: colors[cid % colors.length]
          };
        }
      });
      var graph = new G6.Graph({
        container: 'mountNode',
        width: window.innerWidth,
        height: window.innerHeight,
        modes: {
          default: ['drag-canvas', 'drag-node']
        },
        layout: {
          type: 'fruchterman',
          gravity: 1,
          speed: 5
        },
        animate: true,
        defaultNode: {
          size: [30, 30],
          labelCfg: {
            //position: 'center',
            offset: -30,
            style: {
              fill: "#000",
              fontSize: 10,
            },
          }
        },
        defaultEdge: {
          size: 1,
          color: '#e2e2e2',
          style: {
            endArrow: {
              path: 'M 4,0 L -4,-4 L -4,4 Z',
              d: 4
            }
          }
        }
      });
      graph.data(this.data);
      graph.render();

      var descriptionDiv = document.createElement("div");
      descriptionDiv.innerHTML = 'Fruchterman 布局，重力：1';
      var graphDiv = document.getElementById("mountNode");
      //document.body.insertBefore(descriptionDiv, graphDiv);
      setTimeout(function() {
        descriptionDiv.innerHTML = 'Fructherman 布局，重力：10，按聚类布局';
        graph.updateLayout({
          gravity: 5,
          clustering: true,
          unitRadius: 80
        });
      }, 2500);
    }
  },
};
</script>

<style lang="scss" scoped>
#left {
  position: relative;
  width: 12.875rem;
  height: 11.625rem;
}
#treeChart {
  width: 12.875rem;
  height: 11.625rem;
}

#legend {
  user-select: none;
  z-index: 500;
  position: absolute;
  left: 0.25rem;
  bottom: 0.25rem;
  width: 1.875rem;
  box-shadow: 0.0125rem 0.0125rem 0.125rem 0 #ccc;

  div {
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: Alibaba-PuHuiTi-Medium;

    .lgd1 {
      width: 0.625rem;
      background-color: #edfbed;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd2 {
      width: 0.625rem;
      background-color: #eeedfb;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd3 {
      width: 0.625rem;
      background-color: #fbfdef;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd4 {
      width: 0.625rem;
      background-color: #fcf3ee;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }

    .lgd5 {
      width: 0.625rem;
      background-color: #a183a3;
      height: 0.25rem;
      border: solid 0.0125rem #01a1ff;
    }
    span:nth-child(2) {
      margin-left: 0.0625rem;
      flex: 2;
      display: block;
      line-height: 0.4rem;
    }
    span:nth-child(1) {
      margin-left: 0.0625rem;
      flex: 1;
      display: block;
      line-height: 0.4rem;
    }
  }
  .titile {
    margin-left: 0.125rem;
    font-size: 0.2rem;
    font-weight: 600;
  }
}
</style>
