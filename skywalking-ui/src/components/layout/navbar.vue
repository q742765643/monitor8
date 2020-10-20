<template>
  <a-layout-sider id="slider">
    <div class="logo">
      <span class="web_title"> 气象海洋空间天气信息系统综合监控平台 </span>
    </div>
    <a-menu
      mode="inline"
      :default-open-keys="['sub1']"
      id="menu"
      :selectedKeys="[this.$route.path]"
    >
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
          <router-link to="/discoverLink/linkManager">
            链路设备管理</router-link
          >
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
        <span slot="title">
          <span class="iconfont">&#xe692;</span>主机监视</span
        >

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
        <span slot="title">
          <span class="iconfont">&#xe640;</span>业务视图</span
        >

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

      <a-sub-menu key="sub3">
        <span slot="title">
          <span class="iconfont">&#xe692;</span>旧页面</span
        >

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

    </a-menu>
  </a-layout-sider>
</template>

<script>
export default {
  name: 'navbar',
  data() {
    return {};
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

  .logo {
    padding: 0 0.75rem;
    width: 100%;
    height: 2rem;
    &::after {
      content: '';
      height: 1px;
      background: $borderColor;
      width: calc(100% - 0.75rem);
      position: absolute;
      top: 2rem;
      left: 0.375rem;
    }
    display: flex;
    justify-content: center;
    align-items: center;

    .web_title {
      display: block;
      font-size: 0.3rem;
      color: $nav_textColor;
      font-family: Alibaba-PuHuiTi-Medium;
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
