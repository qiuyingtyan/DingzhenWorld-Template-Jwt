<template>
    <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
        <div style="flex: 1">
            <el-image style="width: 100%;height: 100%" fit="cover"
                      src="https://s21.ax1x.com/2024/10/22/pAd1rE8.webp"/>
        </div>
        <div class="welcome-title">
            <div style="font-size: 30px;font-weight: bold">一眼丁真，鉴定为纯纯的弱智</div>
            <div style="margin-top: 10px">在这里，你能知识学爆</div>
            <div style="margin-top: 5px">还可以抽瑞克五代</div>
        </div>
        <div class="right-card">
            <router-view v-slot="{ Component }">
                <transition name="el-fade-in-linear" mode="out-in">
                    <component :is="Component" style="height: 100%"/>
                </transition>
            </router-view>
        </div>
    </div>
</template>

<script setup>

</script>

<style scoped>
.right-card {
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}

.welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
}
</style>
