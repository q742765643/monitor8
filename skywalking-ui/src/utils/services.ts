import axios from 'axios';
import instance from '@/utils/request';
import { getBaseURL } from 'xe-utils/methods';
import fileDownload from 'js-file-download'
import request from "@/utils/request";
/* let instance = axios.create({
  baseURL: 'http://10.1.100.35:12800',
  timeout: 6000,
}); */

const dataService = {
  // 首页-查询监控总览
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
  //字典方法
  getDicts(dictType: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/dict/data/dictType/' + dictType)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 字典格式化
  formatterselectDictLabel(datas: any, value: String) {
    var actions: any = [];
    Object.keys(datas).map((key) => {
      if (datas[key].dictValue == '' + value) {
        actions.push(datas[key].dictLabel);
        return false;
      }
    });
    return actions.join('');
  },
  // 时间格式化
  parseTime(time: any, pattern: String) {
    if (arguments.length === 0) {
      return null;
    }
    const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}';
    let date;
    if (typeof time === 'object') {
      date = time;
    } else {
      if (typeof time === 'string' && /^[0-9]+$/.test(time)) {
        time = parseInt(time);
      }
      if (typeof time === 'number' && time.toString().length === 10) {
        time = time * 1000;
      }
      date = new Date(time);
      // date.setHours(date.getHours() - 8);
    }
    const formatObj: any = {
      y: date.getFullYear(),
      m: date.getMonth() + 1,
      d: date.getDate(),
      h: date.getHours(),
      i: date.getMinutes(),
      s: date.getSeconds(),
      a: date.getDay(),
    };
    const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
      let value = formatObj[key];
      // Note: getDay() returns 0 on Sunday
      if (key === 'a') {
        return ['日', '一', '二', '三', '四', '五', '六'][value];
      }
      if (result.length > 0 && value < 10) {
        value = '0' + value;
      }
      return value || 0;
    });
    return time_str;
  },
  // 链路设备管理-表格
  hostConfigLIst(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/hostConfig/list', {
          params: params,
        })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 链路设备管理-新增/编辑
  hostConfigPost(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/hostConfig', params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 链路设备管理-详情
  hostConfigDetail(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/hostConfig/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 链路设备管理-删除
  hostConfigDelete(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/hostConfig/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 自动发现配置-表格
  autoDiscoveryList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/autoDiscovery/list', {
          params: params,
        })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 自动发现配置-新增/编辑
  autoDiscoveryPost(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/autoDiscovery', params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 自动发现配置-详情
  autoDiscoveryDetail(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/autoDiscovery/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 自动发现配置-删除
  autoDiscoveryDelete(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/autoDiscovery/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 软件报表-表格
  reportList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/report/list', {
          params: params,
        })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 文件监控-表格
  fileMonitorList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/fileMonitor/list', {
          params: params,
        })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 文件监控-详情
  fileMonitorDetail(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/fileMonitor/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 文件监控-删除
  fileMonitorDelete(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/fileMonitor/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 文件监控-新增/编辑
  fileMonitorPost(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/fileMonitor', params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 告警管理-表格
  alarmCofigList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/alarmCofig/list', {
          params: params,
        })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 告警管理-详情
  alarmCofigDetail(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/alarmCofig/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 告警管理-删除
  alarmCofigDelete(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/alarmCofig/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 告警管理-新增/编辑
  alarmCofigPost(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/alarmCofig', params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 用户管理-新增
  userCofigAdd(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/system/user', params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 用户管理-删除
  userCofigDelete(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/system/user/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 用户管理-根据id查询用户信息
  userCofigSearchById(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/user/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 用户管理-查找用户
  userCofigSearch(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/user/findAllBizUser' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 用户管理-修改
  userCofigEdit(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/user', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 用户管理-用户状态修改
  editUserStatus(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/user/changeStatus', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 用户管理-表格
  userCofigList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/user/list', {
          params: params,
        })
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 用户管理-重置密码
  resetUserPwd(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/user/resetPwd', params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err);
        });
    });
  },

  // 用户管理-用户信息导出
  exportUser(query: object) {
    return request({
      url: '/system/user/export',
      method: 'get',
      params: query,
      responseType: "arraybuffer"
    })
  },

  // 角色管理-分页查询角色信息
  roleConfigList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/role/list', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err);
        });
    });
  },
  // 告警管理-详情
  menuDetail(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/menu/' + params)
        .then((res) => {
          resolve(res);
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 角色管理-新增角色
  roleConfigAdd(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/system/role', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 角色管理-根据id查询角色
  roleCofigSearchById(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/role/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 角色管理-修改角色
  roleConfigEdit(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/role', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 角色管理-角色状态修改
  editRoleStatus(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/role/changeStatus', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 角色管理-删除角色
  roleConfigDelete(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/system/role/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 角色管理-导出角色信息
  roleConfigUpload(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/role/export', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 部门管理-获取部门下拉树列表
  treeselect(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/dept/treeselect', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 部门管理-获取部门列表
  deptList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/dept/list', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 部门管理-新增部门
  addDept(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/system/dept', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 部门管理-修改部门
  editDept(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/dept', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 部门管理-删除部门
  deleteDept(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/system/dept/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 部门管理-根据部门编号查询部门详细信息
  getDept(params: string) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/dept/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 在线用户-分页查询在线用户
  onlineUserList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/monitor/online/list', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 在线用户-强退用户
  forceLogout(params: string) {
    return new Promise((resolve, reject) => {
      instance
        .get('/monitor/online/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-查询菜单树
  menuTreeselect() {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/menu/treeselect')
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-根据角色ID查询对应菜单
  roleMenuTreeselect(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/menu/roleMenuTreeselect/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-查询所有菜单
  menuList(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/menu/list', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-根据菜单编号获取详细信息
  getmenuDetail(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/menu/list' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-查询菜单
  getMenu(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .get('/system/menu/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-删除菜单
  delMenu(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/system/menu/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 菜单管理-修改
  updateMenu(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .put('/system/menu', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },



  // 菜单管理-新增菜单
  addMenu(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .post('/system/menu', params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 登录日志-分页查询登录日志
  getLogin(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/monitor/logininfor/list', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 登录日志-导出日志
  exportLogininfor(query: object) {
    return request({
      url: '/monitor/logininfor/export',
      method: 'get',
      params: query,
      responseType: "arraybuffer"
    })
  },

  // 登录日志-删除登录日志
  delLogininfor(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/monitor/logininfor/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 登录日志-清空登录日志
  cleanLogininfor() {
    return new Promise((resolve, reject) => {
      instance
        .delete('/monitor/logininfor/clean')
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 操作日志-分页查询操作日志
  getOperlog(params: Object) {
    return new Promise((resolve, reject) => {
      instance
        .get('/monitor/operlog/list', {
          params: params
        })
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 操作日志-删除操作日志
  delOperlog(params: String) {
    return new Promise((resolve, reject) => {
      instance
        .delete('/monitor/operlog/' + params)
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 操作日志-清空操作日志
  cleanOperlog() {
    return new Promise((resolve, reject) => {
      instance
        .delete('/monitor/operlog/clean')
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },
  // 操作日志-日志信息导出
  exportOperlog(query: object) {
    return request({
      url: '/monitor/operlog/export',
      method: 'get',
      params: query,
      responseType: "arraybuffer"
    })
  },


  // 通用下载方法
  download(params: String) {
    const baseURL = '/monitor'
    window.location.href = baseURL + "/api/com/downloadByPath?filePath=" + params;
  },


  exportRole(query: object) {
    return request({
      url: '/system/role/export',
      method: 'get',
      params: query,
      responseType: "arraybuffer"
    })
  },

  // 获取验证码
  captchaImage() {
    return new Promise((resolve, reject) => {
      instance
        .get('/captchaImage')
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  },

  // 获取路由
  getRouters() {
    return new Promise((resolve, reject) => {
      instance
        .get('/getRouters')
        .then((res) => {
          resolve(res)
        })
        .catch((err) => {
          reject(err)
        })
    })
  }
};



export default dataService;
