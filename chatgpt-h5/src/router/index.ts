import { useRandomName } from "./../utils/utils"

import {
  createRouter,
  createWebHistory,
  Router,
  RouteRecordRaw
} from "vue-router"
import store from "../store"

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "LAYOUT",
    redirect: "/home",
    component: () => import("@/views/layout/index.vue"),
    children: [
      {
        path: "home",
        name: "HOME",
        component: () => import("@/views/Home.vue")
      },
      {
        path: "mine",
        name: "Mine",
        component: () => import("@/views/mine/index.vue")
      }
    ]
  },
  {
    path: "/login",
    name: "login",
    component: () =>
      import(/* webpackChunkName: "SHOP" */ "../views/login/index.vue")
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

function RouterStack(router: Router) {
  // const stack = [];
  router.afterEach((to, from) => {
    if (!store.state.user.loginUser) {
      store.commit("setUserInfo", useRandomName())
    }

    console.log(to, from)
  })
  return router
}

RouterStack(router)

export default router
