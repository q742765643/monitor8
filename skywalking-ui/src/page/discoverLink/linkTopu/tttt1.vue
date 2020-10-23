<template>
  <div id="mountNode"></div>
</template>

<script>
  import G6 from '@antv/g6';
  import request from '@/utils/request';
  import computerIcon from '@/assets/toupu/computerIcon.png';
  import switchIcon from '@/assets/toupu/switchIcon.png';
  import watchIcon from '@/assets/toupu/watchIcon.png';
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
      //this.drawRectTree();
    },
    methods: {
      drawRectTree() {
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
                  height: 60,
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
                  //radius: r,
                },
                name: 'main-box',
                draggable: true,
              });
              // left icon
              // title text

              return shape;
            },
          },
          'single-node',
        );
        const graph = new G6.Graph({
          container: 'mountNode',
          width: 800,
          height: window.innerHeight,
          fitView: true,
          fitViewPadding: 50,
          layout: {
            type: 'comboForce',
            nodeSpacing: (d) => 8,
            center: [200, 200], // 可选，默认为图的中心
            linkDistance: 50, // 可选，边长
            nodeStrength: 30, // 可选
            edgeStrength: 0.1, // 可选
            preventOverlap: true,
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
          },
          modes: {
            default: ['drag-combo', 'drag-node', 'drag-canvas', 'zoom-canvas'],
          },
        });
        this.data.combos = [
          {
            id: '0',
            label: 'Combo A',
          },
          {
            id: '1',
            label: 'Combo B',
          },
          {
            id: '2',
            label: 'Combo D',
          },
          {
            id: '3',
            label: 'Combo D',
          },
          {
            id: '4',
            label: 'Combo D',
          },
        ];
        graph.data(this.data);
        graph.render();
      },
    },
  };
</script>
