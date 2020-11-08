<template>
  <div class="managerTemplate">

    <div class="head">
      <a-form-model layout="inline" :model="queryParams" class="queryForm">
        <a-form-model-item label="ip地址">
          <a-input v-model="queryParams.ip" placeholder="请输入ip地址"> </a-input>
        </a-form-model-item>
        <a-form-model-item label="运行状态">
          <a-select v-model="queryParams.triggerStatus">
            <a-select-option value="">全部</a-select-option>
            <a-select-option v-for="(dict, index) in triggerStatusOptions" :value="dict.dictValue" :key="index">
              {{ dict.dictLabel }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item>
          <a-button type="primary" html-type="submit" @click="handleQuery">
            搜索
          </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery">
            重置
          </a-button>
        </a-form-model-item>
      </a-form-model>
    </div>
    <div id="linkManger_content">
      <a-row type="flex" class="rowToolbar">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd">
            新增
          </a-button>
          <a-button type="danger" icon="delete" @click="handleDelete">
            删除
          </a-button>
        </a-col>
      </a-row>
      <div id="tablediv">
        <vxe-table :data="tableData" align="center" highlight-hover-row ref="tablevxe">
          <vxe-table-column type="checkbox"></vxe-table-column>
          <vxe-table-column field="taskName" title="设备别名"></vxe-table-column>
          <vxe-table-column field="jobCron" title="监控策略" show-overflow></vxe-table-column>
          <vxe-table-column field="currentStatus" title="设备状态" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(currentStatusOptions, row.currentStatus) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="hostName" title="设备名称" show-overflow></vxe-table-column>
          <vxe-table-column field="ip" title="ip地址" show-overflow></vxe-table-column>
          <vxe-table-column field="monitoringMethods" title="监控方式" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(monitoringMethodsOptions, row.monitoringMethods) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="mediaType" title="设备类型" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(mediaTypeOptions, row.mediaType) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="gateway" title="网关" show-overflow></vxe-table-column>
          <vxe-table-column field="mac" title="mac地址" show-overflow></vxe-table-column>
          <vxe-table-column field="area" title="区域" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(areaOptions, row.area) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="location" title="地址" show-overflow></vxe-table-column>
          <vxe-table-column field="triggerStatus" title="运行状态" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(triggerStatusOptions, row.triggerStatus) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="createTime" title="发现时间" show-overflow>
            <template v-slot="{ row }">
              <span> {{ parseTime(row.createTime) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column width="160" field="date" title="操作">
            <template v-slot="{ row }">
              <a-button type="primary" icon="edit" @click="handleEdit(row)">
                编辑
              </a-button>
              <a-button type="danger" icon="delete" @click="handleDelete(row)">
                删除
              </a-button>
            </template>
          </vxe-table-column>
        </vxe-table>

        <vxe-pager
                id="page_table"
                perfect
                :current-page.sync="queryParams.pageNum"
                :page-size.sync="queryParams.pageSize"
                :total="paginationTotal"
                :page-sizes="[10, 20, 100]"
                :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
                @page-change="handlePageChange"
        >
        </vxe-pager>
      </div>
    </div>
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
<style  lang="scss" scoped>
  .csbsTypes{
    font-size: 14px;
  }
  .ant-table-thead > tr > th {
    font-size: 15px;
  }
  .managerTemplate {
    // background: #eef5fd;
    //width: 20rem;
    width: 100%;
    height: 100%;
    font-family: Alibaba-PuHuiTi-Regular;

    .head {
      box-shadow: $plane_shadow;
      width: 100%;
      height: 1.25rem;
      background: #fff;
      display: flex;
      align-items: center;
      margin-bottom: 0.1rem;
    }

    #linkManger_content {
      box-shadow: $plane_shadow;
      width: 100%;
      // height: calc(100% - 1.5rem);
      background: white;
      padding: 0.25rem;
      overflow: hidden;
    }
  }
</style>
<script>
  import request from "@/utils/request";

  export default {
    data() {
      return {
        tableData: [],
        pagination: {},
        paginationTotal: 0,
        loading: false,
        // 日期范围
        dateRange: [],
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          triggerStatus: undefined,
          taskName: undefined,
          deviceType: 0,
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
      handlePageChange({ currentPage, pageSize }) {
        this.queryParams.pageNum = currentPage;
        this.queryParams.pageSize = pageSize;
        this.fetch();
      },
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
          this.tableData = data.data.pageData;
          this.paginationTotal = data.data.totalCount;
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
