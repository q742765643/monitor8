<template>
  <div class="dictTypeTemplate">
    <a-form-model layout="inline" :model="queryParams" class="queryForm" ref="queryForm">
      <a-form-model-item label="字典名称" prop="dictName">
        <a-input v-model="queryParams.dictName" placeholder="请输入字典名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="字典类型" prop="dictType">
        <a-input v-model="queryParams.dictType" placeholder="请输入字典类型"> </a-input>
      </a-form-model-item>
      <a-form-model-item label="字典状态" prop="status">
        <a-select style="width: 120px" v-model="queryParams.status" placeholder="字典状态">
          <a-select-option v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue">
            {{ dict.dictLabel }}
          </a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item>
        <a-col :span="24">
          <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
          <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
        </a-col>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" @click="handleDelete"> 删除 </a-button>
        </a-col>
      </a-row>
      <!-- <a-table
      :row-selection="rowSelection"
      :row-key="(record) => record.id"
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      :loading="loading"
      @change="handleTableChange"
    >
      <span slot="dictType" slot-scope="text">
        <router-link :to="'/dictData/' + text" class="link-type">
          <span>{{ text }}</span>
        </router-link>
      </span>
      <span slot="status" slot-scope="text">
        {{ statusFormat(text) }}
      </span>
      <span slot="createTime" slot-scope="text">
        {{ parseTime(text) }}
      </span>
      <span slot="operate" slot-scope="text, record">
        <a-button icon="edit" size="small" @click="handleUpdate(record)"> 编辑 </a-button>
        <a-button icon="delete" size="small" @click="handleDelete(record)"> 删除 </a-button>
      </span>
    </a-table> -->
      <vxe-table border ref="xTable" :data="data" highlight-hover-row align="center">
        <vxe-table-column type="checkbox" width="80"></vxe-table-column>
        <vxe-table-column field="dictName" title="字典名称"></vxe-table-column>
        <vxe-table-column field="dictType" title="字典类型" show-overflow>
          <template v-slot="{ row }">
            <router-link :to="'/dictData/' + row.dictType" class="link-type">
              <span>{{ row.dictType }}</span>
            </router-link>
          </template>
        </vxe-table-column>
        <vxe-table-column field="status" title="状态">
          <template v-slot="{ row }">
            <span v-if="row.status == 0">正常 </span>
            <span v-if="row.status == 1">停用 </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="remark" title="备注"></vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="operate" title="操作">
          <template v-slot="{ row }">
            <a-button icon="edit" type="primary" @click="handleUpdate(row)"> 编辑 </a-button>
            <a-button icon="delete" type="danger" @click="handleDelete(row)"> 删除 </a-button>
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
    <a-modal v-model="visible" :title="title" okText="确定" cancelText="取消" width="50%" @ok="submitForm">
      <a-form-model :label-col="{ span: 5 }" :wrapper-col="{ span: 12 }" :model="form" ref="form">
        <a-form-model-item label="字典名称" prop="dictName">
          <a-input v-model="form.dictName" placeholder="请输入字典名称"> </a-input>
        </a-form-model-item>
        <a-form-model-item label="字典类型" prop="dictType">
          <a-input v-model="form.dictType" placeholder="请输入字典类型"> </a-input>
        </a-form-model-item>
        <a-form-model-item label="状态" prop="status">
          <a-radio-group v-model="form.status">
            <a-radio v-for="dict in statusOptions" :key="dict.dictValue" :value="dict.dictValue">{{
              dict.dictLabel
            }}</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="备注" prop="remark">
          <a-input v-model="form.remark" type="textarea" placeholder="请输入内容"> </a-input>
        </a-form-model-item>
      </a-form-model>
    </a-modal>
  </div>
</template>
<script>
import request from '@/utils/request';
// const columns = [
//   {
//     title: '字典名称',
//     dataIndex: 'dictName',
//     width: '20%',
//   },
//   {
//     title: '字典类型',
//     dataIndex: 'dictType',
//     width: '20%',
//     scopedSlots: { customRender: 'dictType' },
//   },
//   {
//     title: '状态',
//     dataIndex: 'status',
//     scopedSlots: { customRender: 'status' },
//   },
//   {
//     title: '备注',
//     dataIndex: 'remark',
//   },
//   {
//     title: '创建时间',
//     dataIndex: 'createTime',
//     scopedSlots: { customRender: 'createTime' },
//   },
//   {
//     title: '操作',
//     dataIndex: 'operate',
//     scopedSlots: { customRender: 'operate' },
//   },
// ];

export default {
  data() {
    return {
      data: [],
      pagination: {},
      loading: false,
      // columns,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dictName: undefined,
        dictType: undefined,
        status: undefined,
      },
      visible: false,
      form: {},
      statusOptions: [],
      title: '',
      ids: [],
      dictNames: [],
      paginationTotal: 0,
    };
  },
  created() {
    this.getDicts('sys_normal_disable').then((response) => {
      if(response.code == 200) {
        this.statusOptions = response.data;
      }
    });
  },
  mounted() {
    this.fetch();
  },
  computed: {
    rowSelection() {
      return {
        onChange: (selectedRowKeys, selectedRows) => {
          this.ids = selectedRows.map((item) => item.id);
          this.dictNames = selectedRows.map((item) => item.dictName);
        },
        getCheckboxProps: (record) => ({
          props: {
            disabled: record.id === 'Disabled User', // Column configuration not to be checked
            name: record.id,
          },
        }),
      };
    },
  },
  methods: {
    statusFormat(status) {
      return this.selectDictLabel(this.statusOptions, status);
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
        url: '/system/dict/type/list',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange),
      }).then((data) => {
        // const pagination = { ...this.pagination };
        // pagination.total = data.data.totalCount;
        this.loading = false;
        this.data = data.data.pageData;
        // this.pagination = pagination;
        this.paginationTotal = data.data.totalCount;
      });
    },
    handleAdd() {
      this.form.id = undefined;
      this.title = '新增字典类型';
      this.visible = true;
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParams.pageNum = currentPage;
      this.queryParams.pageSize = pageSize;
      this.fetch();
    },
    // handleTableChange(pagination, filters, sorter) {
    //   const pager = { ...this.pagination };
    //   this.queryParams.pageNum = pagination.current;
    //   this.pagination = pager;
    //   this.fetch();
    // },
    handleUpdate(row) {
      this.form = {};
      const id = row.id || this.ids;
      request({
        url: '/system/dict/type/' + id,
        method: 'get',
      }).then((response) => {
        this.form = response.data;
        this.visible = true;
        this.title = '修改字典类型';
      });
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      console.log(ids);
      const dictNames = row.dictName || this.dictNames;
      this.$confirm({
        title: '是否确认删除字典标签为"' + dictNames + '"的数据项?',
        content: '',
        okText: '是',
        okType: 'danger',
        cancelText: '否',
        onOk: () => {
          request({
            url: '/system/dict/type/' + ids,
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
    submitForm() {
      if (this.form.id != undefined) {
        request({
          url: '/system/dict/type',
          method: 'put',
          data: this.form,
        }).then((response) => {
          if (response.code === 200) {
            this.msgSuccess('修改成功');
            this.visible = false;
            this.fetch();
          } else {
            this.msgError(response.msg);
          }
        });
      } else {
        console.log(this.form);
        request({
          url: '/system/dict/type',
          method: 'post',
          data: this.form,
        }).then((response) => {
          if (response.code === 200) {
            this.msgSuccess('新增成功');
            this.visible = false;
            this.fetch();
          } else {
            this.msgError(response.msg);
          }
        });
      }
    },
  },
};
</script>
