<!-- Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. -->
<template>
  <div class="app-container">
    <el-row>
    </el-row>
    <el-table v-loading="loading" :data="autoDiscoveryList" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="50"></el-table-column>
      <el-table-column label="名称" prop="disName" />
      <el-table-column label="ip范围" prop="ipRange" />
      <el-table-column label="更新间隔" prop="updateInterval" />
      <el-table-column label="状态">
        <template slot-scope="scope">
          <el-switch
                  v-model.trim="scope.row.status"
                  active-value="0"
                  inactive-value="1"
                  @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" sortable="custom">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" class-name="small-padding fixed-width">
      </el-table-column>
    </el-table>
    <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getList"
    />
  </div>
</template>

<script lang="ts">
  import request from '@/utils/request';
  import Vue from 'vue';
  import { Component, Prop } from 'vue-property-decorator';
  @Component
  export default class AutoDiscovery extends Vue {

    loading = true


    // 选中数组
    ids = []
    // 非单个禁用
    single = true
    // 非多个禁用
    multiple = true
    // 总条数
    total = 0
    // 自动发现表格数据
    autoDiscoveryList =[]
    // 弹出层标题
    title = ""

    // 是否显示弹出层
    open = false

    websocket:any=null;
    // 日期范围
    dateRange = []

    form = {}
    defaultProps = {
      children: "children",
      label: "label"
    }
    // 查询参数
    queryParams ={
      pageNum: 1,
      pageSize: 10,
      userName: undefined,
      phonenumber: undefined,
      status: undefined,
      deptId: undefined,
      params: {
        orderBy: {
          createTime: "desc"
        }
      }
    }
    created() : void {
      var domain=window.location.host;
      if ('WebSocket' in window) {
        this.websocket = new WebSocket('ws://'+domain+'/ws/webSocket/aaa' )
        this.initWebSocket()
      }
      this.getList()
    }
    getList() {
      this.loading = true;
      request({
        url: '/autoDiscovery/list',
        method: 'get',
        params: this.addDateRange(this.queryParams, this.dateRange)
      }).then(
              response => {
                this.autoDiscoveryList = response.data.pageData;
                this.total = response.data.totalCount;
                this.loading = false;
              }
      );
    }

    initWebSocket () {
      // 连接错误
      this.websocket.onerror = this.setErrorMessage

      // 连接成功
      this.websocket.onopen = this.setOnopenMessage

      // 收到消息的回调
      this.websocket.onmessage = this.setOnmessageMessage

      // 连接关闭的回调
      this.websocket.onclose = this.setOncloseMessage

      // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
      window.onbeforeunload = this.onbeforeunload
    }
    setErrorMessage () {
      console.log('WebSocket连接发生错误   状态码：' + this.websocket.readyState)
    }
    setOnopenMessage () {
      console.log('WebSocket连接成功    状态码：' + this.websocket.readyState)
    }
    setOnmessageMessage (event:any) {
      // 根据服务器推送的消息做自己的业务处理
      console.log('服务端返回：' + event.data)
    }
    setOncloseMessage () {
      console.log('WebSocket连接关闭    状态码：' + this.websocket.readyState)
    }
    onbeforeunload () {
      this.closeWebSocket()
    }
    closeWebSocket () {
      this.websocket.close()
    }
  }

</script>

