// 导入vue-router和createWebHistory
import { createRouter, createWebHistory } from 'vue-router'
// 导入unauthorized函数
import { unauthorized } from "@/net";

// 创建路由
const router = createRouter({
    // 使用createWebHistory创建路由历史
    history: createWebHistory(import.meta.env.BASE_URL),
    // 定义路由
    routes: [
        {
            // 路由路径
            path: '/',
            // 路由名称
            name: 'welcome',
            // 路由组件
            component: () => import('@/views/WelcomeView.vue'),
            // 子路由
            children: [
                {
                    // 子路由路径
                    path: '',
                    // 子路由名称
                    name: 'welcome-login',
                    // 子路由组件
                    component: () => import('@/views/welcome/LoginPage.vue')
                }, {
                    // 子路由路径
                    path: 'register',
                    // 子路由名称
                    name: 'welcome-register',
                    // 子路由组件
                    component: () => import('@/views/welcome/RegisterPage.vue')
                }, {
                    // 子路由路径
                    path: 'forget',
                    // 子路由名称
                    name: 'welcome-forget',
                    // 子路由组件
                    component: () => import('@/views/welcome/ForgetPage.vue')
                }
            ]
        }, {
            // 路由路径
            path: '/index',
            // 路由名称
            name: 'index',
            // 路由组件
            component: () => import('@/views/IndexView.vue'),
        }
    ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
    // 调用unauthorized函数，判断是否未授权
    const isUnauthorized = unauthorized()
    // 如果路由名称以welcome开头且未授权，则跳转到index路由
    if(to.name.startsWith('welcome') && !isUnauthorized) {
        next('/index')
    // 如果路由路径以/index开头且已授权，则跳转到welcome路由
    } else if(to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    // 否则，继续执行
    } else {
        next()
    }
})

// 导出路由
export default router
