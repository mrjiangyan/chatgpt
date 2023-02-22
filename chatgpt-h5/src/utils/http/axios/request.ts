import axios from "axios"
import { Toast } from "vant"
import store from "@/store"
import { getToken } from "@/utils/cookie"
// const baseURL = 'http://chat-service.touchbiz.tech:8080/api'
// const baseURL = 'http://127.0.0.1:8080/api'
const requestTimeout = 15000
const instance = axios.create({
  baseURL:
    process.env.NODE_ENV === "production"
      ? "http://chat-service.touchbiz.tech:8080/api"
      : "http://127.0.0.1:8180/api",
  timeout: requestTimeout,
  headers: {
    "Content-Type": "application/json;charset=UTF-8"
  }
})

instance.interceptors.request.use(
  config => {
    // 拦截携带token
    const token = getToken()
    if (token) {
      config.headers = {
        ...config.headers,
        "X-Access-Token": `Bearer ${token}`
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)
instance.interceptors.response.use(
  res => {
    if (res.status !== 200) {
      return Promise.reject(res)
    } else {
      const { success, code, result, message } = res.data
      if (success === true) {
        return result
      } else {
        // TODO错误提示
        Toast.fail({ message: message, duration: 1500 })
        console.log(res.data)
        return Promise.reject(res.data)
      }
    }
  },
  error => {
    const { response } = error
    if (response && response.data) {
      return Promise.reject(error)
    } else {
      return Promise.reject(error)
    }
  }
)

export default instance
