<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>学生课程成绩管理系统</h1>
        <p>Student Course Grade Management System</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            :placeholder="placeholderText.username"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 角色切换按钮组 移到登录按钮上方 -->
        <el-form-item class="role-buttons-wrap">
          <div class="role-buttons">
            <div
              class="role-btn"
              :class="{ active: loginForm.role === 'student' }"
              @click="loginForm.role = 'student'"
            >
              学生
            </div>
            <div
              class="role-btn"
              :class="{ active: loginForm.role === 'teacher' }"
              @click="loginForm.role = 'teacher'"
            >
              教师
            </div>
            <div
              class="role-btn"
              :class="{ active: loginForm.role === 'admin' }"
              @click="loginForm.role = 'admin'"
            >
              管理员
            </div>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  role: 'admin'
})

const placeholderText = computed(() => {
  const map = {
    student: '请输入学号',
    teacher: '请输入工号',
    admin: '请输入用户名'
  }
  return {
    username: map[loginForm.role] || '请输入账号'
  }
})

const validateUsername = (rule, value, callback) => {
  if (!value) {
    const msg = loginForm.role === 'student' ? '请输入学号' : 
                loginForm.role === 'teacher' ? '请输入工号' : '请输入用户名'
    callback(new Error(msg))
  } else {
    callback()
  }
}

const loginRules = {
  username: [
    { required: true, validator: validateUsername, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    await userStore.login(loginForm.username, loginForm.password, loginForm.role)
    
    ElMessage.success('登录成功')
    
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } catch (error) {
    console.error('登录失败', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3a5f, #0d1b2a);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 30% 20%, rgba(70, 130, 200, 0.22) 0%, transparent 58%);
    pointer-events: none;
  }
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(circle at 70% 80%, rgba(50, 100, 170, 0.16) 0%, transparent 62%);
    pointer-events: none;
  }
}

.login-box {
  width: 420px;
  background: rgba(255, 255, 255, 0.07);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border-radius: 14px;
  padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 24px;
    color: #e8edf5;
    margin-bottom: 8px;
    font-weight: 600;
    letter-spacing: 0.12em;
  }

  p {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.65);
    letter-spacing: 0.06em;
  }
}

.login-form {
  // 输入框适配深色背景
  :deep(.el-input__wrapper) {
    background: rgba(255,255,255,0.08);
    box-shadow: 0 0 0 1px rgba(255,255,255,0.15) inset;
  }
  :deep(.el-input__inner) {
    color: #fff;
  }
  :deep(.el-input__inner::placeholder) {
    color: rgba(255,255,255,0.5);
  }
  :deep(.el-input__prefix-inner) {
    color: rgba(255,255,255,0.6);
  }

  // 角色按钮容器
  .role-buttons-wrap {
    margin-bottom: 10px;
  }
  .role-buttons {
    display: flex;
    gap: 10px;
    width: 100%;
  }
  .role-btn {
    flex: 1;
    text-align: center;
    padding: 10px 0;
    border-radius: 8px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    color: rgba(255, 255, 255, 0.75);
    cursor: pointer;
    transition: all 0.25s ease;
    background: rgba(255, 255, 255, 0.06);
    user-select: none;

    &:hover {
      background: rgba(255, 255, 255, 0.12);
      color: #fff;
    }
    &.active {
      background: linear-gradient(135deg, #1e3a5f, #0d1b2a);
      border-color: transparent;
      color: #fff;
      box-shadow: 0 4px 12px rgba(30, 58, 95, 0.4);
    }
  }

  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
    margin-top: 10px;
    border: none;
    background: linear-gradient(135deg, #1e3a5f, #0d1b2a);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(30, 58, 95, 0.55);
    }
    &:active {
      transform: translateY(0);
    }
  }
}
</style>