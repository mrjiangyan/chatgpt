
import { getToken } from '@/utils/cookie'

export const getHost = () => {
    return process.env.NODE_ENV === 'production' ? 'http://chat-service.touchbiz.tech:8080/api/' : 'http://127.0.0.1:8180/api/'
}
  

export const getHeaders = () => {
    const headers= {
        'Content-Type': 'application/json;charset=UTF-8',
        'X-Access-Token': ''
      }
    // 拦截携带token
    const token = getToken()
    if (token) {
      headers['X-Access-Token'] = `Bearer ${token}`
    }
    return headers
}