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

function test<T extends keyof ApiType>(url: T, obj: ApiType[T]) {
  console.log(url, obj)
}
test('/couse/buy', { id: 1 })
