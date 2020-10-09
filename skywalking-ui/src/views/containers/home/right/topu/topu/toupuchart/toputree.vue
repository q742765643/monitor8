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
        data: {
          nodes: [
            {
              id: '0',
              label: 'switch1',
              img: switchIcon,
              type: 'image',
              size: 250,
            },
            { id: '1', label: '交换机1', img: switchIcon, type: 'image', size: 250 },
            { id: '2', label: '交换机2', img: switchIcon, type: 'image', size: 250 },
            { id: '3', label: '交换机3', img: switchIcon, type: 'image', size: 250 },
            { id: '4', label: '交换机4', img: switchIcon, type: 'image', size: 250 },
            { id: '5', label: '交换机5', img: switchIcon, type: 'image', size: 250 },
            { id: '6', label: '监视台', comboId: 'A', img: watchIcon, type: 'image', size: 200 },
            { id: '7', label: '监视台', comboId: 'A', img: watchIcon, type: 'image', size: 200 },
            { id: '8', label: '办公', comboId: 'B', img: computerIcon, type: 'image', size: 200 },
            { id: '9', label: '办公', comboId: 'B', img: computerIcon, type: 'image', size: 200 },
            { id: '10', label: '办公', comboId: 'C', img: computerIcon, type: 'image', size: 200 },
            { id: '11', label: '办公', comboId: 'C', img: computerIcon, type: 'image', size: 200 },
            { id: '12', label: '服务器', comboId: 'D', img: serveiceIcon, type: 'image', size: 200 },
            { id: '13', label: '服务器', comboId: 'D', img: serveiceIcon, type: 'image', size: 200 },
            { id: '14', label: '办公', comboId: 'E', img: computerIcon, type: 'image', size: 200 },
            { id: '15', label: '办公', comboId: 'E', img: computerIcon, type: 'image', size: 200 },
          ],
          edges: [
            {
              source: '0',
              target: '1',
            },
            {
              source: '0',
              target: '2',
            },
            {
              source: '0',
              target: '3',
            },
            {
              source: '0',
              target: '4',
            },
            {
              source: '0',
              target: '5',
            },
            {
              source: '1',
              target: '6',
            },
            {
              source: '1',
              target: '7',
            },

            {
              source: '2',
              target: '8',
            },

            {
              source: '2',
              target: '9',
            },

            {
              source: '3',
              target: '10',
            },

            {
              source: '3',
              target: '11',
            },
            {
              source: '4',
              target: '12',
            },
            {
              source: '4',
              target: '13',
            },
            {
              source: '5',
              target: '14',
            },
            {
              source: '5',
              target: '15',
            },
          ],
          combos: [
            {
              id: 'A',
              label: '',

              style: {
                fill: '#edfbed',
                lineWidth: 5,
                stroke: '#01a1ff',
                lineDash: [14, 14],
              },
            },
            {
              id: 'B',
              label: '',

              style: {
                fill: '#eeedfb',
                lineWidth: 5,
                stroke: '#01a1ff',
                lineDash: [14, 14],
              },
            },
            {
              id: 'C',
              label: '',

              style: {
                fill: '#fbfdef',
                lineWidth: 5,
                stroke: '#01a1ff',
                lineDash: [14, 14],
              },
            },
            {
              id: 'D',
              label: '',
              style: {
                fill: '#fcf3ee',
                lineWidth: 5,
                stroke: '#01a1ff',
                lineDash: [14, 14],
              },
            },
            {
              id: 'E',
              label: '',
              style: {
                fill: '#a183a3',
                lineWidth: 5,
                stroke: '#01a1ff',
                lineDash: [14, 14],
              },
            },
          ],
        },
      };
    },
    components: { mointorWindow },
    mounted() {
      this.$nextTick(() => {
        this.drawRectTree();
      });
    },
    methods: {
      drawRectTree() {
        let sortByCombo = true;
        let width = document.getElementById('mountNode').clientWidth;
        // let height = document.getElementById('mountNode').clientHeight;

        let graph = new G6.Graph({
          width,
          container: 'mountNode',
          height: 700,
          linkCenter: true,
          fitView: true,
          fitViewPadding: 30,
          animate: true,
          modes: {
            default: [
              'drag-combo',
              'drag-canvas',
              'zoom-canvas',
              //'drag-node',
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
            type: 'dagre',
            //type: 'comboForce',
            sortByCombo: true,
            ranksep: 200,
          },
          defaultNode: {
            size: [150, 100],
            type: 'rect',

            labelCfg: {
              offset: -80,
              style: {
                fill: '#000',
                fontSize: 50,
              },
            },
          },
          defaultCombo: {
            type: 'rect',
          },
          defaultEdge: {
            type: 'line',
            style: {
              lineWidth: 2,
              stroke: '#53D7A4',
              endArrow: {
                path: G6.Arrow.vee(40, 40, 50),
                d: 50,
                fill: '#53D7A4',
              },
            },
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
