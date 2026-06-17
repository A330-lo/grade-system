<template>
  <div class="home-page">
    <!-- 欢迎信息 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎回来, {{ userStore.realName || userStore.username || '用户' }} ！</h2>
        <p>今天是 {{ currentDate }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})
</script>

<style scoped lang="scss">
.home-page {
  padding: 20px;
  
  .welcome-card {
    margin-bottom: 20px;
    .welcome-content {
      h2 { margin: 0 0 10px 0; color: #303133; }
      p { margin: 0; color: #606266; }
    }
  }
}
</style>