<!--
<template>
  <div id="managerMaster">
    <div id="header">
      <div id="select">
        <vxe-select v-model="selctValue" placeholder="" size="mini">
          <vxe-option value="IP" label="IP地址"></vxe-option>
          <vxe-option value="name" label="设备名称"></vxe-option>
          <vxe-option value="state" label="当前状态"></vxe-option>
          <vxe-option value="area" label="区域"></vxe-option>
          <vxe-option value="address" label="详细地址"></vxe-option>
        </vxe-select>
      </div>
      <div id="query">
        <input id="queryValue" v-model="queryValue" type="text" v-on:input="queryTbaleData" />
        <span
          v-if="queryValue != ''"
          id="clearIcon"
          class="iconfont iconbaseline-close-px"
          @click="clearQueryValue"
        ></span>
        <span v-else id="queryIcon" class="iconfont iconsousuo "></span>
      </div>
      <div id="operate">
        <span class="iconfont icontianjia1" @click="insertDevice">添加</span>
        <span class="iconfont icondustbin_icon" @click="$refs.xTable.removeCheckboxRow()">删除</span>
        <span class="iconfont icontianjia1" @click="showTopuMoadl = true">从链路中添加</span>
      </div>
    </div>

    <div id="content">
      <div id="table">
        <vxe-table
          stripe
          resizable
          show-overflow
          keep-source
          border
          highlight-current-row
          highlight-hover-row
          ref="xTable"
          :data="tableData"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          :checkbox-config="{ labelField: 'number' }"
        >
          &lt;!&ndash;   :checkbox-config="{ labelField: 'number' }" &ndash;&gt;
          &lt;!&ndash;   <vxe-table-column type="checkbox" width="80" title="序号"></vxe-table-column> &ndash;&gt;
          <vxe-table-column field="number" title="序号" type="checkbox"></vxe-table-column>
          <vxe-table-column
            field="name"
            title="设备别名"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="state"
            title="当前状态"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="IP"
            title="IP地址"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="monitMode"
            title="监视方式"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="time"
            title="采集时间"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="macAdress"
            title="MAC地址"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="gateway"
            title="网关"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="deviceType"
            title="设备类型"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="devCharge"
            title="负责人"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column
            field="area"
            title="区域"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
          ></vxe-table-column>
          <vxe-table-column
            field="address"
            title="详细地址"
            :edit-render="{ name: 'input', immediate: true, attrs: { type: 'text' } }"
            show-overflow
          ></vxe-table-column>
          <vxe-table-column title="操作" width="160">
            <template v-slot="{ row }">
              <template v-if="$refs.xTable.isActiveByRow(row)">
                &lt;!&ndash;   <vxe-button @click="saveRowEvent(row)">保存</vxe-button>
                <vxe-button @click="cancelRowEvent(row)">取消</vxe-button> &ndash;&gt;

                <span class="iconfont iconiconfontcheck" @click="saveRowEvent(row)">保存</span>
                <span class="iconfont iconcuo" @click="cancelRowEvent(row)">取消</span>
              </template>
              <template v-else>
                &lt;!&ndash;  <vxe-button @click="editRowEvent(row)">编辑</vxe-button> &ndash;&gt;
                <span class="iconfont iconbianji1" @click="editRowEvent(row)">编辑</span>
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>

    &lt;!&ndash;   添加设备 &ndash;&gt;
    <template>
      <div>
        &lt;!&ndash;   添加设备 &ndash;&gt;
        <vxe-modal :mask="false" v-model="showAddMoadl" size="mini" title="添加设备">
          <vxe-form ref="xForm" title-width="80" title-align="right">
            <vxe-form-item title="设备名称" field="name" span="24">
              <template v-slot="scope">
                <vxe-input
                  v-model="formData.name"
                  placeholder="请输入设备名称"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="IP地址" field="IP" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入IP地址"
                  v-model="formData.IP"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="MAC地址" field="macAddress" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入MAC地址"
                  v-model="formData.devType"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="网关" field="gateway" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入网关"
                  v-model="formData.gateway"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="设备类型" field="deviceType" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入设备类型"
                  v-model="formData.deviceType"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="设备负责人" field="devCharge" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入设备负责人"
                  v-model="formData.devCharge"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="区域" field="position" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入设备负责人"
                  v-model="formData.position"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>
            <vxe-form-item title="详细地址" field="address" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入详细地址"
                  v-model="formData.address"
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

        &lt;!&ndash;   链路中添加 &ndash;&gt;
        <vxe-modal :mask="false" @hide="clearData" v-model="showTopuMoadl" size="mini" title="链路选择">
          <template v-slot>
            <div id="treeSelect" style="height:330px">
              <treeselect
                v-model="treeValue"
                :multiple="false"
                :clearable="true"
                :searchable="true"
                :options="treeSelctOptions"
                :default-expand-level="2"
                :always-open="true"
              />
            </div>
            <div style="float:right">
              <vxe-button size="mini" status="primary" content="下一步" @click="confirmEvent"></vxe-button>
            </div>
          </template>
        </vxe-modal>

        <vxe-modal :mask="false" @hide="clearData" v-model="showMaterMoadl" size="mini" title="主机添加">
          <template v-slot>
            <div id="services">
              <span class="services_tiitle">Windiows服务器</span>
              <div class="dostype">
                <div class="angent" :class="dostype == 1 ? 'chooseDos' : ''" @click="dostype = 1">
                  <div class="img"></div>
                  <div class="name">Agent Windows</div>
                </div>
                <div class="snmp" :class="dostype == 2 ? 'chooseDos' : ''" @click="dostype = 2">
                  <div class="img"></div>
                  <div class="name">Snmp Windows</div>
                </div>
              </div>
              <span class="services_tiitle">Linux服务器</span>
              <div class="dostype">
                <div class="angent" :class="dostype == 3 ? 'chooseDos' : ''" @click="dostype = 3">
                  <div class="img"></div>
                  <div class="name">Agent Linux</div>
                </div>
                <div class="snmp" :class="dostype == 4 ? 'chooseDos' : ''" @click="dostype = 4">
                  <div class="img"></div>
                  <div class="name">Snmp Linux</div>
                </div>
              </div>
            </div>
            <div style="float:right">
              <vxe-button size="mini" status="primary" content="下一步" @click="confirmDosType"></vxe-button>
            </div>
          </template>
        </vxe-modal>

        <vxe-modal
          :mask="false"
          @hide="clearData"
          v-model="showEditMoadl"
          size="mini"
          :title="`添加设备${dosTypeName[dostype - 1]}`"
        >
          <vxe-form ref="xForm" title-width="120" title-align="right" :rules="formRules" :data="formData">
            <vxe-form-item title="IP地址" field="IP" span="24">
              <template v-slot="scope">
                <vxe-input
                  v-model="formData.IP"
                  placeholder="请输入IP地址"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="端口号" field="port" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入名称"
                  v-model="formData.port"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="设备型号" field="devType" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入名称"
                  v-model="formData.devType"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item v-if="dostype == 1 || dostype == 3" title="可读共同体名称" field="nameRead" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入名称"
                  v-model="formData.nameRead"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="版本" field="version" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入名称"
                  v-model="formData.version"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="位置" field="position" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入名称"
                  v-model="formData.position"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item title="标题" field="title" span="24">
              <template v-slot="scope">
                <vxe-input
                  placeholder="请输入名称"
                  v-model="formData.title"
                  clearable
                  @input="$refs.xForm.updateStatus(scope)"
                ></vxe-input>
              </template>
            </vxe-form-item>

            <vxe-form-item align="center" span="24">
              <template v-slot>
                <vxe-button type="reset" @click="goback()">上一步</vxe-button>
                <vxe-button type="submit" status="primary">保存</vxe-button>
              </template>
            </vxe-form-item>
          </vxe-form>
        </vxe-modal>
      </div>
    </template>
  </div>
</template>

<script>
  import Treeselect from '@riophae/vue-treeselect';
  import '@riophae/vue-treeselect/dist/vue-treeselect.css';
  export default {
    data() {
      return {
        formData: { IP: '', port: '', devType: '', nameRead: '', version: '', position: '', title: '' },
        formRules: {
          IP: [{ required: true, message: '请输入IP地址' }],
          port: [{ required: true, message: '请输入端口好' }],
          devType: [{ required: true, message: '请输入设备型号' }],
          nameRead: [{ required: true, message: '请输入共同体名称' }],
        },
        dosTypeName: ['Agent Windows', 'Snmp Windows', 'Agent Linux', 'Snmp Linux'],
        dostype: 0,
        // dostypeName: '',
        showAddMoadl: false,
        showTopuMoadl: false,
        showMaterMoadl: false,
        showEditMoadl: false,
        selctValue: 'IP',
        queryValue: '',
        tableData: [],
        checkData: [],
        treeValue: null,
        treeSelctOptions: [
          {
            id: '机房区',
            label: '机房区',
            children: [
              {
                id: 'serveice0',
                label: '服务器A',
              },
              {
                id: 'serveice1',
                label: '服务器B',
              },
              {
                id: 'serveice2',
                label: '服务器C',
              },
              {
                id: 'serveice3',
                label: '服务器D',
              },
            ],
          },
          {
            id: '值班区',
            label: '值班区',
            children: [
              {
                id: 'monitor0',
                label: '监控A',
              },
              {
                id: 'monitor1',
                label: '监控B',
              },
              {
                id: 'monitor2',
                label: '监控C',
              },
            ],
          },
          {
            id: '办公楼4楼',
            label: '办公楼5楼',
          },
          {
            id: '办公楼5楼',
            label: '办公楼5楼',
          },
          {
            id: '其它区域',
            label: '其它区域',
          },
        ],
        resData: [
          {
            number: '1',
            name: 'Oracle数据库',
            state: '正常',
            IP: '192.168.0.100',
            monitMode: '代理',
            time: '2020/8/20 10:00',
            macAdress: 'XX:XX:XX:XX',
            gateway: '10.24.23.10',
            deviceType: 'windows Agent',

            area: '机房',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
            devCharge: '张三',
          },
          {
            number: '2',
            name: '报文接收器',
            state: '正常',
            IP: '192.168.0.102',
            monitMode: '代理',
            time: '2020/8/20 10:00',
            macAdress: 'XX:XX:XX:XX',
            gateway: '10.24.23.10',
            deviceType: 'windows Agent',
            area: '机房',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
            devCharge: '张三',
          },
          {
            number: '3',
            name: 'MySql数据库',
            state: '正常',
            IP: '192.168.0.120',
            monitMode: '代理',
            time: '2020/8/20 10:03',
            macAdress: 'XX:XX:XX:XX',
            gateway: '10.24.23.10',
            deviceType: 'windows Agent',
            area: '机房',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
            devCharge: '张三',
          },
          {
            number: '4',
            name: '监控台A',
            state: '正常',
            IP: '192.168.0.104',
            monitMode: 'snmp',
            time: '2020/8/20 10:20',
            macAdress: 'XX:XX:XX:XX',
            gateway: '10.24.23.10',
            deviceType: 'Linux Agent',
            area: '值班室',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
            devCharge: '李四',
          },
          {
            number: '5',
            name: '监控台B',
            state: '正常',
            IP: '192.168.0.107',
            monitMode: 'snmp',
            time: '2020/8/20 10:10',
            macAdress: 'XX:XX:XX:XX',
            gateway: '10.24.23.10',
            deviceType: 'Linux Agent',
            area: '值班室',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
            devCharge: '李四',
          },
          {
            number: '6',
            name: '监控台B',
            state: '正常',
            IP: '192.168.0.103',
            monitMode: 'snmp',
            time: '2020/8/20 10:10',
            macAdress: 'XX:XX:XX:XX',
            gateway: '10.24.23.10',
            deviceType: 'Linux Agent',
            area: '值班室',
            address: 'xxxxxxxxxxxxxxxxxxxxx',
            devCharge: '李四',
          },
        ],
      };
    },
    created() {
      this.tableData = this.resData;
    },
    components: { Treeselect },
    methods: {
      async insertEvent(row) {
        let record = {
          number: parseInt(this.tableData[this.tableData.length - 1].number) + 1,
        };
        let { row: newRow } = await this.$refs.xTable.insertAt(record, row);
        await this.$refs.xTable.setActiveCell(newRow, 'number');
      },
      tableCheck({ row }) {},
      queryTbaleData() {
        if (this.queryValue == '') {
          this.tableData = this.resData;
          return;
        }
        this.tableData = [];
        this.tableData = this.resData.filter((item) => {
          if (item[this.selctValue].indexOf(this.queryValue) > -1) {
            return item;
          }
        });
      },
      clearQueryValue() {
        this.queryValue = '';
        this.tableData = this.resData;
      },
      editRowEvent(row) {
        this.$refs.xTable.setActiveRow(row);
      },
      saveRowEvent(row) {
        this.$refs.xTable.clearActived().then(() => {
          this.loading = true;
          setTimeout(() => {
            this.loading = false;
            this.$XModal.message({ message: '保存成功！', status: 'success' });
          }, 300);
        });
      },
      cancelRowEvent(row) {
        const xTable = this.$refs.xTable;
        xTable.clearActived().then(() => {
          // 还原行数据
          xTable.revertData(row);
        });
      },
      confirmEvent() {
        if (this.treeValue != '' && this.treeValue != null) {
          this.showMaterMoadl = true;
          this.showTopuMoadl = false;
        } else {
          this.$XModal.message({ message: '请先选择设备', status: 'warning' });
        }
      },
      confirmDosType() {
        if (this.dostype !== 0) {
          this.showMaterMoadl = false;
          this.showEditMoadl = true;
        } else {
          this.$XModal.message({ message: '请先选择主机', status: 'warning' });
        }
      },
      goback() {
        this.showEditMoadl = false;
        this.showMaterMoadl = true;
      },
      clearData() {
        this.treeValue = null;
        //this.dostype = 0;
      },
      insertDevice() {
        this.showAddMoadl = true;
      },
    },
    mounted() {},
  };
</script>

<style lang="scss" scoped>
  #managerMaster {
    width: 100%;
    height: 100%;
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    #header {
      z-index: 1;
      width: 100%;
      height: 1.25rem;
      // line-height: 1.25rem;
      font-size: 0.2rem;
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      margin-bottom: 0.1rem;
      display: flex;
      justify-content: center;
      align-items: center;
      div {
        flex: 1;
      }
      #select {
        text-align: right;
      }

      #query {
        text-align: center;
        #queryIcon {
          margin-left: -0.4rem;
          color: #dcdfe6;
        }
        #clearIcon {
          margin-left: -0.4rem;
          color: #676767;
          cursor: pointer;
        }
        #queryValue {
          height: 0.375rem;
          width: 4rem;
          border: 1px solid #dcdfe6;
          border-radius: 0.05rem;
          &:focus {
            outline: none !important;
            border: 1px solid #0f9fec !important;
          }
        }
      }
      #operate {
        display: inline-block;
        text-align: center;
        padding-right: 1rem;
        span {
          color: #409eff;
          background: #ecf5ff;
          cursor: pointer;
          &:hover {
            background: #409eff;
            color: #fff;
          }
          padding: 0.0625rem 0.125rem;
          border: 1px solid #b3d8ff;
          border-radius: 0.05rem;
          // display: block;
          font-weight: 500;
          margin-left: 0.1875rem;
          font-size: 0.2rem !important;
        }
      }
    }
    #content {
      box-shadow: #bddfeb8f 0 0 0.3rem 0.1rem;
      width: 100%;
      height: calc(100% - 1.35rem);
      background: white;
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      overflow: auto;
      #table {
        padding: 0.25rem;
        width: 100%;

        span {
          color: #409eff;
          background: #ecf5ff;
          cursor: pointer;
          &:hover {
            background: #409eff;
            color: #fff;
          }
          padding: 0.0625rem 0.125rem;
          border: 1px solid #b3d8ff;
          border-radius: 0.05rem;
          // display: block;
          font-weight: 500;
          margin-left: 0.1875rem;
          font-size: 0.2rem !important;
        }
      }
    }
  }

  #services {
    width: 100%;
    height: 5rem;
    //padding: 0 0.25rem;
    .services_tiitle {
      font-size: 0.25rem;
      color: green;
      display: block;
      padding-left: 0.25rem;
    }

    .dostype {
      user-select: none;
      height: 2rem;
      width: 100%;
      display: flex;
      justify-content: space-between;
      .angent {
        cursor: pointer;
        margin: 0.4rem 0;
        width: 45%;
        display: flex;
        justify-content: center;
        align-items: center;
        border: 1px solid #ebebeb;
        border-radius: 0.1rem;
        .img {
          background-image: url('../../../../../../assets/img/window1.png');
        }
      }

      .snmp {
        cursor: pointer;
        margin: 0.4rem 0;
        width: 45%;
        display: flex;
        justify-content: center;
        align-items: center;
        border: 1px solid #ebebeb;
        border-radius: 0.1rem;
        .img {
          background-image: url('../../../../../../assets/img/window2.png');
        }
      }

      .chooseDos {
        background: #bdd5f0;
      }
      .img {
        width: 40%;
        height: 100%;
        background-repeat: no-repeat;
        background-size: cover;
      }
      .name {
        width: 60%;
        height: 100%;
        padding: 0.5rem 0;
        font-size: 13px;
      }
    }
  }
</style>
-->
<template>
  <div>
    <div  class="app-container">
      <a-form-model layout="inline"  :model="queryParams" ref="queryForm" >
        <a-form-model-item label="ip地址"  prop="ip">
          <a-input v-model="queryParams.ip" placeholder="请输入ip地址">
          </a-input>
        </a-form-model-item>
        <a-form-model-item label="运行状态" prop="triggerStatus">
          <a-select style="width: 120px"
                    v-model="queryParams.triggerStatus"
          >
            <a-select-option  v-for="dict in triggerStatusOptions"
                              :value="dict.dictValue">
              {{dict.dictLabel}}
            </a-select-option>

          </a-select>
        </a-form-model-item>
        <a-form-model-item >
          <a-col :span="24" :style="{ textAlign: 'right' }">
            <a-button type="primary" html-type="submit" @click="handleQuery">
              搜索
            </a-button>
            <a-button :style="{ marginLeft: '8px' }" @click="resetQuery">
              重置
            </a-button>
          </a-col>
        </a-form-model-item>
      </a-form-model>
    </div>
    <a-row type="flex"  style="margin-left: 2% " >
      <a-col :span="1.5">
        <a-button type="primary"
                  icon="plus"
                  @click="handleAdd">
          新增
        </a-button>
        <a-button type="danger"
                  icon="delete"
                  @click="handleDelete">
          删除
        </a-button>
      </a-col>
    </a-row>
    <a-row type="flex">
      <a-table  :row-selection="rowSelection"
                :row-key="record => record.id"
                :columns="columns"
                :data-source="data"
                :pagination="pagination"
                :loading="loading"
                size="small"
                @change="handleTableChange"
                :rowClassName="fnRowClass"
                :scroll="{ x: 'calc(1200px + 50%)'}"

      >
      <span slot="currentStatus" slot-scope="text">
       {{statusFormat(currentStatusOptions,text)}}
      </span>
        <span slot="mediaType" slot-scope="text">
       {{statusFormat(mediaTypeOptions,text)}}
      </span>
        <span slot="monitoringMethods" slot-scope="text">
       {{statusFormat(monitoringMethodsOptions,text)}}
      </span>
        <span slot="triggerStatus" slot-scope="text">
        {{statusFormat(triggerStatusOptions,text)}}
      </span>
        <span slot="area" slot-scope="text">
        {{statusFormat(areaOptions,text)}}
      </span>

        <span slot="createTime" slot-scope="text">
       {{ parseTime(text) }}
      </span>
        <span slot="operate" slot-scope="text, record">
       <a-button icon="edit" size="small" @click="handleUpdate(record)">
        编辑
       </a-button>
       <a-button icon="delete" size="small"  @click="handleDelete(record)">
        删除
       </a-button>
      </span>
      </a-table>
    </a-row>
    <a-modal v-model="visible" :title="title" okText="确定" cancelText="取消"
             width="50%" @ok="submitForm"
    >
      <a-form-model  :label-col="{ span: 6 }" :wrapperCol="{ span: 17 }" :model="form" ref="form" >
        <a-row>
          <a-col :span="12">
            <a-form-model-item  label="设备别名"  prop="taskName">
              <a-input v-model="form.taskName" placeholder="请输入设备别名">
              </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item  label="设备名称"  prop="hostName">
              <a-input v-model="form.hostName" placeholder="请输入设备名称">
              </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="12">
            <a-form-model-item  label="ip地址"  prop="ip">
              <a-input v-model="form.ip" placeholder="请输入ip地址">
              </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item  label="网关"  prop="gateway">
              <a-input v-model="form.gateway" placeholder="请输入网关">
              </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item  label="子网掩码"  prop="mask">
              <a-input v-model="form.mask" placeholder="请输入子网掩码">
              </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item  label="监控策略"  prop="jobCron">
              <a-input v-model="form.jobCron" placeholder="请输入监控策略">
              </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item  label="mac地址"  prop="mac">
              <a-input v-model="form.mac" placeholder="请输入mac地址">
              </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item  label="负责人"  prop="createBy">
              <a-input v-model="form.createBy" placeholder="负责人">
              </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item  label="监视方式"  prop="monitoringMethods">
              <a-select
                      v-model="form.monitoringMethods"
                      placeholder="字典状态"
              >
                <a-select-option v-for="dict in monitoringMethodsOptions"
                                 :value="parseInt(dict.dictValue)">
                  {{dict.dictLabel}}
                </a-select-option>
              </a-select>

            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item  label="设备类型"  prop="mediaType">
              <a-select
                      v-model="form.mediaType"
              >
                <a-select-option  v-for="dict in mediaTypeOptions"
                                  :value="parseInt(dict.dictValue)">
                  {{dict.dictLabel}}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item  label="区域"  prop="area">
              <a-select
                      v-model="form.area"
              >
                <a-select-option v-for="dict in areaOptions"
                                 :value="parseInt(dict.dictValue)">
                  {{dict.dictLabel}}
                </a-select-option>
              </a-select>

            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item  label="设备状态"  prop="currentStatus">

              <a-select
                      v-model="form.currentStatus"
              >
                <a-select-option v-for="dict in currentStatusOptions"
                                 :value="parseInt(dict.dictValue)">
                  {{dict.dictLabel}}
                </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item  :label-col="{ span: 3 }" :wrapperCol="{ span: 20 }" label="详细地址"  prop="location">
              <a-input type="textarea" v-model="form.location" placeholder="详细地址">
              </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>

        </a-row>
      </a-form-model>
    </a-modal>
  </div>

</template>
<style>
  .csbsTypes{
    font-size: 14px;
  }
  .ant-table-thead > tr > th {
    font-size: 15px;
  }
</style>
<script>
  import request from "@/utils/request";
  const columns = [
    {
      title: '设备别名',
      dataIndex: 'taskName',
      width: '10%',
      scopedSlots: { customRender: 'taskName' },
    },
    {
      title: '监控策略',
      dataIndex: 'jobCron',
      width: '10%',
    },
    {
      title: '设备状态',
      dataIndex: 'currentStatus',
      width: '10%',
      scopedSlots: { customRender: 'currentStatus' },
    },
    {
      title: '设备名称',
      dataIndex: 'hostName',
      width: '10%',
    },
    {
      title: 'ip地址',
      dataIndex: 'ip',
      width: '10%',
    },
    {
      title: '监控方式',
      dataIndex: 'monitoringMethods',
      width: '10%',
      scopedSlots: { customRender: 'monitoringMethods' },
    },
    {
      title: '设备类型',
      dataIndex: 'mediaType',
      width: '10%',
      scopedSlots: { customRender: 'mediaType' },
    },
    {
      title: '网关',
      dataIndex: 'gateway',
      width: '10%',
    },
    {
      title: 'mac地址',
      dataIndex: 'mac',
      width: '15%',
    },
    {
      title: '区域',
      dataIndex: 'area',
      width: '10%',
      scopedSlots: { customRender: 'area' },
    },
    {
      title: '地址',
      dataIndex: 'location',
      width: '15%',
    },
    {
      title: '运行状态',
      dataIndex: 'triggerStatus',
      width: '10%',
      scopedSlots: { customRender: 'triggerStatus' },
    },
    {
      title: '发现时间',
      dataIndex: 'createTime',
      width: '200px',
      scopedSlots: { customRender: 'createTime' },
    },
    {
      title: '操作',
      dataIndex: 'operate',
      width: '200px',
      scopedSlots: { customRender: 'operate' },
    },
  ];

  export default {
    data() {
      return {
        data: [],
        pagination: {},
        loading: false,
        columns,
        // 日期范围
        dateRange: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          triggerStatus: undefined,
          taskName: undefined,
          ip: undefined,
        },
        visible: false,
        form: {},
        triggerStatusOptions: [],
        currentStatusOptions: [],
        mediaTypeOptions: [],
        monitoringMethodsOptions:[],
        areaOptions:[],
        title: "",
        ids: [],
        ips: [],
      };
    },
    created(){
      this.getDicts("job_trigger_status").then(response => {
        this.triggerStatusOptions = response.data;
      });
      this.getDicts("current_status").then(response => {
        this.currentStatusOptions = response.data;
      });
      this.getDicts("media_type").then(response => {
        this.mediaTypeOptions = response.data;
      });
      this.getDicts("monitoring_methods").then(response => {
        this.monitoringMethodsOptions = response.data;
      });
      this.getDicts("media_area").then(response => {
        this.areaOptions = response.data;
      });
    },
    mounted() {
      this.fetch();
    },
    computed: {
      rowSelection() {
        return {
          onChange: (selectedRowKeys, selectedRows) => {
            this.ids = selectedRows.map(item => item.id);
            this.ips = selectedRows.map(item => item.ip);
          },
          getCheckboxProps: record => ({
            props: {
              disabled: record.id === 'Disabled User', // Column configuration not to be checked
              name: record.id,
            },
          }),
        };
      },
    },
    methods: {
      statusFormat(options,status) {
        return this.selectDictLabel(options,status);
      },
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.fetch();
      },
      resetQuery() {
        this.dateRange = [];
        this.$refs.queryForm.resetFields();
        this.handleQuery();
      },
      fetch() {
        this.loading = true;
        request({
          url:'/hostConfig/list',
          method: 'get',
          params: this.addDateRange(this.queryParams, this.dateRange)
        }).then(data => {
          const pagination = { ...this.pagination };
          pagination.total = data.data.totalCount;
          this.loading = false;
          this.data = data.data.pageData;
          this.pagination = pagination;
        });
      },
      handleAdd(){
        this.form.id=undefined;
        this.title="新增链路设备";
        this.visible = true;
      },
      handleTableChange(pagination, filters, sorter) {
        const pager = { ...this.pagination };
        this.queryParams.pageNum = pagination.current;
        this.pagination = pager;
        this.fetch();
      },
      handleUpdate(row) {
        this.form={};
        const id = row.id || this.ids;

        request({
          url: '/hostConfig/' + id,
          method: 'get'
        }).then(response => {
          this.form = response.data;
          //this.form.monitoringMethods= this.form.monitoringMethods.toString();
          //this.form.mediaType= this.form.mediaType.toString();
          //this.form.deviceType= this.form.deviceType.toString();
          //this.form.currentStatus= this.form.currentStatus.toString();
          //this.form.triggerStatus=this.form.triggerStatus.toString();
          this.visible = true;
          this.title = "修改链路设备";
        });
      },
      handleDelete(row) {
        const ids = row.id || this.ids;
        const ips = row.ip || this.ips;
        this.$confirm({
          title:  '是否确认删除ip为"' + ips + '"的数据项?',
          content: '',
          okText: '是',
          okType: 'danger',
          cancelText: '否',
          onOk:()=> {
            request({
              url: '/hostConfig/' + ids,
              method: 'delete'
            }).then((response) => {
              if (response.code === 200) {
                this.fetch();
                this.msgError("删除成功!");
              } else {
                this.msgError(response.msg);
              }
            })
          },
          onCancel() {

          },
        });
      },
      submitForm(){
        if (this.form.id != undefined) {
          request({
            url: '/hostConfig',
            method: 'post',
            data: this.form
          }).then(response => {
            if (response.code === 200) {
              this.msgSuccess("修改成功");
              this.visible = false;
              this.fetch();
            } else {
              this.msgError(response.msg);
            }
          });
        }else {
          request({
            url: '/hostConfig',
            method: 'post',
            data: this.form
          }).then(response => {
            if (response.code === 200) {
              this.msgSuccess("新增成功");
              this.visible = false;
              this.fetch();
            } else {
              this.msgError(response.msg);
            }
          });
        }
      },
      fnRowClass(record,index){
        return  "csbsTypes"
      }
    },
  };
</script>
