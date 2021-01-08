/**
 *
 * 8-20位（数字，字母）或（数字，字母，特殊字符）的组合密码
 */
export function password1(str) {
  const reg = /^[^\u4e00-\u9fa5]*$/
  return reg.test(str)
}
export function password2(str) {
  const reg = /^[0-9]+$/
  return reg.test(str)
}
/**
 *
 * 必须是英文
 */
export function english(str) {
  const reg = /^[a-zA-Z]+$/
  return reg.test(str)
}
/* 英文数字下划线 */
export function EngNumLine(str) {
  const reg = /^[A-Za-z0-9-_]+$/
  return reg.test(str)
}

/* 表名首位允许是字母以及下划线；首位之后可以是字母，数字以及下划线；下划线后不能接下划线 */
export function tableNameVail(str) {
  const reg = /(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)/
  return reg.test(str)
}

/**
 *
 * 10以内的中文
 */
export function chineseLengthTenValidation(str) {
  const reg = /^[\u4E00-\u9FA5]{1,10}$/
  return reg.test(str)
}
/**
 *
 * 手机号码
 */
export function telphoneNumValidation(str) {
  const reg = /^1[34578]\d{9}$/
  return reg.test(str)
}
/**
 *
 * 端口号
 */
export function portNumValidation(str) {
  const reg = /^([0-9]|[1-9]\d|[1-9]\d{2}|[1-9]\d{3}|[1-5]\d{4}|6[0-4]\d{3}|65[0-4]\d{2}|655[0-2]\d|6553[0-5])$/
  return reg.test(str)
}
/**
 *
 * ip地址
 */
export function ipUrlValidation(str) {
  const reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
  return reg.test(str)
}
/**
 *
 * ip地址 IP段样例:192.168.1.%
 */
export function ipUrlValidation2(str) {
  const reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.%$/
  return reg.test(str)
}
/**
 *
 * 数据库账号不能以数字开头
 */
export function unstartnumValidation(str) {
  const reg = /^[0-9][a-zA-Z0-9_]/
  return reg.test(str)
}
/**
 *
 * 密码 必须以英文数字组成
 */
export function englishAndNumValidation(str) {
  const reg = /[0-9]+[a-zA-Z]+[0-9a-zA-Z]*|[a-zA-Z]+[0-9]+[0-9a-zA-Z]*$/
  return reg.test(str)
}
/**
 *
 * 不允许输入小写字母和中文，且需以大写字母开头
 */
export function codeVer(str) {
  const reg = /^[_A-Z][^a-z\u4e00-\u9fa5]*$/
  return reg.test(str)
}
/**
 *
 * 验证由中文组成：
 */
export function chinese(str) {
  const reg = /^[()\u4e00-\u9fa5]+$/
  return reg.test(str)
}
/**
 *
 * 验证由大于0正整数组成：
 */
export function num(info) {
  const reg = /^[0-9]+(\.\d+)?$/;;
  return reg.test(info);
}
/**
 * @param {string} str
 * @returns {Boolean}
 */
export function isString(str) {
  if (typeof str === 'string' || str instanceof String) {
    return true
  }
  return false
}

/**
 * @param {Array} arg
 * @returns {Boolean}
 */
export function isArray(arg) {
  if (typeof Array.isArray === 'undefined') {
    return Object.prototype.toString.call(arg) === '[object Array]'
  }
  return Array.isArray(arg)
}

export function newTeam(data, parentId) {
  let itemArr = [];
  let temp = [];
  for (let i = 0; i < data.length; i++) {
    let node = data[i];
    if (node.pid === parentId) {
      let newNode = {};
      newNode.id = node.id;
      newNode.name = node.name;
      newNode.pid = node.pid;
      newNode.nodeKey = node.nodeKey;
      temp = newTeam(data, node.id);
      if (temp.length > 0 && node.isParent) {
        newNode.children = temp
      }
      itemArr.push(newNode);
    }
  }
  return itemArr;
}


