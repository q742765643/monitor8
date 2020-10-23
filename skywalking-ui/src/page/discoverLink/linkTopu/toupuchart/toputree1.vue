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
      <vxe-modal :mask="false" v-model="showEditWindow" size="mini" title="添加设备">
        <vxe-form ref="xForm" title-width="80" title-align="right" :title-colon="true">
          <vxe-form-item title="设备别名" field="othername" span="24">
            <template v-slot="scope">
              <vxe-input placeholder="请输入设备别名" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
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
              <vxe-option v-for="(item, index) in monitorType" :key="index" :value="item" :label="item"></vxe-option>
            </vxe-select>
          </vxe-form-item>

          <vxe-form-item title="设备类型" field="deviceType" span="24">
            <!--  <template v-slot="scope">
          <vxe-input placeholder="请输入设备类型" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->

            <vxe-select v-model="type" placeholder="默认尺寸">
              <vxe-option v-for="(item, index) in deviceType" :key="index" :value="item" :label="item"></vxe-option>
            </vxe-select>
          </vxe-form-item>

          <vxe-form-item title="设备负责人" field="devCharge" span="24">
            <template v-slot="scope">
              <vxe-input placeholder="请输入设备负责人" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
            </template>
          </vxe-form-item>

          <vxe-form-item title="区域" field="position" span="24">
            <!-- <template v-slot="scope">
          <vxe-input placeholder="请输入设备负责人" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
        </template> -->
            <vxe-select v-model="position" placeholder="默认尺寸">
              <vxe-option v-for="(item, index) in positionType" :key="index" :value="item" :label="item"></vxe-option>
            </vxe-select>
          </vxe-form-item>
          <vxe-form-item title="详细地址" field="address" span="24">
            <template v-slot="scope">
              <vxe-input placeholder="请输入详细地址" clearable @input="$refs.xForm.updateStatus(scope)"></vxe-input>
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
  import mointorWindow from '../window/mointorwindow';
  import request from '@/utils/request';
  import echarts from 'echarts';
  import Icon from '../toupuchart/iconBase64';
  import G6 from '@antv/g6';
  import switchIcon from '@/assets/toupu/switchIcon.png';
  import watchIcon from '@/assets/toupu/watchIcon.png';
  import computerIcon from '@/assets/toupu/computerIcon.png';
  import serveiceIcon from '@/assets/toupu/serveiceIcon.png';

  export default {
    data() {
      return {
        //编辑框
        monitor: 'snmp协议接口',
        type: 'windows服务器',
        position: '机房区',
        deviceType: ['windows服务器', 'linux服务器', '打印机', '交换机', '监视器', '其它'],
        monitorType: ['snmp协议接口', '代理接口', 'ping'],
        positionType: ['机房区', '值班区', '办公区四楼', '办公区五楼', '其它'],

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
        url: '/networkTopy/getTopy',
        method: 'get',
      }).then((data) => {
        let nodes = [];
        this.data = data.data;
        this.data.nodes.forEach((item) => {
          item.img = computerIcon;
          nodes.push(item);
        });
        this.data.nodes = nodes;
        this.drawRectTree();
      });
    },
    mounted() {
      this.$nextTick(() => {});
    },
    methods: {
      drawRectTree() {
        let sortByCombo = true;
        let width = document.getElementById('mountNode').clientWidth - 20;
        G6.registerNode(
          'card-node',
          {
            drawShape: function drawShape(cfg, group) {
              let img = computerIcon;
              if (cfg.mediaType == 1 || cfg.mediaType == 0) {
                img = serveiceIcon;
              }
              if (cfg.mediaType == 2 || cfg.mediaType == 3) {
                img = switchIcon;
              }
              if (cfg.mediaType == 4) {
                img = watchIcon;
              }

              let color = '#6666ff';
              if (cfg.area == 0) {
                color = '#9933cc';
              }
              if (cfg.area == 1) {
                color = '#993333';
              }
              if (cfg.area == 2) {
                color = '#cccc33';
              }
              if (cfg.area == 3) {
                color = '#ff0099';
              }
              const r = 2;

              group.addShape('image', {
                attrs: {
                  x: -12,
                  y: 0,
                  height: 50,
                  width: 60,
                  cursor: 'pointer',
                  img: img,
                },
                name: 'node-icon',
              });
              group.addShape('text', {
                attrs: {
                  textBaseline: 'top',
                  y: 45,
                  x: 15,
                  lineHeight: 20,
                  text: cfg.ip,
                  fill: 'rgba(0,0,0, 0.4)',
                  textAlign: 'center',
                  fontSize: 10,
                },
                name: `index-title`,
              });
              const shape = group.addShape('rect', {
                attrs: {
                  x: 0,
                  y: 0,
                  width: 36,
                  height: 36,
                  stroke: color,
                  lineWidth: 2,
                  lineDash: [12, 12, 24, 12, 24, 12, 24],
                },
                name: 'main-box',
                draggable: true,
              });

              return shape;
            },
          },
          'single-node',
        );
        var graph = new G6.Graph({
          width,
          container: 'mountNode',
          height: window.innerHeight,
          modes: {
            default: [
              'drag-node',
              'drag-canvas',
              'zoom-canvas',
              {
                type: 'collapse-expand-combo',
                relayout: false,
              },
              {
                type: 'tooltip',
                formatText(model) {
                  const text = '单击编辑属性,双击显示详情';
                  return text;
                },
                offset: 20,
              },
            ],
          },
          layout: {
            type: 'fruchterman',
            center: [400, 400], // 可选，默认为图的中心
            gravity: 2, // 可选
            speed: 2, // 可选
            clustering: true, // 可选
            clusterGravity: 1, // 可选
            maxIteration: 2000,
          },
          animate: true,
          defaultNode: {
            type: 'card-node',
          },
          defaultEdge: {
            size: 1,
            type: 'line-arrow',
            style: {
              stroke: '#F6BD16',
              endArrow: {
                path: 'M 0,0 L 12,4 L 6,0 L 12,-4 Z',
                fill: '#F6BD16',
              },
            },
            /*lineWidth: 2,
          stroke: "#53D7A4",
          endArrow: {
            path: G6.Arrow.vee(40, 40, 50),
            d: 50,
            fill: "#53D7A4",
          },*/
          },
        });
        graph.data(this.data);
        graph.render();

        graph.on('node:click', (ev) => {
          clearTimeout(this.timeer);
          this.timeer = setTimeout(() => {
            this.showEditWindow = true;
          }, 300);
        });

        graph.on('node:dblclick', (ev) => {
          clearTimeout(this.timeer);
          clearTimeout(this.timeer);
          this.showMointorWindow = true;
        });
      },

      closeWindow() {
        this.showEditWindow = false;
      },
      closeMonWindow() {
        this.showMointorWindow = false;
      },
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
