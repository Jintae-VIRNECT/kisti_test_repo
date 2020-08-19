<template>
  <modal :visible.sync="visibleFlag" width="32.857rem" class="select-view">
    <div class="select-view__layout">
      <div class="select-view__header">
        <p>선택한 영상을 모든 참가자와 공유하시겠습니까?</p>
        <p>공유 시, 포인팅 기능을 사용할 수 있습니다.</p>
      </div>
      <div class="select-view__body">
        <figure>
          <div class="select-view__box">
            <img src="~assets/image/call/img_view_nomal.svg" />
          </div>
          <button
            class="btn select-view__button normal_view"
            @click="$emit('normal')"
          >
            일반 보기
          </button>
        </figure>
        <figure>
          <div class="select-view__box">
            <img src="~assets/image/call/img_view_sharing.svg" />
          </div>
          <button
            class="btn select-view__button share_view"
            @click="$emit('share')"
          >
            전체 공유
          </button>
        </figure>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'

export default {
  name: 'SettingModal',
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
      type: [Boolean, String],
      default: false,
    },
  },

  watch: {
    visible(flag) {
      this.visibleFlag = !!flag
    },
  },
}
</script>
