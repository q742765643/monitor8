import axios from 'axios';

let instance = axios.create({
  baseURL: 'http://10.1.100.96:12800',
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
