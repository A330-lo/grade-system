<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <el-icon size="28"><Histogram/></el-icon>
        <span class="logo-text">学生课程成绩系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="menu"
        background-color="transparent"
        text-color="#8899b4"
        active-text-color="#5cb8ff"
        router
        :collapse="false"
      >
        <template v-for="item in menuItems" :key="item.path">
          <el-menu-item v-if="!item.children" :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
          <el-sub-menu v-else :index="item.path">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" class="avatar">
                {{ userStore.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.realName || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>

  <!-- 修改密码对话框 -->
  <el-dialog
    v-model="passwordDialogVisible"
    title="修改密码"
    width="400px"
    @close="resetPasswordForm"
  >
    <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="80px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Reading,
  ArrowDown,
  User,
  Lock,
  SwitchButton,
  HomeFilled,
  OfficeBuilding,
  UserFilled,
  Tickets,
  EditPen,
  TrendCharts,
  Notebook,
  Collection,
  Grid,
  Histogram
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const passwordDialogVisible = ref(false)
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => route.meta.title || '首页')

// 根据角色生成菜单
const menuItems = computed(() => {
  const role = userStore.role
  const menus = []

  if (role === 'admin') {
    menus.push(
      { path: '/home', title: '首页', icon: 'HomeFilled' },
      { path: '/score', title: '成绩管理', icon: 'TrendCharts' },
      {
        path: '/system',
        title: '部门管理',
        icon: 'OfficeBuilding',
        children: [
          { path: '/department', title: '学院管理' },
          { path: '/major', title: '专业管理' },
          { path: '/clazz', title: '班级管理' }
        ]
      },
      {
        path: '/user',
        title: '用户管理',
        icon: 'UserFilled',
        children: [
          { path: '/student', title: '学生管理' },
          { path: '/teacher', title: '教师管理' },
        ]
      },
      {
        path: '/course-manage',
        title: '课程管理',
        icon: 'Reading',
        children: [
          { path: '/course', title: '课程管理' },
          { path: '/student-course', title: '选课管理' }
        ]
      },
    )
  } else if (role === 'teacher') {
    menus.push(
      { path: '/home', title: '首页', icon: 'HomeFilled' },
      { path: '/teacher/courses', title: '授课查询', icon: 'Notebook' },
      { path: '/teacher/score', title: '成绩录入', icon: 'EditPen' },
      { path: '/teacher/profile', title: '个人信息', icon: 'User' }
    )
  } else if (role === 'student') {
    menus.push(
      { path: '/home', title: '首页', icon: 'HomeFilled' },
      { path: '/student/my-courses', title: '我的课程', icon: 'Collection' },
      { path: '/student/score', title: '成绩查询', icon: 'TrendCharts' },
      { path: '/student/profile', title: '个人信息', icon: 'User' }
    )
  }

  return menus
})

const handleCommand = (command) => {
  if (command === 'profile') {
    const role = userStore.role
    const profilePath = `/${role}/profile`
    router.push(profilePath)
  } else if (command === 'password') {
    passwordDialogVisible.value = true
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('退出登录成功')
    }).catch(() => {})
  }
}

const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    const role = userStore.role
    let updatePassword
    
    if (role === 'student') {
      const { updateStudentPassword } = await import('@/api/student')
      updatePassword = updateStudentPassword
    } else if (role === 'teacher') {
      const { updateTeacherPassword } = await import('@/api/teacher')
      updatePassword = updateTeacherPassword
    } else {
      const { updateAdminPassword } = await import('@/api/admin')
      updatePassword = updateAdminPassword
    }
    
    await updatePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
    
    // 修改密码后退出登录
    userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('修改密码失败', error)
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100%;
}

.sidebar {
  background: linear-gradient(180deg, #0b1322 0%, #111d32 100%);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 24px rgba(0, 0, 0, 0.3);

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: #e8f0fe;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid rgba(92, 184, 255, 0.12);
    text-shadow: 0 0 20px rgba(92, 184, 255, 0.3);
    letter-spacing: 0.05em;

    .logo-text {
      white-space: nowrap;
      background: linear-gradient(135deg, #e8f0fe, #5cb8ff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .menu {
    flex: 1;
    border-right: none;
    padding: 8px 0;
  }

  :deep(.el-menu-item) {
    margin: 2px 8px;
    border-radius: 8px;
    transition: all 0.25s ease;

    &:hover {
      background-color: rgba(92, 184, 255, 0.08) !important;
    }

    &.is-active {
      background: linear-gradient(135deg, rgba(64, 158, 255, 0.15), rgba(92, 184, 255, 0.08)) !important;
      border-left: 3px solid #5cb8ff;
      box-shadow: 0 0 16px rgba(92, 184, 255, 0.1) inset;
    }
  }

  :deep(.el-sub-menu__title) {
    margin: 2px 8px;
    border-radius: 8px;
    transition: all 0.25s ease;

    &:hover {
      background-color: rgba(92, 184, 255, 0.08) !important;
    }
  }

  :deep(.el-sub-menu.is-active .el-sub-menu__title) {
    color: #5cb8ff !important;
  }

  :deep(.el-menu--inline) {
    background: rgba(0, 0, 0, 0.15) !important;

    .el-menu-item {
      padding-left: 56px !important;
    }
  }
}

.header {
  background-color: #dce3ea;
  border-bottom: 1px solid #cdd4dd;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;
      color: #334155;

      .avatar {
        background: linear-gradient(135deg, #409eff, #5cb8ff);
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
      }

      .username {
        font-size: 14px;
      }
    }
  }
}

.main {
  background-color: #f0f4f8;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>