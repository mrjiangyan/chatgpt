<!-- eslint-disable @typescript-eslint/explicit-function-return-type -->
<!-- eslint-disable @typescript-eslint/explicit-function-return-type -->
<!-- eslint-disable @typescript-eslint/explicit-function-return-type -->
<template>
  <div class="home">
    <div
      v-if="false" 
      class="page_header"
    >
      <span class="page_title">首页</span>
      <div class="page_search">
        <input
          type="text"
          placeholder="点点看看吧😄..."
        >
      </div>
    </div>
    <div
      v-if="false"
      class="header_menu"
    >
      <div
        class="menu_item"
        @click="toDetail('/message')"
      >
        <div>
          <van-icon
            name="wechat"
            size="35"
            color="#52c41a"
          />
        </div>
        <span>朋友圈</span>
      </div>
      <div
        class="menu_item"
        @click="toDetail('/shop')"
      >
        <div>
          <van-icon
            name="cart-circle"
            size="35"
            color="#fa8c16"
          />
        </div>
        <span>购物车</span>
      </div>

      <div
        class="menu_item"
        @click="toDetail('/message')"
      >
        <div>
          <van-icon
            name="map-marked"
            size="35"
            color="#40a9ff"
          />
        </div>
        <span>地图</span>
      </div>
      <div
        class="menu_item"
        @click="toDetail('/video')"
      >
        <div>
          <van-icon
            name="music"
            size="35"
            color="#000000"
          />
        </div>
        <span>短视频</span>
      </div>
      <div
        class="menu_item"
        @click="toDetail('/vuex')"
      >
        <div>
          <van-icon
            name="fire"
            size="35"
            color="#ee0a24"
          />
        </div>
        <span>更多</span>
      </div>
    </div>

    <div class="page_prompt">
      <ChatBox
        ref="chatRef"
        :source-avatar="sourceAvatar"
        :target-avatar="targetAvatar"
        :send-message="sendMessage"
      />
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, ref, unref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { createSession, asyncChat } from '@/api/chat'
import { getCookie } from '@/utils/cookie'
import ChatBox, { Message } from '@/components/ChatBox.vue'
import { CompletionResult, ChatRequest } from '@/entities/chat'
import { ApiResult } from '@/entities/result'


// eslint-disable-next-line @typescript-eslint/no-var-requires
const targetAvatar = require('@/assets/icon/openai.svg')
// eslint-disable-next-line @typescript-eslint/no-var-requires
const sourceAvatar = require('@/assets/icon/my.webp')

export default defineComponent({
  name: 'HOME',
  components: {
    ChatBox
  },
  setup() {
    const router = useRouter()

    const lastAnswer = ref<Message>()

    const chatRef = ref()

    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    function asyncChatting(text: string) {
      const chatRequest = new ChatRequest()
      chatRequest.prompt = text
      const es = asyncChat(chatRequest)

      // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
      es.onmessage = function(event: { data: string }) {
        // to to something…
        console.log('接收信息', event.data)
        // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
        const result = <ApiResult<string>>JSON.parse(event.data)
        if(result.success ===  false || result.result === '[DONE]'){
          es.close()
          return
        }
        // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
        const res = <CompletionResult>JSON.parse(result.result) 
        const choice = res.choices[0]
        
        if(lastAnswer.value?.id === res.id){
          lastAnswer.value.text += choice.text.replace('\n\n', '\n')
          console.log('旧的文本内容替换', lastAnswer.value.text)
        }
        else{
          const answer: Message = {
            text: choice.text.replace('\n\n', '\n'),
            time: new Date(),
            id: res.id,
            direction: 'received'
          }
          lastAnswer.value = answer
          console.log('新的回答', lastAnswer.value.text)
          console.log('anwsers', answer)
          chatRef.value.appendNew(answer)
        }
       
      }

      es.onerror = function(error: ErrorEvent) {
        // 监听错误
        console.log('错误', error)
        es.close()
      }
    }
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    function sendMessage({ text }: Partial<Message>) {
      asyncChatting(text as string)
      return {
        text,
        time: new Date(),
        direction: 'sent'
      }
    }

    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    const toDetail = (path: string) => {
      router.push(path)
    }
    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    const toMessage = () => {
      router.push('/message')
    }

    // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
    const getSession = () => {
      createSession()
    }

    onMounted(() => {
      getSession()
    })

    return {
      getSession,
      toDetail,
      toMessage,
      lastAnswer,
      sourceAvatar,
      targetAvatar,
      sendMessage,
      chatRef,
      getCookie,
      asyncChatting
    }
  }
})
</script>
<style lang="less" scoped>
@import '@/theme/hairline';
.home {
  height: 100%;
  .page_header {
    padding: 12px;
    display: flex;
    align-items: center;
    .page_title {
      width: 50px;
      font-size: 18px;
      font-weight: bold;
    }
    .page_search {
      flex: 1;
      border-radius: 4px;
      input {
        width: 100%;
        border: none;
        background: #f5f5f5;
        padding: 6px;
        box-sizing: border-box;
        border-radius: 4px;
      }
    }
  }
  .header_menu {
    display: flex;
    justify-content: space-between;
    margin-top: 8px;
    margin-bottom: 8px;
    padding: 0 12px;
    .menu_item {
      text-align: center;
      span {
        font-size: 13px;
        display: inline-block;
        margin-top: 6px;
      }
    }
  }
  .hot_rank {
    padding: 0 12px;
    margin-top: 16px;
    .title {
      font-size: 16px;
      font-weight: bold;
    }
    .projects {
      margin-top: 12px;
      :deep(.van-swipe-item) {
        font-size: 20px;
        text-align: center;
        border-radius: 4px;

        img {
          width: 100%;
          height: 152px;
        }
        // background-color: #39a9ed !important;
      }
    }
  }

  .topic_tab {
    padding-left: 12px;

    .topic_box {
      .swipe-item {
        position: relative;
        &::after {
          .hairline-bottom(@border-color);
        }
        .star-button {
          height: 100%;
        }
      }
    }
  }
}

.page_prompt {
  border-radius: 4px;
  width: 100%;
  height: 100%;
  border: none;
  background: #f5f5f5;
  padding: 12px;
  box-sizing: border-box;
  top: 0px;
  text-align: center;

  .btn {
    margin-top: 6px;
  }
}
</style>
