<template>
  <div class="fileLogTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm" ref="queryForm">
      <a-form-model-item label="任务名称" prop="name">
        <a-input v-model="queryParams.taskName" placeholder="请输入任务名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="ip" prop="ip">
        <a-input v-model="queryParams.ip" placeholder="请输入ip"> </a-input>
      </a-form-model-item>
      <a-form-model-item>
        <a-col :span="24">
          <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
        </a-col>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <vxe-table border ref="xTable" :data="tableData" stripe align="center" @checkbox-change="rowSelection">
        <vxe-table-column type="checkbox" width="80"></vxe-table-column>
        <vxe-table-column field="taskName" title="名称"></vxe-table-column>
        <vxe-table-column field="fileNum" title="应到" > </vxe-table-column>
        <vxe-table-column field="realFileNum" title="实到" > </vxe-table-column>
        <vxe-table-column field="lateNum" title="晚到" > </vxe-table-column>
        <vxe-table-column field="fileSize" title="应到大小(K)" > </vxe-table-column>
        <vxe-table-column field="realFileSize" title="实到大小(K)" > </vxe-table-column>
<!--        <vxe-table-column field="folderRegular" title="文件目录" > </vxe-table-column>-->
        <vxe-table-column field="elapsedTime" title="执行耗时"></vxe-table-column>
        <vxe-table-column field="isCompensation" title="是否补偿">
          <template v-slot="{ row }">
            <span v-if="row.isCompensation == 0">否 </span>
            <span v-if="row.isCompensation == 1">是 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="handleCode" title="执行状态">
          <template v-slot="{ row }">
            <span> {{ statusFormat(row.handleCode) }}</span>
          </template>
        </vxe-table-column>

        <vxe-table-column field="createTime" title="创建时间" show-overflow>
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column width="200" field="date" title="操作">
          <template v-slot="{ row }">
            <a-button type="primary" icon="edit" @click="handleUpdate(row)"> 查看 </a-button>
            <a-button type="danger" icon="delete" @click="handleDelete(row)"> 删除 </a-button>
          </template>
        </vxe-table-column>
      </vxe-table>

      <vxe-pager
        id="page_table"
        perfect
        :current-page.sync="queryParams.pageNum"
        :page-size.sync="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 100]"
        @page-change="fetch"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      ></vxe-pager>
    </div>
    <a-modal v-model="visible" class="fileAmodal" :title="title" okText="确定" cancelText="取消" width="70%">
      <a-form-model :label-col="{ span: 6 }" :wrapperCol="{ span: 18 }" :model="form" ref="form">
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="任务名称" prop="taskName">
              <a-input disabled v-model="form.taskName" style="width: 100%" placeholder="请输入任务名称"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="corn表达式" prop="jobCron">
              <a-input disabled v-model="form.jobCron" style="width: 100%" placeholder="corn表达式"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="资料文件目录规则" prop="folderRegular">
              <a-input disabled v-model="form.folderRegular" style="width: 100%" placeholder="资料文件目录规则">
              </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="资料文件名规则 " prop="filenameRegular">
              <a-input disabled v-model="form.filenameRegular" style="width: 100%" placeholder="资料文件名规则 ">
              </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="应到数量" prop="fileNum">
              <a-input disabled v-model="form.fileNum" style="width: 100%" placeholder="请输入应到数量"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="应到大小" prop="fileSize">
              <a-input disabled v-model="form.fileSize" style="width: 100%" placeholder="请输入应到大小"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="远程目录" prop="remotePath">
              <a-input disabled v-model="form.remotePath" style="width: 100%"> </a-input>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="时区" prop="isUt">
              <a-select disabled v-model="form.isUt">
                <a-select-option :value="0"> 北京时 </a-select-option>
                <a-select-option :value="1"> 世界时 </a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>

          <a-col :span="24">
            <a-form-model-item :label-col="{ span: 3 }" :wrapperCol="{ span: 21 }" label="执行过程" prop="handleMsg">
              <a-input disabled type="textarea" v-model="form.handleMsg" style="width: 100%"> </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import request from '@/utils/request';
export default {
  data() {
    return {
      tableData: [],
      total: 0,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        ip: undefined,
      },
      form: {},
      title: '',
      ids: [],
      names: [],
      visible: false,
      handleCodeOptions: [],
    };
  },
  created() {
    this.getDicts('job_running_state').then((response) => {
      if (response.code == 200) {
        this.handleCodeOptions = response.data;
      }
    });
  },
  mounted() {
    this.fetch();
  },
  computed: {},
  methods: {
    statusFormat(status) {
      return this.selectDictLabel(this.handleCodeOptions, status);
    },
    rowSelection({ selection }) {
      this.ids = selection.map((item) => item.id);
      this.names = selection.map((item) => item.name);
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
      request({
        url: '/fileMonitorLog/list',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        this.tableData = data.data.pageData;
        this.total = data.data.totalCount;
      });
    },
    handleUpdate(row) {
      this.form = {};
      const id = row.id || this.ids;
      request({
        url: '/fileMonitorLog/' + id,
        method: 'get',
      }).then((response) => {
        this.form = response.data;
        this.visible = true;
        this.title = '查看日志详情';
      });
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm({
        title: '是否确认删除名称为"' + row.taskName + '"的数据项?',
        content: '',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: () => {
          request({
            url: '/fileMonitorLog/' + ids,
            method: 'delete',
          }).then((response) => {
            if (response.code === 200) {
              this.fetch();
              this.msgError('删除成功!');
            } else {
              this.msgError(response.msg);
            }
          });
        },
        onCancel() {},
      });
    },
  },
};
</script>
<style lang="scss" scoped>
.fileLogTemplate {
  .fileAmodal {
    .ant-input {
      width: 350px;
    }
  }
}
</style>
