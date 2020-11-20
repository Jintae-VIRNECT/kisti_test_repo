<template>
  <modal
    :visible.sync="visibleFlag"
    :dimClose="false"
    width="30.714em"
    class="reconnect"
  >
    <div class="reconnect__layout">
      <transition name="reconnect-logo">
        <div class="reconnect__header" v-show="state !== 'disconnected'">
          <img src="~assets/image/call/img_reconnect.svg" />
          <div class="reconnect__header-dot" ref="reconnectContainer"></div>
        </div>
      </transition>
      <div class="reconnect__body">
        <p class="reconnect__body-description">{{ description }}</p>
        <p class="reconnect__body-text" v-html="text">{{ text }}</p>
      </div>
      <div class="reconnect__footer">
        <button class="btn sub" @click="logout">
          종료
        </button>
        <button class="btn" :disabled="canReconnect" @click="reconnect">
          재연결
        </button>
      </div>
    </div>
  </modal>
</template>

<script>
import Lottie from 'lottie-web'
import * as animationData from 'assets/json/reconnect.lottie.json'
import Modal from 'Modal'

export default {
  name: 'ReconnectModal',
  components: {
    Modal,
  },
  data() {
    return {
      visibleFlag: false,
      state: 'disconnected', // disconnected, network-connect, room-connect, cancel
      lottie: null,
    }
  },
  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
  },
  computed: {
    text() {
      if (this.state === 'disconnected') {
        return '네트워크/서버 문제로 연결이 종료되었습니다. <br>재입장을 하시겠습니까?​'
      }
      if (this.state === 'cancel') {
        return '협업 재연결이 취소 되었습니다.'
      }
      return '30초'
    },
    description() {
      if (this.state === 'network-connect') {
        return '네트워크 연결 중…'
      }
      if (this.state === 'room-connect') {
        return '협업 재연결 중…'
      }
      return ''
    },
    canReconnect() {
      if (this.state === 'disconnected' || this.state === 'cancel') {
        return true
      } else {
        return false
      }
    },
  },

  watch: {
    visible(flag) {
      this.visibleFlag = !!flag
    },
  },
  methods: {
    init() {
      const container = this.$refs['reconnectContainer']
      this.$nextTick(() => {
        this.lottie = Lottie.loadAnimation({
          animationData,
          loop: true,
          autoplay: false,
          container,
        })
      })
    },
    reconnect() {
      if (this.state === 'disconnected') {
        this.state = 'network-connect'
        this.lottie.play()
      } else if (this.state === 'network-connect') {
        this.state = 'room-connect'
      } else if (this.state === 'room-connect') {
        this.lottie.stop()
        this.state = 'cancel'
      } else if (this.state === 'cancel') {
        this.state = 'disconnected'
      }
    },
    logout() {
      this.$eventBus.$emit('call:logout')
    },
  },
  mounted() {
    this.init()
  },
}
</script>

<style>
.reconnect-logo-enter-active,
.reconnect-logo-leave-active {
  transition: all ease 0.5s;
}
.reconnect-logo-enter,
.reconnect-logo-leave-to {
  max-height: 2.071em;
  opacity: 0;
}
.reconnect-logo-enter-to,
.reconnect-logo-leave {
  max-height: 9.643em;
  opacity: 100%;
}
</style>
<style lang="scss" src="assets/style/service/service-reconnect.scss"></style>
