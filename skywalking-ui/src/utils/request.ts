import * as axios from 'axios';

// 这里可根据具体使用的ui组件库进行替换
/* import { Message, MessageBox } from 'element-ui'; */
import { AxiosResponse, AxiosRequestConfig } from 'axios';

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
  maxContentLength: 4000,
  // headers: {
  //   "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8",
  // },
});
/*
let instance = axios.create({
    baseURL: 'http://10.1.100.35:12800',
    timeout: 6000,
  }); */

service.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    config.headers['Authorization'] = window.sessionStorage.getItem('token')
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
    if (response.status !== 200) {
      /*  Message({
             message: `请求错误，${String(response.status)}`,
             type: 'error',
             duration: 3 * 1000
         }); */
      return { code: 100 };
    } else {
      // console.log(response.headers)
      if(response.headers['content-disposition']){
        let fileName = decodeURI(response.headers['content-disposition'].split(';')[1].split('=')[1]);
        window.sessionStorage.setItem('fileName',fileName)
      }
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
