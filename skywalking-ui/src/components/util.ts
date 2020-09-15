export function parseTime(time:any, pattern:String) {
  if (arguments.length === 0) {
    return null
  }
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time);
    // date.setHours(date.getHours() - 8);
  }
  const formatObj:any= {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

// 表单重置
export function resetForm(refName:any) {
  // @ts-ignore
  if (this.$refs[refName] !== undefined) {
    // @ts-ignore
    this.$refs[refName].resetFields();
  }
}

// 添加日期范围
export function addDateRange(params:any, dateRange:any) {
  var search = params;
  var orderBy = {};
  if (search.params) {
    orderBy = search.params.orderBy;
  }
  if (null != dateRange) {

    search.params = {
      // @ts-ignore
      beginTime: this.dateRange[0],
      // @ts-ignore
      endTime: this.dateRange[1],
      orderBy: orderBy
    };
  }
  return search;
}
