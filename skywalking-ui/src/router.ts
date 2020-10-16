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

import Home from './views/containers/home/home.vue';
import mainer from './views/containers/home/right/index/mainer.vue';
import topu from './views/containers/home/right/topu/topu/topu.vue';

Vue.use(Router);

window.axiosCancel = [];

const router = new Router({
  mode: 'history',
  //base: process.env.BASE_URL,
  base: '/',
  linkActiveClass: 'active',
  routes: [
    // {
    //   path: '/login',
    //   component: Login,
    //   meta: { login: true },
    // },
    /*  {
      path: '/',
      component: Index,

      children: [
        {
          path: '',
          component: Dashboard,
        },
        {
          path: 'trace',
          component: Trace,
        },
        {
          path: 'topology',
          component: Topology,
        },
        {
          path: 'alarm',
          component: Alarm,
        },
        {
          path: 'profile',
          component: Profile,
        },
        {
          path: 'autoDiscovery',
          component: AutoDiscovery,
        },
        {
          path: 'networkTopology',
          component: networkTopology,
        },
      ],
    },  */

    {
      path: '/',
      component: Home,
      children: [
        {
          path: '',
          component: mainer,
        },
        {
          path: 'mainer',
          component: mainer,
        },
        {
          path: 'topu',
          component: topu,
        },
        {
          path: 'topuConfig',
          component: () => import('./views/containers/home/right/topu/config/config.vue'),
        },
        {
          path: 'manager',
          component: () => import('./views/containers/home/right/topu/manager/index.vue'),
        },
        {
          path: 'report',
          component: () => import('./views/containers/home/right/topu/report/index.vue'),
        },
        {
          path: 'resource',
          component: () => import('./views/containers/home/right/host/resource/index.vue'),
        },
        {
          path: 'detail',
          component: () => import('./views/containers/home/right/host/details/index.vue'),
        },
        {
          path: 'masterReport',
          component: () => import('./views/containers/home/right/host/report/index.vue'),
        },
        {
          path: 'dataView',
          component: () => import('./views/containers/home/right/busines/dataView/index.vue'),
        },
        {
          path: 'dictType',
          component: () => import('./views/containers/home/right/dict/index.vue'),
        },
        {
          path: 'dictData/:dictId',
          component: () => import('./views/containers/home/right/dict/data.vue'),
        },
        {
          path: 'dictData',
          component: () => import('./views/containers/home/right/dict/data.vue'),
        },
        {
          path: 'job',
          component: () => import('./views/containers/home/right/job/index.vue'),
        },
      ],
    },
  ],
});

router.beforeEach((to, from, next) => {
  const token = window.localStorage.getItem('skywalking-authority');
  if (window.axiosCancel.length !== 0) {
    for (const func of window.axiosCancel) {
      setTimeout(func(), 0);
    }
    window.axiosCancel = [];
  }
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
