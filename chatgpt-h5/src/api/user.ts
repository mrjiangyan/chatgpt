import { LoginResult, UserInfo } from '@/entities/user'
import request from '@/utils/http/axios/request'
import { delToken, removeCookie, setCookie } from '@/utils/cookie'
import { USER_INFO_KEY } from '@/configs/cacheEnum'
import { defHttp } from '@/utils/http/axios';

export const login = (loginParam: any) => {
  return defHttp.post<LoginResult>({
    url: 'chatGpt/login',
    params: loginParam
  })
}

export const getCodeInfo = (key: string) => {
  return defHttp.get<string>({
    url: 'chatGpt/randomImage/' + key,
   
  })
}

export const logout = () => {
  defHttp.post<string>({
    url: 'chatGpt/logout/'
  }).finally(() => {
    delToken()
    window.location.reload()
  })
}

export const userInfo = () => {
  return defHttp.get<UserInfo>({
    url: 'chatGpt/user/'
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
