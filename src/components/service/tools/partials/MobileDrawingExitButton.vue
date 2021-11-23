<template>
  <button class="mobile-drawing-exit-btn" @click="exitDrawing"></button>
</template>

<script>
import { mapGetters } from 'vuex'
import { VIEW } from 'configs/view.config'
import confirmMixin from 'mixins/confirm'
import { DRAWING, ROLE } from 'configs/remote.config'

export default {
  name: 'MobileDrawingExitButton',
  mixins: [confirmMixin],
  computed: {
    ...mapGetters(['view', 'shareFile']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  methods: {
    exitDrawing() {
      if (!this.isLeader) return //리더만 종료 가능(버튼이 리더에게만 활성화 되있긴함)

      if (this.view === VIEW.DRAWING) {
        if (this.shareFile && this.shareFile.id) {
          this.confirmCancel(this.$t('service.toast_exit_drawing'), {
            text: this.$t('button.exit'),
            action: () => {
              this.$call.sendDrawing(DRAWING.END_DRAWING)
              this.$emit('exitDrawing')
              //this.goTabConfirm(type)
            },
          })
          return
        }
      }
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
