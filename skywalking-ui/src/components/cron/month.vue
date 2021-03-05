<template lang="html">
  <div :val="value_" class="singleCon">
    <div v-if="false">
      <el-radio v-model="type" label="1" size="mini" border>每月</el-radio>
    </div>
    <div v-if="false">
      <el-radio v-model="type" label="5" size="mini" border>不指定</el-radio>
    </div>
    <div v-if="false">
      <el-radio v-model="type" label="2" size="mini" border>周期</el-radio>
      <span style="margin-left: 10px; margin-right: 5px;">从</span>
      <el-input-number @change="type = '2'" v-model="cycle.start" :min="1" :max="12" size="mini" style="width: 100px;"></el-input-number>
      <span style="margin-left: 5px; margin-right: 5px;">至</span>
      <el-input-number @change="type = '2'" v-model="cycle.end" :min="2" :max="12" size="mini" style="width: 100px;"></el-input-number>
      月
    </div>
    <div class="timeTitle">
      <el-checkbox v-model="checkedType" label="月" @change="checkedTypeChange"></el-checkbox>
      <el-select v-model="type" size="mini" @change="typeChange">
        <el-option label="间隔" value="3"></el-option>
        <el-option label="指定" value="4"></el-option>
      </el-select>
    </div>
    
    <div v-show="type==3?true:false" class="timeCon">
      <span style="margin-left: 10px; margin-right: 5px;">从</span>
      <el-input-number @change="type = '3'" v-model="loop.start" :min="1" :max="12" size="mini" style="width: 100px;"></el-input-number>
      <span style="margin-left: 5px; margin-right: 5px;">月开始，每</span>
      <el-input-number @change="type = '3'" v-model="loop.end" :min="1" :max="12" size="mini" style="width: 100px;"></el-input-number>
      月执行一次
    </div>
    <div v-show="type==4?true:false" class="timeCon">
      <el-checkbox-group v-model="appoint" style="margin-left: 0px;  line-height: 25px;">
          <el-checkbox @change="hasChecked"  v-for="i in 12" :key="i" :label="i+''"></el-checkbox>
      </el-checkbox-group>
      <el-button @click="checkedAll" type="primary" size="mini" plain class="el-icon-circle-check">全选</el-button>
      <el-button @click="clearChecked" type="warning" size="mini" plain class="el-icon-circle-close">清空</el-button>
      <el-button @click="reverseChecked" type="success" size="mini" plain class="el-icon-refresh">反选</el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: "*"
    }
  },
  data() {
    return {
      type: "3", // 类型
      isChange: false,
      cycle: {
        // 周期
        start: 0,
        end: 0
      },
      loop: {
        // 间隔
        start: 0,
        end: 0
      },
      week: {
        // 指定周
        start: 0,
        end: 0
      },
      work: 0,
      last: 0,
      appoint: [], // 指定
      checkedType: false
    };
  },
  computed: {
    value_() {
      let result = [];
      switch (this.type) {
        case "1": // 每秒
          result.push("*");
          break;
        case "2": // 年期
          result.push(`${this.cycle.start}-${this.cycle.end}`);
          break;
        case "3": // 间隔
          !this.checkedType
            ? result.push("*")
            : result.push(`${this.loop.start}/${this.loop.end}`);
          break;
        case "4": // 指定
          !this.checkedType
            ? result.push("*")
            : this.appoint.length === 0
            ? result.push("*")
            : result.push(this.appoint.join(","));
          break;
        case "6": // 最后
          result.push(`${this.last === 0 ? "" : this.last}L`);
          break;
        default:
          // 不指定
          result.push("?");
          break;
      }
      this.$emit("input", result.join(""));
      return result.join("");
    }
  },
  watch: {
    value(a, b) {
      this.updateVal();
    }
  },
  methods: {
    updateVal() {
      if (!this.value) {
        return;
      }
      if (this.value === "?") {
        this.type = "3";
      } else if (this.value.indexOf("-") !== -1) {
        // 2周期
        if (this.value.split("-").length === 2) {
          this.type = "2";
          this.cycle.start = this.value.split("-")[0];
          this.cycle.end = this.value.split("-")[1];
        }
      } else if (this.value.indexOf("/") !== -1) {
        // 3间隔
        if (this.value.split("/").length === 2) {
          this.type = "3";
          this.loop.start = this.value.split("/")[0];
          this.loop.end = this.value.split("/")[1];
          this.checkedType = true;
          return;
        }
      } else if (this.value.indexOf("*") !== -1) {
        // 1每
        //this.type = "4";
        this.appoint = [];
        this.checkedType = false;
      } else if (this.value.indexOf("L") !== -1) {
        // 6最后
        this.type = "6";
        this.last = this.value.replace("L", "");
      } else if (this.value.indexOf("#") !== -1) {
        // 7指定周
        if (this.value.split("#").length === 2) {
          this.type = "7";
          this.week.start = this.value.split("#")[0];
          this.week.end = this.value.split("#")[1];
        }
      } else if (this.value.indexOf("W") !== -1) {
        // 8工作日
        this.type = "8";
        this.work = this.value.replace("W", "");
      } else {
        // *
        this.type = "4";
        this.appoint = this.value.split(",");
        this.checkedType = true;
      }
    },
    //全选
    checkedAll() {
      let appointArr = [];
      for (let j = 1; j < 13; j++) {
        appointArr.push(j + "");
      }
      this.appoint = appointArr;
      this.checkedType = true;
    },
    //清空
    clearChecked() {
      this.appoint = [];
      this.checkedType = false;
    },
    //反选
    reverseChecked() {
      let appointArr = [];
      for (let j = 1; j < 13; j++) {
        let index = j + "";
        if (!this.appoint.includes(index)) {
          appointArr.push(index);
        }
      }
      this.appoint = appointArr;
      this.appoint.length === 0
        ? (this.checkedType = false)
        : (this.checkedType = true);
    },
    //设置为间隔不可选
    setChange() {
      this.checkedType = false;
      this.isChange = true;
    },
    checkedTypeChange() {
      if (this.isChange) {
        this.checkedType = false;
        this.$notify({
          title: "提示",
          type: "warning",
          message: "已存在间隔"
        });
      }
    },
    typeChange() {
      if (this.isChange) {
        this.isChange = false;
        this.checkedType = true;
      }
    },
    hasChecked() {
      this.type = "4";
      this.appoint.length > 0
        ? (this.checkedType = true)
        : (checkedType = false);
    }
  },
  created() {
    this.updateVal();
  }
};
</script>

<style lang="css">
.el-checkbox + .el-checkbox {
  margin-left: 10px;
}
</style>
