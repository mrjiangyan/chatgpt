export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export interface UserInfo {
  realname: string
  avatar: string
  birthday: string
  sessionCount: number
  memberLevel: string
}
