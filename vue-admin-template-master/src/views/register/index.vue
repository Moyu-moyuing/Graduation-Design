<template>
  <div class="register-container">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">用户注册</h3>
      </div>

      <el-form-item prop="Name">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="Name"
          v-model="registerForm.Name"
          placeholder="输入用户名"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="Pass">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType1"
          ref="Pass"
          v-model="registerForm.Pass"
          :type="passwordType1"

          placeholder="输入密码"
          name="password"
          tabindex="2"
          auto-complete="on"
        />
        <span class="show-pwd" @click="showPwd1">
          <svg-icon :icon-class="passwordType1 === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>
      <el-form-item prop="checkPass">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType2"
          ref="checkPass"
          v-model="registerForm.checkPass"
          :type="passwordType2"
          placeholder="再次确认密码"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleRegister('registerForm')"
        />
        <span class="show-pwd" @click="showPwd2">
          <svg-icon :icon-class="passwordType2 === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>
      <el-row>
        <el-button :loading="loading1" type="primary" style="width:45%;margin-bottom:30px;" @click="handleRegister('registerForm')">注册</el-button>
        <el-button :loading="loading2" type="warning" style="width:45%;margin-bottom:30px; margin-left: 45px;" @click="handleLogin">返回登录</el-button>
<!--        <el-button :loading="loading" type="primary" style="width:45%;margin-bottom:30px;" @click.native.prevent="handleLogin">登录</el-button>-->
<!--        <el-button :loading="loading" type="warning" style="width:45%;margin-bottom:30px; margin-left: 45px;" @click.native.prevent="handleRegister">注册</el-button>-->
      </el-row>

      <div class="tips">
        <span style="margin-right:20px;">用户名：1-8位</span>
        <span> 密码：不能少于6位</span>
      </div>

    </el-form>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'

export default {
  name: 'Register',
  data() {
    // const validateUsername = (rule, value, callback) => {
    //   if (value.length<=0||value.length>8) {
    //     callback(new Error('请输入正确的用户名格式'))
    //   } else {
    //     callback()
    //   }
    // }
    // const validatePassword = (rule, value, callback) => {
    //   if (value.length < 6) {
    //     callback(new Error('密码不能少于6位数'))
    //   } else {
    //     callback()
    //   }
    // }
    const validateName=(rule,value,callback)=>{
      if(value.length<=0||value.length>8){
        callback(new Error('请输入正确的用户格式：1-8位'));
      }else {
        callback()
      }

    }
    const validatePass=(rule,value,callback)=>{
      if(value===''){
        callback(new Error("请输入密码"));
      }else if(value.length < 6){
        callback(new Error("密码不能少于6位"));
      }
      else{
        if(this.registerForm.Pass!=''){
          this.$refs.registerForm.validateField('checkPass');
        }
        callback();
      }
    }
    const validatePass2=(rule,value,callback)=>{
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registerForm.Pass) {
        console.log(value);
        console.log(this.registerForm);
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    }
    return {
      registerForm: {
        Name:'',
        Pass:'',
        checkPass:''
      },

      registerRules:{
        Name:[{
          required: true,validator:validateName,trigger: 'blur'
        }],
        Pass:[{
          required: true, validator:validatePass,trigger: 'blur'
        }],
        checkPass:[{
          required: true,validator:validatePass2,trigger: 'blur'
        }]
      },
      loading1: false,
      loading2: false,
      passwordType1: 'password',
      passwordType2: 'password',
      redirect: undefined,
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {

    showPwd1() {
      if (this.passwordType1 === 'password') {
        this.passwordType1 = ''
      } else {
        this.passwordType1 = 'password'
      }
      this.$nextTick(() => {
        this.$refs.Pass.focus()
      })
    },
    showPwd2() {
      if (this.passwordType2 === 'password') {
        this.passwordType2 = ''
      } else {
        this.passwordType2 = 'password'
      }
      this.$nextTick(() => {
        this.$refs.checkPass.focus()
      })
    },
    handleRegister(formName){
      let that=this;
      this.$refs[formName].validate((valid)=>{
        if(valid){
          let userform={
            id:0,
            name:this.registerForm.Name,
            password:this.registerForm.Pass
          };
          that.loading1=true;
          this.request.post("http://localhost:9090/user",userform).then(res=>{
            if(res){
              if(res.success){
                this.$message.success("注册成功");
              }
              else{
                this.$message.error("注册失败!"+res.data);
              }
              console.log(res);
              this.registerForm={
                Name:'',
                Pass:'',
                checkPass:''
              };
              this.loading1=false;
            }
          })
        }
      })
    },
    handleLogin() {
      this.$router.push({name:'Login'});
    }
  }
}
</script>

<style lang="scss" >
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray: #ffffff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

/* reset element-ui css */
.register-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.register-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .register-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
