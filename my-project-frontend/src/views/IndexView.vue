<template>
  <div>
    <!-- 点击退出登录按钮时，调用userLogout函数 -->
    <el-button @click="userLogout">退出登录</el-button>
  </div>
</template>

<script setup>
// 引入logout函数和router
import { logout } from '@/net'
import router from "@/router";

// 定义userLogout函数
function userLogout() {
  // 调用logout函数，并在回调函数中跳转到首页
  logout(() => router.push("/"))
}
</script>

<style scoped>

</style>