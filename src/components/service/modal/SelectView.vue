<template>
  <modal :visible.sync="visibleFlag" width="32.857rem" class="select-view">
    <div class="select-view__layout">
      <div class="select-view__header">
        <p>{{ $t('service.stream_choice_share') }}</p>
        <p>{{ $t('service.stream_choice_share_description') }}</p>
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
            {{ $t('button.stream_normal') }}
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
            {{ $t('button.stream_sharing') }}
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
      type: [Boolean, Object],
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
