export interface ApiResult<T> {
  result: T
  message: string
  code: number
  success: boolean
}
