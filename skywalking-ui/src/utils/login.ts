import instance from '@/utils/request';

const loginService = {
    // 获取验证码
    getCodeImg() {
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

    // 登录方法
    logins(params: object) {
        return new Promise((resolve, reject) => {
            instance
                .post('/login', params)
                .then((res) => {
                    resolve(res)
                })
                .catch((err) => {
                    reject(err)
                })
        })
    },

    // 获取用户基本信息和权限
    getInfo() {
        return new Promise((resolve, reject) => {
            instance
                .get('/getInfo')
                .then((res) => {
                    resolve(res)
                })
                .catch((err) => {
                    reject(err)
                })
        })
    }
}

export default loginService
