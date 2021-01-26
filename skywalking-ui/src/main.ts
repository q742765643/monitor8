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
import moment from 'dayjs';
import clickout from '@/utils/clickout';
import tooltip from '@/utils/tooltip';
import zh from '@/assets/lang/zh';
import en from '@/assets/lang/en';
import VueI18n from 'vue-i18n';
import eventBus from './event-bus';
import Antd from 'ant-design-vue';
import App from './App.vue';
import router from './router';
// import router from './router-dynamic';
import store from './store';
import components from './components';
import 'echarts/lib/chart/line';
import 'echarts/lib/chart/graph';
import 'echarts/lib/chart/bar';
import 'echarts/lib/chart/scatter';
import 'echarts/lib/chart/heatmap';
import 'echarts/lib/chart/sankey';
import 'echarts/lib/component/legend';
import 'echarts/lib/component/tooltip';
import VModal from 'vue-js-modal';
import { queryOAPTimeInfo } from './utils/localtime';
import './assets';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import Pagination from '@/components/Pagination/index.vue';
import { parseTime, resetForm, addDateRange, getDicts, selectDictLabel, downloadfileCommon } from '@/components/util';
import '@/assets/iconfont/iconfont.css';
// import 'lib-flexible';
import './views/utils/flexible';
import { Button } from 'ant-design-vue';

//VXETable
import 'xe-utils';
import VXETable from 'vxe-table';
import 'vxe-table/lib/style.css';
import 'ant-design-vue/dist/antd.css';
import { FormModel } from 'ant-design-vue';

Vue.use(ElementUI);
Vue.use(FormModel);
Vue.use(VXETable);
Vue.use(Antd);
VXETable.setup({ size: 'mini' });
//全局引入 cron表达式
import VueCron from 'vue-cron';
Vue.use(VueCron); //使用方式：<vueCron></vueCron>

import '@/assets/css/reset.scss';
import '@/assets/css/style.scss';
import '@/assets/css/mediaStyle.scss';

import axios from 'axios';
Vue.prototype.$axios = axios;

// import VXETablePluginExportPDF from 'vxe-table-plugin-export-pdf';
// VXETable.use(VXETablePluginExportPDF);
// import VXETablePluginExportXLSX from 'vxe-table-plugin-export-xlsx';
// VXETable.use(VXETablePluginExportXLSX);
// VXETablePluginExportPDF.setup({
//   fontName: 'SourceHanSans-Normal',
//   fonts: [
//     {
//       fontName: 'SourceHanSans-Normal',
//       //fontUrl: './static/pdfHans/source-han-sans-normal.js',
//       fontUrl: 'https://cdn.jsdelivr.net/npm/vxe-table-plugin-export-pdf/fonts/source-han-sans-normal.js',
//     },
//   ],
// });
//拖动
/* import VueDND from 'awe-dnd' */
let VueDND: any = require('awe-dnd');
Vue.use(VueDND);
Vue.use(Button);

Vue.prototype.parseTime = parseTime;
Vue.prototype.resetForm = resetForm;
Vue.prototype.addDateRange = addDateRange;
Vue.prototype.getDicts = getDicts;
Vue.prototype.selectDictLabel = selectDictLabel;
Vue.prototype.downloadfileCommon = downloadfileCommon;
declare module 'vue/types/vue' {
  interface Vue {
    parseTime(time: any, pattern: String): any;
    resetForm(refName: any): any;
    addDateRange(params: any, dateRange: any): any;
    getDicts(dictType: String): any;
    selectDictLabel(datas: any, value: String): any;
    downloadfileCommon(datas: any): any;
  }
}
Vue.prototype.msgSuccess = function (msg: any) {
  this.$message.success(msg);
};

Vue.prototype.msgError = function (msg: any) {
  this.$message.success(msg);
};
Vue.use(eventBus);
Vue.use(VueI18n);
Vue.use(components);
Vue.use(VModal, { dialog: true });
Vue.directive('clickout', clickout);
Vue.directive('tooltip', tooltip);
Vue.component('Pagination', Pagination);

Vue.filter('dateformat', (dataStr: any, pattern: string = 'YYYY-MM-DD HH:mm:ss') => moment(dataStr).format(pattern));

const savedLanguage = window.localStorage.getItem('lang');
let language = navigator.language.split('-')[0];
if (!savedLanguage) {
  window.localStorage.setItem('lang', language);
}
language = savedLanguage ? savedLanguage : language;

const i18n = new VueI18n({
  locale: language,
  messages: {
    zh,
    en,
  },
});

if (!window.Promise) {
  window.Promise = Promise;
}

Vue.config.productionTip = false;

queryOAPTimeInfo().then(() => {
  new Vue({
    i18n,
    router,
    store,
    render: (h) => h(App),
  }).$mount('#app');
});
