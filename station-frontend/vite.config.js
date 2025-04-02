import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
    // {
    //   configureServer(server) {
    //     server.middlewares.use((_req, res, next) => {
    //       res.setHeader("X-Frame-Options", "ALLOW-FROM *");
    //       next();
    //     });
    //   }
    // }
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
