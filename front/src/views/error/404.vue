
<template>
  <div class="error-404">
    <div class="error-container">
      <div class="error-icon">
        <el-icon :size="120" color="#409EFF">
          <Warning />
        </el-icon>
      </div>
      <h1 class="error-code">404</h1>
      <p class="error-message">页面未找到</p>
      <p class="error-desc">您访问的页面不存在，请检查网址是否正确</p>
      <div class="error-actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Warning } from '@element-plus/icons-vue'

const goHome = () => {
  window.location.href = '/'
}

const goBack = () => {
  if (window.history.length > 1) {
    window.history.back()
  } else {
    window.location.href = '/'
  }
}
</script>

<style scoped lang="scss">
.error-404 {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  .error-container {
    text-align: center;
    background: white;
    padding: 60px 80px;
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  }
  .error-icon {
    margin-bottom: 20px;
  }
  .error-code {
    font-size: 72px;
    font-weight: bold;
    color: #409EFF;
    margin: 0 0 10px 0;
  }
  .error-message {
    font-size: 24px;
    color: #333;
    margin: 0 0 10px 0;
  }
  .error-desc {
    font-size: 14px;
    color: #999;
    margin: 0 0 30px 0;
  }
  .error-actions {
    display: flex;
    gap: 15px;
    justify-content: center;
  }
}
</style>