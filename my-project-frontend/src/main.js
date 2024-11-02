// 导入createApp函数，用于创建Vue应用
import { createApp } from 'vue'
// 导入App组件，作为Vue应用的根组件
import App from './App.vue'
// 导入路由配置
import router from './router'
// 导入axios库，用于发送HTTP请求
import axios from "axios";

// 导入Element Plus的暗黑主题样式
import 'element-plus/theme-chalk/dark/css-vars.css'

// 设置axios的默认请求地址
axios.defaults.baseURL = 'http://localhost:8080'

// 创建Vue应用实例
const app = createApp(App)

// 使用路由配置
app.use(router)

// 挂载Vue应用实例到页面上的#app元素
app.mount('#app')
