<template>
  <div class="home">
    <div v-if="false" class="page_header">
      <span class="page_title">首页</span>
      <div class="page_search">
        <input type="text" placeholder="点点看看吧😄..." />
      </div>
    </div>
    <div v-if="false" class="header_menu">
      <div class="menu_item" @click="toDetail('/message')">
        <div>
          <van-icon name="wechat" size="35" color="#52c41a" />
        </div>
        <span>朋友圈</span>
      </div>
      <div class="menu_item" @click="toDetail('/shop')">
        <div>
          <van-icon name="cart-circle" size="35" color="#fa8c16" />
        </div>
        <span>购物车</span>
      </div>

      <div class="menu_item" @click="toDetail('/message')">
        <div>
          <van-icon name="map-marked" size="35" color="#40a9ff" />
        </div>
        <span>地图</span>
      </div>
      <div class="menu_item" @click="toDetail('/video')">
        <div>
          <van-icon name="music" size="35" color="#000000" />
        </div>
        <span>短视频</span>
      </div>
      <div class="menu_item" @click="toDetail('/vuex')">
        <div>
          <van-icon name="fire" size="35" color="#ee0a24" />
        </div>
        <span>更多</span>
      </div>
    </div>
    <!-- <div v-if="false" class="hot_rank">
      <div class="title">今日热榜</div>
      <div>
        <van-swipe class="projects" :loop="false" :width="300">
          <van-swipe-item v-for="(img, index) in banners" :key="index">
            <img
              :src="img"
              @click="showImg(banners, { startPosition: index })"
              alt=""
            />
          </van-swipe-item>
        </van-swipe>
      </div>
    </div> -->
    <div v-if="false" class="hot_topic">
      <van-tabs v-model:active="activeTopic" sticky class="topic_tab my-tab" color="#85a5ff">
        <van-tab title="发现">
          <div class="topic_box">
            <van-swipe-cell class="swipe-item" v-for="(item, index) in list" :key="index">
              <Recommend :data="item" />
              <template #right>
                <van-button
                  @click="toCollectResource(item)"
                  square
                  :text="handleItemIsSelect(item) ? '关注' : '取消'"
                  type="danger"
                  color="#85a5ff"
                  class="star-button"
                />
              </template>
            </van-swipe-cell>
          </div>
        </van-tab>
        <van-tab title="关注">
          <div class="topic_box">
            <van-empty v-if="collection.length == 0" description="发现列表向左滑动每一项来关注😄" />
            <van-swipe-cell v-else class="swipe-item" v-for="(item, index) in collection" :key="'collection' + index">
              <Recommend :data="item" />
              <template #right>
                <van-button
                  @click="toCollectResource(item)"
                  square
                  :text="handleItemIsSelect(item) ? '关注' : '取消'"
                  color="#85a5ff"
                  class="star-button"
                />
              </template>
            </van-swipe-cell>
          </div>
        </van-tab>
      </van-tabs>
    </div>

    <div class="page_prompt">
      <ChatBox
        ref="chatRef"
        :sourceAvatar="sourceAvatar"
        :targetAvatar="targetAvatar"
        :loadHistory="loadHistory"
        :sendMessage="sendMessage"
      />
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs, ref, unref, onMounted } from 'vue'
import { Notify, Toast } from 'vant'
import { useRouter } from 'vue-router'
import { getResouceList } from '@/api/resource'
import { ResourceOption } from '@/entities/resource'

import { chat } from '@/api/chat'
import { menus, resource } from '@/mock/data'
import Recommend from '@/components/Recommend.vue'
import { showImg } from '@/utils/utils'
import ChatBox, { Message } from '@/components/ChatBox.vue'

// eslint-disable-next-line @typescript-eslint/no-var-requires
const targetAvatar = require('@/assets/icon/openai.svg')
// eslint-disable-next-line @typescript-eslint/no-var-requires
const sourceAvatar = require('@/assets/icon/my.webp')

export default defineComponent({
  name: 'HOME',
  components: {
    Recommend,
    ChatBox
  },
  setup() {
    const router = useRouter()
    const state: {
      list: ResourceOption[]
      collection: ResourceOption[]
    } = reactive({
      list: [],
      collection: []
    })

    const activeTopic = ref(0)

    const prompt = ref<Message[]>([])

    const content = ref('')

    const chatRef = ref()

    function loadHistory() {
      return {
        // 消息数据，字段如下，应以时间的倒序给出。
        messages: unref(prompt),
        // 定义是否还有历史消息，如果为 false，将停止加载。读者可将其改为 true 演示一下自动滚动更新的效果。
        hasMore: false
      }
    }

    function getChatAnswer() {
      console.log(prompt)
      const param = {
        prompt: unref(prompt)
          .map(v => v.text)
          .join('\n\n')
      }
      chat(param)
        .then(res => {
          console.log(res)
          if (res.success) {
            const anwsers = res.result.choices.map(
              (choice): Message => ({
                text: choice.text.replace('\n\n', '\n'),
                time: new Date(),
                direction: 'received'
              })
            )
            console.log('anwsers', anwsers)
            prompt.value.push(...anwsers)
            chatRef.value.appendNew(...anwsers)
          } else {
            Toast.fail(res.message)
          }

          // state.list = result;
        })
        .catch()
    }

    function sendMessage({ text }: Partial<Message>) {
      prompt.value.push({
        text: text as string,
        time: new Date(),
        direction: 'sent'
      })
      getChatAnswer()
      return {
        text,
        time: new Date(),
        direction: 'sent'
      }
    }

    const toDetail = (path: string) => {
      router.push(path)
    }
    const toMessage = () => {
      router.push('/message')
    }

    const getData = () => {
      getResouceList()
        .then(() => {
          // state.list = result;
        })
        .catch()
    }

    function toCollectResource(resource: ResourceOption) {
      Notify({
        color: '#ffffff',
        background: '#85a5ff',
        message: '关注成功！'
      })
      const index = state.collection.findIndex(item => item.title === resource.title)
      if (index === -1) {
        state.collection.push(resource)
      } else {
        state.collection.splice(index, 1)
      }
    }

    function handleItemIsSelect(resource: ResourceOption) {
      const index = state.collection.findIndex(item => item.title === resource.title)
      return index === -1
    }

    onMounted(() => {
      state.list = resource
      getData()
    })

    return {
      ...toRefs(state),
      menus,
      toDetail,
      toMessage,
      content,
      toCollectResource,
      handleItemIsSelect,
      activeTopic,
      prompt,
      showImg,
      sourceAvatar,
      targetAvatar,
      loadHistory,
      sendMessage,
      chatRef
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
