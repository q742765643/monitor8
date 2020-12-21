<template>
  <div class="roleTemplate">
    <a-form-model layout="inline" :model="queryParams" ref="queryForm" class="queryForm">
      <a-form-model-item label="任务名称">
        <a-input v-model="queryParams.roleName" placeholder="请输入任务名称"> </a-input>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" html-type="submit" @click="handleQuery"> 搜索 </a-button>
        <a-button :style="{ marginLeft: '8px' }" @click="resetQuery"> 重置 </a-button>
      </a-form-model-item>
    </a-form-model>
    <div class="tableDateBox">
      <!-- <a-row type="flex" class="rowToolbar" :gutter="10">
        <a-col :span="1.5">
          <a-button type="primary" icon="plus" @click="handleAdd"> 新增 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="primary" icon="edit" :disabled="single" @click="handleUpdate"> 修改 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="danger" icon="delete" :disabled="single" @click="handleDelete"> 删除 </a-button>
        </a-col>
        <a-col :span="1.5">
          <a-button type="primary" icon="vertical-align-bottom" @click="handleUpload"> 导出 </a-button>
        </a-col>
      </a-row> -->

      <vxe-table
        :data="roleListData"
        @checkbox-change="selectBox"
        align="center"
        highlight-hover-row
        ref="roleListRef"
        border
      >
        <vxe-table-column type="checkbox" width="50"></vxe-table-column>
        <vxe-table-column field="roleName" title="角色名称"></vxe-table-column>
        <vxe-table-column field="roleKey" title="权限字符"></vxe-table-column>
        <vxe-table-column field="status" title="状态">
        </vxe-table-column>
        <vxe-table-column field="createTime" title="创建时间">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </vxe-table-column>
      </vxe-table>
      <!-- <vxe-pager
        id="page_table"
        perfect
        :current-page.sync="queryParams.pageNum"
        :page-size.sync="queryParams.pageSize"
        :total="paginationTotal"
        :page-sizes="[10, 20, 100]"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      >
      </vxe-pager> -->
    </div>
  </div>
</template>

<script>
  import hongtuConfig from '@/utils/services';
  import request from '@/utils/request';
  export default {
    data() {
      return {
        queryParams: {
          taskName: '',
          pageNum: 1,
          pageSize: 10,
        },
        roleListData: [],
        paginationTotal: 0,
      };
    },
    created() {
      this.getRoleList();
      hongtuConfig.getDicts('sys_normal_disable').then((res) => {
        if (res.code == 200) {
          this.statusOptions = res.data;
        }
      });
    },
    methods: {
     
      getRoleList() {
        if (this.queryParams.dateRange) {
          this.dateRange = this.queryParams.dateRange;
        } else {
          this.dateRange = [];
        }
        hongtuConfig.fileDetail(this.queryParams).then((res) => {
          console.log(res);
        //   this.roleListData = res.data.pageData;
        //   this.paginationTotal = res.data.totalCount;
        });
      },
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getRoleList();
      },
      resetQuery() {
        this.queryParams = {
          pageNum: 1,
          pageSize: 10,
          roleName: undefined,
          roleKey: undefined,
          status: undefined,
        };
        this.getRoleList();
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

<style scoped></style>
