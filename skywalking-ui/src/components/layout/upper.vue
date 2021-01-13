<template>
  <div class="upper">
    <div class="left">
      <span class="web_title"> 气象海洋空间天气信息系统综合监控平台 </span>
    </div>
    <div class="right">
      <div class="user" @click="resizePassword">
        <div class="manger"></div>
        <div>管理员</div>
      </div>
      <div class="tool">
        <span class="icon iconfont iconshuaxin" @click="refreshSystem"></span>
        <span class="icon iconfont iconlingdang" @click="jumper">
          <span v-if="warnNum" class="warnNum">{{ warnNum }}</span>
        </span>
        <span class="icon iconfont iconskin" @click="changeColor"></span>
        <span class="icon iconfont iconxinxi" @click="aboutInfo"></span>
        <!-- <span class="out" @click="logout">注销/退出</span> -->
        <span class="icon iconfont iconguanbi1" @click="logout"></span>
      </div>
    </div>
    <audio muted style="display: none" id="ring1">
      <source src="../../assets/sounds/click.mp3" type="audio/mpeg" />
    </audio>

    <a-modal
      v-model="visibleModelPassword"
      title="修改密码"
      @ok="handleOk"
      okText="确定"
      cancelText="取消"
      width="50%"
      :maskClosable="false"
      :centered="true"
      class="dialogBox"
    >
      <a-form-model
        v-if="visibleModelPassword"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 19 }"
        :model="formDialogWord"
        ref="formModel"
        :rules="rulesWord"
      >
        <a-row>
          <a-col :span="24">
            <a-form-model-item label="原密码" prop="oldPassword">
              <a-input v-model="formDialogWord.oldPassword" placeholder="请输入原密码"> </a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="24">
            <a-form-model-item label="新密码" prop="newPassword">
              <a-input
                v-model="formDialogWord.newPassword"
                placeholder="请输入8-20位（数字，字母）或（数字，字母，特殊字符）的组合密码"
              >
              </a-input>
            </a-form-model-item>
          </a-col>

          <a-col :span="24">
            <a-form-model-item label="再次确认" prop="newPassword2">
              <a-input
                v-model="formDialogWord.newPassword2"
                placeholder="请输入8-20位（数字，字母）或（数字，字母，特殊字符）的组合密码"
              >
              </a-input>
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-modal>

    <a-modal
      v-model="visibleModelAbout"
      title="关于"
      @ok="visibleModelAbout = false"
      width="460px"
      :maskClosable="false"
      class="dialogBox aboutDialog"
      :footer="null"
    >
      <div v-if="visibleModelAbout" class="aboutBox">
        <h4>气象海洋空间天气信息系统综合监控平台</h4>
        <p><span>版本：</span> V1.0.1</p>
        <p><span>XX：</span>XXXXXX</p>
        <p><span>XX：</span>XXXXXX</p>
        <h5>CopyRight@2021 xxxxx 、航天宏图 版权共同所有</h5>
        <div class="ant-modal-footer" style="padding-bottom: 0">
          <a-button type="primary" @click="visibleModelAbout = false">关闭</a-button>
        </div>
        <div class="iconBox">
          <img src="../../assets/imgs/icon1.png" alt="" />
          <img src="../../assets/imgs/icon2.png" alt="" />
          <img src="../../assets/imgs/icon3.png" alt="" />
        </div>
      </div>
    </a-modal>

    <a-modal
      v-model="visibleModelChange"
      title="主题颜色"
      @ok="trueChange"
      width="460px"
      :maskClosable="false"
      class="dialogBox changeDialog"
      okText="确定"
      cancelText="取消"
    >
      <div v-if="visibleModelChange" class="changeBox">
        <a-radio-group name="radioGroup" v-model="SelectColorBody" @change="changeSelectColor">
          <a-radio value="whiteBody"> 白色主题 </a-radio>
          <a-radio value="blueBody"> 深蓝主题 </a-radio>
          <a-radio value="darkBody"> 黑色主题 </a-radio>
        </a-radio-group>
      </div>
    </a-modal>
  </div>
</template>

<script>
// 接口地址
import hongtuConfig from '@/utils/services';
import { password1, english, password2 } from '@/components/commonVaildate.js';
import { removeToken } from '@/utils/auth';
import request from '@/utils/request';
import { createWebSocket } from '@/components/utils/WebSocket.js';
export default {
  data() {
    const passwordValidate = (rule, value, callback) => {
      if (password1(value) || english(value) || password2(value)) {
        callback(new Error('请输入8-20位（数字，字母）或（数字，字母，特殊字符）的组合密码'));
      } else {
        callback();
      }
    };
    const passwordValidate2 = (rule, value, callback) => {
      if (value != this.formDialogWord.newPassword) {
        callback(new Error('2次密码不一致，请确认'));
      } else {
        callback();
      }
    };
    return {
      SelectColorBody: localStorage.getItem('colorBody') == undefined ? 'whiteBody' : localStorage.getItem('colorBody'),
      visibleModelChange: false,
      visibleModelAbout: false,
      warnNum: 0,
      visibleModelPassword: false,
      formDialogWord: {},
      rulesWord: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          {
            min: 8,
            max: 20,
            message: '密码在8-20位之间',
            trigger: 'blur',
          },
          { required: true, validator: passwordValidate, trigger: 'blur' },
        ],
        newPassword2: [{ required: true, validator: passwordValidate2, trigger: 'blur' }],
      }, //规则
    };
  },
  created() {
    document.getElementsByTagName('body')[0].className = this.SelectColorBody;
    request({
      url: '/main/getAlarm',
      method: 'get',
    }).then((data) => {
      this.warnNum = data.data.length;
    });
    // var domain = window.location.host;
    var domain = '10.1.100.35:12801';
    createWebSocket('ws://' + domain + '/webSocket/12345', '');
  },
  mounted() {
    window.wsonmessage = this;
  },
  destroyed() {},
  methods: {
    changeSelectColor(val) {
      this.SelectColorBody = val.target.value;
    },
    trueChange() {
      debugger;
      localStorage.setItem('colorBody', this.SelectColorBody);
      document.getElementsByTagName('body')[0].className = this.SelectColorBody;
      this.visibleModelChange = false;
    },
    changeColor() {
      this.visibleModelChange = true;
      //document.getElementsByTagName('body')[0].className = 'blueBody';
      // return require('@/assets/css/blue.scss');
    },
    aboutInfo() {
      this.visibleModelAbout = true;
    },
    resizePassword() {
      this.visibleModelPassword = true;
    },
    handleOk() {
      this.$refs.formModel.validate((valid) => {
        if (valid) {
          /*  hongtuConfig.updatePwd(this.formDialogWord.newPassword, this.formDialogWord.oldPassword).then((response) => {
            if (response.code == 200) {
              this.$message.success('重置成功');
              this.visibleModelPassword = false;
            }
          }); */
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    logout() {
      let that = this;
      this.$confirm({
        title: '温馨提示',
        content: '确定注销并退出系统吗？',
        okText: '是',
        cancelText: '否',
        onOk() {
          localStorage.clear();
          removeToken();
          that.$router.push('/login');
        },
        onCancel() {},
      });
    },
    jumper() {
      this.$router.push('/unAlarm');
    },
    refreshSystem() {
      window.location.reload();
    },
  },
};
</script>

<style lang="scss" scoped>
.upper {
  width: 100%;
  height: 100%;
  z-index: 100;

  display: flex;
  position: relative;
  padding-left: 34px;
  box-shadow: $plane_shadow;
  align-items: center;
  justify-content: space-between;
  .left {
    .web_title {
      font-size: 30px;
      font-family: NotoSansHans-Medium;
      font-weight: 500;
      color: #4a5a7f;
    }
  }
  .right {
    display: flex;
    align-items: center;
    justify-content: center;
    .icon {
      cursor: pointer;
      font-size: 22px;
      font-weight: 600;
    }
    .tool {
      span {
        padding: 0 12px;
        position: relative;
        .warnNum {
          padding: 0 !important;
          font-weight: 400;
          background: red;
          color: white;
          font-size: 12px;
          border-radius: 10px;
          height: 22px;
          width: 22px;
          line-height: 23px;
          text-align: center;
          min-width: 10px;
          display: inline-block;
          position: absolute;
          right: 0;
          top: -12px;
        }
      }
    }
    .out {
      cursor: pointer;
      font-size: 16px;
    }
    .user {
      display: flex;
      margin-right: 24px;
      align-items: center;
      font-size: 16px;
      color: #666;
      cursor: pointer;
      .manger {
        height: 40px;
        width: 40px;
        border-radius: 50%;
        margin-right: 12px;
        background-image: url('../../assets/imgs/manager.png');
        background-size: contain;
      }
    }
  }
}
.aboutDialog {
  .aboutBox {
    text-align: center;
    position: relative;
    h4 {
      width: 84%;
      margin: auto;
      color: #125df1;
      font-size: 36px;
      margin-bottom: 40px;
      font-family: loginFont;
    }
    p {
      margin-bottom: 10px;
    }
    span {
      display: inline-block;
      width: 70px;
      text-align: right;
    }
    h5 {
      margin-top: 60px;
      font-size: 14px;
    }
    .iconBox {
      position: absolute;
      bottom: 42px;
      right: -12px;
    }
    img {
      &:first-child {
        width: 140%;
        opacity: 0.32;
        position: absolute;
        right: 35px;
      }
      &:nth-child(2) {
        width: 90%;
        opacity: 0.2;
      }
      &:last-child {
        opacity: 0.2;
      }
    }
  }
}
</style>
