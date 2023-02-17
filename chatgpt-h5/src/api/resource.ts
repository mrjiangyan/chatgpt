import { ResourceOption } from '@/entities/resource'
import request from '@/utils/http/axios/request'
export const getResouceList = () => {
  return request<ResourceOption[]>({
    url: 'api/resource/list',
    method: 'get'
  })
}
interface ApiType {
  '/couse/buy': {
    id: number
  }
}
