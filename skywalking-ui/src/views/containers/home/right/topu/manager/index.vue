<!--<template>-->
<!--  <div class="managerTemplate">-->
<!--    <div class="head">-->
<!--      <div class="rightBox">-->
<!--        <input type="text" v-model="querform.toolName" placeholder="设备别名" />-->
<!--        <select v-model="querform.toolStatus" placeholder="设备别名">-->
<!--          <option value>状态</option>-->
<!--        </select>-->
<!--        <input type="text" v-model="querform.toolIp" placeholder="IP地址" />-->
<!--        <select v-model="querform.toolType" placeholder="设备别名">-->
<!--          <option value>设备类型</option>-->
<!--        </select>-->
<!--        <select v-model="querform.toolMethods" placeholder="设备别名">-->
<!--          <option value>监视方式</option>-->
<!--        </select>-->
<!--        <select v-model="querform.toolArea" placeholder="设备别名">-->
<!--          <option value>区域</option>-->
<!--        </select>-->
<!--      </div>-->
<!--      <div class="leftBox">-->
<!--        <button><i class="icon iconfont"></i> 搜索</button>-->
<!--        <button><i class="icon iconfont iconxinzeng"></i>添加设备</button>-->
<!--        <button><i class="icon iconfont icondelete"></i>取消监视</button>-->
<!--        <button @click="showChart"><i class="icon iconfont iconchuli"></i>生成报表</button>-->
<!--      </div>-->
<!--    </div>-->

<!--    <div class="body">-->
<!--      <div class="leftBox">-->
<!--        <tree-table-->
<!--          ref="recTree"-->
<!--          :list.sync="treeDataSource"-->
<!--          @actionFunc="actionFunc"-->
<!--          @handlerExpand="handlerExpand"-->
<!--          @handlerChecked="handlerChecked"-->
<!--        ></tree-table>-->
<!--      </div>-->

<!--      <div class="rightBox"></div>-->
<!--    </div>-->

<!--    <div class="dialogBox" v-if="showChartFlag">-->
<!--      <div class="eldialog">-->
<!--        <div class="dialogHeader">-->
<!--          <div>-->
<!--            <button>生成PDF</button>-->
<!--            <button>生成EXL</button>-->
<!--          </div>-->
<!--          <div class="dialogHandleBox" @click="showChartFlag = false">-->
<!--            <i class="icon iconfont iconbaseline-close-px"></i>关闭-->
<!--          </div>-->
<!--        </div>-->
<!--        <div class="dialogBody" id="itemChartBody"></div>-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--  import echarts from 'echarts';-->
<!--  import treeTable from './treeTable/tree-table';-->
<!--  export default {-->
<!--    data() {-->
<!--      return {-->
<!--        showChartFlag: false,-->
<!--        querform: {-->
<!--          toolName: '',-->
<!--          toolStatus: '',-->
<!--          toolIp: '',-->
<!--          toolType: '',-->
<!--          toolMethods: '',-->
<!--          toolArea: '',-->
<!--        },-->
<!--        treeDataSource: [-->
<!--          {-->
<!--            label: '一级 1',-->
<!--            id: '1',-->
<!--            isChecked: false,-->
<!--            children: [-->
<!--              {-->
<!--                label: '二级 1-1',-->
<!--                id: '1-1',-->
<!--                isChecked: false,-->
<!--                children: [-->
<!--                  {-->
<!--                    label: '三级 1-1-1',-->
<!--                    id: '1-1-1',-->
<!--                    isChecked: false,-->
<!--                  },-->
<!--                ],-->
<!--              },-->
<!--            ],-->
<!--          },-->
<!--          {-->
<!--            label: '一级 2',-->
<!--            id: '2',-->
<!--            isChecked: false,-->
<!--            children: [-->
<!--              {-->
<!--                label: '二级 2-1',-->
<!--                id: '2-1',-->
<!--                isChecked: false,-->
<!--                children: [-->
<!--                  {-->
<!--                    label: '三级 2-1-1',-->
<!--                    id: '2-1-1',-->
<!--                    isChecked: false,-->
<!--                  },-->
<!--                ],-->
<!--              },-->
<!--              {-->
<!--                label: '二级 2-2',-->
<!--                id: '2-2',-->
<!--                isChecked: false,-->
<!--                children: [-->
<!--                  {-->
<!--                    label: '三级 2-2-1',-->
<!--                    id: '2-2-1',-->
<!--                    isChecked: false,-->
<!--                  },-->
<!--                ],-->
<!--              },-->
<!--            ],-->
<!--          },-->
<!--          {-->
<!--            label: '一级 3',-->
<!--            id: '3',-->
<!--            isChecked: false,-->
<!--            children: [-->
<!--              {-->
<!--                label: '二级 3-1',-->
<!--                id: '3-1',-->
<!--                isChecked: false,-->
<!--                children: [-->
<!--                  {-->
<!--                    label: '三级 3-1-1',-->
<!--                    id: '3-1-1',-->
<!--                    isChecked: false,-->
<!--                  },-->
<!--                ],-->
<!--              },-->
<!--              {-->
<!--                label: '二级 3-2',-->
<!--                id: '3-2',-->
<!--                isChecked: false,-->
<!--                children: [-->
<!--                  {-->
<!--                    label: '三级 3-2-1',-->
<!--                    id: '3-2-1',-->
<!--                    isChecked: false,-->
<!--                  },-->
<!--                ],-->
<!--              },-->
<!--            ],-->
<!--          },-->
<!--          {-->
<!--            label: '四级 1',-->
<!--            id: '4',-->
<!--            isChecked: false,-->
<!--          },-->
<!--        ],-->
<!--      };-->
<!--    },-->
<!--    components: {-->
<!--      treeTable,-->
<!--    },-->
<!--    mounted() {},-->
<!--    methods: {-->
<!--      showChart() {-->
<!--        this.showChartFlag = true;-->
<!--        this.$nextTick(() => {-->
<!--          this.drawChart();-->
<!--        });-->
<!--        // 基于准备好的dom，初始化echarts实例-->
<!--      },-->
<!--      drawChart() {-->
<!--        var myChart = echarts.init(document.getElementById('itemChartBody'));-->
<!--        myChart.setOption({-->
<!--          color: ['#5793f3', '#6e64bd', '#d14a61'],-->
<!--          tooltip: {-->
<!--            trigger: 'axis',-->
<!--            axisPointer: {-->
<!--              type: 'cross',-->
<!--              crossStyle: {-->
<!--                color: '#999',-->
<!--              },-->
<!--            },-->
<!--          },-->
<!--          toolbox: {-->
<!--            feature: {-->
<!--              dataView: { show: true, readOnly: false },-->
<!--              magicType: { show: true, type: ['line', 'bar'] },-->
<!--              restore: { show: true },-->
<!--              saveAsImage: { show: true },-->
<!--            },-->
<!--          },-->
<!--          legend: {-->
<!--            data: ['在线时长', '平均丢包率', '最大丢包率'],-->
<!--          },-->
<!--          xAxis: [-->
<!--            {-->
<!--              type: 'category',-->
<!--              data: [-->
<!--                'A设备',-->
<!--                'B设备',-->
<!--                'C设备',-->
<!--                'D设备',-->
<!--                'E设备',-->
<!--                'F设备',-->
<!--                'G设备',-->
<!--                'H设备',-->
<!--                'I设备',-->
<!--                'J设备',-->
<!--                'k设备',-->
<!--                'L设备',-->
<!--              ],-->
<!--              axisPointer: {-->
<!--                type: 'shadow',-->
<!--              },-->
<!--            },-->
<!--          ],-->
<!--          yAxis: [-->
<!--            {-->
<!--              type: 'value',-->
<!--              name: '丢包率',-->
<!--              min: 0,-->
<!--              max: 100,-->
<!--              interval: 20,-->
<!--              axisLabel: {-->
<!--                formatter: '{value} %',-->
<!--              },-->
<!--              axisLine: {-->
<!--                lineStyle: {-->
<!--                  color: '#d14a61',-->
<!--                },-->
<!--              },-->
<!--            },-->
<!--            {-->
<!--              type: 'value',-->
<!--              name: '在线时长',-->
<!--              min: 0,-->
<!--              max: 250,-->
<!--              interval: 50,-->
<!--              axisLabel: {-->
<!--                formatter: '{value} h',-->
<!--              },-->
<!--              axisLine: {-->
<!--                lineStyle: {-->
<!--                  color: '#5793f3',-->
<!--                },-->
<!--              },-->
<!--            },-->
<!--          ],-->
<!--          series: [-->
<!--            {-->
<!--              name: '在线时长',-->
<!--              type: 'bar',-->
<!--              data: [2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],-->
<!--            },-->
<!--            {-->
<!--              name: '平均丢包率',-->
<!--              type: 'line',-->
<!--              data: [2.6, 5.9, 9.0, 26.4, 8.7, 0.7, 25.6, 22.2, 38.7, 18.8, 6.0, 2.3],-->
<!--            },-->
<!--            {-->
<!--              name: '最大丢包率',-->
<!--              type: 'line',-->
<!--              yAxisIndex: 1,-->
<!--              data: [2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2],-->
<!--            },-->
<!--          ],-->
<!--        });-->
<!--      },-->
<!--      actionFunc(m) {-->
<!--        alert('编辑');-->
<!--      },-->

<!--      handlerExpand(m) {-->
<!--        console.log('展开/收缩');-->
<!--        m.isExpand = !m.isExpand;-->
<!--      },-->
<!--      handlerChecked(m) {-->
<!--        console.log(m);-->
<!--        console.log(m.isChecked);-->
<!--        this.resetData(m.children, m.isChecked);-->
<!--      },-->
<!--      resetData(dataArr, checked) {-->
<!--        for (var i in dataArr) {-->
<!--          dataArr[i].isChecked = checked;-->
<!--          this.resetData(dataArr[i].children, checked);-->
<!--        }-->
<!--      },-->
<!--    },-->
<!--  };-->
<!--</script>-->

<!--<style lang="scss" scoped>-->
<!--  .managerTemplate {-->
<!--    height: 100%;-->
<!--    padding: 0.5rem 0.375rem 0.375rem 0.2rem;-->
<!--    background: #eef5fd;-->
<!--    width: 20rem;-->
<!--    input,-->
<!--    select,-->
<!--    button {-->
<!--      border: 1px solid #dcdfe6;-->
<!--      border-radius: 0.05rem;-->
<!--      color: #606266;-->
<!--      display: inline-block;-->
<!--      font-size: inherit;-->
<!--      height: 0.375rem;-->
<!--      line-height: 0.375rem;-->
<!--      outline: none;-->
<!--      padding: 0 0.1875rem;-->
<!--      transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);-->
<!--      margin-left: 0.075rem;-->
<!--    }-->
<!--    button {-->
<!--      color: #409eff;-->
<!--      background: #ecf5ff;-->
<!--      border-color: #b3d8ff;-->
<!--      cursor: pointer;-->
<!--      &:hover {-->
<!--        background: #409eff;-->
<!--        color: #fff;-->
<!--      }-->
<!--    }-->
<!--    .head {-->
<!--      width: 100%;-->
<!--      height: 1rem;-->
<!--      background: #fff;-->
<!--      display: flex;-->
<!--      align-items: center;-->
<!--      justify-content: space-between;-->
<!--      padding: 0 0.125rem;-->
<!--      .leftBox {-->
<!--        padding-right: 0.075rem;-->
<!--      }-->
<!--    }-->
<!--    .body {-->
<!--      width: 100%;-->
<!--      height: calc(100% - 1rem);-->
<!--      display: flex;-->
<!--      align-items: center;-->
<!--      justify-content: space-between;-->
<!--      padding-top: 0.2rem;-->
<!--      .leftBox,-->
<!--      .rightBox {-->
<!--        height: 100%;-->
<!--        background: #fff;-->
<!--        padding: 0.2rem;-->
<!--      }-->
<!--      .leftBox {-->
<!--        width: 76%;-->
<!--        margin-right: 0.2rem;-->
<!--      }-->
<!--      .rightBox {-->
<!--        width: calc(24% - 0.2rem);-->
<!--      }-->
<!--    }-->
<!--    .dialogBox {-->
<!--      position: fixed;-->
<!--      top: 0;-->
<!--      right: 0;-->
<!--      bottom: 0;-->
<!--      left: 0;-->
<!--      overflow: auto;-->
<!--      z-index: 1000;-->
<!--      background: rgba(0, 0, 0, 0.75);-->
<!--      .eldialog {-->
<!--        width: 7.875rem;-->
<!--        height: 4.125rem;-->
<!--        position: relative;-->
<!--        margin: 0 auto 0.625rem;-->
<!--        margin-top: 15vh;-->
<!--        background: #fff;-->
<!--        border-radius: 0.025rem;-->
<!--        box-shadow: 0 0.0125rem 0.0375rem rgba(0, 0, 0, 0.3);-->
<!--        box-sizing: border-box;-->
<!--        .dialogHeader {-->
<!--          cursor: pointer;-->
<!--          display: flex;-->
<!--          align-items: center;-->
<!--          justify-content: space-between;-->
<!--          padding: 0.15rem;-->
<!--          span {-->
<!--            font-size: 0.175rem;-->
<!--          }-->
<!--        }-->
<!--        #itemChartBody {-->
<!--          width: 100%;-->
<!--          height: 3.375rem;-->
<!--        }-->
<!--      }-->
<!--    }-->
<!--  }-->
<!--</style>-->
<!--
<template>
  <div id="config">
    <div class="contnet">
      <div class="name">
        <span>网段名称</span>
        <div>
          <input type="text" />
        </div>
      </div>
      <div class="startIP">
        <span>起始IP</span>
        <div class="ip">
          <input type="text" />— <input type="text" />— <input type="text" />—
          <input type="text" />
        </div>
      </div>
      <div class="endIP">
        <span>结束IP</span>
        <div class="ip">
          <input type="text" />— <input type="text" />— <input type="text" />—
          <input type="text" />
        </div>
      </div>
      <div class="manager">
        <span class="iconfont iconxinzeng">新增</span>
        <span class="iconfont iconxiugai">修改</span>
        <span class="iconfont icondelete">删除</span>
      </div>
      <div class="tableEdit">
        <vxe-table border ref="xTable2" :data="tableInfo" align="center" :checkbox-config="{ labelField: 'id' }">
          &lt;!&ndash;   <vxe-table-column field="id" title="序号"></vxe-table-column> &ndash;&gt;
          <vxe-table-column type="checkbox" title="序号"></vxe-table-column>
          <vxe-table-column field="netname" title="网段名称"></vxe-table-column>
          <vxe-table-column field="sIP" title="起始IP"></vxe-table-column>
          <vxe-table-column field="eIP" title="结束IP"></vxe-table-column>
          <vxe-table-column field="cTime" title="创建时间" show-overflow></vxe-table-column>
        </vxe-table>
        &lt;!&ndash;   :checkbox-config="{ labelField: 'netname' }" &ndash;&gt;
        &lt;!&ndash; <div class="title">
          :checkbox-config="{labelField: 'name'}"
          <span class="number">序号</span>
          <span class="netname">网段名称</span>
          <span class="sIP">起始IP</span>
          <span class="eIP">结束IP</span>
          <span class="cTime">创建时间</span>
        </div>

        <div class="info" v-for="(item, index) in tableInfo" :key="index">
          <span class="number">
            <span>{{ index }}</span>
            <input type="checkbox" />
          </span>
          <span class="netname">{{ item.netname }}</span>
          <span class="sIP">{{ item.sIP }}</span>
          <span class="eIP">{{ item.eIP }}</span>
          <span class="cTime">{{ item.cTime }}</span>
        </div> &ndash;&gt;
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        tableInfo: [
          { id: '1', netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { id: '2', netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { id: '3', netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { id: '4', netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { id: '5', netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
          { id: '6', netname: '机房网段', sIP: '10.65.24.1', eIP: '10.65.24.159', cTime: '2019-08-24' },
        ],
      };
    },
  };
</script>

<style lang="scss" scoped>
  #config {
    padding: 0.5rem 0.375rem 0.375rem 0.2rem;
    // display: flex;
    // flex-direction: column;
    background: #eef5fd;
    width: 20rem;

    //font-weight: 600;

    .contnet {
      height: 11.625rem;
      width: 19.625rem;
      background: white;
      padding-left: 0.375rem;
      font-size: 0.2rem;
      input[type='text'] {
        height: 0.35rem;
        font-size: 16px;
        width: 2rem;
        // border: 1px solid #c9c1c1 !important;
        border: 1px solid #dcdfe6;
        border-radius: 0.05rem;
        &:focus {
          outline: none !important;
          border: 1px solid #0f9fec !important;
        }
      }
      .name {
        padding-top: 0.25rem;
        input {
          width: 2.5rem;
        }
        span {
          line-height: 0.5rem;
        }
      }
      .startIP {
        margin-top: 0.25rem;
        span {
          line-height: 0.5rem;
        }
        .ip {
          display: flex;
          //justify-content: center;
          align-items: center;
          line-height: 0.5rem;
        }
      }
      .endIP {
        margin-top: 0.25rem;
        span {
          line-height: 0.5rem;
        }
        .ip {
          line-height: 0.5rem;
          display: flex;
          //justify-content: center;
          align-items: center;
        }
      }
      .manager {
        padding-right: 0.5rem;
        margin-top: 0.4rem;
        display: flex;
        justify-content: flex-end;
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
          display: block;
          font-weight: 500;
          margin-left: 0.1875rem;
          font-size: 0.2rem;
        }
      }
      .tableEdit {
        padding-right: 0.3rem;
        margin-top: 0.5rem;
        width: 100%;
        div:nth-child(2n) {
          background: #eef5fd;
        }
        .title {
          line-height: 0.7rem;
          width: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          span {
            text-align: center;
            display: inline-block;
            font-size: 0.25rem;
          }
          .number {
            flex: 1;
          }
          .netname {
            flex: 3;
          }
          .sIP {
            flex: 3;
          }
          .eIP {
            flex: 3;
          }
          .cTime {
            flex: 4;
          }
        }
        .info {
          &:hover {
            background: #eeeeee;
          }
          @extend .title;
          border-bottom: 1px solid #eeeeee;

          .number {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            input[type='checkbox'] {
              display: inline-block;
              height: 0.45rem;
              font-size: 16px;
              width: 0.5rem;
            }
          }
          font-size: 0.2rem;
          font-weight: 300;
        }
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
