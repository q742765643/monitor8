<template>
  <div class="login">
    <a-row>
      <a-col>
        <a-form-model :model="loginForm">
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
            <a-input v-model.trim="loginForm.code" style="width:60%" placeholder="验证码">
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
  </div>
</template>

<script>
import loginService from '@/utils/login';
import request from "@/utils/request";
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
          url:'/login',
          method: 'post',
          params: this.loginForm
        }).then(data => {
          window.sessionStorage.setItem('token',data.data.token)
           this.$router.push({path: '/home'})
        });
        // loginService.logins(this.loginForm).then((response) => {
        //   console.log(response)
        // })
    },
  },
};
</script>

<style scoped>
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
}
.ant-row {
  background: #fff;
  padding: 10px 10px 0 0;
  border-radius: 10px;
}
.ant-col {
  width: 380px;
}
.ant-form-item-control {
  height: 60px;
}
.ant-input {
  height: 60px;
}
.ant-btn {
  width: 100%;
  height: 40px;
  border-radius: 10px;
}
.ant-form-item {
  margin-left: 20px;
}
.login-code {
  width: 35%;
  float: right;
}
.login-code img {
  cursor: pointer;
  vertical-align: middle;
}
</style>