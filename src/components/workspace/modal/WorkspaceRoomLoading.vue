<template>
  <modal
    :showClose="false"
    :visible.sync="visibleFlag"
    customClass="modal-room-loading"
    :width="responsiveWidth"
    :height="responsiveHeight"
    :beforeClose="beforeClose"
    :dimClose="false"
    :class="{ modalless: true }"
  >
    <img
      class="room-loading__img"
      src="~assets/image/gif_loading.gif"
      alt="Loading"
    />
    <!-- @TODO:다국어 추가 -->
    <p class="room-loading__text">
      {{ $t('workspace.loading_prepare_remote') }}
    </p>
    <p class="room-loading__text opacity">
      {{ $t('workspace.loading_checking_device') }}
    </p>
  </modal>
</template>

<script>
import Modal from 'Modal'

export default {
  name: 'WorkspaceRoomLoading',
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
  computed: {
    responsiveWidth() {
      if (this.isMobileSize) return '21.4rem'
      else return '33em'
    },
    responsiveHeight() {
      if (this.isMobileSize) return '24.6rem'
      else return '22.8571em'
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
  },
}
</script>
<style lang="scss">
@import '~assets/style/mixin';
.modal.modal-room-loading {
  .modal--inner {
    background: #151517;
    border: 1px solid #525252;
    border-radius: 10px;
    box-shadow: none;
    opacity: 0.9;
  }
  .modal--header {
    display: none;
    flex: 0;
  }

  .modal--body {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
  }

  .room-loading__img {
    width: 8.5714rem;
    height: auto;
    margin-bottom: 1.2857rem;
  }

  .room-loading__text {
    color: #fafafa;
    font-weight: 500;
    font-size: 1.1429rem;
    text-align: center;

    &.opacity {
      opacity: 0.9;
    }
  }
  &.modalless {
    background-color: unset;
  }
}

@include responsive-mobile {
  .modal.modal-room-loading {
    .modal--inner {
      background-color: rgba(#000000, 0.86);
      border: 1.5px solid #757b8c;
    }
    .room-loading__img {
      margin-bottom: 1.5rem;
    }
    .room-loading__text {
      @include fontLevel(150);
      color: $new_color_text_sub;
      &.opacity {
        color: $new_color_text_main;
      }
    }
  }
}
</style>
