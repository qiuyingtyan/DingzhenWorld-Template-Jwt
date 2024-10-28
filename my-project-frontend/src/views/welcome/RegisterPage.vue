<script setup>
import {computed, reactive, ref} from 'vue';
import {User, Lock, Message, EditPen} from '@element-plus/icons-vue'
import router from "@/router/index.js";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import {post} from "@/net";


const coldTime = ref(0)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符，只能包含数字、字母、汉字'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const rule = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},

  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {validator: validatePassword, trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入电子邮箱地址', trigger: 'blur'},
    {type: 'email', message: '请输入正确的电子邮箱地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 6, max: 6, message: '验证码长度为6个字符', trigger: ['blur', 'change']}
  ]
}

function askCode() {
  if (isEmailValid) {
    coldTime.value = 60
    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
      ElMessage.success(`验证码已发送到邮箱${form.email}，请注意查收`)
      setInterval(() => coldTime.value--, 1000)
    },(message) => {
      ElMessage.warning(message)
      coldTime.value = 0;
    })
  } else {
    ElMessage.warning('请输入正确的电子邮箱地址')
  }

}

const isEmailValid = computed(  () => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.([a-zA-Z]{2,})$/.test(form.email))

function register() {
  formRef.value.validate((valid) => {
    if (valid) {
      post(`/api/auth/register`, {...form}, () => {
        ElMessage.success('注册成功,欢迎加入芝士学爆')
        router.push('/')
      })
    } else {
      ElMessage.warning('请检查表单信息是否正确')
    }
  })
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: gray">欢迎注册芝士学爆，请在下方填写相关信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" maxlength="20" type="password" placeholder="重复密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" maxlength="30" type="text" placeholder="电子邮箱地址">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="15" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code" maxlength="6" type="text" placeholder="验证码">
                <template #prefix>
                  <el-icon>
                    <el-icon>
                      <EditPen/>
                    </el-icon>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5" style="text-align: center">
              <el-button @click="askCode" :disabled="!isEmailValid || coldTime" type="primary">
                {{ coldTime > 0 ? `请稍后${coldTime}秒` : '获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 80px">
      <el-button @click="register"  type="warning" style="width: 270px" plain>立即注册</el-button>
    </div>
    <div style="margin-top: 25px">
      <span style="font-size: 14px;line-height: 15px;color: gray">已有账号？</span>
      <el-link style="translate: 0 -1px;font-size: 14px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>

<style scoped>

</style>