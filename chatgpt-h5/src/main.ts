import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { vantPlugins } from './plugins/vant'
import { globalPlugins } from './plugins/global'
// import { setupWechatAuth } from '@/configs/wechatAuth'
import './assets/style/reset.less'
import './assets/style/global.less'
import 'vant/es/toast/style'
import { Toast } from 'vant'
import { getToken, delToken } from '@/utils/cookie'

const app = createApp(App)
app.config.globalProperties.$toast = Toast

app
  .use(getToken)
  .use(delToken)
  .use(Toast)
  .use(store)
  .use(router)
  .use(globalPlugins)
  .use(vantPlugins)
  .mount('#app')
