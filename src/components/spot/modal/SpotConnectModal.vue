<template>
  <modal
    :visible.sync="visibleFlag"
    :dimClose="false"
    :showClose="false"
    :width="modalWidth"
    class="spot-reconnect"
  >
    <div class="spot-reconnect__layout">
      <img class="spot-reconnect__img" :src="imgSrc" alt="" />

      <div class="spot-reconnect__body">
        <p class="reconnect-title">
          {{
            isReconnect
              ? $t('service.spot_reconnecting')
              : $t('service.spot_connecting')
          }}
        </p>
        <p class="reconnect-timer">
          {{ `${secondTimer} ${$t('date.second')}` }}
        </p>
      </div>
    </div>
    <div class="spot-reconnect__footer">
      <button class="cancel" @click="cancelReconnect">
        {{ $t('button.exit') }}
      </button>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'

export default {
  components: {
    Modal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    isReconnect: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visibleFlag: true,
      secondTimer: 0,
      timerId: null,
    }
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.startTimer
      } else {
        if (this.timerId) clearInterval(this.timerId)
        this.secondTimer = 0
      }

      this.visibleFlag = !!flag
    },
  },
  computed: {
    modalWidth() {
      if (this.isMobileSize) return '28.8rem'
      return '430px'
    },
    imgSrc() {
      if (this.isMobileSize)
        return require('assets/image/spot/img_spot_connect_mobile.svg')
      return require('assets/image/spot/img_spot_connect.svg')
    },
  },
  methods: {
    cancelReconnect() {
      this.$emit('reconnectCancel')
    },
    startTimer() {
      this.timerId = setInterval(() => {
        this.secondTimer++
      }, 1000)
    },
  },
  created() {
    if (this.visible) this.startTimer()
  },
}
</script>

<style lang="scss" src="assets/style/spot/spot-reconnect.scss"></style>
