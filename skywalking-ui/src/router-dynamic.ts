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
import Home from './page/home.vue';
import { getToken } from '@/utils/auth';
import store from './store';

Vue.use(Router);

window.axiosCancel = [];

export const constantRoutes = [
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
                path: '/linkTopu/mointorWindow',
                name: 'mointorWindow',
                component: () => import('@/page/discoverLink/linkTopu/window/mointorwindow.vue'),
            },
            {
                path: '/dictData/:dictId',
                name: 'dictData',
                component: () => import('@/page/dictMonitoring/dictData.vue'),
            },
        ],
    },
]

const router = new Router({
    mode: 'history',
    //base: process.env.BASE_URL,
    base: '/',
    linkActiveClass: 'active',
    routes: constantRoutes
});

router.beforeEach((to, from, next) => {

    // if(getToken()) {
    //     if(to.path === '/login') {
    //         next({ path: '/' })
    //     }else {
    //         store.dispatch('GenerateRoutes').then((accessRoutes) => {
    //             router.addRoutes(accessRoutes)
    //             next()
    //         })
    //     }
    // }
    
    //
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
