<template>
  <div id="left">
    <div id="mountNode"></div>
    <div class="serachBox">
      <a-input v-model="topuIp" placeholder="请输入IP地址" @keyup.enter.native="handleQuery"> </a-input>
    </div>
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
      topuIp: '',
      Ggraph: null,
    };
  },
  components: { mointorWindow },
  async created() {
    await this.getDicts('media_area').then((response) => {
      this.legendList = response.data;
      this.legendList.push({
        remark: 'red',
        dictLabel: '告警',
      });
    });

    await request({
      url: '/networkTopy/getTopy',
      method: 'get',
    }).then((data) => {
      let nodes = [];
      let firsttopuId = '';
      this.TopyData = data.data;
      this.TopyData.nodes.forEach((item, index) => {
        item.img = computerIcon;
        if (index == 0) {
          firsttopuId = item.id;
          this.getInfoById(firsttopuId);
        }
        nodes.push(item);
      });
      this.TopyData.edges.forEach((item, index) => {
        if (item.currentStatus == 2) {
          item.style = {
            stroke: 'red', //节点之间连线的样式
          };
        } else {
          item.style = {
            stroke: '#2dd246', //节点之间连线的样式
          };
        }
      });

      this.TopyData.nodes = nodes;
      this.drawRectTree();
      // 通过ID查询节点实例
      this.selectRect = this.Ggraph.findById(firsttopuId);
      // this.Ggraph.updateItem(item);
      this.Ggraph.setItemState(this.selectRect, 'selected', true);
    });
  },
  mounted() {
    this.$nextTick(() => {});
  },
  methods: {
    handleQuery() {
      if (this.topuIp) {
        let that = this;
        let id = null;
        this.TopyData.nodes.forEach((element) => {
          if (element.ip == that.topuIp) {
            id = element.id;
            that.getInfoById(id);
          }
        });
        if (this.selectRect) {
          // 取消单个状态
          this.Ggraph.clearItemStates(this.selectRect, 'selected');
        }
        // 通过ID查询节点实例
        this.selectRect = this.Ggraph.findById(id);
        // this.Ggraph.updateItem(item);
        this.Ggraph.setItemState(this.selectRect, 'selected', true);
        //将元素移动到视口中心，该方法可用于做搜索后的缓动动画
        this.Ggraph.focusItem(this.selectRect);
      }
    },
    drawRectTree() {
      let width = document.getElementById('mountNode').clientWidth - 20;
      var g6Height = document.getElementById('mountNode').clientHeight - 20;
      G6.registerNode(
        'card-node',
        {
          drawShape: function drawShape(cfg, group) {
            var firstFill = '';
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
              color = '#f109b4'; //办公区
            }
            if (cfg.area == 1) {
              color = '#2dd246'; //值班区
            }
            if (cfg.area == 2) {
              color = '#ff9700'; //机房区
            }
            if (cfg.area == 3 || !cfg.area) {
              color = '#a183a3'; //其他区
            }
            if (cfg.currentStatus == 2) {
              color = 'red';
            }
            const r = 2;
            let taskName = '';
            if (cfg.taskName) {
              taskName = cfg.taskName;
            } else {
              taskName = '未命名';
            }

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
                text: taskName + '\n' + cfg.ip,
                fill: 'rgba(0,0,0, 0.4)',
                textAlign: 'center',
                fontSize: 10,
              },
              name: `index-title`,
            });
            const shape = group.addShape('rect', {
              attrs: {
                id: cfg.ip,
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
      //registerEdge函数会遍历每一个节点
      //下面这两个参数是为了设置连线上圆圈的不同颜色
      let edgeCircleColorArr = '#2bb9f7';
      G6.registerEdge(
        'circle-running',
        {
          afterDraw(cfg, group) {
            if (2 == cfg.currentStatus) {
              edgeCircleColorArr = 'red';
            } else {
              edgeCircleColorArr = '#2dd246';
            }
            const shape = group.get('children')[0];
            const startPoint = shape.getPoint(0);
            //创建节点之间的圆圈，并为每一个设置样式
            const circle = group.addShape('circle', {
              attrs: {
                x: startPoint.x,
                y: startPoint.y,
                fill: edgeCircleColorArr,
                r: 4, //圆圈大小
              },
              name: 'circle-shape',
            });

            // 实现动态效果
            circle.animate(
              (ratio) => {
                const tmpPoint = shape.getPoint(ratio);
                return {
                  x: tmpPoint.x,
                  y: tmpPoint.y,
                };
              },
              {
                repeat: true, //动画是否重复
                duration: 3000, //一次动画持续时长
              },
            );
          },
        },
        'cubic',
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
                const text = '单击显示属性,双击显示详情';
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
          type: 'circle-running', //节点之间连线类型
          style: {
            //节点之间连线的样式
            lineWidth: 2,
            stroke: '#2bb9f7',
          },
        },
        nodeStateStyles: {
          selected: {
            fill: 'rgba(0,255,255,0.5)',
          },
          selectedError: {
            fill: 'rgba(230,0,18,0.5)',
          },
        },
        edgeStateStyles: {
          selected: {
            fill: 'rgba(0,255,255,0.5)',
          },
          selectedError: {
            fill: 'rgba(230,0,18,0.5)',
          },
        },
        stateStyles: {
          selected: {
            fill: 'rgba(0,255,255,0.5)',
          },
          selectedError: {
            fill: 'rgba(230,0,18,0.5)',
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
        this.$router.push({
          name: 'mointorWindow',
          params: {
            ip: this.ip,
            titleName: '设备运行状态',
            parentPageName: 'linkTopu',
          },
        });
      });
      this.Ggraph = graph;
    },
    getInfoById(topuId) {
      request({
        url: '/hostConfig/' + topuId,
        method: 'get',
      }).then((data) => {
        this.$emit('findInfoData', data.data);
      });
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
.serachBox {
  position: absolute;
  z-index: 500;
  left: 20px;
  top: 10px;
  box-shadow: 1px 1px 10px 0 #ccc;
  background: #fff;
  font-size: 14px;
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
