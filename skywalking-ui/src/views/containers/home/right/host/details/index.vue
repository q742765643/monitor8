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
          <!--   :checkbox-config="{ labelField: 'number' }" -->
          <!--   <vxe-table-column type="checkbox" width="80" title="序号"></vxe-table-column> -->
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
                <!--   <vxe-button @click="saveRowEvent(row)">保存</vxe-button>
                <vxe-button @click="cancelRowEvent(row)">取消</vxe-button> -->

                <span class="iconfont iconiconfontcheck" @click="saveRowEvent(row)">保存</span>
                <span class="iconfont iconcuo" @click="cancelRowEvent(row)">取消</span>
              </template>
              <template v-else>
                <!--  <vxe-button @click="editRowEvent(row)">编辑</vxe-button> -->
                <span class="iconfont iconbianji1" @click="editRowEvent(row)">编辑</span>
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>

    <!--   添加设备 -->
    <template>
      <div>
        <!--   添加设备 -->
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

        <!--   链路中添加 -->
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
