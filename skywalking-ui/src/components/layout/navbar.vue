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
    <el-scrollbar class="leftMenuScroll">
      <a-menu mode="inline" :default-open-keys="['sub1']" id="menuNav" :selectedKeys="[this.$route.path]">
        <!-- <a-menu-item key="/mainMonitor" id="main">
        <span class="iconfont">&#xe60c;</span>
        <span> <router-link to="/mainMonitor"> 综合监控</router-link></span>
      </a-menu-item>

      <a-sub-menu key="sub2">
        <span slot="title">
          <span class="iconfont">&#xe8bb;</span>链路发现</span>
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
        <span slot="title"> <span class="iconfont">&#xe6b5;</span>主机监视</span>

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
      </a-sub-menu> -->

        <!--
      <a-sub-menu key="sub4">
        <span slot="title"> <span class="iconfont">&#xe6d4;</span>业务视图</span>

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
-->

        <!-- <a-sub-menu key="sub5">
        <span slot="title"> <span class="iconfont">&#xe61b;</span>文件监控</span>

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
        <a-menu-item key="/businessView/dataView">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/businessView/dataView"> 数据视图</router-link>
        </a-menu-item>
      </a-sub-menu>
      <a-sub-menu key="sub6">
        <span slot="title"> <span class="iconfont">&#xe60d;</span>告警管理</span>
        <a-menu-item key="/alarmMonitoring">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/alarmMonitoring"> 告警管理</router-link>
        </a-menu-item>
        <a-menu-item key="/alarmMonitoring/alarmReport">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/alarmMonitoring/alarmReport"> 告警查询</router-link>
        </a-menu-item>
        <a-menu-item key="/alarmMonitoring/alarmReportTable">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/alarmMonitoring/alarmReportTable"> 告警报表</router-link>
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

      <a-sub-menu key="sub8">
        <span slot="title"> <span class="iconfont">&#xe616;</span>用户管理</span>
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
        <a-sub-menu key="sub9" class="log">
          <span slot="title"> <span class="iconfont">&#xe640;</span>日志管理</span>
          <a-menu-item key="/operlog">
            <span class="iconfont">&#xe616;</span>
            <router-link to="/operlog"> 操作日志</router-link>
          </a-menu-item>
          <a-menu-item key="/loginlog">
            <span class="iconfont">&#xe616;</span>
            <router-link to="/loginlog"> 登录日志</router-link>
          </a-menu-item>
        </a-sub-menu>
        <a-menu-item key="/onlineUser">
          <span class="iconfont">&#xe60c;</span>
          <router-link to="/onlineUser"> 在线用户</router-link>
        </a-menu-item>
      </a-sub-menu>

      <a-sub-menu key="sub10">
        <span slot="title"> <span class="iconfont">&#xe640;</span>进程监控</span>
        <a-menu-item key="/process/processConfig">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/process/processConfig">进程配置</router-link>
        </a-menu-item>
        <a-menu-item key="/process/processReport">
          <span class="iconfont">&#xe611;</span>
          <router-link to="/process/processReport">进程报表</router-link>
        </a-menu-item>
      </a-sub-menu> -->

<<<<<<< HEAD
      <template v-for="item in menuOptions">
        <a-menu-item :key="item.path" v-if="!item.children" id="main">
          <span class="iconfont" :class="item.meta.icon"></span>
          <router-link :to="item.path">{{item.name}}</router-link>
        </a-menu-item>
        <a-sub-menu v-else :key="item.path">
          <span slot="title"> <span class="iconfont" :class="item.meta.icon"></span>{{ item.name }}</span>
          <template v-for="subItem in item.children">
            <a-menu-item v-if="!subItem.children" :key="'/' + subItem.path">
              <span class="iconfont" :class="subItem.meta.icon"></span>
              <router-link :to="'/' + subItem.path">{{ subItem.name }}</router-link>
            </a-menu-item>
            <a-sub-menu v-else :key="'/' + subItem.path" class="log">
              <span slot="title"> <span class="iconfont" :class="subItem.meta.icon"></span>{{subItem.name}}</span>
              <a-menu-item :key="'/' + aSubItem.path" v-for="aSubItem in subItem.children">
                <span class="iconfont" :class="aSubItem.meta.icon"></span>
                <router-link :to="'/' + aSubItem.path">{{aSubItem.name}}</router-link>
=======
        <template v-for="item in menuOptions">
          <a-menu-item :key="item.path" v-if="!item.children" id="main">
            <span class="iconfont" :class="item.meta.icon"></span>
            <router-link :to="item.path">{{ item.name }}</router-link>
          </a-menu-item>
          <a-sub-menu v-else :key="item.path">
            <span slot="title"> <span class="iconfont" :class="item.meta.icon"></span>{{ item.name }}</span>
            <template v-for="subItem in item.children">
              <a-menu-item v-if="!subItem.children" :key="'/' + subItem.path">
                <span class="iconfont" :class="subItem.meta.icon"></span>
                <router-link :to="'/' + subItem.path">{{ subItem.name }}</router-link>
>>>>>>> 1feca18b337ee9028fbcfb51f8fe08caaf7be68e
              </a-menu-item>
              <a-sub-menu v-else :key="'/' + subItem.path">
                <span slot="title"> <span class="iconfont" :class="subItem.meta.icon"></span>{{ subItem.name }}</span>
                <a-menu-item :key="'/' + aSubItem.path" v-for="aSubItem in subItem.children">
                  <span class="iconfont" :class="aSubItem.meta.icon"></span>
                  <router-link :to="'/' + aSubItem.path">{{ aSubItem.name }}</router-link>
                </a-menu-item>
              </a-sub-menu>
            </template>
          </a-sub-menu>
        </template>
      </a-menu>
    </el-scrollbar>
  </a-layout-sider>
</template>

<script>
  import moment from 'moment';
  import hongtuConfig from '@/utils/services';
  export default {
    name: 'navbar',
    data() {
      return {
        date: '',
        week: '',
        time: '',
        timer: null,
        menuOptions: [],
      };
    },
    created() {
      this.getTime();
      hongtuConfig.getRouters().then((res) => {
        this.menuOptions = res.data;
        console.log(this.menuOptions);
      });
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
    .timeBox {
      width: calc(100% - 60px);
      padding: 10px 0 14px 0;
      display: flex;
      position: relative;
      justify-content: center;
      align-items: center;
      border-bottom: 1px solid #dae4ec;
      margin: auto;
      .date {
        font-size: $ant_font_size;
        .day {
          font-size: 20px;
          font-family: electronicFont;
        }
        .week {
          text-align: center;
          font-family: NotoSansHans-Regular;
        }
      }
      .time {
        font-size: 24px;
        margin-left: 24px;
        font-family: electronicFont;
      }
    }
<<<<<<< HEAD

    .ant-menu-item > a {
      display: inline-block !important;
      text-decoration: none;
      // color: rgba(0, 0, 0, 0.65);
      color: #5d76ae;
    }
    // .ant-menu-item > a:hover {
    //   color: #fff;
    // }
    .ant-menu-item {
      font-size: $nav_fontSize;
      height: 50px;
      line-height: 50px;
    }
    .ant-menu-item[id='main']:hover {
      color: #1890ff !important;
      a {
        color: #1890ff;
      }
    }
    .ant-menu-item:hover {
      span {
        color: #1890ff;
      }
      a {
        color: #1890ff;
      }
    }

    .ant-menu-submenu-selected .ant-menu-submenu-title {
      border-radius: 6px;
      color: #fff !important;
      background: linear-gradient(90deg, #428dff, #397dff) !important;
      box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.4);
    }
    .iconfont {
      font-size: 22px;
      margin-right: 18px;
    }
    .ant-menu-sub {
      .iconfont {
=======
    .leftMenuScroll {
      height: calc(100% - 90px);
      #menuNav {
        background: #f1f2f4;
        padding: 0 30px;
        padding-top: 30px;
        font-size: $nav_fontSize;
>>>>>>> 1feca18b337ee9028fbcfb51f8fe08caaf7be68e
        color: #5d76ae;
        border: none;
        font-family: Microsoft YaHei;
        .ant-menu-submenu-title {
          padding-left: 30px !important;
          .ant-menu-submenu-arrow {
            right: 10px !important;
          }
        }

<<<<<<< HEAD
    .ant-menu-item-selected[id='main'] {
      height: 50px;
      line-height: 50px;
      color: #fff !important;
      background: linear-gradient(90deg, #428dff, #397dff) !important;
      box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.4);
      border-radius: 6px;
      span, a {
        color: #fff !important;
      }
    }
    .ant-menu-item-selected[id='main']:hover {
      span, a {
         color: #fff !important;
      }
    }
    .ant-menu-item .ant-menu-item-selected {
      // color: #1890ff;
    }
    .log {
      .ant-menu-submenu-title:hover {
        background: transparent !important;
        span {
          color:#1890ff;
        }
      }
    }
    
    .ant-menu-item-selected,
    .ant-menu-item-selected > a,
    .ant-menu-item-selected > a:hover {
      // color: #1890ff !important;
    }
    .ant-menu:not(.ant-menu-horizontal) {
      .ant-menu-item-selected {
        background: #b8e3f6;
        color: #1890ff;
=======
        .ant-menu-item > a {
          display: inline-block !important;
          text-decoration: none;
          color: rgba(0, 0, 0, 0.65);
        }
        // .ant-menu-item > a:hover {
        //   color: #fff;
        // }
        .ant-menu-item {
          font-size: $nav_fontSize;
          height: 50px;
          line-height: 50px;
        }

        .ant-menu-submenu-selected .ant-menu-submenu-title {
          border-radius: 6px;
          color: #fff !important;
          background: linear-gradient(90deg, #428dff, #397dff) !important;
          box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.4);
        }
>>>>>>> 1feca18b337ee9028fbcfb51f8fe08caaf7be68e
        .iconfont {
          font-size: 22px;
          margin-right: 18px;
        }
        .ant-menu-sub {
          .iconfont {
            color: #5d76ae;
            font-size: 20px !important;
          }
        }

        .ant-menu-item-selected[id='main'] {
          height: 50px;
          line-height: 50px;
          color: #fff !important;
          background: linear-gradient(90deg, #428dff, #397dff) !important;
          box-shadow: 0px 5px 30px 0px rgba(22, 115, 255, 0.4);
          border-radius: 6px;
        }
<<<<<<< HEAD
        span {
          color: #1890ff !important;
        }
        &:before {
          position: absolute;
          top: 0;
          left: 0;
          bottom: 0;
          border: none;
          border-right: 8px solid #00c0ff;
          content: '';
        }
        &:after {
          display: none;
=======
        .ant-menu-item-selected[id='main'] a {
          color: #fff !important;
        }

        .ant-menu-item-selected,
        .ant-menu-item-selected > a,
        .ant-menu-item-selected > a:hover {
          color: #1890ff !important;
>>>>>>> 1feca18b337ee9028fbcfb51f8fe08caaf7be68e
        }
        .ant-menu-item-selected[id='main'] > a:hover {
          color: #fff !important;
        }
        .ant-menu:not(.ant-menu-horizontal) {
          .ant-menu-item-selected {
            background: #b8e3f6;
            color: #1890ff;
            .iconfont {
              color: #1890ff;
            }
            &:before {
              position: absolute;
              top: 0;
              left: 0;
              bottom: 0;
              border: none;
              border-right: 8px solid #00c0ff;
              content: '';
            }

            &:after {
              display: none;
            }
          }
        }
        // &::-webkit-scrollbar {
        //   width: 3px;
        //   background-color: #f5f5f5;
        // }
        // &::-webkit-scrollbar-thumb {
        //   background-color: #5aa6ee;
        // }
        // &::-webkit-scrollbar-track {
        //   box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.3);
        //   background-color: #f5f5f5;
        // }
      }
    }
  }
</style>
