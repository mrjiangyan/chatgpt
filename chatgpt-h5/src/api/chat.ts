import { ApiResult } from '@/entities/result'
import { CompletionResult } from '@/entities/chat'
import request from '@/utils/http/axios/request'
export const chat = (chat: any) => {
  return request<ApiResult<CompletionResult>>({
    url: 'chatGpt/chatting',
    method: 'post',
    data: chat
  })
}
interface ApiType {
  '/couse/buy': {
    id: number
  }
}
