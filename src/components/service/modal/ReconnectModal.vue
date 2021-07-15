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
        <button class="btn sub" @click="cancel">
          {{ cancelText }}
        </button>
        <button
          class="btn"
          :disabled="!canReconnect"
          @click="setConnectionState('network-connect')"
        >
          {{ $t('button.reconnect') }}
        </button>
      </div>
    </div>
  </modal>
</template>

<script>
import Lottie from 'lottie-web'
import * as animationData from 'assets/json/reconnect.lottie.json'
import Modal from 'Modal'
import { mapGetters } from 'vuex'
import { getRoomInfo } from 'api/http/room'
import roomMixin from 'mixins/room'
import { checkOnline } from 'utils/network'
import { ROLE } from 'configs/remote.config'
import toastMixin from 'mixins/toast'

export default {
  name: 'ReconnectModal',
  components: {
    Modal,
  },
  mixins: [roomMixin, toastMixin],
  data() {
    return {
      visibleFlag: false,
      state: 'disconnected', // disconnected, network-connect, room-connect, cancel
      lottie: null,

      duration: 30,
      startTime: null,
      timer: null,
      retry: 0,
    }
  },
  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
  },
  computed: {
    ...mapGetters(['roomInfo']),
    cancelText() {
      if (this.state === 'disconnected' || this.state === 'cancel') {
        return this.$t('button.exit')
      }
      return this.$t('button.cancel')
    },
    text() {
      if (this.state === 'disconnected') {
        return this.$t('service.reconnect_disconnected')
      }
      if (this.state === 'cancel') {
        return this.$t('service.reconnect_reconnect_cancel')
      }
      return this.duration + this.$t('date.second')
    },
    description() {
      if (this.state === 'network-connect') {
        return this.$t('service.reconnect_network_connecting')
      }
      if (this.state === 'room-connect') {
        return this.$t('service.reconnect_room_connecting')
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
      if (flag) {
        this.$eventBus.$emit('map:close')
      }
    },
  },
  methods: {
    lottieInit() {
      const container = this.$refs['reconnectContainer']
      this.$nextTick(() => {
        if (!this.lottie) {
          this.lottie = Lottie.loadAnimation({
            animationData,
            loop: true,
            autoplay: false,
            container,
          })
          this.lottie.addEventListener('DOMLoaded', () => {
            this.lottie.play()
          })
        } else {
          this.lottie.play()
        }
      })
    },
    async setConnectionState(connect) {
      if (this.state === connect) return
      if (connect === 'network-connect') {
        this.duration = 30
        this.startTime = this.$dayjs().unix()
        this.lottieInit()
        this.timeRunner()
      } else if (connect === 'room-connect') {
        setTimeout(() => {
          this.retry = 0
          this.tryRoomConnect()
        }, 5000)
      } else if (connect === 'cancel') {
        this.lottie.stop()
        this.stopTimeRunner()
        // } else if (connect === 'disconnected') {
      }
      this.state = connect
    },
    async tryRoomConnect() {
      try {
        const room = await getRoomInfo({
          sessionId: this.roomInfo.sessionId,
          workspaceId: this.workspace.uuid,
        })
        const user = room.memberList.find(
          member => member.memberType === ROLE.LEADER,
        )
        const joinRes = await this.join({
          ...room,
          leaderId: user ? user.uuid : null,
        })
        if (joinRes) {
          this.lottie.stop()
          this.stopTimeRunner()
          // this.$emit('update:visible', false)
          this.$eventBus.$emit('reJoin')
        } else {
          this.logout()
        }
      } catch (err) {
        if (err.code === 4002) {
          this.toastError(this.$t('workspace.remote_already_removed'))
        } else if (err.code === 4016) {
          if (this.retry > 1) {
            this.retry = 0
            this.toastError(this.$t('workspace.remote_already_invite'))
          } else {
            this.retry++
            setTimeout(() => {
              this.tryRoomConnect()
            }, 5000)
          }
        } else {
          this.toastError(this.$t('workspace.remote_invite_impossible'))
        }
        this.logout()
      }
    },
    timeRunner() {
      this.checkingNetwork = false
      this.stopTimeRunner()
      this.timer = setInterval(() => {
        const diff = this.$dayjs().unix() - this.startTime

        this.duration =
          30 -
          Math.round(
            this.$dayjs.duration(diff, 'seconds').as('milliseconds') / 1000,
          )
        if (!this.checkingNetwork) {
          this.checkingNetwork = true
          checkOnline().then(res => {
            if (res === true) {
              this.logger('network', 'onLine')
              this.setConnectionState('room-connect')
            } else {
              this.checkingNetwork = false
              this.logger('network', 'offLine')
            }
          })
        }
        if (this.duration === 0) {
          this.stopTimeRunner()
          this.setConnectionState('cancel')
        }
      }, 1000)
    },
    stopTimeRunner() {
      if (this.timer === null) return
      clearInterval(this.timer)
      this.timer = null
    },
    cancel() {
      switch (this.state) {
        case 'disconnected':
        case 'cancel':
          this.logout()
          return
        default:
          this.setConnectionState('cancel')
      }
    },
    logout() {
      this.$eventBus.$emit('call:logout')
    },
  },
  beforeDestroy() {
    this.stopTimeRunner()
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
