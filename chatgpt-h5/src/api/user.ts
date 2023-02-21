import { LoginResult, UserInfo } from '@/entities/user'
import request from '@/utils/http/axios/request'
import { delToken, removeCookie, setCookie } from '@/utils/cookie'
import { USER_INFO_KEY } from '@/configs/cacheEnum'

export const login = (loginParam: any) => {
  return request<LoginResult>({
    url: 'chatGpt/login',
    method: 'post',
    data: loginParam
  })
}

export const getCodeInfo = (key: string) => {
  return request<string>({
    url: 'chatGpt/randomImage/' + key,
    method: 'get'
  })
}

export const logout = () => {
  request<string>({
    url: 'chatGpt/logout/',
    method: 'post'
  }).finally(() => {
    delToken()
  })
}

export const userInfo = () => {
  return request<UserInfo>({
    url: 'api/chatGpt/user/',
    method: 'get'
  }).then(res => {
    if (res === null) {
      removeCookie(USER_INFO_KEY)
      return null
    } else {
      setCookie(USER_INFO_KEY, JSON.stringify(res), 7)
      return res
    }
  })
}
