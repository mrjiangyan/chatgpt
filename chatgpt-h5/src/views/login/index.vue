<template>
  <div class="home">
    <van-form @submit="handleLogin" @onFailed="onFailed">
      <van-cell-group inset>
        <van-field
          v-model="formData.username"
          placeholder="用户名"
          :rules="[{ required: true, message: '请填写用户名' }]"
        />
        <van-field
          v-model="formData.password"
          type="password"
          placeholder="密码"
          :rules="[{ required: true, message: '请填写密码' }]"
        />
        <div>
          <van-field
            v-model="formData.captcha"
            type="text"
            placeholder="验证码"
            style="max-width:200px"
            :rules="[{ required: true, message: '请填写验证码' }]"
          />
          <div :style="{ 'text-align': 'right', 'margin-left': '20px' }" class="enter-x">
            <img
              v-if="randCodeData.requestCodeSuccess"
              style="margin-top: 2px; max-width: initial"
              :src="randCodeData.randCodeImage"
              @click="handleChangeCheckCode"
            />
            <img
              v-else
              style="margin-top: 2px; max-width: initial"
              src="../../assets/images/checkcode.png"
              @click="handleChangeCheckCode"
            />
          </div>
        </div>
      </van-cell-group>
      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit">
          提交
        </van-button>
      </div>
    </van-form>
  </div>
</template>
<script lang="ts">
import { defineComponent, reactive, toRefs, ref, unref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login, getCodeInfo } from '@/api/user'
import { Toast } from 'vant'
import { setToken, setCookie } from '@/utils/cookie'

export default defineComponent({
  name: 'HOME',
  components: {},
  setup() {
    const router = useRouter()
    const formData = reactive({
      username: '',
      password: '',
      captcha: ''
    })
    const randCodeData = reactive({
      randCodeImage: '',
      requestCodeSuccess: false,
      checkKey: ''
    })

    const onFailed = errorInfo => {
      console.log('failed', errorInfo)
    }

    function handleChangeCheckCode() {
      formData.captcha = ''
      //TODO 兼容mock和接口，暂时这样处理
      randCodeData.checkKey = new Date().getTime() + ''
      getCodeInfo(randCodeData.checkKey).then(res => {
        randCodeData.randCodeImage = res
        randCodeData.requestCodeSuccess = true
      })
    }
    function handleLogin() {
      Toast.loading('登录中')
      login(formData)
        .then(res => {
          Toast.clear()
          //保存token以及用户信息
          setToken(res.token, 1)
          setCookie('userInfo', JSON.stringify(res.userInfo), 7)
          router.push('/home')
        })
        .catch(err => {
          console.log(err)
        })
      //update-begin-author:taoyan date:2022-5-3 for: issues/41 登录页面，当输入验证码错误时，验证码图片要刷新一下，而不是保持旧的验证码图片不变
      handleChangeCheckCode()
    }

    onMounted(() => {
      handleChangeCheckCode()
    })

    return {
      handleLogin,
      onFailed,
      formData,
      randCodeData,
      handleChangeCheckCode
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
