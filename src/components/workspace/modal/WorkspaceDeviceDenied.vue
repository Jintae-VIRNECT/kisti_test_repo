<template>
  <modal
    title=""
    width="39rem"
    height="13.7143rem"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="custom-modal"
    :class="{ modalLess: modalLess }"
  >
    <div class="device-denied">
      <div class="device-denied--header">
        카메라와 마이크 접근이 차단되었습니다.
      </div>
      <div class="device-denied__content">
        <div class="device-denieds__content--icon">
          <img src="~assets/image/call/gnb_ic_video_on.svg" />
        </div>
        <div class="device-denied__content--vbar"></div>
        <div class="device-denied__content--text">
          브라우저 주소창의
          <span class="device-denied__content--emphasize">아이콘</span>을
          클릭하여<br />
          접근을 허용 후 새로고침 해주세요.
        </div>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'

export default {
  name: 'WorkspaceDeviceDenied',

  components: {
    Modal,
  },
  data() {
    return {
      visibleFlag: false,
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    modalLess: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },

  /* Lifecycles */

  mounted() {},
}
</script>
<style lang="scss">
@import '~assets/style/vars';

/* custom Modal.vue */
.modal.custom-modal {
  &.modalLess {
    background-color: unset;
  }

  .modal--inner {
    background-color: $color_darkgray_500;
    border-radius: 4px;
  }
  .modal--header {
    height: 3.1429rem;
    padding: 0;
    border: none;
  }
  .modal--body {
    padding: 0;
  }
}

.device-denied {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.device-denied--header {
  margin-bottom: 1.7143rem;
  color: $color_white;
  font-size: 1.5714rem;
  text-align: center;
}

.device-denied__content {
  display: flex;
  flex-direction: row;
}

.device-denied__content--vbar {
  width: 0.072rem;
  height: 3.2857rem;
  margin: 0 1.1429rem 0 1.1429rem;
  background: #4a4a4d;
  border-radius: 0px;
}
.device-denied__content--text {
  color: #ababab;
  font-size: 1.1429rem;
}
.device-denied__content--emphasize {
  color: $color_primary_500;
}

.device-denieds__content--icon {
  width: 3.2857rem;
  height: 3.2857rem;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
