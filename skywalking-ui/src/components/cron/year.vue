<template lang="html">
  <div :val="value_" class="singleCon">
    
    <div v-if="false">
      <el-radio v-model="type" label="5" size="mini" border>不指定</el-radio>
    </div>

    <div class="timeTitle">
      <el-checkbox v-model="checkedType" label="年"></el-checkbox>
      <el-select v-model="type" size="mini" >
        <el-option label="间隔" value="1"></el-option>
        <el-option label="指定" value="2"></el-option>
      </el-select>
    </div>
    
    <div v-show="type==1?true:false" class="timeCon">
      <span v-model="type" label="1">每年</span>
    </div>
    <div v-show="type==2?true:false" class="timeCon">
      <span style="margin-left: 10px; margin-right: 5px;">从</span>
      <el-input-number @change="type = '2'" v-model="cycle.start" :min="2000" size="mini" style="width: 100px;"></el-input-number>
      <span style="margin-left: 5px; margin-right: 5px;">至</span>
      <el-input-number @change="type = '2'" v-model="cycle.end" :min="2000"  size="mini" style="width: 100px;"></el-input-number>
      年
    </div>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: String,
      default: ""
    }
  },
  data() {
    let year = new Date().getFullYear();
    return {
      type: "1", // 类型
      cycle: {
        // 周期
        start: year,
        end: year
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
          result.push(`${this.loop.start}/${this.loop.end}`);
          break;
        case "4": // 指定
          result.push(this.appoint.join(","));
          break;
        case "6": // 最后
          result.push(`${this.last === 0 ? "" : this.last}L`);
          break;
        default:
          // 不指定
          result.push("");
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
          this.checkedType = true;
        }
      } else if (this.value.indexOf("/") !== -1) {
        // 3间隔
        if (this.value.split("/").length === 2) {
          this.type = "3";
          this.loop.start = this.value.split("/")[0];
          this.loop.end = this.value.split("/")[1];
        }
      } else if (this.value.indexOf("*") !== -1) {
        // 1每
        this.type = "1";
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
