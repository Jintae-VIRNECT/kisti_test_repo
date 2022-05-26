<template>
  <modal
    :visible.sync="visibleFlag"
    :dimClose="false"
    width="420"
    class="reconnect"
  >
    <div class="request-content-control-layout">
      <p>{{ $t('service.3d_content_control_request_ask') }}</p>
      <div class="check-toggle-wrap">
        <check
          :value.sync="doNotShowValue"
          :text="$t('button.do_not_show_again')"
        ></check>
      </div>

      <div class="request-content-control__footer">
        <button class="cancel" @click="cancel">
          {{ $t('button.cancel') }}
        </button>
        <button class="confirm" @click="request">
          {{ $t('button.confirm') }}
        </button>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import Check from 'Check'
import Cookies from 'js-cookie'
import { mapMutations } from 'vuex'

export default {
  name: 'Control3dRequestModal',
  components: {
    Modal,
    Check,
  },
  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
  },
  data() {
    return {
      visibleFlag: false,
      doNotShowValue: true,
    }
  },
  watch: {
    visible(flag) {
      this.visibleFlag = !!flag
    },
  },
  methods: {
    ...mapMutations(['SET_IS_REQUEST_MODAL_VISIBLE']),
    close() {
      this.SET_IS_REQUEST_MODAL_VISIBLE(false)
    },
    cancel() {
      this.$emit('cancel')
      this.close()
    },
    request() {
      if (this.doNotShowValue) Cookies.set('doNotShow_controlRequest', true)
      else {
        Cookies.remove('doNotShow_controlRequest')
      }

      //@TODO 요청 보내기

      this.$emit('requestSend')
      this.close()
    },
  },
}
</script>

<style
  lang="scss"
  src="assets/style/service/service-control-request-modal.scss"
></style>
