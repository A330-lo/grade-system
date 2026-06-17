import { defineStore } from 'pinia'
import { login, logout } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userId: localStorage.getItem('userId') || '',
    username: localStorage.getItem('username') || '',
    realName: localStorage.getItem('realName') || '',
    role: localStorage.getItem('role') || ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin',
    isTeacher: (state) => state.role === 'teacher',
    isStudent: (state) => state.role === 'student'
  },

  actions: {
    async login(username, password, role) {
      try {
        const res = await login({ username, password, role })
        this.token = res.token
        this.userId = res.userId
        this.username = res.username
        this.realName = res.realName
        this.role = res.role

        localStorage.setItem('token', res.token)
        localStorage.setItem('userId', res.userId)
        localStorage.setItem('username', res.username)
        localStorage.setItem('realName', res.realName)
        localStorage.setItem('role', res.role)

        return res
      } catch (error) {
        throw error
      }
    },

    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('登出失败', error)
      } finally {
        this.clearUserInfo()
      }
    },

    clearUserInfo() {
      this.token = ''
      this.userId = ''
      this.username = ''
      this.realName = ''
      this.role = ''

      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('realName')
      localStorage.removeItem('role')
    }
  }
})
