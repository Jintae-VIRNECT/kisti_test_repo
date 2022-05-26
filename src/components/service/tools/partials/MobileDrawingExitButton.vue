<template>
  <button class="mobile-drawing-exit-btn" @click="exitDrawing"></button>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'
import drawingMixin from 'mixins/drawing/drawing'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'MobileDrawingExitButton',
  mixins: [confirmMixin, drawingMixin],
  computed: {
    ...mapGetters(['view', 'shareFile']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    ...mapActions(['showImage', 'setView']),
    exitDrawing() {
      if (!this.isLeader) return
      this.$_exitDrawing()
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-drawing-exit-btn {
  @include mobile-circle-btn(#e4434a);
  @include mobile-circle-btn-icon('~assets/image/ic_close_24.svg');
}
</style>
