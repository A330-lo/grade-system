import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    children: [
      // 管理员路由
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', requiresAuth: true, roles: ['admin', 'teacher', 'student'] }
      },
      {
        path: '/department',
        name: 'Department',
        component: () => import('@/views/department/index.vue'),
        meta: { title: '系管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/major',
        name: 'Major',
        component: () => import('@/views/major/index.vue'),
        meta: { title: '专业管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/clazz',
        name: 'Clazz',
        component: () => import('@/views/clazz/index.vue'),
        meta: { title: '班级管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/student',
        name: 'Student',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学生管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/teacher',
        name: 'Teacher',
        component: () => import('@/views/teacher/index.vue'),
        meta: { title: '教师管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/course',
        name: 'Course',
        component: () => import('@/views/course/index.vue'),
        meta: { title: '课程管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/student-course',
        name: 'StudentCourse',
        component: () => import('@/views/student-course/index.vue'),
        meta: { title: '选课管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/course-teacher',
        name: 'CourseTeacher',
        component: () => import('@/views/course-teacher/index.vue'),
        meta: { title: '授课管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: '/score',
        name: 'Score',
        component: () => import('@/views/admin/score.vue'),
        meta: { title: '成绩管理', requiresAuth: true, roles: ['admin'] },
      },
      // 教师路由
      {
        path: '/teacher/courses',
        name: 'TeacherCourses',
        component: () => import('@/views/teacher/courses.vue'),
        meta: { title: '授课查询', requiresAuth: true, roles: ['teacher'] }
      },
      {
        path: '/teacher/score',
        name: 'TeacherScore',
        component: () => import('@/views/teacher/score.vue'),
        meta: { title: '成绩录入', requiresAuth: true, roles: ['teacher'] }
      },
      {
        path: '/student/my-courses',
        name: 'StudentMyCourses',
        component: () => import('@/views/student/my-courses.vue'),
        meta: { title: '我的课程', requiresAuth: true, roles: ['student'] }
      },
      {
        path: '/student/score',
        name: 'StudentScore',
        component: () => import('@/views/student/score.vue'),
        meta: { title: '成绩查询', requiresAuth: true, roles: ['student'] }
      },
      {
        path: '/student/profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/profile.vue'),
        meta: { title: '个人信息', requiresAuth: true, roles: ['student'] }
      },
      // 教师个人信息
      {
        path: '/teacher/profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/profile.vue'),
        meta: { title: '个人信息', requiresAuth: true, roles: ['teacher'] }
      },
      // 管理员个人信息
      {
        path: '/admin/profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/profile.vue'),
        meta: { title: '个人信息', requiresAuth: true, roles: ['admin'] }
      }
    ]
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', requiresAuth: false }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title + ' - 学生选课成绩管理系统'
  }

  // 不需要登录的页面
  if (!to.meta.requiresAuth) {
    next()
    return
  }

  // 需要登录的页面，检查是否已登录
  if (!userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 检查角色权限
  if (to.meta.roles && to.meta.roles.length > 0) {
    if (!to.meta.roles.includes(userStore.role)) {
      // 权限不足，跳转到首页或提示页
      next({ path: '/home' })
      return
    }
  }

  next()
})

export default router
