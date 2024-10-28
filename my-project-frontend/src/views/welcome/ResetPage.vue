<script setup xmlns="">

import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";

const active = ref(0);

const form = reactive({
  email: '',
  code: '',
  password:'',
  password_repeat:''
})
</script>

<template>
  <div style="text-align: center">
    <div style="margin-top: 40px">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证邮箱"/>
        <el-step title="重置密码"/>
      </el-steps>
    </div>
    <div style="margin:0 20px" v-if="active === 0">
      <div style="margin-top: 80px">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: gray">请输入需要重置密码的电子邮箱地址</div>
      </div>
      <div style="margin-top: 50px">
        <el-form model="form">
          <el-form-item prop="email">
            <el-input v-model="form.email" type="text" placeholder="电子邮箱地址">
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
                <el-button type="primary">获取验证码</el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 80px">
        <el-button @click="active++" type="warning" style="width: 270px" plain>开始重置密码</el-button>
      </div>
    </div>
    <div style="margin:0 20px" v-if="active === 1">
    <div style="margin-top: 80px">
      <div style="font-size: 25px;font-weight: bold">重置密码</div>
      <div style="font-size: 14px;color: gray">请填写你的新密码，务必牢记，防止丢失</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form">
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
      </el-form>
    </div>
      <div style="margin-top: 80px">
        <el-button @click="active++" type="danger" style="width: 270px" plain>立即重置密码</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>