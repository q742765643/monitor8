/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Vue from 'vue';
import Router from 'vue-router';
// import Login from './views/containers/login.vue';
import Index from './views/containers/index.vue';
import Dashboard from './views/containers/dashboard.vue';
import Trace from './views/containers/trace.vue';
import Topology from './views/containers/topology/topology.vue';
import Alarm from './views/containers/alarm.vue';
import Profile from './views/containers/profile.vue';
import AutoDiscovery from './views/config/autoDiscovery.vue';
import networkTopology from './views/containers/topology/networkTopology.vue';

/* import Home from './views/containers/home/home.vue';
import mainer from './views/containers/home/right/index/mainer.vue';
import topu from './views/containers/home/right/topu/topu/topu.vue'; */

import Home from './page/home.vue';
import Login from './page/login/login.vue';

Vue.use(Router);

window.axiosCancel = [];

const router = new Router({
  mode: 'history',
  //base: process.env.BASE_URL,
  base: '/',
  linkActiveClass: 'active',
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/page/login/login.vue'),
    },
    {
      path: '/home',
      component: Home,
      children: [
        {
          path: '',
          redirect: '/mainMonitor',
        },
        {
          path: '/mainMonitor',
          name: 'mainMonitor',
          component: () => import('@/page/mainMonitor/index.vue'),
        },
        {
          path: '/discoverLink/linkTopu',
          name: 'linkTopu',
          component: () => import('@/page/discoverLink/linkTopu/index.vue'),
        },
        {
          path: '/discoverLink/linkManager',
          name: 'linkManager',
          component: () => import('@/page/discoverLink/linkManager/index.vue'),
        },
        {
          path: '/discoverLink/linkConfig',
          name: 'linkConfig',
          component: () => import('@/page/discoverLink/linkConfig/index.vue'),
        },
        {
          path: '/discoverLink/linkReport',
          name: 'linkReport',
          component: () => import('@/page/discoverLink/linkReport/index.vue'),
        },
        {
          path: '/masterMonitor/resourceView',
          name: 'resourceView',
          component: () => import('@/page/masterMonitor/resourceView/index.vue'),
        },
        {
          path: '/masterMonitor/masterManager',
          name: 'masterManager',
          component: () => import('@/page/masterMonitor/masterManager/index.vue'),
        },
        {
          path: '/masterMonitor/masterReport',
          name: 'masterReport',
          component: () => import('@/page/masterMonitor/masterReport/index.vue'),
        },
        {
          path: '/businessView/dataView',
          name: 'dataView',
          component: () => import('@/page/businessView/dataView/index.vue'),
        },
        {
          path: '/businessView/softwareView',
          name: 'softwareView',
          component: () => import('@/page/businessView/softwareView/index.vue'),
        },
        {
          path: '/businessView/dataReport',
          name: 'dataReport',
          component: () => import('@/page/businessView/dataReport/index.vue'),
        },
        {
          path: '/businessView/softwareReport',
          name: 'softwareReport',
          component: () => import('@/page/businessView/softwareReport/index.vue'),
        },
        // 文件监控
        {
          path: '/fileMonitoring',
          name: 'fileMonitoring',
          component: () => import('@/page/fileMonitoring/index.vue'),
        },
        // 告警管理
        {
          path: '/alarmMonitoring/alarmReport',
          name: 'alarmReport',
          component: () => import('@/page/alarmMonitoring/alarmReport/index.vue'),
        },
        {
          path: '/alarmMonitoring/alarmReportTable',
          name: 'alarmReport',
          component: () => import('@/page/alarmMonitoring/alarmReport.vue'),
        },
        {
          path: '/unAlarm',
          name: 'unAlarm',
          component: () => import('@/page/alarmMonitoring/unAlarm.vue'),
        },
        {
          path: '/alarmMonitoring',
          name: 'alarmMonitoring',
          component: () => import('@/page/alarmMonitoring/index.vue'),
        },
        {
          path: '/dictType',
          name: 'dictType',
          component: () => import('@/page/dictMonitoring/dictType.vue'),
        },
        {
          path: '/dictData/:dictId',
          name: 'dictData',
          component: () => import('@/page/dictMonitoring/dictData.vue'),
        },
        {
          path: '/dictData',
          name: 'dictData',
          component: () => import('@/page/dictMonitoring/dictData.vue'),
        },
        {
          path: '/job',
          name: 'job',
          component: () => import('@/page/dictMonitoring/job.vue'),
        },
        {
          path: '/fileMonitoring/directory_account',
          component: () => import('@/page/fileMonitoring/directory_account.vue'),
        },
        {
          path: '/fileMonitoring/file_report',
          component: () => import('@/page/fileMonitoring/file_report_row.vue'),
        },
        {
          path: '/fileMonitoring/file_log',
          component: () => import('@/page/fileMonitoring/file_log.vue'),
        },
        // 用户管理
        {
          path: '/userMonitoring',
          name: 'userMonitoring',
          component: () => import('@/page/userMonitoring/index.vue'),
        },
        // 角色管理
        {
          path: '/roleMonitoring',
          name: 'roleMonitoring',
          component: () => import('@/page/userMonitoring/role.vue'),
        },
        // 部门管理
        {
          path: '/departmentMonitoring',
          name: 'departmentMonitoring',
          component: () => import('@/page/userMonitoring/department.vue'),
        },
        // 在线用户
        {
          path: '/onlineUser',
          name: 'onlineUser',
          component: () => import('@/page/userMonitoring/onlineUser.vue'),
        },
        // 菜单管理
        {
          path: '/menuManagement',
          name: 'menuManagement',
          component: () => import('@/page/userMonitoring/menuManagement.vue'),
        },
        // 日志管理-操作日志
        {
          path: '/operlog',
          name: 'oerlog',
          component: () => import('@/page/userMonitoring/record/operlog.vue'),
        },
        // 日志管理-登录日志
        {
          path: '/loginlog',
          name: 'loginlog',
          component: () => import('@/page/userMonitoring/record/loginLog.vue'),
        },
        {
          path: '/job',
          component: () => import('@/views/containers/home/right/job/index.vue'),
        },
        {
          path: '/process/processConfig',
          component: () => import('@/page/process/processConfig.vue'),
        },
        {
          path: '/process/processReport',
          component: () => import('@/page/process/processReport.vue'),
        },
        {
          path: '/processView',
          name: 'processView',
          component: () => import('@/page/process/processView/index.vue'),
        },
      ],
    },
  ],
});

router.beforeEach((to, from, next) => {
  /*  const token = window.localStorage.getItem('skywalking-authority');
  if (window.axiosCancel.length !== 0) {
    for (const func of window.axiosCancel) {
      setTimeout(func(), 0);
    }
    window.axiosCancel = [];
  }*/

  //if (to.path === '/login') return next();
  // 获取token
  //const tokenstr = window.sessionStorage.getItem('token');
  //if (!tokenstr) return next('/login');
  // if (to.meta.login && (token === null || token === 'guest')) {
  //   next();
  // } else if (token === null || token === 'guest') {
  //   next('/login');
  // } else if (to.meta.login) {
  //   next(from.path);
  // } else {
  //   next();
  // }
  next();
});

export default router;
