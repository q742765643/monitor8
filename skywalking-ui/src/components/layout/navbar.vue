<template>
  <a-layout-sider id="slider">
    <div class="timeBox">
      <div class="date">
        <div class="day">{{ date }}</div>
        <div class="week">{{ week }}</div>
      </div>
      <div class="time">
        <span>{{ time }}</span>
      </div>
    </div>
    <a-menu mode="inline" :default-open-keys="['sub1']" id="menu" :selectedKeys="[this.$route.path]">
      <!--   :selectedKeys="[this.$route.path]" -->
      <!--  :default-selected-keys="['1']" -->
      <!--     :default-open-keys="['sub1']" -->
      <a-menu-item key="/mainMonitor" id="main">
        <span class="iconfont">&#xe6b5;</span>
        <span> <router-link to="/mainMonitor"> 综合监控</router-link></span>
      </a-menu-item>

      <a-sub-menu key="sub2">
        <span slot="title">
          <span class="iconfont">&#xe8bb;</span>链路发现
          <!--    <i class="iconfont expand">&#xe68a;</i> --></span
        >

        <a-menu-item key="/discoverLink/linkTopu">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/discoverLink/linkTopu"> 链路拓扑</router-link>
        </a-menu-item>
        <a-menu-item key="/discoverLink/linkManager">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/discoverLink/linkManager"> 链路设备管理</router-link>
        </a-menu-item>
        <a-menu-item key="/discoverLink/linkConfig">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/discoverLink/linkConfig"> 自动发现配置</router-link>
        </a-menu-item>
        <a-menu-item key="/discoverLink/linkReport">
          <span class="iconfont">&#xe611;</span>

          <router-link to="/discoverLink/linkReport"> 软件报表</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="sub3">
        <span slot="title"> <span class="iconfont">&#xe692;</span>主机监视</span>

        <a-menu-item key="/masterMonitor/resourceView">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/masterMonitor/resourceView"> 资源概览</router-link>
        </a-menu-item>
        <a-menu-item key="/masterMonitor/masterManager">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/masterMonitor/masterManager"> 主机管理</router-link>
        </a-menu-item>
        <a-menu-item key="/masterMonitor/masterReport">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/masterMonitor/masterReport"> 主机报表</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="sub4">
        <span slot="title"> <span class="iconfont">&#xe640;</span>业务视图</span>

        <a-menu-item key="/businessView/dataView">
          <span class="iconfont">&#xe611;</span>

          <router-link to="/businessView/dataView"> 数据视图</router-link>
        </a-menu-item>

        <a-menu-item key="/businessView/softwareView">
          <span class="iconfont">&#xe611;</span>

          <router-link to="/businessView/softwareView"> 软件视图</router-link>
        </a-menu-item>
        <a-menu-item key="/businessView/dataReport">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/businessView/dataReport"> 数据报表</router-link>
        </a-menu-item>

        <a-menu-item key="/businessView/softwareReport">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/businessView/softwareReport"> 软件报表</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="sub5">
        <span slot="title"> <span class="iconfont">&#xe640;</span>文件监控</span>

        <a-menu-item key="/fileMonitoring">
          <span class="iconfont">&#xe611;</span>

          <router-link to="/fileMonitoring"> 文件监控</router-link>
        </a-menu-item>
        <a-menu-item key="/fileMonitoring/directory_account">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/fileMonitoring/directory_account"> 共享目录管理</router-link>
        </a-menu-item>
        <a-menu-item key="/fileMonitoring/file_report">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/fileMonitoring/file_report"> 文件报表</router-link>
        </a-menu-item>
        <a-menu-item key="/fileMonitoring/file_log">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/fileMonitoring/file_log"> 文件调度日志</router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub6">
        <span slot="title"> <span class="iconfont">&#xe640;</span>告警管理</span>
        <a-menu-item key="/alarmMonitoring">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/alarmMonitoring"> 告警管理</router-link>
        </a-menu-item>
        <a-menu-item key="/alarmMonitoring/alarmReport">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/alarmMonitoring/alarmReport"> 告警报表</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="sub7">
        <span slot="title"> <span class="iconfont">&#xe692;</span>旧页面</span>

        <a-menu-item key="/dictType">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/dictType"> 字典管理</router-link>
        </a-menu-item>
        <a-menu-item key="/dictData">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/dictData"> 字典数据</router-link>
        </a-menu-item>
        <a-menu-item key="/job">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/job"> 调度管理</router-link>
        </a-menu-item>
        <a-menu-item key="/topuConfig">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/topuConfig"> 自动发现配置</router-link>
        </a-menu-item>
        <a-menu-item key="/manager">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/manager"> 链路设备管理</router-link>
        </a-menu-item>
        <a-menu-item key="/report">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/report"> 链路设备报表</router-link>
        </a-menu-item>
      </a-sub-menu>

      <!-- 用户管理 -->
      <a-sub-menu key="sub8">
        <span slot="title"> <span class="iconfont">&#xe640;</span>用户管理</span>
        <a-menu-item key="/userMonitoring">
          <span class="iconfont">&#xe616;</span>
          <router-link to="/userMonitoring"> 用户管理</router-link>
        </a-menu-item>
        <a-menu-item key="/roleMonitoring">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/roleMonitoring"> 角色管理</router-link>
        </a-menu-item>
        <a-menu-item key="/menuManagement">
          <span class="iconfont">&#xe630;</span>
          <router-link to="/menuManagement"> 菜单管理</router-link>
        </a-menu-item>
        <a-menu-item key="/departmentMonitoring">
          <span class="iconfont">&#xe60c;</span>
          <router-link to="/departmentMonitoring"> 部门管理</router-link>
        </a-menu-item>
        <a-menu-item key="/onlineUser">
          <span class="iconfont">&#xe60c;</span>
          <router-link to="/onlineUser"> 在线用户</router-link>
        </a-menu-item>
      </a-sub-menu>
    </a-menu>
  </a-layout-sider>
</template>

<script>
  import moment from 'moment';
  export default {
    name: 'navbar',
    data() {
      return {
        date: '',
        week: '',
        time: '',
        timer: null,
      };
    },
    created() {
      this.getTime();
    },
    mounted() {
      let data;

      this.timer = setInterval(() => this.getTime(), 1000);
    },
    destroyed() {
      clearInterval(this.timer); // 清除定时器
      this.timer = null;
    },
    methods: {
      getTime: function() {
        moment.locale('zh-cn');
        this.date = moment().format('YYYY-MM-DD');
        this.week = moment().format('dddd');
        this.time = moment().format('LTS');
      },
    },
  };
</script>

<style lang="scss" scoped>
  #slider {
    // width: 4.25rem !important;
    width: 100% !important;
    height: 100%;
    flex: none !important;
    max-width: none !important;
    min-width: none !important;
    background: #f1f2f4;
    .timeBox {
      height: 1rem;
      display: flex;
      position: relative;
      justify-content: center;
      &::after {
        content: '';
        height: 1px;
        background: $borderColor;
        width: calc(100% - 0.75rem);
        position: absolute;
        bottom: 0;
        left: 0.375rem;
      }
      .date {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        .day {
          font-family: electronicFont;
          font-size: 0.275rem;
        }
        .week {
          font-family: Alibaba-PuHuiTi-Medium;
          font-size: 0.225rem;
        }
      }
      .time {
        font-size: 0.375rem;
        font-family: electronicFont;
        margin-left: 0.375rem;
        //height: 100%;
        line-height: 1rem;
      }
    }

    #menu {
      font-family: Alibaba-PuHuiTi-Regular;
      height: calc(100% - 2rem);
      overflow-y: auto;
      overflow-x: hidden;
      background: #f1f2f4;
      padding: 0.5rem 0.375rem 0.375rem 0.375rem;
      color: $nav_textColor;
      .iconfont {
        font-size: 0.3rem !important;
        margin-right: 0.25rem;
      }
      .expand {
        position: absolute;
        right: 0.275rem;
        margin: 0;
        padding-top: 0.0375rem;
        font-size: 0.25rem !important;
      }
    }

    #menu::-webkit-scrollbar {
      width: 3px;
      background-color: #f5f5f5;
    }

    #menu::-webkit-scrollbar-thumb {
      background-color: #5aa6ee;
    }
    #menu::-webkit-scrollbar-track {
      box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.3);
      background-color: #f5f5f5;
    }
  }
</style>
