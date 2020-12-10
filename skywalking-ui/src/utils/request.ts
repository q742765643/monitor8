import * as axios from 'axios';

// 这里可根据具体使用的ui组件库进行替换
/* import { Message, MessageBox } from 'element-ui'; */
import { AxiosResponse, AxiosRequestConfig } from 'axios';
import { notification } from 'ant-design-vue';
import Cookies from 'js-cookie';
import { getToken } from '@/utils/auth';
const TokenKey = 'Admin-Token';
export interface AjaxResponse {
  code: number;
  message: string;
  data: any;
}

// baseURL根据实际进行定义
const baseURL = '/monitor';

// 创建axios实例
const service = axios.default.create({
  baseURL,
  timeout: 10000, // 请求超时时间
  maxContentLength: Infinity,
});
/*
let instance = axios.create({
    baseURL: '',
    timeout: 6000,
  }); */

service.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    if (getToken()) {
      config.headers['Authorization'] = getToken(); // 让每个请求携带自定义token 请根据实际情况自行修改
    } else {
      config.headers['Authorization'] = Cookies.get(TokenKey);
    }
    return config;
  },
  (err: any) => {
    /* Message({
        message: err.message,
        type: 'error',
        duration: 3 * 1000
    }); */
    Promise.reject(err);
  },
);

service.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.data instanceof ArrayBuffer) {
      return response;
    }
    const code = response.data.code;
    if (code !== 200) {
      notification.open({
        message: '错误',
        description: response.data.msg,
        onClick: () => {},
      });
      return Promise.reject(new Error(response.data.msg));
    } else {
      let res = response.data;
      return res;
    }
  },
  (err: any) => {
    /*  Message({
         message: err,
         type: 'error',
         duration: 3 * 1000
     }); */
    return { code: 100 };
  },
);

export default service;
