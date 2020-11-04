<template>
  <div class="userMonitorTemplate">
    <div class="left">
      <a-input-search style="margin-bottom: 8px" placeholder="请输入部门名称" @change="onChange" />
      <a-tree
        :expanded-keys="expandedKeys"
        :auto-expand-parent="autoExpandParent"
        :tree-data="gData"
        @expand="onExpand"
      >
        <template slot="title" slot-scope="{ title }">
          <span v-if="title.indexOf(searchValue) > -1">
            {{ title.substr(0, title.indexOf(searchValue)) }}
            <span style="color: #f50">{{ searchValue }}</span>
            {{ title.substr(title.indexOf(searchValue) + searchValue.length) }}
          </span>
          <span v-else>{{ title }}</span>
        </template>
      </a-tree>
    </div>
    <div class="right">
      <div class="searchBox">
        <a-form-model layout="inline" :model="queryParams" class="queryForm">
          <a-form-model-item label="用户名称">
            <a-input v-model="queryParams.userName" placeholder="请输入用户名称"> </a-input>
          </a-form-model-item>
          <a-form-model-item label="手机号码">
            <a-input v-model="queryParams.userNumber" placeholder="请输入手机号码"> </a-input>
          </a-form-model-item>
          <a-form-model-item label="状态">
            <a-select default-value="用户状态" style="width: 240px" @change="handleChange">
              <a-select-option value="jack">
                正常
              </a-select-option>
              <a-select-option value="lucy">
                停用
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="创建时间">
            <a-range-picker style="width:240px" @change="onTimeChange" />
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

      <div id="linkUser_content">
        <a-row type="flex" class="rowToolbar">
          <a-col :span="1.5">
            <a-button type="primary" icon="plus" @click="handleAdd">
              新增
            </a-button>
            <a-button type="primary" icon="edit" @click="handleAdd">
              修改
            </a-button>
            <a-button type="danger" icon="delete" @click="handleDelete">
              删除
            </a-button>
            <a-button type="primary" icon="vertical-align-bottom" @click="handleAdd">
              导出
            </a-button>
          </a-col>
        </a-row>
        <div id="tableDiv">
          <vxe-table :data="userListData" align="center" highlight-hover-row ref="userListRef" border>
            <vxe-table-column type="checkbox" width="50"></vxe-table-column>
            <vxe-table-column field="userName" title="用户名称"></vxe-table-column>
            <vxe-table-column field="userName2" title="用户昵称"></vxe-table-column>
            <vxe-table-column field="departmentName" title="部门"></vxe-table-column>
            <vxe-table-column field="userPhone" title="手机号码"></vxe-table-column>
            <vxe-table-column field="userStatus" title="状态"></vxe-table-column>
            <vxe-table-column field="userCreateTime" title="创建时间"></vxe-table-column>
            <vxe-table-column field="userOperation" title="操作" width="160">
              <template v-slot="{ row }">
                <a-button type="primary" icon="edit" @click="handleEdit(row)">
                  修改
                </a-button>
                <a-button type="danger" icon="delete" @click="handleDelete(row)">
                  删除
                </a-button>
                <a-button type="danger" icon="setting" @click="handleReset(row)">
                  重置
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
    </div>
    <a-modal
      v-model="visibleModel"
      :title="dialogTitle"
      @ok="handleOk"
      okText="确定"
      cancelText="取消"
      width="80%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
    </a-modal>
  </div>
</template>

<script>
// 接口地址
import hongtuConfig from '@/utils/services';
const gData = [
  {
    key: '0-0',
    scopedSlots: {
      title: 'title',
    },
    title: '0-0',
    children: [
      {
        key: '0-0-0',
        scopedSlots: {
          title: 'title',
        },
        title: '0-0-0',
      },
      {
        key: '0-0-1',
        scopedSlots: {
          title: 'title',
        },
        title: '0-0-1',
      },
      {
        key: '0-0-2',
        scopedSlots: {
          title: 'title',
        },
        title: '0-0-2',
      },
    ],
  },
  {
    key: '0-1',
    scopedSlots: {
      title: 'title',
    },
    title: '0-1',
    children: [
      {
        key: '0-1-0',
        scopedSlots: {
          title: 'title',
        },
        title: '0-1-0',
      },
      {
        key: '0-1-1',
        scopedSlots: {
          title: 'title',
        },
        title: '0-1-1',
      },
      {
        key: '0-1-2',
        scopedSlots: {
          title: 'title',
        },
        title: '0-1-2',
      },
    ],
  },
  {
    key: '0-2',
    scopedSlots: {
      title: '0-2',
    },
    title: '0-2',
  },
];

const dataList = [];
const generateList = (data) => {
  for (let i = 0; i < data.length; i++) {
    const node = data[i];
    const key = node.key;
    dataList.push({ key, title: key });
    if (node.children) {
      generateList(node.children);
    }
  }
};
generateList(gData);

const getParentKey = (key, tree) => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};
export default {
  data() {
    return {
      expandedKeys: [],
      searchValue: '',
      autoExpandParent: true,
      gData,
      queryParams: {
        userName: '',
        userNumber: '',
        userStatus: '',
        pageNum: 1,
        pageSize: 10,
      },
      userListData: [],
      paginationTotal: 0,
    };
  },
  created() {
    this.getUserList();
  },
  methods: {
    onExpand(expandedKeys) {
      this.expandedKeys = expandedKeys;
      this.autoExpandParent = false;
    },
    onChange(e) {
      const value = e.target.value;
      const expandedKeys = dataList
        .map((item) => {
          if (item.title.indexOf(value) > -1) {
            return getParentKey(item.key, gData);
          }
          return null;
        })
        .filter((item, i, self) => item && self.indexOf(item) === i);
      Object.assign(this, {
        expandedKeys,
        searchValue: value,
        autoExpandParent: true,
      });
    },
    getUserList() {
      hongtuConfig.userCofigList(this.queryParams).then((response) => {
        console.log(response)
      })
    },
    onTimeChange() {},
    handleQuery() {},
    handleChange() {},
    resetQuery() {},
    handlePageChange() {},
    // 新增
    handleAdd() {

    },
    handleDelete() {},
    // 修改
    handleEdit(row) {},
    // 删除
    handleDelete(row) {},
    // 重置
    handleReset(row) {},
  },
};
</script>

<style lang='scss' scoped>
.userMonitorTemplate {
  display: flex;
  width: 100%;
  height: 100%;
  font-family: Alibaba-PuHuiTi-Regular;
  padding: 30px 20px;
  box-shadow: $plane_shadow;

  .left {
    width: 15%;
    height: 100%;
    padding: 0 10px;
    border-right: 1px solid #ece;
  }

  .right {
    padding-right: 20px;

    .searchBox {
      margin-left: 20px;
      height: 100px;
      width: 100%;
      background: #f2f2f2;
      padding: 15px;
      border-radius: 8px;

      .ant-input {
        width: 240px;
        padding: 0 30px 0 15px;
        margin-bottom: 30px;
      }

      .ant-calendar-range-picker-input {
        height: 32px;
      }

      .ant-btn {
        width: 60px;
      }
    }

    #linkUser_content {
      margin: 20px -15px 20px 20px;

      .ant-btn {
        width: 60px;
        margin: 0 10px 10px 0;
      }
    }
  }
}
</style>