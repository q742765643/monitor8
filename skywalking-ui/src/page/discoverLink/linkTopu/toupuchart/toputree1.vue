<template>
  <div id="left">
    <div id="mountNode"></div>
    <div id="legend">
      <span class="titile">图例</span>
      <div v-for="(item, index) in legendList" :key="index">
        <span class="lgd" :style="'background:' + item.remark"></span>
        <span>{{ item.dictLabel }}</span>
      </div>
    </div>
    <!-- <mointorWindow v-if="showMointorWindow" :ip="ip"></mointorWindow> -->
  </div>
</template>

<script>
  /*   import editWindow from '../window/editwindow'; */
  import mointorWindow from '../window/mointorwindow';
  import request from '@/utils/request';
  import G6 from '@antv/g6';
  import switchIcon from '@/assets/toupu/switchIcon.png';
  import watchIcon from '@/assets/toupu/watchIcon.png';
  import computerIcon from '@/assets/toupu/computerIcon.png';
  import serveiceIcon from '@/assets/toupu/serveiceIcon.png';

  export default {
    data() {
      return {
        legendList: [],
        showMointorWindow: false,
        TopyData: {},
        ip: '',
        selectRect: null,
      };
    },
    components: { mointorWindow },
    async created() {
      await this.getDicts('media_area').then((response) => {
        this.legendList = response.data;
      });
      await request({
        url: '/networkTopy/getTopy',
        method: 'get',
      }).then((data) => {
        let nodes = [];
        this.TopyData = data.data;
        this.TopyData.nodes.forEach((item, index) => {
          item.img = computerIcon;
          if (index == 0) {
            item.firstFlag = true;
            let topuId = item.id;
            this.getInfoById(topuId);
          }
          nodes.push(item);
        });
        this.TopyData.nodes = nodes;
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
        var g6Height = document.getElementById('mountNode').clientHeight - 20;
        G6.registerNode(
          'card-node',
          {
            drawShape: function drawShape(cfg, group) {
              var firstFill = '';
              if (cfg.firstFlag) {
                firstFill = 'rgba(0,255,255,0.5)';
              }
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
                color = '#f109b4';
              }
              if (cfg.area == 1) {
                color = '#2dd246';
              }
              if (cfg.area == 2) {
                color = '#ff9700';
              }
              if (cfg.area == 3) {
                color = '#a183a3';
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
                  fill: firstFill,
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
          height: g6Height,
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
          },
          nodeStateStyles: {
            selected: {
              fill: 'rgba(0,255,255,0.5)',
            },
          },
          edgeStateStyles: {
            selected: {
              fill: 'rgba(0,255,255,0.5)',
            },
          },
          stateStyles: {
            selected: {
              fill: 'rgba(0,255,255,0.5)',
            },
          },
        });
        graph.data(this.TopyData);
        graph.render();

        graph.on('node:click', (ev) => {
          console.log(ev);
          if (this.selectRect) {
            // 取消单个状态
            graph.clearItemStates(this.selectRect, 'selected');
          }
          //添加状态
          const { item } = ev;
          this.selectRect = item;
          graph.setItemState(item, 'selected', true);
          let topuId = ev.item._cfg.id;
          this.getInfoById(topuId);
        });
        graph.on('node:dblclick', (ev) => {
          this.ip = ev.item._cfg.model.ip;
          // this.showMointorWindow = true;
          this.$router.push({ name: 'mointorWindow',params: {
            ip: this.ip
          } })
        });
      },
      getInfoById(topuId) {
        request({
          url: '/hostConfig/' + topuId,
          method: 'get',
        }).then((data) => {
          this.$emit('findInfoData', data.data);
        });
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
    width: 100%;
    height: 100%;
    #mountNode {
      width: 100%;
      height: 100%;
    }
  }
  #treeChart {
    width: 100%;
    height: 100%;
  }

  #legend {
    width: 150px;
    user-select: none;
    z-index: 500;
    position: absolute;
    left: 20px;
    bottom: 20px;
    box-shadow: 1px 1px 10px 0 #ccc;
    background: #fff;
    font-size: 14px;
    div {
      display: flex;
      justify-content: center;
      align-items: center;
      .lgd {
        width: 50px;
        height: 20px;
        border: solid 1px #01a1ff;
      }
      .lgd1 {
        background-color: #edfbed;
      }
      .lgd2 {
        background-color: #eeedfb;
      }

      .lgd3 {
        background-color: #fbfdef;
      }

      .lgd4 {
        background-color: #fcf3ee;
      }

      .lgd5 {
        background-color: #a183a3;
      }
      span:nth-child(2) {
        margin-left: 5px;
        flex: 2;
        display: block;
        line-height: 32px;
      }
      span:nth-child(1) {
        margin-left: 5px;
        flex: 1;
        display: block;
        line-height: 32px;
      }
    }
    .titile {
      font-size: 16px;
      margin-left: 10px;
      font-weight: 600;
    }
  }
</style>
