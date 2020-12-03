<template>
  <div class="login">
    <a-row class="wrap">
      <a-col :span="13">
        <span style="opacity: 0">1</span>
      </a-col>
      <a-col :span="11">
        <!-- <img src="@/assets/img/title.png" class="title" /> -->
        <div class="title">气象海洋空间天气信息系统综合监控平台</div>
        <a-form-model :model="loginForm" ref="loginForm">
          <a-form-model-item>
            <a-input v-model.trim="loginForm.username" placeholder="账号">
              <a-icon slot="prefix" type="user" />
            </a-input>
          </a-form-model-item>
          <a-form-model-item>
            <a-input v-model.trim="loginForm.password" placeholder="密码"
              ><a-icon slot="prefix" type="lock" />
            </a-input>
          </a-form-model-item>
          <a-form-model-item>
            <a-input v-model.trim="loginForm.code" style="width: 66%" placeholder="验证码">
              <a-icon slot="prefix" type="code" />
            </a-input>
            <div class="login-code">
              <img :src="codeUrl" alt="" @click="getCode" />
            </div>
          </a-form-model-item>
          <a-form-model-item>
            <a-button type="primary" class="loginBtn" @click.native.prevent="handleLogin">
              <span>登录</span>
            </a-button>
          </a-form-model-item>
        </a-form-model>
      </a-col>
    </a-row>
    <div class="el-login-footer">
      <p>地址：北京海淀区中关村南大街46号 | 邮编：100081 | 电话 : 86-10-68407499</p>
    </div>
  </div>
</template>

<script>
import loginService from '@/utils/login';
import request from '@/utils/request';
import hongtuConfig from '@/utils/services';
export default {
  data() {
    return {
      loginForm: {
        username: 'users',
        password: '111',
        code: '',
        uuid: '',
      },
      codeUrl: '',
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      loginService.getCodeImg().then((response) => {
        // console.log(response)
        this.codeUrl = 'data:img/gif;base64,' + response.data.img;
        this.loginForm.uuid = response.data.uuid;
      });
    },
    handleLogin() {
      request({
        url: '/login',
        method: 'post',
        params: this.loginForm,
      }).then((data) => {
        window.sessionStorage.setItem('token', data.data.token);
        this.$router.push({ path: '/home' });
      });
      // loginService.logins(this.loginForm).then((response) => {
      //   console.log(response)
      // })
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: linear-gradient(
    25deg,
    rgb(17, 139, 252) 38%,
    rgb(24, 151, 253) 38%,
    rgb(17, 139, 252) 45%,
    rgb(24, 151, 253) 45%,
    rgb(17, 139, 252) 70%,
    rgb(25, 150, 253) 70%,
    rgb(17, 139, 252) 85%,
    rgb(25, 150, 253) 85%
  );
  .wrap {
    width: 60%;
    text-align: right;
    background: url('../../assets/img/login-bg.png') no-repeat;
    background-size: 100% 100%;
    .title {
      font-size: 30px;
      font-family: loginFont;
      width: 60%;
      margin: auto;
      color: #125df1;
      text-align: center;
      margin-top: 30px;
      margin-bottom: 40px;
    }
    .ant-form-item {
      margin-bottom: 26px;
    }
    .ant-input {
      font-size: 12px;
      height: 40px;
      line-height: 40px;
    }
    .ant-form {
      padding: 0 15%;
    }
    .loginBtn {
      width: 100%;
      height: 46px;
      border-radius: 4px;
      line-height: 46px;
      font-size: 20px;
      background-color: #1890ff;
      margin-bottom: 80px;
    }
    .login-code {
      width: 34%;
      float: right;
      height: 40px;
      img {
        cursor: pointer;
        vertical-align: middle;
      }
    }
  }
  .el-login-footer {
    color: #99d5fb;
    position: fixed;
    width: 100%;
    bottom: 20px;
    text-align: center;
    font-size: 14px;
    line-height: 24px;
    p {
      margin: 0;
    }
  }
}
</style>