import { LoginResult } from '@/entities/user'
import request from '@/utils/http/axios/request'

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
