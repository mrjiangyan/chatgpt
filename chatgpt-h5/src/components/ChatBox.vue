<template>
  <div class="container">
    <div class="content-container" ref="contentContainer">
      <div ref="content">
        <InfiniteLoading direction="top" @infinite="loadMoreHistory">
          <template #no-more>
            <div class="message-prompt"></div>
          </template>
          <template #no-results>
            <div class="message-prompt"></div>
          </template>
        </InfiniteLoading>
        <div v-for="(message, index) in messages" :key="index">
          <div class="message-prompt" v-if="isShowTimes[index]">
            {{ accordingToNow(message.time) }}
          </div>
          <div
            class="message-cell"
            :style="{
              flexDirection:
                message.direction === 'received' ? 'row' : 'row-reverse'
            }"
          >
            <van-image
              width="32"
              height="32"
              :src="
                message.direction === 'received' ? targetAvatar : sourceAvatar
              "
            />
            <van-button
              type="default"
              size="small"
              round
              v-html="transformSpecialChars(message.text)"
            ></van-button>
          </div>
        </div>
      </div>
    </div>
    <div class="footer">
      <van-field v-model="typingText" placeholder="输入内容" border>
        <template #button>
          <van-image @click.enter="sendText" class="button" :src="chatImg" />
        </template>
      </van-field>
    </div>
  </div>
</template>

<script lang="ts">
import zhCode from "date-fns/locale/zh-CN";
import format from "date-fns/format";
import formatDistance from "date-fns/formatDistance";
import differenceInMinutes from "date-fns/differenceInMinutes";
import differenceInYears from "date-fns/differenceInYears";
import isSameDay from "date-fns/isSameDay";
import InfiniteLoading from "infinite-loading-vue3-ts";
import VanButton from "vant/lib/button";
import "vant/lib/button/style";
import VanImage from "vant/lib/image";
import "vant/lib/image/style";
import VanField from "vant/lib/field";
import "vant/lib/field/style";
import { computed, defineComponent, nextTick, ref, unref } from "vue";
// eslint-disable-next-line @typescript-eslint/no-var-requires
const chatImg = require("@/assets/icon/chat.svg");

export type Message = {
  text: string;
  time: Date;
  direction: "sent" | "received";
};
export default defineComponent({
  name: "Chat",
  components: {
    InfiniteLoading,
    "van-button": VanButton,
    "van-image": VanImage,
    "van-field": VanField
  },
  props: {
    sourceAvatar: String,
    targetAvatar: String,
    loadHistory: {
      type: Function,
      default: () => {
        return { messages: [], hasMore: false };
      }
    },
    sendMessage: {
      type: Function,
      default: ({ text }: Partial<Message>) => {
        return { text, direction: "sent" };
      }
    }
  },
  setup(props) {
    const messages = ref<Message[]>([]);
    const typingText = ref("");
    const scrolledToBottom = ref(false);
    const content = ref();
    const contentContainer = ref();
    function accordingToNow(date: string | number | Date) {
      date = date instanceof Date ? date : new Date(date);
      const now = new Date();
      if (differenceInMinutes(now, date) <= 30) {
        return formatDistance(new Date(date), now, {
          locale: zhCode,
          addSuffix: true
        });
      } else if (isSameDay(now, date)) {
        return format(date, "p", { locale: zhCode });
      } else if (differenceInYears(now, date) < 1) {
        return format(date, "MMM do p", { locale: zhCode });
      } else {
        return format(date, "PPP p", { locale: zhCode });
      }
    }
    const isShowTimes = computed(() => {
      let lastTime = new Date(0);
      return unref(messages).map((message: Message) => {
        const messageTime =
          message.time instanceof Date ? message.time : new Date(message.time);
        if (differenceInMinutes(messageTime, lastTime) > 10) {
          lastTime = messageTime;
          return true;
        } else {
          return false;
        }
      });
    });
    function scrollToBottom() {
      contentContainer.value.scrollTop = Number.MAX_SAFE_INTEGER;
    }

    function appendNew(...msgs: Message[]) {
      const newMessages: Message[] = msgs.map(message =>
        Object.assign({ direction: "received" }, message)
      );
      messages.value.push(...newMessages);
      nextTick(scrollToBottom);
    }

    function sendText() {
      const message = props.sendMessage({ text: unref(typingText) });
      typingText.value = "";
      if (message instanceof Promise) {
        message
          .then(message =>
            appendNew(
              Object.assign({ time: new Date(), direction: "sent" }, message)
            )
          )
          .catch(e => console.error("发送消息出错", e));
      } else {
        appendNew(
          Object.assign({ time: new Date(), direction: "sent" }, message)
        );
      }
    }
    function prependHistory(
      history: { messages: Message[]; hasMore: boolean },
      $state
    ) {
      const oldMessages = history.messages || [];
      // messages 以逆序排列
      messages.value.unshift(...oldMessages);
      history.hasMore ? $state.loaded() : $state.complete();
      nextTick(() => {
        // if (scrolledBarVisible.value) return;
        if (content.value.clientHeight > contentContainer.value.clientHeight) {
          scrollToBottom();
          scrolledToBottom.value = true;
        }
      });
    }
    function loadMoreHistory($state) {
      const history = props.loadHistory();
      if (history instanceof Promise) {
        history
          .then(history => {
            prependHistory(history, $state);
          })
          .catch(e => {
            console.error("加载历史消息出错", e);
          });
      } else {
        prependHistory(history, $state);
      }
    }

    function transformSpecialChars(text) {
      return text.replace(/^\n*|\n*$/, "").replaceAll("\n", "<br />");
    }
    return {
      messages,
      typingText,
      scrolledToBottom,
      content,
      contentContainer,
      accordingToNow,
      isShowTimes,
      sendText,
      prependHistory,
      loadMoreHistory,
      scrollToBottom,
      appendNew,
      transformSpecialChars,
      chatImg
    };
  }
});
</script>

<style lang="less" scoped>
/deep/ :root {
  --van-cell-line-height: 24px;
  --van-cell-vertical-padding: 8px;
}
.container {
  display: flex;
  flex-direction: column;
  height: 100%;
  .content-container {
    flex-grow: 1;
    overflow-y: auto;
  }
  .footer {
    height: 50px;
  }
}
.message-cell {
  display: flex;
  padding: 2px;
  margin-bottom: 2px;
  > * {
    margin: 2px;
  }
  .van-image {
    flex-shrink: 0;
  }

  .van-button {
    height: auto;
    border-radius: 8px;
    padding: 5px;
  }
}
.message-prompt {
  text-align: center;
  color: #969799;
  font-size: 14px;
  line-height: 24px;
}
.button {
  cursor: pointer;
}


</style>
