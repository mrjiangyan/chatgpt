export interface CompletionResult {
  id: string
  object: string
  created: number
  model: string
  choices: Array<CompletionChoice>
  usage: Usage
}
export interface CompletionChoice {
  text: string
  index: number
  logprobs: LogProbResult
  finish_reason: string
}

export interface Usage {
  promptTokens: number
  completionTokens: number
  totalTokens: number
}

export interface LogProbResult {
  tokens: Array<string>
  tokenLogprobs: Array<number>
  topLogprobs: Array<Map<string, number>>
  textOffset: Array<number>
}
