<<<<<<< HEAD
import request from '@/utils/request';

const dataService = {
  getMonitorViewVo() {
    return new Promise((resolve, reject) => {
      request({
        url: '/main/getMonitorViewVo',
        method: 'get',
      }).then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  getAlarmDistribution() {
    return new Promise((resolve, reject) => {
      request({
        url: '/main/getAlarmDistribution',
        method: 'get',
      }).then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  getAlarm() {
    return new Promise((resolve, reject) => {
      request({
        url: '/main/getAlarm',
        method: 'get',
      })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  getDeviceStatus() {
    return new Promise((resolve, reject) => {
      request({
        url: '/main/getDeviceStatus',
        method: 'get',
      }).then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
};

export default dataService;
=======
import axios from 'axios';

let instance = axios.create({
  baseURL: 'http://10.1.100.35:12800',
  timeout: 6000,
});

const dataService = {
  getMonitorViewVo() {
    return new Promise((resolve, reject) => {
      instance
        .get('/main/getMonitorViewVo')
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  getAlarmDistribution() {
    return new Promise((resolve, reject) => {
      instance
        .get('/main/getAlarmDistribution')
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  getAlarm() {
    return new Promise((resolve, reject) => {
      instance
        .get('/main/getAlarm')
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  getDeviceStatus() {
    return new Promise((resolve, reject) => {
      instance
        .get('/main/getDeviceStatus')
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
};

export default dataService;
>>>>>>> 0606a8a8326bc1e09138c0baeb84ace7da4011a6
