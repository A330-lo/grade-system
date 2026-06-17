import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
css: {
  preprocessorOptions: {
    scss: {
      api: 'modern-compiler'  // 使用现代 API
    }
  }
},
 build: {
   rollupOptions: {
     output: {
       manualChunks: {
         'element-plus': ['element-plus']
       }
     }
   }
 },
  server: {
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path
      }
    }
  }
})
