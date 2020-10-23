<template>
  <div class="linkConfigTemplate">
    <div class="head">
      <a-form-model layout="inline" :model="queryParams" class="queryForm">
        <a-form-model-item label="网段名称">
          <a-input v-model="queryParams.taskName" placeholder="请输入网段名称"> </a-input>
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
          <vxe-table-column field="taskName" title="网段名称"></vxe-table-column>
          <vxe-table-column field="jobCron" title="发现策略" show-overflow></vxe-table-column>
          <vxe-table-column field="ipRange" title="ip范围" show-overflow></vxe-table-column>
          <vxe-table-column field="triggerStatus" title="状态" show-overflow>
            <template v-slot="{ row }">
              <span> {{ statusFormat(triggerStatusOptions, row.triggerStatus) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="jobDesc" title="描述" show-overflow></vxe-table-column>
          <vxe-table-column field="createTime" title="创建时间" show-overflow>
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

    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      okText="确定"
      cancelText="取消"
      width="50%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model
        v-if="visibleModel"
        :label-col="{ span: 6 }"
        :wrapperCol="{ span: 17 }"
        :model="formDialog"
        ref="formModel"
        :rules="rules"
      >
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="网段名称" prop="taskName">
              <a-input v-model="formDialog.taskName" placeholder="请输入网段名称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="ip范围" prop="ipRange">
              <a-input v-model="formDialog.ipRange" placeholder="请输入ip范围,格式为10.1.100.20-55"> </a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="12">
            <a-form-model-item label="发现策略" prop="jobCron">
              <a-input v-model="formDialog.jobCron" placeholder="请输入发现策略"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="状态" prop="triggerStatus">
              <a-radio-group v-model="formDialog.triggerStatus">
                <a-radio v-for="(dict, index) in triggerStatusOptions" :key="index" :value="dict.dictValue">{{
                  dict.dictLabel
                }}</a-radio>
              </a-radio-group>
            </a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 20 }" label="备注" prop="jobDesc">
              <a-input type="textarea" v-model="formDialog.jobDesc" placeholder="请输入备注内容"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
  import echarts from 'echarts';
  // 接口地址
  import services from '@/utils/services';

  export default {
    data() {
      return {
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          taskName: '',
          triggerStatus: '',
        },
        triggerStatusOptions: [], //运行状态
        tableData: [], //表格
        paginationTotal: 0,
        visibleModel: false, //弹出框
        dialogTitle: '',
        formDialog: {
          taskName: '',
          ipRange: '',
          jobCron: '',
          triggerStatus: '',
          jobDesc: '',
        },
        rules: { taskName: [{ required: true, message: '请输入设备别名', trigger: 'blur' }] }, //规则
      };
    },
    created() {
      services.getDicts('job_trigger_status').then((res) => {
        if (res.status == 200 && res.data.code == 200) {
          this.triggerStatusOptions = res.data.data;
        }
      });
      this.queryTable();
    },
    mounted() {
      this.$nextTick(() => {
        this.setTableHeight();
      });
      window.addEventListener('resize', () => {
        this.setTableHeight();
      });
    },
    methods: {
      /* 查询 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.queryTable();
      },
      /* 重置 */
      resetQuery() {
        this.queryParams = {
          pageNum: 1,
          pageSize: 10,
          ip: '',
          triggerStatus: '',
        };
        this.queryTable();
      },
      /* 翻页 */
      handlePageChange({ currentPage, pageSize }) {
        this.queryParams.pageNum = currentPage;
        this.queryParams.pageSize = pageSize;
        this.queryTable();
      },
      /* table方法 */
      queryTable() {
        services.autoDiscoveryList(this.queryParams).then((response) => {
          this.tableData = response.data.data.pageData;
          this.paginationTotal = response.data.data.totalCount;
        });
      },
      /* 字典格式化 */
      statusFormat(list, text) {
        return services.formatterselectDictLabel(list, text);
      },
      handleAdd() {
        /* 新增 */
        this.dialogTitle = '新增';
        this.formDialog = {
          taskName: '',
          ipRange: '',
          jobCron: '',
          triggerStatus: '',
          jobDesc: '',
        };
        this.visibleModel = true;
      },
      /* 编辑 */
      handleEdit(row) {
        services.autoDiscoveryDetail(row.id).then((response) => {
          if (response.data.code == 200) {
            response.data.data.triggerStatus = response.data.data.triggerStatus + '';
            this.formDialog = response.data.data;
            this.visibleModel = true;
            this.dialogTitle = '编辑';
          }
        });
      },
      /* 确认 */
      handleOk() {
        this.$refs.formModel.validate((valid) => {
          if (valid) {
            services.autoDiscoveryPost(this.formDialog).then((response) => {
              if (response.data.code == 200) {
                this.$message.success(this.dialogTitle + '成功');
                this.visibleModel = false;
                this.queryTable();
              }
            });
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      /* 删除 */
      handleDelete(row) {
        let ids = [];
        let taskNames = [];
        if (!row.id) {
          let cellsChecked = this.$refs.tablevxe.getCheckboxRecords();
          cellsChecked.forEach((element) => {
            ids.push(element.id);
            taskNames.push(element.taskName);
          });
        } else {
          ids.push(row.id);
          taskNames.push(row.taskName);
        }
        this.$confirm({
          title: '是否确认删除ip为"' + taskNames.join(',') + '"的数据项?',
          content: '',
          okText: '是',
          okType: 'danger',
          cancelText: '否',
          onOk: () => {
            services.autoDiscoveryDelete(ids.join(',')).then((response) => {
              if (response.data.code == 200) {
                this.$message.success('删除成功');
                this.resetQuery();
              }
            });
          },
          onCancel() {},
        });
      },
      setTableHeight() {
        let h = document.getElementById('tablediv').offsetHeight;
        let padding = getComputedStyle(document.getElementById('linkManger_content'), false)['paddingTop'];
        let h_page = document.getElementById('page_table').offsetHeight;

        // let chartHeight = document.getElementById("chartdiv").clientHeight;
        this.tableheight = h + parseInt(padding) * 2 - h_page - 1;
      },
    },
  };
</script>

<style lang="scss" scoped>
  .linkConfigTemplate {
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
