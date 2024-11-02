<script setup>
// 导入useDark和useToggle函数
import { useDark, useToggle } from '@vueuse/core'

// 使用useDark函数，设置暗黑模式的选择器、属性、暗黑模式和明亮模式的值
useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})

// 使用useDark函数，当暗黑模式改变时，调用useToggle函数
useDark({
  onChanged(dark) { useToggle(dark) }
})

</script>

<template>
  <!-- 定义页头 -->
  <header>
    <!-- 定义页头内容 -->
    <div class="wrapper">
      <!-- 使用router-view组件，用于渲染路由匹配的组件 -->
      <router-view/>
    </div>
  </header>
</template>

<style scoped>
/* 定义页头样式 */
header {
  line-height: 1.5;
}
</style>
