<template>
  <modal
    title=""
    width="34.125rem"
    height="12rem"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="devicedenied-modalless"
  >
    <div class="modalless">
      <div class="modalless__header">
        카메라와 마이크 접근이 차단되었습니다.
      </div>
      <div class="modalless__content">
        <div class="modalless__icon">
          <img src="~assets/image/call/gnb_ic_video_on.svg" />
        </div>
        <div class="modalless__vbar"></div>
        <div class="modalless__text">
          브라우저 주소창의
          <span class="modalless__text--emphasize">아이콘</span>을 클릭하여<br />
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
.modal.devicedenied-modalless {
  background-color: unset;
  .modal--inner {
    background-color: $color_darkgray_500;
  }
  .modal--header {
    height: 2.75rem;
    padding: 0;
    border: none;
  }
  .modal--body {
    padding: 0;
  }
}

.modalless {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.modalless__content {
  display: flex;
  flex-direction: row;
}
.modalless__header {
  margin-bottom: 1.5rem;
  color: $color_white;
  font-size: 1.375rem;
  text-align: center;
}
.modalless__vbar {
  width: 0.063rem;
  height: 2.875rem;
  margin: 0 1rem 0 1rem;
  background: #4a4a4d;
  border-radius: 0px;
}
.modalless__text {
  color: #ababab;
  font-size: 1rem;
}
.modalless__text--emphasize {
  color: $color_primary_500;
}
</style>
