import { CompletionResult, ChatSession, ChatRequest } from '@/entities/chat'
import { setCookie, getCookie } from '@/utils/cookie'
import { SESSION_ID_KEY } from '@/configs/cacheEnum'
import { EventSourcePolyfill } from 'event-source-polyfill'
import { defHttp } from '@/utils/http/axios';

import { getHost , getHeaders} from '@/utils/http/axios/http'
// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
export const chat = (chat: unknown) => {
  return defHttp.post<CompletionResult>({
    url: 'chatGpt/chatting',
    method: 'post',
    params: chat
  })
}

// eslint-disable-next-line @typescript-eslint/explicit-function-return-type
export const createSession = async () => {
  const res = await defHttp.post<ChatSession>({
    url: 'chatGpt/chatting/session',
    method: 'post'
  })
  setCookie(SESSION_ID_KEY, res.sessionId, 1)
}


export const asyncChat = (chat: ChatRequest) => {
  chat.sessionId = getCookie(SESSION_ID_KEY) as string
  console.log(chat.sessionId)
  const url = getHost() + 'chatGpt/chatting/completion/?sessionId='
  + chat.sessionId + "&prompt=" + encodeURI(chat.prompt)
  const es = new EventSourcePolyfill(url, {
    headers: getHeaders()
  })
  es.onopen = function(event: any) {
    console.log('连接成功', event)
  }
  return es
}