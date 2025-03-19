import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue2'
import { resolve } from 'path'

// 后端服务地址
const target = 'http://localhost:8080'

export default defineConfig({
  plugins: [
    vue()
  ],
  publicDir: 'public',
  server: {
    port: 3000,
    proxy: {
      '^/api': {
        target,
        changeOrigin: true,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('proxy error', err)
          })
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Sending Request to:', target + req.url)
          })
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('Received Response from:', target + req.url, proxyRes.statusCode)
          })
        }
      }
    },
    open: true,
    historyApiFallback: true
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    },
    extensions: ['.js', '.vue', '.json']
  },
  css: {
    preprocessorOptions: {
      scss: {
        charset: false
      }
    }
  }
})