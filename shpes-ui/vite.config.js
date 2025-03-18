import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue2'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue()
  ],
  publicDir: 'public',
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('proxy error', err)
          })
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('Sending Request:', req.method, req.url)
          })
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('Received Response:', proxyRes.statusCode, req.url)
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