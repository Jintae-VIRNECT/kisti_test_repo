<template>
  <modal
    :width="'90%'"
    :height="'90%'"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :showHeader="false"
    customClass="modal-player"
    ><video :src="url" controls></video
  ></modal>
</template>

<script>
import Modal from 'Modal'
export default {
  name: 'ModalPlayer',
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
    url: {
      type: String,
      default: null,
    },
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

    > video {
      width: 100%;
      height: 100%;
      object-fit: fill;
      border: none;
    }
  }
}
</style>
