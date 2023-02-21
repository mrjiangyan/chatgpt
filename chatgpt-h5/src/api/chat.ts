import { CompletionResult, ChatSession } from '@/entities/chat'
import request from '@/utils/http/axios/request'
import { setCookie } from '@/utils/cookie'
import { SESSION_ID_KEY } from '@/configs/cacheEnum'

export const chat = (chat: any) => {
  return request<CompletionResult>({
    url: 'chatGpt/chatting',
    method: 'post',
    data: chat
  })
}

export const createSession = async () => {
  const res = await request<ChatSession>({
    url: 'chatGpt/chatting/session',
    method: 'post'
  })
  setCookie(SESSION_ID_KEY, res.sessionId, 1)
}
