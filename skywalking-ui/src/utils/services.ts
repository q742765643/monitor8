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
