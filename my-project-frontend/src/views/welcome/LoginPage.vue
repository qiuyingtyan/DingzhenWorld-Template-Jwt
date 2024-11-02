<template>
  <div style="text-align: center;margin: 0 20px">
    <!-- 登录标题 -->
    <div style="margin-top: 150px">
      <div style="font-size: 25px;font-weight: bold">登录</div>
      <div style="font-size: 14px;color: grey">在进入系统之前请先输入用户名和密码进行登录</div>
    </div>
    <!-- 表单 -->
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <!-- 用户名输入框 -->
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" maxlength="20" style="margin-top: 10px" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!-- 记住我 -->
        <el-row style="margin-top: 5px">
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox v-model="form.remember" label="记住我"/>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link @click="router.push('/forget')">忘记密码？</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <!-- 登录按钮 -->
    <div style="margin-top: 40px">
      <el-button @click="userLogin()" style="width: 270px" type="success" plain>立即登录</el-button>
    </div>
    <!-- 分割线 -->
    <el-divider>
      <span style="color: grey;font-size: 13px">没有账号</span>
    </el-divider>
    <!-- 注册按钮 -->
    <div>
      <el-button style="width: 270px" @click="router.push('/register')" type="warning" plain>注册账号</el-button>
    </div>
  </div>
</template>

<script setup>
import {User, Lock} from '@element-plus/icons-vue'
import router from "@/router";
import {reactive, ref} from "vue";
import {login} from '@/net'

// 表单引用
const formRef = ref()
// 表单数据
const form = reactive({
  username: '',
  password: '',
  remember: false
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名' }
  ],
  password: [
    { required: true, message: '请输入密码'}
  ]
}

// 登录方法
function userLogin() {
  // 验证表单
  formRef.value.validate((isValid) => {
    if(isValid) {
      // 调用登录接口
      login(form.username, form.password, form.remember, () => router.push("/index"))
    }
  });
}
</script>

<style scoped>

</style>