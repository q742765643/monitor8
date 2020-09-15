<template>
  <div :class="tdClass">
    <span class="before-line" v-if="root !== 0 && nodes !== 1" :style="{'left':model.bLeft + 'px'}"></span>
    <table>
      <tr>
        <td :colspan="colSpan">
          <table>
            <tr class="leve" :class="levelClass">
              <td class="td1 tdItem">
                <div class="td-title" @dblclick="handlerExpand(model)">
                  <span
                    v-if="model.children.length > 0"
                    class="tree-close"
                    :class="{'tree-open':model.isExpand}"
                    @click="handlerExpand(model)"
                  ></span>
                  <input type="checkbox" v-model="model.isChecked" @change="handlerChecked(model)" />
                  <a class="ellipsis">
                    <span :title="model.label">{{model.label}}</span>
                  </a>
                </div>
              </td>
              <td class="td2 tdItem">{{model.label}}</td>
              <td class="td3 tdItem">{{model.label}}</td>
              <td class="td4 tdItem">{{model.label}}</td>
              <td class="td5 tdItem">{{model.label}}</td>
              <td class="td6 tdItem">{{model.label}}</td>
              <td class="td7 tdItem">{{model.label}}</td>
              <td class="td8 tdItem">{{model.label}}</td>
              <td class="td9 tdItem">{{model.label}}</td>
              <td class="td10 tdItem">{{model.label}}</td>
              <td class="td11 tdItem">{{model.label}}</td>
              <td class="td12 tdItem">{{model.label|formatDate}}</td>
              <td class="td13 tdItem">
                <span :title="model.label" class="ellipsis">{{model.label}}</span>
              </td>
              <td class="td14 tdItem">{{model.label}}</td>
              <td class="td15 tdItem">
                <span class="editSpan" @click="actionFunc(model)">编辑</span>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    <div v-show="model.isExpand" class="other-node" :class="otherNodeClass">
      <tree-item
        v-for="(m, i) in model.children"
        :key="String('child_node'+i)"
        :num="i"
        :root="1"
        @actionFunc="actionFunc"
        @handlerExpand="handlerExpand"
        @handlerChecked="handlerChecked"
        :nodes.sync="model.children.length"
        :trees.sync="trees"
        :model.sync="m"
      ></tree-item>
    </div>
  </div>
</template>

<script>
export default {
  name: 'treeItem',
  props: ['model', 'num', 'nodes', 'root', 'trees'],
  data() {
    return {
      parentNodeModel: null,
    };
  },
  computed: {
    colSpan() {
      return this.root === 0 ? '' : 6;
    },
    tdClass() {
      return this.root === 0 ? 'td-border' : 'not-border';
    },
    levelClass() {
      return this.model ? 'leve-' + this.model.level : '';
    },
    childNodeClass() {
      return this.root === 0 ? '' : 'child-node';
    },
    otherNodeClass() {
      return this.model ? 'other-node-' + this.model.level : '';
    },
  },
  watch: {
    // 'model': {
    // 	handler() {
    // 		console.log('对象变化', this.model.isExpand)
    // 	},
    // 	deep: true
    // }
  },
  methods: {
    getParentNode(m) {
      // 查找点击的子节点
      var recurFunc = (data, list) => {
        data.forEach((l) => {
          if (l.id === m.id) this.parentNodeModel = list;
          if (l.children) {
            recurFunc(l.children, l);
          }
        });
      };
      recurFunc(this.trees, this.trees);
    },
    handlerExpand(m) {
      this.$emit('handlerExpand', m);
    },
    handlerChecked(m) {
      this.$emit('handlerChecked', m);
    },
    actionFunc(m) {
      this.$emit('actionFunc', m);
    },
  },
  filters: {
    formatDate: function(date) {
      // 后期自己格式化
      return date; //Utility.formatDate(date, 'yyyy/MM/dd')
    },
  },
};
</script>
