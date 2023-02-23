import { CompletionResult, ChatSession } from '@/entities/chat'
import request from '@/utils/http/axios/request'
import { setCookie } from '@/utils/cookie'
import { SESSION_ID_KEY } from '@/configs/cacheEnum'

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
export const chat = (chat: unknown) => {
  return request<CompletionResult>({
    url: 'chatGpt/chatting',
    method: 'post',
    data: chat
  })
}

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
export const createSession = async () => {
  const res = await request<ChatSession>({
    url: 'chatGpt/chatting/session',
    method: 'post'
  })
  setCookie(SESSION_ID_KEY, res.sessionId, 1)
}
