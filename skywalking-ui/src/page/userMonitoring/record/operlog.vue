<template>
  <div class="operlogMonitorTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm" ref="queryForm">
      <a-form-model-item label="系统模块">
        <a-input v-model="queryParams.title" placeholder="请输入系统模块"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="操作人员">
        <a-input v-model="queryParams.operName" placeholder="请输入操作人员"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="类型">
        <a-select v-model="queryParams.businessType" style="width: 240px">
          <a-select-option v-for="item in typeOptions" :key="item.dictValue">{{ item.dictLabel }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="状态">
        <a-select v-model="queryParams.status" style="width: 240px">
          <a-select-option v-for="item in statusOptions" :key="item.dictValue">{{ item.dictLabel }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="创建时间">
        <a-range-picker v-model="queryParams.dateRange" />
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
      </a-form-model-item>
    </a-form-model>
    <div id="linkrole_content">
      <a-row type="flex" class="rowToolbar">
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" @click="handleDelete"> 删除 </a-button>
          <a-button type="danger" icon="delete" @click="handleClean"> 清空 </a-button>
          <a-button type="primary" icon="vertical-align-bottom" @click="handleUpload"> 导出 </a-button>
        </a-col>
      </a-row>

      <div id="tableDiv">
        <vxe-table
          :data="operListData"
          align="center"
          highlight-hover-row
          ref="operListRef"
          border
        >
          <vxe-table-column type="checkbox" width="50"></vxe-table-column>
          <vxe-table-column field="title" title="系统模块"></vxe-table-column>
          <vxe-table-column field="businessType" title="操作类型">
            <template v-slot="{ row }">
              <span> {{ typeFormat(typeOptions, row.businessType) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="requestMethod" title="请求方式"></vxe-table-column>
          <vxe-table-column field="operName" title="操作人员"></vxe-table-column>
          <vxe-table-column field="operIp" title="主机"></vxe-table-column>
          <vxe-table-column field="operLocation" title="操作地点"></vxe-table-column>
          <vxe-table-column field="status" title="操作状态">
            <template v-slot="{ row }">
              <span> {{ statusFormat(statusOptions, row.status) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column field="operTime" title="操作日期">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.operTime) }}</span>
            </template>
          </vxe-table-column>
          <vxe-table-column title="操作">
            <template v-slot="{ row }">
              <a-button type="primary" icon="edit" @click="handleView(row)"> 详细 </a-button>
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
    <!-- 操作日志详细 -->
    <a-modal
      v-model="open"
      title="操作日志详细"
      @ok="handleOk"
      @cancel="handleCancel"
      okText="确定"
      cancelText="取消"
      width="50%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model v-if="open" ref="formModel" :label-col="{ span: 6 }" :wrapperCol="{ span: 17 }" :model="form">
        <a-row>
          <a-col :span="12">
            <a-form-model-item label="操作模块："> {{ form.title }} / 修改 </a-form-model-item>
            <a-form-model-item label="登录模块：">
              {{ form.operName }} / {{ form.operIp }} / {{ form.operLocation }}
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="请求地址："> {{ form.openUrl }}</a-form-model-item>
            <a-form-model-item label="请求方式：">
              {{ form.requestMethod }}
            </a-form-model-item>
          </a-col>
          <a-col :span="13">
            <a-form-model-item label="操作方法："> {{ form.method }}</a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="请求参数："> {{ form.operParam }}</a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="返回参数："> {{ form.jsonResult }}</a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="操作状态：">
              <div v-if="form.status === 0">正常</div>
              <div v-else-if="form.status === 1">失败</div>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="操作时间："> {{ parseTime(form.operTime) }}</a-form-model-item>
          </a-col>
          <a-col :span="24">
            <a-form-model-item label="异常信息：" v-if="form.status === 1"> {{ form.errorMsg }}</a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>
  </div>
</template>

<script>
import hongtuConfig from '@/utils/services';
import request from '@/utils/request';
export default {
  data() {
    return {
      queryParams: {
        operName: undefined,
        title: this.$route.params.title,
        status: undefined,
        businessType: undefined,
        params: {
          orderBy: {
            operTime: 'desc',
          },
        },
        pageNum: 1,
        pageSize: 10,
      },
      ids: [],
      multiple: true,
      operListData: [],
      paginationTotal: 0,
      typeOptions: [],
      statusOptions: [],
      dateRange: [],
      form: {},
      open: false,
    };
  },
  created() {
    this.getOperlogList();
    hongtuConfig.getDicts('sys_oper_type').then((res) => {
      if (res.code == 200) {
        this.typeOptions = res.data;
      }
    });
    hongtuConfig.getDicts('sys_common_status').then((res) => {
      if (res.code == 200) {
        this.statusOptions = res.data;
      }
    });
  },
  methods: {
    getOperlogList() {
      if (this.queryParams.dateRange) {
        this.dateRange = this.queryParams.dateRange;
      } else {
        this.dateRange = [];
      }
      hongtuConfig.getOperlog(this.addDateRange(this.queryParams, this.dateRange)).then((res) => {
        this.operListData = res.data.pageData;
        this.paginationTotal = res.data.totalCount;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getOperlogList();
    },
    resetQuery() {
      this.dateRange = [];
      // this.resetForm('queryForm')
      (this.queryParams = {
        operName: undefined,
        title: this.$route.params.title,
        status: undefined,
        businessType: undefined,
        params: {
          orderBy: {
            operTime: 'desc',
          },
        },
        pageNum: 1,
        pageSize: 10,
      }),
        this.getOperlogList();
    },
    handleSelectionChange(selection) {
      console.log(selection)
      this.ids = selection.map((item) => item.id);
      this.multiple = !selection.length;
    },
    handleDelete(row) {
      let ids = [];
      let cellsChecked = this.$refs.operListRef.getCheckboxRecords()
      cellsChecked.forEach((element) => {
        ids.push(element.id)
      })
      // this.multiple = !ids.length
      console.log(ids)
      // const ids = row.id || this.ids;
      this.$confirm({
        title: '是否确认删除日志编号为"' + ids + '"的数据项?',
        content: '',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          hongtuConfig.delOperlog(ids).then((res) => {
            if(res.code == 200) {
              this.getOperlogList();
              this.$message.success('删除成功')
            }
          });
        },
        onCancel() {},
      });
    },
    handleClean() {
      this.$confirm({
        title: '是否确认清空所有操作日志数据项?',
        content: '',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk: () => {
          hongtuConfig.cleanOperlog().then((res) => {
            if(res.code == 200) {
              this.getOperlogList();
              this.$message.success('清空成功')
            }
          });
        },
        onCancel() {},
      });
    },
    handleOk() {},
    // 字典格式化
    typeFormat(list,text) {
      return hongtuConfig.formatterselectDictLabel(list, text);
    },
    statusFormat(list,text) {
      return hongtuConfig.formatterselectDictLabel(list,text)
    },
    handleCancel() {},
    handleView(row) {
      this.open = true;
      this.form = row;
    },
    handleUpload() {
      hongtuConfig.exportOperlog(this.queryParams).then((res) => {
        this.downloadfileCommon(res)
      })
    },
    // 分页事件
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.getRoleList();
    },
  },
};
</script>

<style scoped>
.operlogMonitorTemplate {
  width: 100%;
  height: 100%;
  padding: 20px;
  font-family: Alibaba-PuHuiTi-Regular;
}
.queryForm {
  width: 100%;
  height: 90px;
  background: #f2f2f2;
  border-radius: 8px;
  padding-top: 20px;
}
.ant-form-item {
  margin-bottom: 15px;
}
.ant-input {
  width: 240px;
  margin-right: 20px;
}
#linkrole_content {
  padding: 20px 0;
}
#tableDiv {
  margin-top: 20px;
}
</style>