<template>
  <div class="mine-layout">
    <section class="mine-header">
      <div class="header-circle">
        <img
          src="../../assets/icon/chat.svg"
          class="header-img"
        >
      </div>

      <div
        v-if="user == null"
        class="login-regist"
      >
        <router-link
          to="/login"
          class="order-item"
        >
          登录
        </router-link>
        <!-- <router-link to="/register/phoneRegister" class="order-item" tag="span">/注册</router-link> -->
      </div>
      <ul class="user-info">
        <li class="user-name" />
        <li class="user-name">
          {{ user != null ? user.memberLevel : '' }}
        </li>
      </ul>
    </section>
    <section class="my-info">
      <ul class="info-list">
        <li class="info-item">
          <b>{{ user != null ? 0 : 0 }}</b>
          <span>提问次数</span>
        </li>
        <!-- <li class="info-item">
          <b>09</b>
          <span>店铺关注</span>
        </li> -->
        <li class="info-item">
          <b>{{ user != null ? user.sessionCount : 0 }}</b>
          <span>会话数</span>
        </li>
      </ul>
    </section>

    <section class="mine-content">
      <ul class="options-list">
        <!-- <router-link to="/node/nodeApplication" class="option-item">
          <div class="item-info">
            <svg-icon class="incon" icon-class="node-application"></svg-icon>
            <span>节点申请</span>
          </div>
          <van-icon name="arrow" color="#DBDBDB" />
        </router-link> -->
        <!-- <router-link to="/wallet/myWallet" class="option-item">
          <div class="item-info">
            <svg-icon class="incon" icon-class="businessmen-stationed"></svg-icon>
            <span>商家入驻</span>
          </div>
          <van-icon name="arrow" color="#DBDBDB" />
        </router-link> -->
        <router-link
          to="/wallet/myWallet"
          class="option-item"
        >
          <div class="item-info">
            <svg-icon
              class="incon"
              icon-class="sharing-links"
            />
            <span>分享链接</span>
          </div>
          <van-icon
            name="arrow"
            color="#DBDBDB"
          />
        </router-link>

        <li
          v-if="getToken() !== false"
          class="option-item"
          @click="clickLogout"
        >
          <div class="item-info">
            <svg-icon
              class="incon"
              icon-class="my-assets"
            />
            <span>退出登录</span>
          </div>
          <van-icon
            name="arrow"
            color="#DBDBDB"
          />
        </li>
      </ul>
    </section>

    <section
      v-if="false"
      class="mine-content"
    >
      <ul class="options-list">
        <router-link
          to="/mine/shippingAddress"
          class="option-item"
        >
          <div class="item-info">
            <svg-icon
              class="incon"
              icon-class="shipping-address"
            />
            <span>收货地址</span>
          </div>
          <van-icon
            name="arrow"
            color="#DBDBDB"
          />
        </router-link>
        <router-link
          to="/mine/messageCenter"
          class="option-item"
        >
          <div class="item-info">
            <svg-icon
              class="incon"
              icon-class="message-center"
            />
            <span>消息中心</span>
          </div>
          <van-icon
            color="#DBDBDB"
            name="arrow"
          />
        </router-link>
        <router-link
          to="/mine/helpCenter"
          class="option-item"
        >
          <div class="item-info">
            <svg-icon
              class="incon"
              icon-class="help-center"
            />
            <span>帮助中心</span>
          </div>
          <van-icon
            color="#DBDBDB"
            name="arrow"
          />
        </router-link>
        <router-link
          to="/mine/setting"
          class="option-item"
        >
          <div class="item-info">
            <svg-icon
              class="incon"
              icon-class="setting"
            />
            <span>设置</span>
          </div>
          <van-icon
            color="#DBDBDB"
            name="arrow"
          />
        </router-link>
      </ul>
    </section>
    <tabbar />
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue'
import { logout, userInfo } from '@/api/user'
import { getToken } from '@/utils/cookie'
import { UserInfo } from '@/entities/user'
export default {
  name: 'Index',
  setup() {
    const user = ref<UserInfo | null>()
    const clickLogout = () => {
      logout()
    }

    const getUserInfo = () => {
      userInfo().then(res => {
        user.value = res
      })
    }
    onMounted(() => {
      getUserInfo()
    })

    return { clickLogout, getToken, getUserInfo, user }
  }
}
</script>

<style lang="less" scoped>
.mine-layout {
  padding: 30px 16px;
  min-height: 812px;
  padding-bottom: 50px;

  background: linear-gradient(gray, #ff9351);
  .mine-header {
    display: flex;
    padding-left: 24px;
    justify-content: flex-start;
    align-items: center;
    flex-direction: row;
    padding-bottom: 20px;
    .header-circle {
      width: 60px;
      height: 60px;
      background-color: #fff;
      border-radius: 100%;
      overflow: hidden;
      .header-img {
        margin: 12px;
        height: 36px;
      }
    }

    .user-info {
      padding-left: 16px;
      font-size: 15px;
      color: #fff;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: flex-start;
      .node-info {
        padding-top: 5px;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        .sharing-node {
          padding-left: 18px;
          display: flex;
          justify-content: center;
          align-items: center;
          width: 78px;
          height: 25px;
          font-size: 11px;
          text-align: center;
          background: url('../../assets/images/product/sharing-node.png')
            no-repeat center center;
          background-size: 100% 100%;
          border-radius: 11px 11px;
        }
        .business-node {
          margin-left: 5px;
          display: flex;
          justify-content: center;
          align-items: center;
          width: 60px;
          height: 22px;
          font-size: 11px;
          text-align: center;
          background-color: #71c0f6;
          border-radius: 11px 11px;
        }
      }
    }

    .login-regist {
      font-size: 15px;
      color: #fff;
      padding-left: 16px;
    }
  }
  .my-info {
    font-size: 13px;
    color: #fff;
    padding-bottom: 16px;
    .info-list {
      display: flex;
      justify-content: space-around;
      align-items: center;
      .info-item {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        b {
          font-size: 16px;
        }
      }
    }
  }
  .order-all {
    box-shadow: 0 5px 15px 0 rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    background-color: #fff;
    border-radius: 8px;
    .look-orders {
      font-size: 11px;
      font-weight: 600;
      color: #3a3a3a;
      padding-left: 19px;
      padding-top: 14px;
      padding-bottom: 18px;
    }
    .order-list {
      padding-bottom: 18px;
      display: flex;
      justify-content: space-around;
      align-items: center;
      .order-item {
        font-size: 14px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        span {
          padding-top: 5px;
          font-weight: 600;
        }
      }
    }
  }
  .mine-content {
    box-shadow: 0 5px 15px 0 rgba(0, 0, 0, 0.1);
    background-color: #fff;
    border-radius: 8px;
    padding: 0 16px;
    margin-top: 18px;
    .options-list {
      padding-top: 20px;
      .option-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 16px;
        padding-bottom: 22px;
        .item-info {
          display: flex;
          justify-content: center;
          align-items: center;
          font-size: 15px;
          color: #888;
          .incon {
            padding-right: 16px;
          }
          .header-img {
            width: 50px;
            height: 50px;
            padding-right: 16px;
          }
        }
      }
    }
  }
  .node-dialog {
    .dialog-header {
      text-align: center;
    }
    .my-node-data {
      padding: 10px 40px;
      display: flex;
      justify-content: space-around;
      align-items: center;
      flex-wrap: wrap;
      .data-item {
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        margin-left: 10px;
        margin-bottom: 20px;
        .node-text {
          font-size: 13px;
          padding-top: 5px;
          color: #3a3a3a;
        }
      }
    }
    .node-bottom {
      position: relative;
      .know-btn {
        display: flex;
        justify-content: center;
        align-items: center;
        margin: auto;
        line-height: 40px;
        width: 154px;
        border-radius: 20px 20px;
        background-color: #fce14b;
        margin-bottom: 30px;
      }
      .gray-img {
        position: absolute;
        right: 12px;
        top: 0;
      }
    }
  }
}
</style>
