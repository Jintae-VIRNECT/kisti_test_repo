<template>
  <modal
    :width="'90%'"
    :height="'90%'"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :showHeader="false"
    customClass="modal-player"
  >
    <div @click="beforeClose" class="player-wrapper">
      <video
        @click.stop
        class="player-wrapper--video"
        :src="url"
        controls
      ></video></div
  ></modal>
</template>

<script>
import Modal from 'Modal'
export default {
  name: 'ModalPlayer',
  components: {
    Modal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    url: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      visibleFlag: false,
    }
  },
  watch: {
    async visible(flag) {
      this.visibleFlag = flag
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
.modal.modal-player {
  .modal--inner {
    background-color: transparent;
    border: none;
  }

  .modal--body {
    width: 100%;
    height: 100%;
    padding: 0px;

    .player-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
    }

    .player-wrapper--video {
      max-width: 100%;
      max-height: 57.1429rem;
      border: none;
    }
  }
}
</style>
