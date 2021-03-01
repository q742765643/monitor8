import { parseTime } from '@/components/util';
var notifications = [];
// =====================================心跳包重连CODE START=======================================
window.lockReconnect = false; // 避免ws重复连接
let ws = null; // 判断当前浏览器是否支持WebSocket
let wsUrl = '';
// createWebSocket(wsUrl);   // 连接ws

function createWebSocket(url, setSocketData) {
  // console.log(`url=====${url}`);
  wsUrl = url;
  try {
    if ('WebSocket' in window) {
      ws = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
      const { MozWebSocket } = window;
      ws = new MozWebSocket(url);
    } else {
      console.error(
        '您的浏览器不支持websocket协议,建议使用新版谷歌、火狐等浏览器，请勿使用IE10以下浏览器，360浏览器请使用极速模式，不要使用兼容模式！',
      );
    }
    initEventHandle(setSocketData);
  } catch (e) {
    console.error(e);
    reconnect(url, setSocketData);
  }
}

// 心跳检测
const heartCheck = {
  timeout: 5000, // 5秒发一次心跳
  timeoutObj: null,
  serverTimeoutObj: null,
  reset() {
    clearTimeout(this.timeoutObj);
    clearTimeout(this.serverTimeoutObj);
    return this;
  },
  start() {
    const self = this;
    this.timeoutObj = setTimeout(function() {
      // 这里发送一个心跳，后端收到后，返回一个心跳消息，
      // onmessage拿到返回的心跳就说明连接正常
      ws.send('ping');
      // console.log('ping!');
      self.serverTimeoutObj = setTimeout(function() {
        // 如果超过一定时间还没重置，说明后端主动断开了
        // 如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
        ws.close();
      }, self.timeout);
    }, this.timeout);
  },
};

function initEventHandle(setSocketData) {
  ws.onclose = function() {
    // console.log('ws连接关闭!');
    reconnect(wsUrl, setSocketData);
  };
  ws.onerror = function() {
    console.error('ws连接错误!');
    reconnect(wsUrl, setSocketData);
  };
  ws.onopen = function() {
    heartCheck.reset().start(); // 心跳检测重置
    // console.log('ws连接成功!');
    window.lockReconnect = false; // 允许自动重连
  };
  ws.onmessage = function(event) {
    const audio = document.getElementById('ring1');
    audio.play();
    // 如果获取到消息，心跳检测重置
    heartCheck.reset().start(); // 拿到任何消息都说明当前连接是正常的
    const msg = event.data;
    if (msg === 'ping') {
      return;
    } else {
      let msgInfo = JSON.parse(msg);
      msgInfo = JSON.parse(msgInfo);
      //console.log(msgInfo);
      let level;
      if (msgInfo.level == 0) {
        level = '一般';
      } else if (msgInfo.level == 1) {
        level = '危险';
      } else if (msgInfo.level == 2) {
        level = '故障';
      }
      for (let key in notifications) {
        setTimeout(() => {
          if (notifications[key]) {
            notifications[key].close();
            delete notifications[key];
          }
        }, 300);
      }
      let notificationItem = window.wsonmessage.$notify.error({
        customClass: 'wsErrorNotification',
        title: '警告',
        dangerouslyUseHTMLString: true,
        duration: 0,
        message:
          '<p>时间：' +
          parseTime(msgInfo.timestamp) +
          '</p>' +
          '<p>告警级别：' +
          level +
          '</p>' +
          '<p>告警信息：' +
          msgInfo.message +
          '</p>',
      });
      notifications.push(notificationItem);
    }
  };
}

function closews() {
  ws && ws.close();
  window.lockReconnect = true; // 不允许自动重连
  // ws.send("close")
}

// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function() {
  ws.close();
};

function reconnect(url, setSocketData) {
  if (window.lockReconnect) return;
  window.lockReconnect = true;
  setTimeout(function() {
    // 没连接上会一直重连，设置延迟避免请求过多
    createWebSocket(url, setSocketData);
    window.lockReconnect = false;
  }, 2000);
}

//= ===================================================心跳包重连CODE END=========================================

// 处理消息
/* function handMsg() {
 alert('111')
} */

export { createWebSocket, closews };
